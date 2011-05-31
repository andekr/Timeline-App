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
