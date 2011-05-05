package com.fabula.android.timeline.models;

import java.util.List;

import com.google.myjson.annotations.SerializedName;



/**
 * Wrapper classs for XML-serialization
 * 
 * 
 * @author andekr
 *
 */
public class Users {

	/**
	 * No-args for Gson  
	 */
	public Users() {}
	
	@SerializedName("users")
	private List<User> users;
	
	
	public Users(List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

	
	
	
}
