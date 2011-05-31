/*******************************************************************************
 * Copyright (c) 2011 Andreas Storlien and Anders Kristiansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Storlien and Anders Kristiansen - initial API and implementation
 ******************************************************************************/
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
