package com.fabula.android.timeline.models;

import java.util.Date;
import java.util.UUID;

import com.google.android.maps.GeoPoint;

import android.accounts.Account;
import android.location.Location;

public class BaseEvent {
	
	private String id;
	private String experienceid;
	private transient Location location;
	private transient Date datetime;
	private double longitude;
	private double latitude;
	private long datetimemillis;
	private boolean shared;
	private Account user;
	private String creator;
	
	public BaseEvent() {	
	}
	
	public BaseEvent(String exID, Location location, Account user) {
		super();
		id =  UUID.randomUUID().toString();
		this.experienceid = exID;
		
		setDatetime(new Date());
		setLocation(location);
		setUser(user);
	}
	
	public BaseEvent(String id, String exID, Date dateTime, Location location, Account user) {
		super();
		this.id = id;
		this.experienceid = exID;
		setDatetime(dateTime);
		setLocation(location);
		setUser(user);
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.longitude = location.getLongitude();
		this.latitude = location.getLatitude();
		this.location = location;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		datetimemillis = datetime.getTime();
		this.datetime = datetime;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public long getDatetimemillis() {
		return datetimemillis;
	}

	private void setUser(Account user) {
		this.creator = user.name;
		this.user = user;
		
	}
	
	public Account getUser() {
		if(user==null)
			this.user = new Account(creator, "com.google");
		return user;
	}
	
	public GeoPoint getGeoPointLocation() {
		return new GeoPoint((int)(getLatitude() * 1E6) , (int) (getLongitude() * 1E6));
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

}
