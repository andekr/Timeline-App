package com.fabula.android.timeline.contentmanagers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.Experience.TagColumns;
import com.fabula.android.timeline.models.Experience.TaggedEventsColumns;

public class TagManager {

	private Context context;
	
	public TagManager(Context context) {
		this.context = context;
	}
	
	public void addTagToDatabase(String name) {
		System.out.println("Adder "+name+" til database");
		 ContentValues values = new ContentValues();
		 values.put(TagColumns.TAG_NAME, name);
		 
		 if(getTagID(name) != 0) {
			 context.getContentResolver().insert(TagColumns.CONTENT_URI, values);
		 }
	}
	
	public void addTagToEventInDatabase(String tagName, BaseEvent event) {
		ContentValues values = new ContentValues();
		
		values.put(TagColumns.TAG_ID, getTagID(tagName));
		values.put(EventColumns._ID, event.getId());
		
		context.getContentResolver().insert(TaggedEventsColumns.CONTENT_URI, values);
	}
	
	public int getTagID(String tagName) {
		
		String[] tagColumnProjection = new String[]{TagColumns.TAG_ID };
		
		String where = TagColumns.TAG_NAME + " = '"+ tagName + "'"; 
		int id = 0;		
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnProjection, where, null, null);
		
			if(c.moveToFirst()) {
			id = c.getInt(c.getColumnIndex(TagColumns.TAG_ID));
			}
			
		return id;
		}
	
	public String getTag(int tagID) {
		String[] tagColumnProjection = new String[]{TagColumns.TAG_NAME };
		
		String where = TagColumns.TAG_ID + " = '" +tagID+"'";
		String tag = "";
		
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnProjection, where, null, null);
		
		if(c.moveToNext()) {
			tag = c.getString(c.getColumnIndex(TagColumns.TAG_NAME));
		}
		return tag;
	}
	
	public ArrayList<String> getAllTags() {
		
		String[] tagColumnsProjection = new String[]{TagColumns.TAG_NAME};
		ArrayList<String> allTags = new ArrayList<String>();
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnsProjection, null, null, null);
		
		if(c.moveToFirst()) {
			do {
				allTags.add(c.getString(c.getColumnIndex(TagColumns.TAG_NAME)));
			} while (c.moveToNext());
		}
		return allTags;
	}
	
	public ArrayList<String> getAllEventsConnectedToTag(String tagName) {
		ArrayList<String> allEventID = new ArrayList<String>();
		String[] tagColumnsProjection = new String[]{EventColumns._ID};
		
		String where = TagColumns.TAG_ID +" = '"+ getTagID(tagName)+"'"; 
		
		Cursor c = context.getContentResolver().query(TaggedEventsColumns.CONTENT_URI, tagColumnsProjection, where, null, null);
		
		if(c.moveToFirst()) {
			do {
				allEventID.add(c.getString(c.getColumnIndex(EventColumns._ID)));
			} while (c.moveToNext());
		}
		
		return allEventID;
	}
	
	public ArrayList<String> getAllTagsConnectedToEvent(String eventID) {
		ArrayList<String> allTags = new ArrayList<String>();
		String[] tagColumnsProjection = new String[]{TagColumns.TAG_ID};
		
		String where = EventColumns.EVENT_ID + " = '"+ eventID+"'";
		
		Cursor c = context.getContentResolver().query(TaggedEventsColumns.CONTENT_URI, tagColumnsProjection, where, null, null);
		
		if(c.moveToFirst()) {
			do {
				allTags.add(getTag(c.getInt(c.getColumnIndex(TagColumns.TAG_ID))));
			} while (c.moveToNext());
		}
		return allTags;
	}
	
	public void DeleteTag(String tagName) {
		
		String where = TagColumns.TAG_NAME + " = '" +tagName +"'";
		
		context.getContentResolver().delete(TagColumns.CONTENT_URI, where, null);
		
		deleteEventTags(tagName);
	}

	private void deleteEventTags(String tagName) {

		String where = TaggedEventsColumns.TAG_ID +" = '"+getTagID(tagName)+"'";
		
		context.getContentResolver().delete(TaggedEventsColumns.CONTENT_URI, where, null);
		
	}
}
