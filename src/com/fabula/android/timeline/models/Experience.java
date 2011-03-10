package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.accounts.Account;
import android.net.Uri;
import android.provider.BaseColumns;

import com.fabula.android.timeline.providers.ExperienceProvider;

/**
 * 
 * @author andekr
 *
 */
@Root
public class Experience {

	@Attribute
	private String id;
	@Element
	private String title;
	@Element
	private boolean shared;
	private Account creator;
	@ElementList
	private ArrayList<Event> Events;
	private Uri uriToExperience;
	

	public Experience(String title, boolean shared, Account creator) {
		this.id = UUID.randomUUID().toString();
		this.title = title;
		this.shared = shared;
		this.creator = creator;
	}
	
	public Experience(String id, String title, boolean shared, Account creator) {
		this.id = id;
		this.title = title;
		this.shared = shared;
		this.creator = creator;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public ArrayList<Event> getEvents() {
		return Events;
	}
	public void setEvents(ArrayList<Event> events) {
		this.Events = events;
	}
	public void addEvent(Event event){
		this.Events.add(event);
	}
	
	public void removeEvent(Event event){
		this.Events.remove(event);
	}
	
	public void setUriToExperience(Uri uriToExperience) {
		this.uriToExperience = uriToExperience;
	}
	public Uri getUriToExperience() {
		return uriToExperience;
	}
	
	public Account getCreator() {
		return creator;
	}

	public void setCreator(Account creator) {
		this.creator = creator;
	}
	
	@Attribute
	public String getUsername() {
		return creator.name;
	}
	
	@Attribute
	public void setUsername(String username) {
		//
	}

	
	public float getTimeScopeOfExperience(){
		float min=0, max=0, diff;//Min blir aldri satt!
		
		for (Event event : Events) {
			if(min==0)
				min = event.getDatetimemillis();
			
			if(event.getDatetimemillis()>max)
				max = event.getDatetimemillis();
			
			if(event.getDatetimemillis()<=min)
				min = event.getDatetimemillis();
		}
		
		diff = max-min;
		return diff;
	}
	
	public Date[] getMinAndMaxDate(){
		Date[] dates = new Date[2];
		Date min = null, max = null;
		
		
		for (Event event : Events) {
			if(min==null)
				min = event.getDatetime();
			if(max==null)
				max = event.getDatetime();
			
			if(event.getDatetime().compareTo(max)>0)
				max = event.getDatetime();
			
			if(event.getDatetime().compareTo(min)<=0)
				min = event.getDatetime();
		}
		
	     dates[0] = min;
	     dates[1] = max;
	     
	     return dates;
	}
	
	public static final class ExperienceColumns implements BaseColumns {
		
		private ExperienceColumns(){}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" +ExperienceProvider.AUTHORITY+ "/experiences");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.experiences";
		
		public static final String experience_ID = "_id";
		
		public static final String EXPERIENCE_NAME = "name";
		
		public static final String EXPERIENCE_SHARED = "SHARED";
		
		public static final String EXPERIENCE_CREATOR = "creator";
		
	}

	public int isSharedAsInt() {
		return isShared() ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
