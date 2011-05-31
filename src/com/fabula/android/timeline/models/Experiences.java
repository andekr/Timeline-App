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

import java.util.ArrayList;
import java.util.List;



/**
 * Wrapper class for JSON-serialization
 * 
 * 
 * @author andekr
 *
 */
public class Experiences {

	/**
	 * No-args for Gson  
	 */
	public Experiences() {}

	private List<Experience> experiences;
	
	
	public Experiences(List<Experience> experiences) {
		this.experiences = experiences;
	}

	public List<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(ArrayList<Experience> experiences) {
		this.experiences = experiences;
	}
	
	
	
}
