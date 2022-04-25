import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Server2{
	public static void main(String[] args) {
        ServerSocket connSocket=null;
        try{
		    connSocket = new ServerSocket(5057);
        }
        catch (Exception e2){
            System.out.println("Erreur de connexion");
        }
		Profile.inintaliseProfiles();
		while (true){
			Socket commSocket = null;
			try{
				commSocket = connSocket.accept();
				System.out.println("Nouvelle connection : " + commSocket);
				DataInputStream dis = new DataInputStream(commSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(commSocket.getOutputStream());
				Thread t = new GestionClient(commSocket, dis, dos);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
class GestionClient extends Thread{
	private final DataInputStream dis;
	private final DataOutputStream dos;
	private final Socket commthread;
	private Connection conn;
	private Statement stmt;
	private ResultSet rs;
	private Profile profile=null;
	private String msg;
	private String receiver;
	private boolean loginOK=false;
	public GestionClient(Socket s, DataInputStream diss, DataOutputStream doss){
		this.commthread = s;
		this.dis = diss;
		this.dos = doss;
		try{
			this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ness_chat","root","Sudo516225");
			this.stmt = this.conn.createStatement();
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Erreur database");
			error();
		}
		if(this.conn != null && this.stmt != null){
			this.start();
		}
		else{
			error();
		}
	}
	@Override
	public void run(){
		while (true){
			while(!this.loginOK){
				try{
					String message = this.dis.readUTF();
					String strarray[] = message.split(" ",2);
					if(strarray.length==2){
						if(strarray[0].equals("login")){
							this.loginOK=login(strarray[1].toLowerCase(),dis.readUTF());
							if(this.loginOK){//load Message from DB
								try{
									String sql="select * from messages where receiver_Email='"+this.profile.getEmail()+"'";
									rs=stmt.executeQuery(sql);
									while(rs.next()){
                                        if(rs.getString("messageType").equals("text"))
                                            dos.writeUTF(rs.getString("messageType")+"@@@"+rs.getString("sender_Email")+"@@@"+rs.getString("date")+"@@@"+rs.getString("message"));
                                        else{
                                            dos.writeUTF(rs.getString("messageType")+"@@@"+rs.getString("sender_Email")+"@@@"+rs.getString("date"));
                                            byte[] bytes=Base64.getDecoder().decode(rs.getString("message"));
											this.dos.writeInt(bytes.length);
											this.dos.write(bytes);
                                        }
                                    }
                                    sql="delete from messages where receiver_Email='"+this.profile.getEmail()+"'";
									stmt.executeUpdate(sql);
									ArrayList<Integer> Group_messages_received=new ArrayList<Integer>();
									sql="select * from Groupe_Messages join Groupe_Message_content on Groupe_Messages.GMC_id=Groupe_Message_content.GMC_id where receiver_id='"+this.profile.getId()+"'";
									rs=stmt.executeQuery(sql);
									while(rs.next()){
										Group_messages_received.add(rs.getInt("GMC_id"));
                                        if(rs.getString("messageType").equals("text"))
										    dos.writeUTF(rs.getString("messageType")+"@@@Group@@@"+rs.getString("Groupe_id")+"@@@"+rs.getString("sender_Email")+"@@@"+rs.getString("date")+"@@@"+rs.getString("content"));
                                        else{
                                            dos.writeUTF(rs.getString("messageType")+"@@@Group@@@"+rs.getString("Groupe_id")+"@@@"+rs.getString("sender_Email")+"@@@"+rs.getString("date"));
                                            byte[] bytes=Base64.getDecoder().decode(rs.getString("content"));
											this.dos.writeInt(bytes.length);
											this.dos.write(bytes);
                                        }
									}
									sql="delete from Groupe_Messages where receiver_id='"+this.profile.getId()+"'";
									stmt.executeUpdate(sql);
									this.profile.unlockMe();
									for(int i=0;i<Group_messages_received.size();i++){
										sql ="select * from Groupe_Messages where GMC_id='"+Group_messages_received.get(i)+"'";
										rs=stmt.executeQuery(sql);
										if(!rs.next()){
											sql="delete from Groupe_Message_content where GMC_id='"+Group_messages_received.get(i)+"'";
											stmt.executeUpdate(sql);
										}
									}
								}
								catch(Exception e){
									e.printStackTrace();
									error();
									break;
								}
							}
						}
						else if(strarray[0].equals("register")){
							//name email password
							if(register(strarray[1],dis.readUTF(),dis.readUTF()))
								dos.writeUTF("registerOK");
							else
								dos.writeUTF("registerError");
						}
						else
							System.out.println("Unknown command : "+message);
					}
					else
						System.out.println("Unknown command : "+message);
				}
				catch(Exception e){
					System.out.println("Client disconnected");
					error();
					break;
				}
			}
			if(!this.loginOK)
				error();
			try {
				//User Connect, we wait for what he wanna do
				receiver = dis.readUTF();
				System.out.println(this.profile.getName()+" :  "+receiver.substring(0, Math.min(50, receiver.length())));
                String strarray[] = receiver.split("@@@",2);
				if(strarray.length==2){//confirme que le split a marché
					if (strarray[0].equals("messageUser")){
						System.out.println("receiver : " + strarray[1]);
						msg = dis.readUTF();
						this.profile.sendMessage(strarray[1], msg);
					}
					else if (strarray[0].equals("messageGroup")){
						System.out.println("receiver : Groupe_id = " + strarray[1]);
						msg=dis.readUTF();
						if(this.profile.sendMessageToGroup(Integer.parseInt(strarray[1]), msg))
							System.out.println(this.profile.getName()+" in "+strarray[1]+" : "+ msg);
						else
							System.out.println("User "+this.profile.getName()+" is not in group "+strarray[1]);
					}
					else{
						System.out.println("1 receiver not found " + strarray[0]+ " " + strarray[1]);
					}
				}
				else if(receiver.equals("Disconnect")){
					this.loginOK=false;
					this.profile.disconnect();;
					System.out.println(this.profile.getName()+" disconnected");
				}
				else
					System.out.println("2 receiver not found " + receiver);
			}
			catch (IOException e) {
				if(this.profile!=null)
					System.out.println(profile.getName()+" disconnected");
				error();
				break;
			}
		}
	}
	private  boolean login(String login, String password){
		try{
			String sql = "SELECT * FROM users WHERE email = '"+login+"' AND password = '"+password+"'";
			this.rs = this.stmt.executeQuery(sql);
			if(this.rs.next()){
                if(rs.getString("password").equals(password)){
                    this.dos.writeUTF("Login Successful");
                    System.out.println(login+" : Login Successful");
                    this.profile = new Profile(this.rs.getInt("user_id"),this.rs.getString("email"),
                    this.rs.getString("name"), this.dos, this.dis, this.stmt);
                    this.profile.lockMe();
                    return true;
                }
			}
			this.dos.writeUTF("Login Failed");
			System.out.println(login+" : Login Failed");
			return false;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	private boolean register(String name, String Email, String password){
		try{
			String sql = "SELECT * FROM users WHERE email = '"+Email+"'";
			this.rs = this.stmt.executeQuery(sql);
			if(this.rs.next()){
				this.dos.writeUTF(Email+" already used");
				System.out.println(Email+" already used");
				return false;
			}
			else{
				sql = "INSERT INTO users (name, email, password) VALUES ('"+name+"','"+Email+"','"+password+"')";
				this.stmt.executeUpdate(sql);
				this.dos.writeUTF("Registration Successful");
				System.out.println("Registration Successful");
				return true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	private  void error(){
		if(this.loginOK&&this.profile!=null){
			this.profile.disconnect();
		}
		try{
			this.dis.close();
			this.dos.close();			
		}catch(IOException e3){
			e3.printStackTrace();
		}
		try{
			this.commthread.close();
		}catch(IOException e4){
			e4.printStackTrace();
		}
	}
}
class Profile{
	private int id;
	private String email;
	private String name;
	private DataOutputStream dos;
    private DataInputStream dis;
	private Statement stmt;
	private ArrayList<Group> myGroups;
	private static Map<Profile, DataOutputStream> mapDos;
	private static ArrayList<Group> allGroups;
	private boolean inVideoCall;
	private Lock lock;
	public Profile(int id, String email, String name, DataOutputStream dos, DataInputStream dis ,Statement stmt){
		inVideoCall=false;
		this.email = email;
		this.name = name;
		this.id = id;
		this.dos = dos;
        this.dis=dis;
		this.stmt = stmt;
		this.lock = new ReentrantLock();
		
		mapDos.put(this, this.dos);
		this.myGroups = new ArrayList<>();
		addMyGroups();
	}
	public static void inintaliseProfiles(){
		if(mapDos == null){
			System.out.print("initialising ...");
			mapDos = new HashMap<>();
			allGroups = new ArrayList<>();
			System.out.println(" Done !");
		}
	}
	public int getId(){return this.id;}
	public String getEmail(){return this.email;}
	public String getName(){return this.name;}
	public void addMyGroups(){
		String sql="select * from Groupe, users_groups where users_groups.user_id = '"+this.id+"'";
		try{
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				int group_id = rs.getInt("group_id");
				boolean groupFound=false;
				for (int i = 0; i < allGroups.size(); i++){
					if(allGroups.get(i).getId()==group_id){
						this.myGroups.add(allGroups.get(i));
						groupFound=true;
						break;
					}
				}
				if(!groupFound){
					Group g = new Group(group_id,rs.getString("groupe_name"),rs.getString("groupe_description"),rs.getInt("Groupe_admin_id"));
					this.myGroups.add(g);
					allGroups.add(g);
				}
			}
			for (int i = 0; i < this.myGroups.size(); i++){
				if(!this.myGroups.get(i).isMember(this.getId())){
					//get group memebers from database
					sql = "select * from users_groups where group_id = '"+this.myGroups.get(i).getId()+"'";
					rs = stmt.executeQuery(sql);
					while(rs.next()){
						this.myGroups.get(i).addMember(rs.getInt("user_id"));
					}
				}
				this.myGroups.get(i).memberConnected(this.getId());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public boolean sendMessage(String target,String msg){
        String s[]=msg.split("@@@");
        if(!s[0].equals("videoCall")&&!s[0].equals("audioCall"))
            System.out.println(this.getEmail()+" is sending "+s[0]+" to "+target);
        byte bytes[]=new byte[0];
        if(!s[0].equals("text")){
            try{
                int i=dis.readInt();
                bytes=new byte[i];
                dis.readFully(bytes);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
		Iterator<Map.Entry<Profile, DataOutputStream>> iterator = mapDos.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Profile, DataOutputStream>
			entry= iterator.next();
			if (target.equals(entry.getKey().getEmail())) {
				try {
					entry.getKey().lockMe();
					entry.getValue().writeUTF(msg);
                    if(!s[0].equals("text")){
                        entry.getValue().writeInt(bytes.length);
                        entry.getValue().write(bytes);
                    }
					entry.getKey().unlockMe();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					entry.getKey().unlockMe();
					System.out.println("Failed to print message");
					return false;
				}
			}
		}
        if(s[0].equals("videoCall")||s[0].equals("audioCall"))
            return false;
		String sql = "insert into messages (sender_Email,receiver_Email,message,messageType,date) values ('"+this.getEmail()+"','"+target+"','";
        if(s[0].equals("text"))
            sql+=s[3]+"', '"+s[0]+"', '"+s[2]+"')";
        else
            sql+= (new String(Base64.getEncoder().encode(bytes))) +"', '"+s[0]+"', '"+s[2]+"')";
		try{stmt.executeUpdate(sql);}
		catch(Exception e){e.printStackTrace();}
		return true;
	}
	public boolean sendMessageToGroup(int target,String msg){
        String s[]=msg.split("@@@");
        System.out.println(this.getEmail()+" is sending "+s[0]+" to group "+target);
        byte bytes[]=new byte[0];
        if(!s[0].equals("text")){
            try{
                int i=dis.readInt();
                bytes=new byte[i];
                dis.readFully(bytes);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
		for (int i = 0; i < this.myGroups.size(); i++){
			if(this.myGroups.get(i).getId()==target){
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				String messageSendingTimeStamp = dtf.format(now);
				Iterator<Map.Entry<Profile, DataOutputStream>> iterator = mapDos.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Profile, DataOutputStream>
					entry= iterator.next();
					if (this.myGroups.get(i).isMember(entry.getKey().getId())) {
						if (this.myGroups.get(i).isConnected(entry.getKey().getId())&&entry.getKey().getId()!=this.getId()){
							try {
								entry.getKey().lockMe();
								entry.getValue().writeUTF(msg);
                                if(!s[0].equals("text")){
                                    entry.getValue().writeInt(bytes.length);
                                    entry.getValue().write(bytes);
                                }
								entry.getKey().unlockMe();
							} catch (IOException e) {e.printStackTrace();entry.getKey().unlockMe();}
						}
					}
				}
				String [] offlineMembers = this.myGroups.get(i).getDisconnectedMembers().split("\n");
				if(offlineMembers.length>0){
					String sql = "insert into Groupe_Message_content (Groupe_id,sender_name,sender_Email,messageType,content,date) values ('"+
					this.myGroups.get(i).getId()+"','"+this.getName()+"','"+this.getEmail()+"','"+s[0]+"','";
                    if(s[0].equals("text"))
                        sql+=s[5]+"','"+s[4]+"')";
                    else
                        sql+= (new String(Base64.getEncoder().encode(bytes))) +"','"+s[4]+"')";
					try{
						stmt.executeUpdate(sql);
						sql = "select * from Groupe_Message_content where sender_Email = '"+this.getEmail()+"' order by GMC_id desc limit 1";
						ResultSet rs = stmt.executeQuery(sql);
						int GMC_id = 0;
						if(rs.next())
							GMC_id = rs.getInt("GMC_id");
						for(String elm: offlineMembers){
							sql = "insert into Groupe_Messages (receiver_id, GMC_id) values ('"+elm+"','"+GMC_id+"')";
							try {
								stmt.executeUpdate(sql);
							} catch (SQLException e) {e.printStackTrace();}
						}
					}
					catch(Exception e){e.printStackTrace();}
				}
				return true;
			}
		}
		return false;
	}
	public boolean isInVideoCall(){
		return this.inVideoCall;
	}
	public void enteredVideoCall(){
		this.inVideoCall=true;
	}
	public void exitedVideoCall(){
		this.inVideoCall=false;
	}
	public void lockMe(){
		this.lock.lock();
	}
	public void unlockMe(){
		this.lock.unlock();
	}
	public void disconnect(){
		mapDos.remove(this);
		for (int i = 0; i < this.myGroups.size(); i++){
			this.myGroups.get(i).memberDisconnected(this.getId());
			if(this.myGroups.get(i).allOffline())
				allGroups.remove(this.myGroups.get(i));
		}
	}
}
class Group{
	private int id;
	private String name;
	private String description;
	private int adminId;
	private Map<Integer,Boolean> membersMap; 
	public Group(int id, String name, String description, int admin){
		this.id = id;
		this.name = name;
		this.description = description;
		this.adminId = admin;
		this.membersMap = new HashMap<>();
	}
	public int getId(){return this.id;}
	public String getName(){return this.name;}
	public String getDescription(){return this.description;}
	public int getAdminId(){return this.adminId;}
	public boolean isMember(int id){return this.membersMap.containsKey(id);}
	public String getDisconnectedMembers(){
		String result = "";
		Iterator<Map.Entry<Integer, Boolean> >
		iterator = membersMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Boolean>
			entry= iterator.next();
			if (!entry.getValue())
				result += entry.getKey()+"\n";	
		}
		return result.substring(0, result.length() - 1);
	}
	public void memberConnected(int memberID){
		if(membersMap.containsKey(memberID))
			this.membersMap.put(memberID, true);
	}
	public void memberDisconnected(int memberID){
		if(membersMap.containsKey(memberID))
			this.membersMap.put(memberID, false);
	}
	public boolean isConnected(int memberID){
		if(membersMap.containsKey(memberID))
			return this.membersMap.get(memberID);
		else
			return false;
	}
	public void addMember(int memberID){
		this.membersMap.put(Integer.valueOf(memberID), false);
	}
	public void removeMember(int memberID){
		if(membersMap.containsKey(memberID))
			this.membersMap.remove(memberID);
	}
	public boolean allOffline(){
		Iterator<Map.Entry<Integer, Boolean> >
		iterator = membersMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Boolean>
			entry= iterator.next();
			if (entry.getValue())
				return false;
		}
		return true;
	}
}
