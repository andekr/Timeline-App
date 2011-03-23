package com.fabula.android.timeline.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;



/**
 * Wrapper classs for XML-serialization
 * 
 * 
 * @author andekr
 *
 */
public class Groups {

	/**
	 * No-args for Gson  
	 */
	public Groups() {}
	
	@SerializedName("groups")
	private List<Group> groups;
	
	
	public Groups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getGroups() {
		return groups;
	}

	
	
	
}
