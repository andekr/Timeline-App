package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.fabula.android.timeline.providers.EventProvider;
import com.google.android.maps.GeoPoint;

@Root
public class Event {
	
	@Attribute
	private String id;
	@Element
	private String experienceid;
	
	private Location location;
	
	private Date datetime;
	@Element
	private long datetimemillis;
	@ElementList
	private ArrayList<EventItem> eventItems;
	@ElementList
	private ArrayList<Emotion> emotionList;
	
	public Event(String exID, Location location) {
		super();
		id =  UUID.randomUUID().toString();
		this.experienceid = exID;
		Log.i("EVENT", "id satt til: "+id);
		eventItems = new ArrayList<EventItem>();
		emotionList = new ArrayList<Emotion>();
		setDatetime(new Date());
		this.location = location;
	}
	
	public Event(String id, String exID, Date dateTime, Location location) {
		super();
		this.id = id;
		this.experienceid = exID;
		eventItems = new ArrayList<EventItem>();
		emotionList = new ArrayList<Emotion>();
		setDatetime(dateTime);
		this.location = location;
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
		this.location = location;
	}
	
	@Element
	public Double getLatitude(){
		return getLocation().getLatitude();
	}
	
	@Element 
	public void setLatitude(Double latitude){
	}
	
	@Element
	public Double getLongitude(){
		return getLocation().getLongitude();
	}
	
	@Element 
	public void setLongitude(Double longitude){
	}
	
	public void addEventItem(EventItem evItem){
		eventItems.add(evItem);
	}
	
	public ArrayList<EventItem> getEventItems() {
		return eventItems;
	}
	public void setEventItems(ArrayList<EventItem> eventItems) {
		this.eventItems = eventItems;
	}
	
	public void addEmotion(Emotion emotion){
		emotionList.add(emotion);
	}
	
	public ArrayList<Emotion> getEmotionList() {
		return emotionList;
	}

	public void setEmotionList(ArrayList<Emotion> emotionList) {
		this.emotionList = emotionList;
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
	}
	
}
