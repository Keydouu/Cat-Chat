package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.sql.*;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class GestionClient extends Thread {

    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Socket commthread;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private Profile profile = null;
    private String msg;
    private String receiver;
    private boolean loginOK = false;
    private int KEY_SIZE = 128;
    private SecretKey key;//HADI HIYA KEY LI GHANESTA3MEL

    public GestionClient(Socket s, DataInputStream diss, DataOutputStream doss) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        this.commthread = s;
        this.dis = diss;
        this.dos = doss;
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/catchat", "root", "");
            this.stmt = this.conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur database");
            error();
        }
        if (this.conn != null && this.stmt != null) {
            this.start();
        } else {
            error();
        }
    }
    public String encryptRsa(String message, PublicKey publickey) throws Exception {
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(cipher.ENCRYPT_MODE, publickey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }
    byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    
    public String encrypt(String plainPwd){
        byte[] outputBytes = new byte[]{};
        String returnString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            outputBytes = cipher.doFinal(plainPwd.getBytes("utf-8"));
            if (null != outputBytes)
		returnString = encode(outputBytes);
            return returnString.trim();
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(outputBytes).trim();
    }

    public String decrypt(String encryptedPwd) {
        byte[] outputBytes = new byte[]{};
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] inputBytes = decode(encryptedPwd);
            if (null != inputBytes) {
                outputBytes = cipher.doFinal(inputBytes);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(outputBytes).trim();
    }
    @Override
    public void run() {
        String keypubC;
        try {
            keypubC = dis.readUTF();
            System.out.println("public key : "+ keypubC);
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(keypubC));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publickey = keyFactory.generatePublic(keySpecPublic);
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(KEY_SIZE);
            key = generator.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
            String finalKeyString=encryptRsa(encodedKey, publickey);
            dos.writeUTF(finalKeyString);
            System.out.println("AES: "+ encodedKey);
        } catch (Exception ex) {
            Logger.getLogger(GestionClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            while (!this.loginOK) {
                try {
                    String message = decrypt(this.dis.readUTF());
                    String strarray[] = message.split(" ", 2);
                    if (strarray.length == 2) {
                        if (strarray[0].equals("login")) {
                            this.loginOK = login(strarray[1].toLowerCase(), decrypt(dis.readUTF()));
                            if (this.loginOK) {//load Message from DB
                                try {
                                    String sql = "select * from messages where receiver_Email='" + this.profile.getEmail() + "'";
                                    rs = stmt.executeQuery(sql);
                                    while (rs.next()) {
                                        if (rs.getString("messageType").equals("text")) {
                                            dos.writeUTF(encrypt(rs.getString("messageType") + "@@@" + rs.getString("sender_Email") + "@@@" + rs.getString("date") + "@@@" + rs.getString("message")));
                                        } else {
                                            dos.writeUTF(encrypt(rs.getString("messageType") + "@@@" + rs.getString("sender_Email") + "@@@" + rs.getString("date") + "@@@" + rs.getString("fileName")));
                                            byte[] bytes=Base64.getDecoder().decode(rs.getString("message"));
                                            this.profile.writeEncoded(bytes);
                                        }
                                    }
                                    sql = "delete from messages where receiver_Email='" + this.profile.getEmail() + "'";
                                    stmt.executeUpdate(sql);
                                    ArrayList<Integer> Group_messages_received = new ArrayList<Integer>();
                                    sql = "select * from Groupe_Messages join Groupe_Message_content on Groupe_Messages.GMC_id=Groupe_Message_content.GMC_id where receiver_id='" + this.profile.getId() + "'";
                                    rs = stmt.executeQuery(sql);
                                    while (rs.next()) {
                                        Group_messages_received.add(rs.getInt("GMC_id"));
                                        if (rs.getString("messageType").equals("text")) {
                                            dos.writeUTF(encrypt(rs.getString("messageType") + "@@@Group@@@" + rs.getString("Groupe_id") + "@@@" + rs.getString("sender_Email") + "@@@" + rs.getString("date") + "@@@" + rs.getString("content")));
                                        } else {
                                            dos.writeUTF(encrypt(rs.getString("messageType") + "@@@Group@@@" + rs.getString("Groupe_id") + "@@@" + rs.getString("sender_Email") + "@@@" + rs.getString("date") + "@@@" + rs.getString("fileName")));
                                            byte[] bytes=Base64.getDecoder().decode(rs.getString("message"));
                                            this.profile.writeEncoded(bytes);
                                        }
                                    }
                                    sql = "delete from Groupe_Messages where receiver_id='" + this.profile.getId() + "'";
                                    stmt.executeUpdate(sql);
                                    this.profile.unlockMe();
                                    for (int i = 0; i < Group_messages_received.size(); i++) {
                                        sql = "select * from Groupe_Messages where GMC_id='" + Group_messages_received.get(i) + "'";
                                        rs = stmt.executeQuery(sql);
                                        if (!rs.next()) {
                                            sql = "delete from Groupe_Message_content where GMC_id='" + Group_messages_received.get(i) + "'";
                                            stmt.executeUpdate(sql);
                                        }
                                    }
                                    this.profile.connected(this.dos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    error();
                                    break;
                                }
                            }
                        } else if (strarray[0].equals("register")) {
                            if (register(strarray[1], decrypt(dis.readUTF()), decrypt(dis.readUTF()))) {
                                dos.writeUTF(encrypt("registerOK"));
                            } else {
                                dos.writeUTF(encrypt("registerError"));
                            }
                        } else {
                            System.out.println("Unknown command : " + message);
                        }
                    } else {
                        System.out.println("Unknown command : " + message);
                    }
                } catch (Exception e) {
                    System.out.println("Client disconnected");
                    error();
                    break;
                }
            }
            if (!this.loginOK) {
                error();
            }
            try {
                //User Connect, we wait for what he wanna do
                receiver = decrypt(dis.readUTF());
                //System.out.println(this.profile.getName()+" :  "+receiver.substring(0, Math.min(50, receiver.length())));
                String strarray[] = receiver.split("@@@", 2);
                if (strarray.length == 2) { //confirme que le split a marché
                    switch (strarray[0]) {
                        case "messageUser" -> {
                            //System.out.println("receiver : " + strarray[1]);
                            msg = decrypt(dis.readUTF());
                            this.profile.sendMessage(strarray[1], msg);
                        }
                        case "messageGroup" -> {
                            //System.out.println("receiver : Groupe_id = " + strarray[1]);
                            msg = decrypt(dis.readUTF());
                            if (this.profile.sendMessageToGroup(Integer.parseInt(strarray[1]), msg)) {
                                System.out.println(this.profile.getName() + " in " + strarray[1] + " : " + msg);
                            } else {
                                System.out.println("User " + this.profile.getName() + " is not in group " + strarray[1]);
                            }
                        }
                        case "checkIfExist" -> {
                            String sql = "select * from Users where email='" + strarray[1] + "'";
                            try {
                                rs = stmt.executeQuery(sql);
                                if (rs.next()) {
                                    dos.writeUTF(encrypt("exist@@@" + strarray[1]));
                                } else {
                                    dos.writeUTF(encrypt("notExist@@@" + strarray[1]));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        default ->
                            System.out.println("1 receiver not found " + strarray[0] + " " + strarray[1]);
                    }
                } else if (receiver.equals("Disconnect")) {
                    this.loginOK = false;
                    this.profile.disconnect();;
                    System.out.println(this.profile.getName() + " disconnected");
                } else {
                    System.out.println("2 receiver not found " + receiver);
                }
            } catch (IOException e) {
                if (this.profile != null) {
                    System.out.println(profile.getName() + " disconnected");
                }
                error();
                break;
            }
        }
    }

    private boolean login(String login, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email = '" + login + "' AND password = '" + password + "'";
            this.rs = this.stmt.executeQuery(sql);
            if (this.rs.next()) {
                if (rs.getString("password").equals(password)) {
                    this.dos.writeUTF(encrypt("Login Successful"));
                    System.out.println(login + " : Login Successful");
                    this.profile = new Profile(this.rs.getInt("user_id"), this.rs.getString("email"),
                            this.rs.getString("name"), this.dos, this.dis, this.stmt, this.key);
                    this.profile.lockMe();
                    return true;
                }
            }
            this.dos.writeUTF(encrypt("Login Failed"));
            System.out.println(login + " : Login Failed");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean register(String name, String Email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email = '" + Email + "'";
            this.rs = this.stmt.executeQuery(sql);
            if (this.rs.next()) {
                this.dos.writeUTF(encrypt("Email already used"));
                System.out.println(Email + " already used");
                return false;
            } else {
                sql = "INSERT INTO users (name, email, password) VALUES ('" + name + "','" + Email + "','" + password + "')";
                this.stmt.executeUpdate(sql);
                this.dos.writeUTF(encrypt("Registration Successful"));
                System.out.println("Registration Successful");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void error() {
        if (this.loginOK && this.profile != null) {
            this.profile.disconnect();
        }
        try {
            this.dis.close();
            this.dos.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            this.commthread.close();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }
}
