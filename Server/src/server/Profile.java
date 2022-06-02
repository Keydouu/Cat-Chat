package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

class Profile {

    private int id;
    private String email;
    private String name;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Statement stmt;
    private ArrayList<Group> myGroups;
    private static Map<Profile, DataOutputStream> mapDos;
    private static ArrayList<Group> allGroups;
    private Lock lock;
    private SecretKey myKey;

    public Profile(int id, String email, String name, DataOutputStream dos, DataInputStream dis, Statement stmt, SecretKey key) {
        //this.dos.writeUTF( encrypt(Integer.toString(bytes.length)) );
        this.email = email.toLowerCase();
        this.name = name;
        this.id = id;
        this.dos = dos;
        this.dis = dis;
        this.stmt = stmt;
        this.lock = new ReentrantLock();
        this.myKey=key;

        mapDos.put(this, this.dos);
        this.myGroups = new ArrayList<>();
        addMyGroups();
    }
    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    public void writeUTFEncoded(String s) throws IOException{
        this.dos.writeUTF(encrypt(s));
    }
    public void writeEncoded(byte[] b) throws IOException{
        b=encrypt(new String(Base64.getEncoder().encode(b))).getBytes(); 
        writeUTFEncoded(Integer.toString(b.length));
        this.dos.write(b);
    }
    private String encrypt(String plainPwd){
        byte[] outputBytes = new byte[]{};
        String returnString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, myKey);
            outputBytes = cipher.doFinal(plainPwd.getBytes("utf-8"));
            if (null != outputBytes)
		returnString = encode(outputBytes);
            return returnString.trim();
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(outputBytes).trim();
    }

    private String decrypt(String encryptedPwd) {
        byte[] outputBytes = new byte[]{};
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, myKey);
            byte[] inputBytes = decode(encryptedPwd);
            if (null != inputBytes) {
                outputBytes = cipher.doFinal(inputBytes);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(outputBytes).trim();
    }
    public static Map<Profile, DataOutputStream> getMap(){return mapDos;}
    public static void inintaliseProfiles() {
        if (mapDos == null) {
            System.out.print("initialising ...");
            mapDos = new HashMap<>();
            allGroups = new ArrayList<>();
            System.out.println(" Done !");
        }
    }

    public int getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public void addMyGroups() {
        String sql = "select * from Groupe, users_groups where users_groups.user_id = '" + this.id + "'";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int group_id = rs.getInt("group_id");
                boolean groupFound = false;
                for (int i = 0; i < allGroups.size(); i++) {
                    if (allGroups.get(i).getId() == group_id) {
                        this.myGroups.add(allGroups.get(i));
                        groupFound = true;
                        break;
                    }
                }
                if (!groupFound) {
                    Group g = new Group(group_id, rs.getString("groupe_name"), rs.getString("groupe_description"), rs.getInt("Groupe_admin_id"));
                    this.myGroups.add(g);
                    allGroups.add(g);
                }
            }
            for (int i = 0; i < this.myGroups.size(); i++) {
                if (!this.myGroups.get(i).isMember(this.getId())) {
                    //get group memebers from database
                    sql = "select * from users_groups where group_id = '" + this.myGroups.get(i).getId() + "'";
                    rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        this.myGroups.get(i).addMember(rs.getInt("user_id"));
                    }
                }
                this.myGroups.get(i).memberConnected(this.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Profile isOnline(String searchedEmail) {
        Iterator<Map.Entry<Profile, DataOutputStream>> iterator = mapDos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Profile, DataOutputStream> entry = iterator.next();
            if (searchedEmail.toLowerCase().equals(entry.getKey().getEmail().toLowerCase())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void connected(DataOutputStream dos) {
        Iterator<Map.Entry<Profile, DataOutputStream>> iterator = Profile.mapDos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Profile, DataOutputStream> entry = iterator.next();
            if (!entry.getKey().getEmail().equals(this.getEmail())) {
                try {
                    entry.getKey().lockMe();
                    entry.getKey().writeUTFEncoded("connection@@@" + this.getEmail());
                    this.lockMe();
                    writeUTFEncoded("connection@@@" + entry.getKey().getEmail());
                    this.unlockMe();
                    entry.getKey().unlockMe();
                } catch (Exception e) {
                    this.unlockMe();
                    entry.getKey().unlockMe();
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean sendMessage(String target, String msg) {
        String s[] = msg.split("@@@");
        if (!s[0].equals("videoCall") && !s[0].equals("audioCall")) {
            System.out.println(this.getEmail() + " is sending " + s[0] + " to " + target);
        }
        byte bytes[] = new byte[0];
        if (!s[0].equals("text")) {
            try {
                int i = Integer.parseInt(decrypt(dis.readUTF()));
                bytes = new byte[i];
                dis.readFully(bytes);
                bytes=Base64.getDecoder().decode(decrypt(new String(bytes)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Profile p = isOnline(target);
        if (p != null) {
            try {
                p.lockMe();
                p.writeUTFEncoded(msg);
                if (!s[0].equals("text")) {
                    p.writeEncoded(bytes);
                }
                p.unlockMe();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                p.unlockMe();
                System.out.println("Failed to print message");
                return false;
            }
        }
        System.out.println(this.getEmail() + " failed to send " + s[0] + " to " + target);
        if (s[0].equals("videoCall") || s[0].equals("audioCall")) {
            return false;
        }
        String sql;
        if (s[0].equals("text")) {
            sql = "insert into messages (sender_Email,receiver_Email,message,messageType,date) values ('" + this.getEmail() + "','" + target + "','"
                    +s[3].replace("'", "\\'") + "', '" + s[0] + "', '" + s[2] + "')";
        } else {
            sql = "insert into messages (sender_Email,receiver_Email,message,messageType, fileName,date) values ('" + this.getEmail() + "','" + target + "','"
                    +(new String(Base64.getEncoder().encode(bytes))) + "', '" + s[0] + "', '" + s[3].replace("'", "\\'") + "', '" + s[2] + "')";
        }
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean sendMessageToGroup(int target, String msg) {
        String s[] = msg.split("@@@");
        System.out.println(this.getEmail() + " is sending " + s[0] + " to group " + target);
        byte bytes[] = new byte[0];
        if (!s[0].equals("text")) {
            try {
                int i = Integer.parseInt(decrypt(dis.readUTF()));
                bytes = new byte[i];
                dis.readFully(bytes);
                bytes=Base64.getDecoder().decode(decrypt(new String(bytes)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < this.myGroups.size(); i++) {
            if (this.myGroups.get(i).getId() == target) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String messageSendingTimeStamp = dtf.format(now);
                Iterator<Map.Entry<Profile, DataOutputStream>> iterator = mapDos.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Profile, DataOutputStream> entry = iterator.next();
                    if (this.myGroups.get(i).isMember(entry.getKey().getId())) {
                        if (this.myGroups.get(i).isConnected(entry.getKey().getId()) && entry.getKey().getId() != this.getId()) {
                            try {
                                entry.getKey().lockMe();
                                entry.getKey().writeUTFEncoded(msg);
                                if (!s[0].equals("text")) {
                                    entry.getKey().writeEncoded(bytes);
                                }
                                entry.getKey().unlockMe();
                            } catch (IOException e) {
                                e.printStackTrace();
                                entry.getKey().unlockMe();
                            }
                        }
                    }
                }
                String[] offlineMembers = this.myGroups.get(i).getDisconnectedMembers().split("\n");
                if (offlineMembers.length > 0) {
                    String sql;
                    if (s[0].equals("text")) {
                        sql = "insert into Groupe_Message_content (Groupe_id,sender_name,sender_Email,messageType,content,date) values ('"
                            + this.myGroups.get(i).getId() + "','" + this.getName() + "','" + this.getEmail() + "','" + s[0] + "','"+ s[5].replace("'", "\\'") + "','" + s[4] + "')";
                    } else {
                        sql = "insert into Groupe_Message_content (Groupe_id,sender_name,sender_Email,messageType,content, fileName,date) values ('"
                            + this.myGroups.get(i).getId() + "','" + this.getName() + "','" + this.getEmail() + "','" + s[0] + "','"+ (new String(Base64.getEncoder().encode(bytes))) + "','" + s[4].replace("'", "\\'") + "','"+s[5]+"')";
                    }
                    try {
                        stmt.executeUpdate(sql);
                        sql = "select * from Groupe_Message_content where sender_Email = '" + this.getEmail() + "' order by GMC_id desc limit 1";
                        ResultSet rs = stmt.executeQuery(sql);
                        int GMC_id = 0;
                        if (rs.next()) {
                            GMC_id = rs.getInt("GMC_id");
                        }
                        for (String elm : offlineMembers) {
                            sql = "insert into Groupe_Messages (receiver_id, GMC_id) values ('" + elm + "','" + GMC_id + "')";
                            try {
                                stmt.executeUpdate(sql);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void lockMe() {
        this.lock.lock();
    }

    public void unlockMe() {
        this.lock.unlock();
    }

    public void disconnect() {
        mapDos.remove(this);
        for (int i = 0; i < this.myGroups.size(); i++) {
            this.myGroups.get(i).memberDisconnected(this.getId());
            if (this.myGroups.get(i).allOffline()) {
                allGroups.remove(this.myGroups.get(i));
            }
        }
        Iterator<Map.Entry<Profile, DataOutputStream>> iterator = mapDos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Profile, DataOutputStream> entry = iterator.next();
            entry.getKey().lockMe();
            try {
                entry.getKey().writeUTFEncoded("disconnection@@@" + this.getEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }
            entry.getKey().unlockMe();
        }
    }
}
