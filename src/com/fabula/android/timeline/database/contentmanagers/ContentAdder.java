package com.fabula.android.timeline.database.contentmanagers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Emotion;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Emotion.EmotionColumns;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.Experience.ExperienceColumns;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimplePicture.PictureColumns;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleRecording.RecordingColumns;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.SimpleVideo.VideoColumns;

/**
 * Helper class for adding different models to their respective ContentProviders (Saving them using SQLite)
 */
public class ContentAdder {
	
	private Context context;

	public ContentAdder(Context context) {
		this.context = context;
	}
	
	public void addEventToEventContentProvider(BaseEvent event) {
		
		 ContentValues values = new ContentValues();
		 
		 values.put(EventColumns._ID, event.getId());
		 values.put(EventColumns.EVENT_EXPERIENCEID, event.getExperienceid());
		 values.put(EventColumns.EVENT_TITLE, event.getDatetimemillis());
		 values.put(EventColumns.IS_SHARED, event.isSharedAsInt());
		 values.put(EventColumns.CREATOR, event.getUser().name);
		 
		 if(event instanceof MoodEvent)  {
			 MoodEvent mood = (MoodEvent) event;
			 values.put(EventColumns.MOOD,  mood.getMood().getMoodInt());
		 }
		 else {
			 values.put(EventColumns.MOOD, 1000); //UGLY HACK FOR SEPERATING BETWEEN EVENT AND MOODEVENT
		 }
		 	
		 try {
			 values.put(EventColumns.EVENT_LOCATION_LAT, event.getLatitude());
			 values.put(EventColumns.EVENT_LOCATION_LNG, event.getLongitude());
		} catch (Exception e) {
			Log.e("addEventToEventContentProvider", "No known location");
		}
		 
		 
		 context.getContentResolver().insert(EventColumns.CONTENT_URI, values);
		 
		 if(event instanceof Event) {
			 addEventItemsToDB((Event)event); 
		 }
		 
//		 Log.d("CONTENT ADDER", "Added event to DB: " + event.getId());
	}
	
	public void addEventItemToEventContentProviderIfEventAlreadyExists(Event selectedEvent, EventItem item) {
		
		connectEventWithEventItemInDatabase(selectedEvent, item);
		
		if(item instanceof SimpleNote) {
			SimpleNote note = (SimpleNote) item;
			addNoteToNoteContentProvider(selectedEvent, note);
		}
		else if(item instanceof SimplePicture) {
			SimplePicture picture = (SimplePicture) item;
			addPictureToImageContentProvider(selectedEvent, picture);
		}
		else if(item instanceof SimpleRecording) {
			SimpleRecording recording = (SimpleRecording) item;
			addRecordingToRecordingContentProvider(selectedEvent, recording);
		}
		else if(item instanceof SimpleVideo) {
			SimpleVideo video = (SimpleVideo) item;
			addVideoToVideoContentProvider(selectedEvent, video);
		}
		
//		Log.d("CONTENT ADDER", "Added eventitem to eventItemContentProvider: " + item.getId());
	}
	
	public void addExperienceToTimelineContentProvider(Experience experience){
	 ContentValues values = new ContentValues();
		 
		 values.put(ExperienceColumns._ID, experience.getId());
		 values.put(ExperienceColumns.EXPERIENCE_NAME, experience.getTitle());
		 values.put(ExperienceColumns.EXPERIENCE_SHARED, experience.isSharedAsInt());
		 values.put(ExperienceColumns.EXPERIENCE_CREATOR, experience.getUser().name);
		
		 
		 if (experience.isShared()) {
			 values.put(ExperienceColumns.EXPERIENCE_SHARED_WITH, experience.getSharingGroupObject().getId());
		 }
		 
		 context.getContentResolver().insert(ExperienceColumns.CONTENT_URI, values);
		 
		 if(experience.getEvents()!=null){
			 for (BaseEvent e : experience.getEvents()) {
				DatabaseHelper eventDatabaseHelper = new DatabaseHelper(context, experience.getTitle()+".db");
				DatabaseHelper.getCurrentTimelineDatabase().beginTransaction();
				addEventToEventContentProvider(e);
				DatabaseHelper.getCurrentTimelineDatabase().setTransactionSuccessful();
				DatabaseHelper.getCurrentTimelineDatabase().endTransaction();
				eventDatabaseHelper.close();
			}
		 }
		 
//		 Log.d("CONTENT ADDER", "Added experience to DB: " + experience.getId());
	}
	
	private void addEventItemsToDB(Event event) {
		
		if(event.getEventItems() != null) {
			
		for (EventItem item : event.getEventItems()) { 
			connectEventWithEventItemInDatabase(event, item);
			
			if(item instanceof SimpleNote) {
				SimpleNote note = (SimpleNote) item;
				addNoteToNoteContentProvider(event, note);
			}
			else if(item instanceof SimplePicture) {
				SimplePicture picture = (SimplePicture) item;
				addPictureToImageContentProvider(event, picture);
			}
			else if(item instanceof SimpleRecording) {
				SimpleRecording recording = (SimpleRecording) item;
				addRecordingToRecordingContentProvider(event, recording);
			}
			else if(item instanceof SimpleVideo) {
				SimpleVideo video = (SimpleVideo) item;
				addVideoToVideoContentProvider(event, video);
			}
		}
		}
		if(event.getEmotionList()!=null){
			for (Emotion emo : event.getEmotionList()) {
				addEmotionToDatabase(event, emo);
			}
		}
		
		
	}

	private void connectEventWithEventItemInDatabase(Event event, EventItem item) {
		
			ContentValues values = new ContentValues();
			values.put(EventColumns._ID, event.getId());
			values.put(EventItemsColumns.EVENT_ITEM_ID, item.getId());
			values.put(EventItemsColumns.CREATED_DATE, System.currentTimeMillis());
			values.put(EventItemsColumns.EVENT_ITEM_TYPE, EventItem.EventItemTypes.getItemType(item));
						
			context.getContentResolver().insert(EventItemsColumns.CONTENT_URI, values);

	}

	private void addNoteToNoteContentProvider(Event event, SimpleNote note) {
		
		  ContentValues values = new ContentValues();
		  
		  values.put(NoteColumns._ID, note.getId());
		  values.put(NoteColumns.TITLE, note.getNoteTitle());
		  values.put(NoteColumns.NOTE, note.getNoteText()); 
		  values.put(EventItemsColumns.USERNAME, note.getCreator());
		  values.put(NoteColumns.CREATED_DATE, event.getDatetimemillis());
		  		  
		  context.getContentResolver().insert(NoteColumns.CONTENT_URI, values);
	}

	
	private void addPictureToImageContentProvider(Event event, SimplePicture picture) {
		
		ContentValues values = new ContentValues();
		
		values.put(PictureColumns._ID, picture.getId());
		values.put(PictureColumns.URI_PATH, picture.getPictureUri().toString());
		values.put(PictureColumns.FILENAME, picture.getPictureFilename());
		values.put(EventItemsColumns.USERNAME, picture.getCreator());
		
		context.getContentResolver().insert(PictureColumns.CONTENT_URI, values);
	}
	
	private void addRecordingToRecordingContentProvider(Event event, SimpleRecording recording) {
		
		ContentValues values = new ContentValues();
		
		values.put(RecordingColumns._ID, recording.getId());
		values.put(RecordingColumns.FILE_URI, recording.getRecordingUri().toString());
		values.put(EventItemsColumns.USERNAME, recording.getCreator());
		values.put(RecordingColumns.FILENAME, recording.getRecordingFilename());
		values.put(RecordingColumns.DESCRIPTION, recording.getRecordingDescription());
		
		context.getContentResolver().insert(RecordingColumns.CONTENT_URI, values);
	}
	
	private void addVideoToVideoContentProvider(Event event, SimpleVideo video) {
		ContentValues values = new ContentValues();
		
		values.put(VideoColumns._ID, video.getId());
		values.put(VideoColumns.FILE_PATH, video.getVideoUri().toString());
		values.put(VideoColumns.FILENAME, video.getVideoFilename());
		values.put(EventItemsColumns.USERNAME, video.getCreator());
		
		context.getContentResolver().insert(VideoColumns.CONTENT_URI, values);
	}
	
	public void addEmotionToDatabase(Event tag, Emotion emo) {
		
		ContentValues values = new ContentValues();
		
		values.put(EmotionColumns._ID, emo.getEmotionid());
		values.put(EmotionColumns.EVENT_ID, tag.getId());
		values.put(EmotionColumns.EMOTION_TYPE, emo.getEmotionType().getType());
		
		context.getContentResolver().insert(EmotionColumns.CONTENT_URI, values);
	}

	
	
}
