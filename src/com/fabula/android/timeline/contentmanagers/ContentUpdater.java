package com.fabula.android.timeline.contentmanagers;

import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * Helper method for updating model objects in database.
 * Only Notes supports updating.
 * 
 * @author andekr
 *
 */
public class ContentUpdater {
	
	private Context context;

	public ContentUpdater(Context context) {
		this.context = context;
	}
	
	public void updateNoteInDB(SimpleNote updatedNote){
		  ContentValues values = new ContentValues();
		  values.put(NoteColumns.TITLE, updatedNote.getNoteTitle());
		  values.put(NoteColumns.NOTE, updatedNote.getNoteText()); 
		  values.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
		  
		  context.getContentResolver().update(NoteColumns.CONTENT_URI, values, NoteColumns._ID+"=+'"+updatedNote.getId()+"'", null);
		
		  Log.i("CONTENT UPDATE NOTE", "Updated Note in DB: ID: " +updatedNote.getId()+ " Title: "+ updatedNote.getNoteTitle() );
	}
	
	public void setEventShared(Event event) {
		
		ContentValues values = new ContentValues();
		values.put(EventColumns.IS_SHARED, event.isSharedAsInt());
		String where = EventColumns._ID+ " = '" +event.getId()+"'"; 
		context.getContentResolver().update(EventColumns.CONTENT_URI, values, where, null);
		
	}

}
