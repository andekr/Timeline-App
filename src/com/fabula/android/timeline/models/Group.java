package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.UUID;

import com.fabula.android.timeline.providers.GroupProvider;
import com.fabula.android.timeline.providers.UserProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Group {
	
	private String id;
	private String name;
	private ArrayList<User> members;
	
	public Group(String name, ArrayList<User> members) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.members = members;
	}
	
	public Group(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
	}
	
	public Group(String id, String name) {
		this.id = id;
		this.name = name;
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

	public ArrayList<User> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<User> members) {
		this.members = members;
	}
	
	public void addMembers(User user) {
		members.add(user);
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
