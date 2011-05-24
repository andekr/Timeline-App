package com.fabula.android.timeline.database.contentmanagers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.database.UserGroupDatabaseHelper;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.Experience.TagColumns;
import com.fabula.android.timeline.models.Experience.TaggedEventsColumns;
import com.fabula.android.timeline.utilities.Constants;

public class TagManager {

	private Context context;
	
	public TagManager(Context context) {
		this.context = context;
	}
	
	/**
	 * Adds a new tag to the database if it doesn't exists from before
	 * @param name. The name of the tag to be added
	 */
	public void addTagToDatabase(String name) {
		System.out.println("Adder "+name+" til database");
		 ContentValues values = new ContentValues();
		 values.put(TagColumns.TAG_NAME, name);
		 

		 if(!tagAlreadyExists(name)) {
			 context.getContentResolver().insert(TagColumns.CONTENT_URI, values);
		 }
	}
	
	private boolean tagAlreadyExists(String name) {
		String[] tagColumnProjection = new String[]{TagColumns.TAG_NAME };
		String where = TagColumns.TAG_NAME+ " = '"+ name+ "'";
		
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnProjection, where, null, null);
		 
		return c.getCount() != 0;
	}

	/**
	 * Adds a tag to an event and saves it in the database
	 * @param tagName name of the tag
	 * @param event the event to tag
	 */
	public void addTagToEventInDatabase(String tagName, BaseEvent event) {
		ContentValues values = new ContentValues();
		
		values.put(TagColumns.TAG_ID, getTagID(tagName));
		values.put(EventColumns._ID, event.getId());
		
		context.getContentResolver().insert(TaggedEventsColumns.CONTENT_URI, values);
	}
	
	/**
	 * Returns the ID of a tag
	 * @param tagName The name of the tag
	 * @return the tag id
	 */
	public int getTagID(String tagName) {
		
		String[] tagColumnProjection = new String[]{TagColumns.TAG_ID };
		
		String where = TagColumns.TAG_NAME + " = '"+ tagName + "'"; 
		int id = 0;		
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnProjection, where, null, null);
		
			if(c.moveToFirst()) {
			id = c.getInt(c.getColumnIndex(TagColumns.TAG_ID));
			}
		
			c.close();
		return id;
		}
	
	/**
	 * Return a tag based on a tag id
	 * @param tagID id of the tag
	 * @return the tag connected to the id
	 */
	public String getTag(int tagID) {
		String[] tagColumnProjection = new String[]{TagColumns.TAG_NAME };
		
		String where = TagColumns.TAG_ID + " = '" +tagID+"'";
		String tag = "";
		
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnProjection, where, null, null);
		
		if(c.moveToNext()) {
			tag = c.getString(c.getColumnIndex(TagColumns.TAG_NAME));
		}
		c.close();
		return tag;
	}
	
	/**
	 * Gets all the tags saved in the database
	 * @return a list of all the tags in the database
	 */
	public ArrayList<String> getAllTags() {

		
		String[] tagColumnsProjection = new String[]{TagColumns.TAG_NAME};
		ArrayList<String> allTags = new ArrayList<String>();
		Cursor c = context.getContentResolver().query(TagColumns.CONTENT_URI, tagColumnsProjection, null, null, null);
		
		if(c.moveToFirst()) {
			do {
				allTags.add(c.getString(c.getColumnIndex(TagColumns.TAG_NAME)));
			} while (c.moveToNext());
		}
		c.close();
		return allTags;
	}
	
	/**
	 * Gets all events associated with a tag
	 * @param tagName The tag
	 * @return A list of all the event id's connected to the tag
	 */
	public ArrayList<String> getAllEventIDsConnectedToTag(String tagName) {
		ArrayList<String> allEventID = new ArrayList<String>();
		String[] tagColumnsProjection = new String[]{EventColumns._ID};
		
		String where = TagColumns.TAG_ID +" = '"+ getTagID(tagName)+"'"; 
		
		Cursor c = context.getContentResolver().query(TaggedEventsColumns.CONTENT_URI, tagColumnsProjection, where, null, null);
		
		if(c.moveToFirst()) {
			do {
				allEventID.add(c.getString(c.getColumnIndex(EventColumns._ID)));
			} while (c.moveToNext());
		}
		c.close();
		return allEventID;
	}
	
	public ArrayList<BaseEvent> getAllEventsConnectedToTag(List<String> tagNames) {
		
		ArrayList<BaseEvent> allEventsConnectedToTags = new ArrayList<BaseEvent>();
		
		for (String tagName : tagNames) {
			for (BaseEvent baseEvent : getAllEventsConnectedToTag(tagName)) {
				allEventsConnectedToTags.add(baseEvent);
			}
		}
		return allEventsConnectedToTags;
	}
	
	public ArrayList<BaseEvent> getAllEventsConnectedToTag(String tagName) {
		
		ArrayList<BaseEvent> allEventsConnectedToTag = new ArrayList<BaseEvent>();
		ContentLoader contentLoader = new ContentLoader(context);
		
		new UserGroupDatabaseHelper(context, Constants.USER_GROUP_DATABASE_NAME);
		new TimelineDatabaseHelper(context, Constants.ALL_TIMELINES_DATABASE_NAME);
		ArrayList<Experience> allExperiencesInDatabase = contentLoader.LoadAllExperiencesFromDatabase();
		TimelineDatabaseHelper.getCurrentTimeLineDatabase().close();
		
		for (Experience experience : allExperiencesInDatabase) {
			new DatabaseHelper(context, experience.getTitle());
			experience.setEvents(contentLoader.LoadAllEventsFromDatabase());
			
			for (BaseEvent baseEvent : experience.getEvents()) {
				if(baseEvent.hasTag(tagName)) {
					allEventsConnectedToTag.add(baseEvent);
				}
			}
		DatabaseHelper.getCurrentTimelineDatabase().close();
		}
		UserGroupDatabaseHelper.getUserDatabase().close();
		
		return allEventsConnectedToTag;
	}
	
	/**
	 * Get all tags conntected to a given event
	 * @param eventID the eventID of the event to be checked
	 * @return a list of all the tags connected to the event
	 */
	public ArrayList<String> getAllTagsConnectedToEvent(String eventID) {
		new TimelineDatabaseHelper(context, Constants.ALL_TIMELINES_DATABASE_NAME);
		ArrayList<String> allTags = new ArrayList<String>();
		String[] tagColumnsProjection = new String[]{TagColumns.TAG_ID};
		
		String where = EventColumns._ID + " = '"+ eventID+"'";
		
		Cursor c = context.getContentResolver().query(TaggedEventsColumns.CONTENT_URI, tagColumnsProjection, where, null, null);
		
		if(c.moveToFirst()) {
			do {
				allTags.add(getTag(c.getInt(c.getColumnIndex(TagColumns.TAG_ID))));
			} while (c.moveToNext());
		}
		c.close();
		return allTags;
	}
	
	/**
	 * Deletes a tag from the database
	 * @param tagName the name of the tag to be deleted
	 */
	public void DeleteTag(String tagName) {
		
		String where = TagColumns.TAG_NAME + " = '" +tagName +"'";
		
		context.getContentResolver().delete(TagColumns.CONTENT_URI, where, null);
		
		deleteEventTags(tagName);
	}

	/**
	 * Delete event to tag connection from the database based on the tagName
	 * @param tagName the name of the tag to delete
	 */
	private void deleteEventTags(String tagName) {

		String where = TaggedEventsColumns.TAG_ID +" = '"+getTagID(tagName)+"'";
		
		context.getContentResolver().delete(TaggedEventsColumns.CONTENT_URI, where, null);
		
	}
}
