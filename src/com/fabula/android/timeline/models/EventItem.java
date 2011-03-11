package com.fabula.android.timeline.models;

import java.util.UUID;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.providers.EventItemProvider;

public abstract class EventItem {
	
	private String id;
	private transient Account user;
	private String creator;
	
	//Workaroud for GSON to serialize these fields in subclasses
	String className;
	String pictureFilename;
	String noteTitle;
	String noteText;
	
	public EventItem(Context c) {
		super();
		this.id = UUID.randomUUID().toString();
		setUser(Utilities.getUserAccount(c));
		Log.i("Event item created by: ", user.name);
	}

	public EventItem(String id, Account u) {
		super(); 
		this.id = id;
		setUser(u);
	}

	public String getId() {
		return id;
	}
	
		
	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		creator = user.name;
		this.user = user;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String username) {
		//
	}

	public void setId(String id) {
		this.id = id;
	}

	public abstract View getView(Context context);
	
	public abstract Intent getIntent();
	
	public abstract Uri getUri();
	
	
	public static final class EventItemsColumns implements BaseColumns {
		private EventItemsColumns(){	
		}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" +EventItemProvider.AUTHORITY+ "/eventItems");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.eventitems";
				
		public static final String EVENT_ID = EventColumns._ID;
		
		public static final String EVENT_ITEM_ID = "eventItemID";
		
		public static final String EVENT_ITEM_TYPE = "item_type";
		
		public static final String CREATED_DATE = "created";
		
		public static final String USERNAME = "username";
	}
	
	public enum EventItemTypes {
		SimpleNote(1), SimplePicture(2), SimpleVideo(3), SimpleRecording(4), SimpleUrl(5);
		
		private final int numberOfType;
		
		private EventItemTypes(int number) {
			this.numberOfType = number;
		}
		
		public int numberOfType() { return numberOfType; }

		public static int getItemType(EventItem item) {
			
			 if (item instanceof SimpleNote) {
				return SimpleNote.numberOfType;
			 }
			 else if (item instanceof SimplePicture) {
				 return SimplePicture.numberOfType;
			 }
			 else if (item instanceof SimpleVideo) {
				 return SimpleVideo.numberOfType;
			 }
			 else if (item instanceof SimpleRecording){
				 return SimpleRecording.numberOfType;
			 }
			return 0;
		}
	}

}
