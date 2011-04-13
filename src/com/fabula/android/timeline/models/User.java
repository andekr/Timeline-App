package com.fabula.android.timeline.models;

import java.util.UUID;

import com.fabula.android.timeline.database.providers.UserProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class User {
	
	private transient String id;
	private String username;
	
	public User() {}
	
	public User(String userName) {
		this.id = UUID.randomUUID().toString();
		this.username = userName;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}
	
	public static final class UserColumns implements BaseColumns {
		
		public static final String USER_ID = "_id";
		public static final String USER_NAME = "user_name";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + UserProvider.AUTHORITY + "/users");
	}
	
	@Override
	public String toString() {
		return this.username;
	}
}
