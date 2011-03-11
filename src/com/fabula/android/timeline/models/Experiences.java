package com.fabula.android.timeline.models;

import java.util.ArrayList;


/**
 * Wrapper classs for XML-serialization
 * 
 * 
 * @author andekr
 *
 */
public class Experiences {

	private ArrayList<Experience> experiences;
	
	public Experiences(ArrayList<Experience> experiences) {
		this.experiences = experiences;
	}

	public ArrayList<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(ArrayList<Experience> experiences) {
		this.experiences = experiences;
	}
	
	
	
}
