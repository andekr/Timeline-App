package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.net.Uri;
import android.provider.BaseColumns;

import com.fabula.android.timeline.providers.GroupProvider;

public class Group {
	
	private String id;
	private String name;
	private List<User> members;
	
	public Group(){}
	
	public Group(String name, ArrayList<User> members) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.members = members;
	}
	
	public Group(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		members = new ArrayList<User>();
	}
	
	public Group(String id, String name) {
		this.id = id;
		this.name = name;
		members = new ArrayList<User>();
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<User> members) {
		this.members = members;
	}
	
	public void addMembers(User user) {
		
		if(!userAlreadyExistsInMembers(user)) {
			members.add(user);
		}
		
	}
	
	private boolean userAlreadyExistsInMembers(User user) {
		for (User u : members) {
			if(u.getUserName().equals(user.getUserName())) {
				return true;
			}
		}
		return false;
	}

	public void removeMember(User user) {
		members.remove(user);
	}
	
	public User getMember(int index) {
		return members.get(index);
	}
		
	@Override
	public String toString() {
		return this.name;
	}
	
	public static final class GroupColumns implements BaseColumns {
		
		private GroupColumns() {}
		
		public static final String GROUP_NAME = "group_name";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + GroupProvider.AUTHORITY + "/groups");
	}
	
	

}
