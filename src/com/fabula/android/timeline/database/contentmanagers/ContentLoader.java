package com.fabula.android.timeline.database.contentmanagers;

import java.util.ArrayList;
import java.util.Date;

import android.accounts.Account;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Emotion;
import com.fabula.android.timeline.models.Emotion.EmotionColumns;
import com.fabula.android.timeline.models.Emotion.EmotionEnum;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.Experience.ExperienceColumns;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.Group.GroupColumns;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.ModelType;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.MoodEvent.MoodEnum;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimplePicture.PictureColumns;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleRecording.RecordingColumns;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.SimpleVideo.VideoColumns;

/**
 * Helper method for loading model objects from the database using Content Providers
 * 
 * @author andekr
 * @author andrstor
 *
 */
public class ContentLoader {
	
	private Context context;
	private String whereStatement;
	private TagManager tagManager;

	public ContentLoader(Context context) {
		this.context = context;
		tagManager = new TagManager(context);
	}
	
	public ArrayList <BaseEvent> LoadAllEventsFromDatabase() {
		
		ArrayList<BaseEvent> allEvents = new ArrayList<BaseEvent>();
		
		String[] eventTableColumns = new String[]{EventColumns._ID, EventColumns.EVENT_EXPERIENCEID, EventColumns.EVENT_TITLE, EventColumns.EVENT_LOCATION_LAT, EventColumns.EVENT_LOCATION_LNG, EventColumns.IS_SHARED, EventColumns.CREATOR, EventColumns.MOOD};
		
		Cursor c = context.getContentResolver().query(EventColumns.CONTENT_URI, eventTableColumns, null, null, null);
		
		if(c.moveToFirst()) {
			do{
				Date createdDate = new Date(c.getLong(c.getColumnIndex(EventColumns.EVENT_TITLE)));
				Location location = new Location("");
				location.setLatitude(Double.parseDouble(c.getString(c.getColumnIndex(EventColumns.EVENT_LOCATION_LAT))));
				location.setLongitude(Double.parseDouble(c.getString(c.getColumnIndex(EventColumns.EVENT_LOCATION_LNG))));
				
				if(((Integer) c.getInt(c.getColumnIndex(EventColumns.MOOD)) == 1000)) {
					Event event = new Event(c.getString(c.getColumnIndex(EventColumns._ID)),
							c.getString(c.getColumnIndex(EventColumns.EVENT_EXPERIENCEID)), 
							createdDate,
							location,
							new Account(c.getString(c.getColumnIndex(EventColumns.CREATOR)), "com.google"));
					
					event.setShared((c.getInt((c.getColumnIndex(EventColumns.IS_SHARED)))==1) ? true : false);
					loadAllEmotions(event);
					loadAllConnectedEventItems(event);
					event.setTags(tagManager.getAllTagsConnectedToEvent(event.getId()));
					allEvents.add(event);
				}else {
					MoodEvent moodEvent = new MoodEvent(c.getString(c.getColumnIndex(EventColumns._ID)),
							c.getString(c.getColumnIndex(EventColumns.EVENT_EXPERIENCEID)), 
							createdDate,
							location,
							MoodEnum.getType(c.getInt(c.getColumnIndex(EventColumns.MOOD))),
							new Account(c.getString(c.getColumnIndex(EventColumns.CREATOR)), "com.google"));
					allEvents.add(moodEvent);
				}	
			}while(c.moveToNext());
		}
		c.close();
		
		Log.i("CONTENTLOADER - EVENTS", allEvents.size()+" events loaded");
		
		return allEvents;
	}
	
	public ArrayList<Experience> LoadAllExperiencesFromDatabase(){
		
		ArrayList<Experience> allExperiences = new ArrayList<Experience>();
		
		String[] experienceTableColumns = new String[]{ExperienceColumns._ID, ExperienceColumns.EXPERIENCE_NAME, ExperienceColumns.EXPERIENCE_SHARED, ExperienceColumns.EXPERIENCE_CREATOR, ExperienceColumns.EXPERIENCE_SHARED_WITH};
		
		Cursor c = context.getContentResolver().query(ExperienceColumns.CONTENT_URI, experienceTableColumns, null, null, null);
		
		if(c.moveToFirst()) {
			do{
				Experience experience = new Experience(c.getString(c.getColumnIndex(ExperienceColumns._ID)), 
						c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_NAME)),
						(c.getInt((c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED)))==1) ? true : false,
						new Account(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_CREATOR)), "com.google"));
				
				if(experience.isShared()) {
					experience.setSharingGroupObject(getGroupSharedWithExperience(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED_WITH))));
				}
				allExperiences.add(experience);
				System.out.println("Hentet experience med shared: "+experience.isShared());	
			}while(c.moveToNext());
		}
		c.close();
		
		Log.i("CONTENTLOADER - EXPERIENCES", allExperiences.size()+" experiences loaded");
		
		return allExperiences;
		
	}
	
	private Group getGroupSharedWithExperience(String groupId) {
		String[] GroupTableColumns = new String[] {GroupColumns._ID, GroupColumns.GROUP_NAME};
		
		Group g = null;
		String where = GroupColumns._ID +" = '" +groupId+"'";
		Cursor c = context.getContentResolver().query(GroupColumns.CONTENT_URI, GroupTableColumns, where, null, null);
		
		if(c.moveToNext()) {
			g = new Group(c.getString(c.getColumnIndex(GroupColumns._ID)), c.getString(c.getColumnIndex(GroupColumns.GROUP_NAME)));
		}
		c.close();
		return g;
	}

	public ArrayList<Experience> LoadPrivateExperiencesFromDatabase(){
		
		ArrayList<Experience> allExperiences = new ArrayList<Experience>();
		
		String[] experienceTableColumns = new String[]{ExperienceColumns._ID, ExperienceColumns.EXPERIENCE_NAME, ExperienceColumns.EXPERIENCE_SHARED, ExperienceColumns.EXPERIENCE_CREATOR};
		
		whereStatement = ExperienceColumns.EXPERIENCE_SHARED +"='0'";
		
		Cursor c = context.getContentResolver().query(ExperienceColumns.CONTENT_URI, experienceTableColumns, whereStatement, null, null);
		
		if(c.moveToFirst()) {
			do{
				Experience experience = new Experience(c.getString(c.getColumnIndex(ExperienceColumns._ID)), 
						c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_NAME)),
						(c.getInt((c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED)))==1) ? true : false,
						new Account(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_CREATOR)), "com.google"));
				allExperiences.add(experience);
				System.out.println("Hentet experience med shared: "+experience.isShared());	
			}while(c.moveToNext());
		}
		c.close();
		
		Log.i("CONTENTLOADER - EXPERIENCES", allExperiences.size()+" experiences loaded");
		
		return allExperiences;
		
	}
	
	public ArrayList<Experience> LoadAllSharedExperiencesFromDatabase(){
		
		ArrayList<Experience> allExperiences = new ArrayList<Experience>();
		
		String[] experienceTableColumns = new String[]{ExperienceColumns._ID, ExperienceColumns.EXPERIENCE_NAME, ExperienceColumns.EXPERIENCE_SHARED, ExperienceColumns.EXPERIENCE_CREATOR, ExperienceColumns.EXPERIENCE_SHARED_WITH};
		
		whereStatement = ExperienceColumns.EXPERIENCE_SHARED +"='1'";
		
		Cursor c = context.getContentResolver().query(ExperienceColumns.CONTENT_URI, experienceTableColumns, whereStatement, null, null);
		
		if(c.moveToFirst()) {
			do{
				Experience experience = new Experience(c.getString(c.getColumnIndex(ExperienceColumns._ID)), 
						c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_NAME)),
						(c.getInt((c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED)))==1) ? true : false,
						new Account(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_CREATOR)), "com.google"));
				
				if(experience.isShared()) {
					experience.setSharingGroupObject(getGroupSharedWithExperience(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED_WITH))));
				}
				
				allExperiences.add(experience);
			}while(c.moveToNext());
		}
		c.close();
		
		Log.i("CONTENTLOADER - EXPERIENCES", allExperiences.size()+" experiences loaded");
		
		return allExperiences;
		
	}
	
	public ArrayList<Experience> LoadAllSharedExperiencesOnGroupFromDatabase(Group group){
		
		ArrayList<Experience> allExperiences = new ArrayList<Experience>();
		
		String[] experienceTableColumns = new String[]{ExperienceColumns._ID, ExperienceColumns.EXPERIENCE_NAME, ExperienceColumns.EXPERIENCE_SHARED, ExperienceColumns.EXPERIENCE_CREATOR, ExperienceColumns.EXPERIENCE_SHARED_WITH};
		
		whereStatement = ExperienceColumns.EXPERIENCE_SHARED +"='1' AND "+ExperienceColumns.EXPERIENCE_SHARED_WITH+"='"+group.getId()+"'";
		
		Cursor c = context.getContentResolver().query(ExperienceColumns.CONTENT_URI, experienceTableColumns, whereStatement, null, null);
		
		if(c.moveToFirst()) {
			do{
				Experience experience = new Experience(c.getString(c.getColumnIndex(ExperienceColumns._ID)), 
						c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_NAME)),
						(c.getInt((c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED)))==1) ? true : false,
						new Account(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_CREATOR)), "com.google"));
				
				if(experience.isShared()) {
					experience.setSharingGroupObject(getGroupSharedWithExperience(c.getString(c.getColumnIndex(ExperienceColumns.EXPERIENCE_SHARED_WITH))));
				}
				
				allExperiences.add(experience);
			}while(c.moveToNext());
		}
		c.close();
		
		Log.i("CONTENTLOADER - EXPERIENCES", allExperiences.size()+" experiences loaded");
		
		return allExperiences;
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param experience The {@linkplain Experience} to get events from
	 * @return ArrayList of events connected to the experience
	 */
//	public ArrayList<Event> getEvents(Experience experience){
//		
//		String[] eventItemsTableColumns = new String[]{EventItemsColumns.EVENT_ID, EventItemsColumns.EVENT_ITEM_ID, EventItemsColumns.EVENT_ITEM_TYPE};
//		
//		whereStatement = EventItemsColumns.EVENT_ID +"='"+ event.getId()+"'";
//		String sortOrder = EventItemsColumns.CREATED_DATE+" ASC";
//	
//		Cursor listOfEventItems = context.getContentResolver().query(EventItemsColumns.CONTENT_URI, eventItemsTableColumns, whereStatement, null, sortOrder);
//		DatabaseUtils.dumpCursor(listOfEventItems);
//		Log.i("CURSOR", String.valueOf(listOfEventItems.moveToFirst()));
//		if(listOfEventItems.moveToFirst()) {
//			do{
//				
//				String eventItemID = listOfEventItems.getString(listOfEventItems.getColumnIndex(EventItemsColumns.EVENT_ITEM_ID));
//				int itemType = listOfEventItems.getInt(listOfEventItems.getColumnIndex(EventItemsColumns.EVENT_ITEM_TYPE));
//				
//				if(itemType == ModelType.SimpleNote.numberOfType()) {
//
//					addNoteItemToEvent(event, eventItemID);
//				}
//				if(itemType == ModelType.SimplePicture.numberOfType()) {
//					addPictureItemToEvent(event, eventItemID);
//				}
//				if(itemType == ModelType.SimpleRecording.numberOfType()) {
//					addRecordingItemToEvent(event, eventItemID);
//				}
//				if(itemType == ModelType.SimpleVideo.numberOfType()) {
//					addVideoItemToEvent(event, eventItemID);
//				}
//				
//			}while(listOfEventItems.moveToNext());
//		}
//		
//		listOfEventItems.close();
//		
//		
//	}


	private void loadAllConnectedEventItems(Event event) {
			
			String[] eventItemsTableColumns = new String[]{EventItemsColumns.EVENT_ID, EventItemsColumns.EVENT_ITEM_ID, EventItemsColumns.EVENT_ITEM_TYPE};
			
			whereStatement = EventItemsColumns.EVENT_ID +"='"+ event.getId()+"'";
			String sortOrder = EventItemsColumns.CREATED_DATE+" ASC";
		
			Cursor listOfEventItems = context.getContentResolver().query(EventItemsColumns.CONTENT_URI, eventItemsTableColumns, whereStatement, null, sortOrder);
			DatabaseUtils.dumpCursor(listOfEventItems);
			Log.i("CURSOR", String.valueOf(listOfEventItems.moveToFirst()));
			if(listOfEventItems.moveToFirst()) {
				do{
					
					String eventItemID = listOfEventItems.getString(listOfEventItems.getColumnIndex(EventItemsColumns.EVENT_ITEM_ID));
					int itemType = listOfEventItems.getInt(listOfEventItems.getColumnIndex(EventItemsColumns.EVENT_ITEM_TYPE));
					
					if(itemType == ModelType.SimpleNote.numberOfType()) {

						addNoteItemToEvent(event, eventItemID);
					}
					if(itemType == ModelType.SimplePicture.numberOfType()) {
						addPictureItemToEvent(event, eventItemID);
					}
					if(itemType == ModelType.SimpleRecording.numberOfType()) {
						addRecordingItemToEvent(event, eventItemID);
					}
					if(itemType == ModelType.SimpleVideo.numberOfType()) {
						addVideoItemToEvent(event, eventItemID);
					}
					
				}while(listOfEventItems.moveToNext());
			}
			
			listOfEventItems.close();
	}

	private void addVideoItemToEvent(Event event, String eventItemID) {
		
		String [] videoColumns = new String[] {VideoColumns._ID, VideoColumns.FILE_PATH, EventItemsColumns.USERNAME, VideoColumns.FILENAME};
		String whereStatement = VideoColumns._ID+"='"+eventItemID+"'";
		
		Cursor cursorOnVideoList = context.getContentResolver().query(VideoColumns.CONTENT_URI, videoColumns, whereStatement, null, null);
		DatabaseUtils.dumpCursor(cursorOnVideoList);
		
		if(cursorOnVideoList.moveToFirst()) {
			do{
				SimpleVideo video = new SimpleVideo(
						cursorOnVideoList.getString(cursorOnVideoList.getColumnIndex(VideoColumns._ID)),
						Uri.parse(cursorOnVideoList.getString(cursorOnVideoList.getColumnIndex(VideoColumns.FILE_PATH))), 
						new Account(cursorOnVideoList.getString(cursorOnVideoList.getColumnIndex(EventItemsColumns.USERNAME)), "com.google"),
						cursorOnVideoList.getString(cursorOnVideoList.getColumnIndex(RecordingColumns.FILENAME))
				);
				
				event.addEventItem(video);
			}while(cursorOnVideoList.moveToNext());
		cursorOnVideoList.close();
		}
		
		
	}

	private void addRecordingItemToEvent(Event event, String eventItemID) {
		
		String [] recordingColumns = new String[] {RecordingColumns._ID, RecordingColumns.FILE_URI, EventItemsColumns.USERNAME,RecordingColumns.FILENAME };
		String whereStatement = RecordingColumns._ID+"='"+eventItemID+"'";
		
		Cursor cursorOnRecordingList = context.getContentResolver().query(RecordingColumns.CONTENT_URI, recordingColumns, whereStatement, null, null);
		DatabaseUtils.dumpCursor(cursorOnRecordingList);
		
		if(cursorOnRecordingList.moveToFirst()) {
			do{
				SimpleRecording recording = new SimpleRecording(
						cursorOnRecordingList.getString(cursorOnRecordingList.getColumnIndex(RecordingColumns._ID)),
						Uri.parse(cursorOnRecordingList.getString(cursorOnRecordingList.getColumnIndex(RecordingColumns.FILE_URI))), 
						new Account(cursorOnRecordingList.getString(cursorOnRecordingList.getColumnIndex(EventItemsColumns.USERNAME)), "com.google"),
						cursorOnRecordingList.getString(cursorOnRecordingList.getColumnIndex(RecordingColumns.FILENAME))		
						);
				
				event.addEventItem(recording);
			}while(cursorOnRecordingList.moveToNext());
		cursorOnRecordingList.close();
		}
	}

	private void addPictureItemToEvent(Event event, String eventItemID) {
		
		String[] pictureColumns = new String[] {PictureColumns._ID, PictureColumns.URI_PATH, EventItemsColumns.USERNAME, PictureColumns.FILENAME};
		String whereStatement = PictureColumns._ID+"='"+eventItemID+"'";
		
		Cursor cursorOnListOfPicturesBelongingToEvent = context.getContentResolver().query(PictureColumns.CONTENT_URI, pictureColumns, whereStatement, null, null);
		DatabaseUtils.dumpCursor(cursorOnListOfPicturesBelongingToEvent);
		if(cursorOnListOfPicturesBelongingToEvent.moveToFirst()) {
			do{
				SimplePicture picture = new SimplePicture(
						cursorOnListOfPicturesBelongingToEvent.getString(cursorOnListOfPicturesBelongingToEvent.getColumnIndex(PictureColumns._ID)),
						Uri.parse(cursorOnListOfPicturesBelongingToEvent.getString(cursorOnListOfPicturesBelongingToEvent.getColumnIndex(PictureColumns.URI_PATH))), 
						new Account(cursorOnListOfPicturesBelongingToEvent.getString(cursorOnListOfPicturesBelongingToEvent.getColumnIndex(EventItemsColumns.USERNAME)), "com.google"),
						cursorOnListOfPicturesBelongingToEvent.getString(cursorOnListOfPicturesBelongingToEvent.getColumnIndex(PictureColumns.FILENAME))
						);
				
				event.getEventItems().add(picture);
			}while(cursorOnListOfPicturesBelongingToEvent.moveToNext());
		
		cursorOnListOfPicturesBelongingToEvent.close();
		}
	}

	private void addNoteItemToEvent(Event event, String eventItemID) {
		
		String [] columns = new String[] {NoteColumns._ID, NoteColumns.TITLE, NoteColumns.NOTE, NoteColumns.CREATED_DATE, NoteColumns.MODIFIED_DATE, EventItemsColumns.USERNAME};
		String whereStatement = NoteColumns._ID+"='"+eventItemID+"'";
		Cursor listOfNotesBelongingToEvent = context.getContentResolver().query(NoteColumns.CONTENT_URI, columns, whereStatement, null, null);
		
		if(listOfNotesBelongingToEvent.moveToFirst()) {
			do{
			SimpleNote note = new SimpleNote(
					  listOfNotesBelongingToEvent.getString(listOfNotesBelongingToEvent.getColumnIndex(NoteColumns._ID)), 
					  listOfNotesBelongingToEvent.getString(listOfNotesBelongingToEvent.getColumnIndex(NoteColumns.TITLE)), 
					  listOfNotesBelongingToEvent.getString(listOfNotesBelongingToEvent.getColumnIndex(NoteColumns.NOTE)), 
					  new Account(listOfNotesBelongingToEvent.getString(listOfNotesBelongingToEvent.getColumnIndex(EventItemsColumns.USERNAME)), "com.google"));

			event.getEventItems().add(note);
			
			}while(listOfNotesBelongingToEvent.moveToNext());
		
		listOfNotesBelongingToEvent.close();
		}
	}
	
	
	private void loadAllEmotions(Event event) {
		
		String[] columns = new String[] {EmotionColumns._ID, EmotionColumns.EVENT_ID, EmotionColumns.EMOTION_TYPE};
		String where = EmotionColumns.EVENT_ID+ "='"+event.getId()+"'";
		
		Cursor listOfEmotions = context.getContentResolver().query(EmotionColumns.CONTENT_URI, columns, where, null, null);
		
		if(listOfEmotions.moveToFirst()){
			do{
				String id = listOfEmotions.getString(listOfEmotions.getColumnIndex(EmotionColumns._ID));
				switch (listOfEmotions.getInt(listOfEmotions.getColumnIndex(EmotionColumns.EMOTION_TYPE))) {
				case 1:
					event.addEmotion(new Emotion(id, EmotionEnum.LIKE));
					break;
				case 2:
					event.addEmotion(new Emotion(id, EmotionEnum.COOL));
					break;
				case 3:
					event.addEmotion(new Emotion(id, EmotionEnum.DISLIKE));
					break;
				case 4:
					event.addEmotion(new Emotion(id, EmotionEnum.SAD));
					break;
				}
				
			}while(listOfEmotions.moveToNext());
		}
		listOfEmotions.close();
		
	}
}
