package server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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