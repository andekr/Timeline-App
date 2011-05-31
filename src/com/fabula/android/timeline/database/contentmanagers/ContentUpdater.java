/*******************************************************************************
 * Copyright (c) 2011 Andreas Storlien and Anders Kristiansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Storlien and Anders Kristiansen - initial API and implementation
 ******************************************************************************/
package com.fabula.android.timeline.database.contentmanagers;

import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.Experience.ExperienceColumns;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;

import android.content.ContentValues;
import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.util.Log;

/**
 * Helper method for updating model objects in database.
 * Only {@link Note} and {@link Experience} supports updating.
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
	
	public void updateExperience(Experience experience) {
		
		ContentValues values = new ContentValues();
		 values.put(ExperienceColumns.EXPERIENCE_SHARED, experience.isSharedAsInt());
		 if (experience.isShared()) {
			 values.put(ExperienceColumns.EXPERIENCE_SHARED_WITH, experience.getSharingGroup());
		 }
		String where = ExperienceColumns._ID+ " = '" +experience.getId()+"'"; 
		System.out.println("Set experience: "+where);
		context.getContentResolver().update(ExperienceColumns.CONTENT_URI, values, where, null);
		
	}
	
	
	

}
