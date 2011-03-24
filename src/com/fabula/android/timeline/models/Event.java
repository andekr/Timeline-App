package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.fabula.android.timeline.providers.EventProvider;
import com.google.android.maps.GeoPoint;

public class Event {
	
	private String id;
	private String experienceid;
	
	private transient Location location;
	private transient Date datetime;
	
	private double longitude;
	private double latitude;
	
	private long datetimemillis;
	private List<EventItem> eventItems;
	private List<Emotion> emotionList;
	private boolean shared;
	
	public Event(){}
	
	public Event(String exID, Location location) {
		super();
		id =  UUID.randomUUID().toString();
		this.experienceid = exID;
		Log.i("EVENT", "id satt til: "+id);
		eventItems = new ArrayList<EventItem>();
		emotionList = new ArrayList<Emotion>();
		setDatetime(new Date());
		setLocation(location);
	}
	
	public Event(String id, String exID, Date dateTime, Location location) {
		super();
		this.id = id;
		this.experienceid = exID;
		eventItems = new ArrayList<EventItem>();
		emotionList = new ArrayList<Emotion>();
		setDatetime(dateTime);
		setLocation(location);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	public String getExperienceid() {
		return experienceid;
	}

	public void setExperienceid(String experienceid) {
		this.experienceid = experienceid;
	}

	public Date getDatetime() {
		return datetime;
	}
	
	public long getDatetimemillis(){
		return datetimemillis;
	}
	
	public void setDatetime(Date datetime) {
		datetimemillis = datetime.getTime();
		this.datetime = datetime;
	}
	public Location getLocation() {
		return location;
	}
	
	public GeoPoint getGeoPointLocation() {

		return new GeoPoint((int)(getLatitude() * 1E6) , (int) (getLongitude() * 1E6));
	}

	public void setLocation(Location location) {
		this.longitude = location.getLongitude();
		this.latitude = location.getLatitude();
		this.location = location;
	}
	
	public Double getLatitude(){
		return latitude;
	}
	
	public void setLatitude(Double latitude){
	}
	
	public Double getLongitude(){
		return longitude;
	}
	
	public void setLongitude(Double longitude){
	}
	
	public void addEventItem(EventItem evItem){
		eventItems.add(evItem);
	}
	
	public List<EventItem> getEventItems() {
		return eventItems;
	}
	public void setEventItems(List<EventItem> eventItems) {
		this.eventItems = eventItems;
	}
	
	public void addEmotion(Emotion emotion){
		emotionList.add(emotion);
	}
	
	public List<Emotion> getEmotionList() {
		if(emotionList==null)
			emotionList = new ArrayList<Emotion>();
		return emotionList;
	}

	public void setEmotionList(List<Emotion> emotionList) {
		this.emotionList = emotionList;
	}
	
	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	public int isSharedAsInt() {
		return isShared() ? 1 : 0;
	}



	public static final class EventColumns implements BaseColumns {
		
		private EventColumns(){}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" +EventProvider.AUTHORITY+ "/events");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.events";
		
		public static final String EVENT_ID = "_id";
		
		public static final String EVENT_EXPERIENCEID = "experienceid";
		
		public static final String EVENT_LOCATION_LAT = "latitude";
		public static final String EVENT_LOCATION_LNG = "longitude";
		
		public static final String EVENT_TITLE = "event_title";
		
		public static final String EVENT_ITEMS_ID = "event_items_id";

		public static final String IS_SHARED = "event_is_shared";
	}
	
}
