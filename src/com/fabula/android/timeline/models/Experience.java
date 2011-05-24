package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.accounts.Account;
import android.net.Uri;
import android.provider.BaseColumns;

import com.fabula.android.timeline.database.providers.ExperienceProvider;
import com.fabula.android.timeline.database.providers.TagProvider;

/**
 * 
 * A central model as it is the container of all events(and items)
 * 
 * @author andekr
 *
 */

public class Experience {


	private String id;
	private String title;
	private boolean shared;
	private transient Account user;
	private String creator;
	private List<BaseEvent> events;
	private Uri uriToExperience;
	private transient Group sharingGroupObject;
	private String sharingGroup;
	
	public Experience() {}

	public Experience(String title, boolean shared, Account user) {
		this.id = UUID.randomUUID().toString();
		this.title = title;
		this.shared = shared;
		events = new ArrayList<BaseEvent>();
		setUser(user);
	}
	
	public Experience(String title, boolean shared, Account user, Group group) {
		this.id = UUID.randomUUID().toString();
		this.title = title;
		this.shared = shared;
		this.setSharingGroupObject(group);
		events = new ArrayList<BaseEvent>();
		setUser(user);
	}
	
	public Experience(String id, String title, boolean shared, Account user) {
		this.id = id;
		this.title = title;
		this.shared = shared;
		events = new ArrayList<BaseEvent>();
		setUser(user);
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
	public List<BaseEvent> getEvents() {
		return events;
	}
	public void setEvents(List<BaseEvent> events) {
		this.events = events;
	}
	public void addEvent(BaseEvent event){
		this.events.add(event);
	}
	
	public void removeEvent(BaseEvent event){
		this.events.remove(event);
	}
	
	public void setUriToExperience(Uri uriToExperience) {
		this.uriToExperience = uriToExperience;
	}
	public Uri getUriToExperience() {
		return uriToExperience;
	}
	
	public BaseEvent getEvent(String eventId) {
		
		for (BaseEvent event : events) {
			if(event.getId().equals(eventId)) 
				return event;
		}
		return null;
	}
	
	public Account getUser() {
		if(user==null)
			this.user = new Account(creator, "com.google");
		
		return user;
	}

	public void setUser(Account user) {
		this.creator = user.name;
		this.user = user;
	}
	
		
	@SuppressWarnings("unused")
	private String getCreator() {
		return creator;
	}

	@SuppressWarnings("unused")
	private void setCreator(String creator) {
		this.user = new Account(creator, "com.google");
		this.creator = creator;
	}
		

	public Group getSharingGroupObject() {
		return sharingGroupObject;
	}

	public void setSharingGroupObject(Group sharingGroupObject) {
		this.sharingGroupObject = sharingGroupObject;
		this.sharingGroup = sharingGroupObject.getId();
	}


	public String getSharingGroup() {
		return sharingGroup;
	}


	/**
	 * Method that gives the time span of the experience. Can be useful to set the
	 * granularity of the view.
	 * 
	 */
	public float getTimeScopeOfExperience(){
		float min=0, max=0, diff;
		
		for (BaseEvent event : events) {
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
		
		
		for (BaseEvent event : events) {
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
		
		public static final String EXPERIENCE_LAST_MODIFIED = "last_modified";
		
		public static final String EXPERIENCE_SHARED_WITH = "shared_with_group";

	}
	
	public static final class TagColumns implements BaseColumns {
		private TagColumns() {}
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.tags";
		public static final Uri CONTENT_URI = Uri.parse("content://" + TagProvider.AUTHORITY+ "/tags");
		public static final String TAG_NAME = "tag_name";
		public static final String TAG_ID = "tag_id";
	}
	
	public static final class TaggedEventsColumns implements BaseColumns {
		private TaggedEventsColumns() {}
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.taggedevents";
		public static final Uri CONTENT_URI = Uri.parse("content://" + TagProvider.AUTHORITY+ "/tagged_events");
		public static final String TAG_ID = "tag_id";
		public static final String _ID = "_id";
	}

	public int isSharedAsInt() {
		return isShared() ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return this.title;
	}


}
