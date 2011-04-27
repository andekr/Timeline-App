package com.fabula.android.timeline.database;

import com.fabula.android.timeline.models.Emotion.EmotionColumns;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.Experience.ExperienceColumns;
import com.fabula.android.timeline.models.Experience.TagColumns;
import com.fabula.android.timeline.models.Group.GroupColumns;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;
import com.fabula.android.timeline.models.SimplePicture.PictureColumns;
import com.fabula.android.timeline.models.SimpleRecording.RecordingColumns;
import com.fabula.android.timeline.models.SimpleVideo.VideoColumns;
import com.fabula.android.timeline.models.User.UserColumns;

/**
 *  "Static" class with statements for creating database tables.
 * 
 * @author andekr
 *
 */
public class SQLStatements {
	
	public static final String EVENT_DATABASE_TABLE_NAME = "events";
	public static final String NOTE_DATABASE_TABLE_NAME = "notes";
	public static final String EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME = "event_to_event_items";
	public static final String PICTURE_DATABASE_TABLE_NAME = "pictures";
	public static final String RECORDINGS_DATABASE_TABLE_NAME ="recordings";
	public static final String VIDEO_DATABASE_TABLE_NAME = "videos";
	public static final String EMOTIONS_DATABASE_TABLE_NAME = "emotions";	
	public static final String TIMELINES_DATABASE_TABLE_NAME = "timelines";
	public static final String USER_DATABASE_TABLE_NAME = "users";
	public static final String GROUP_DATABASE_TABLE_NAME = "groups";
	public static final String USER_GROUP_DATABASE_TABLE_NAME = "user_groups";
	public static final String MOOD_DATABASE_NAME= "mood_events";
	public static final String TAG_DATABASE_TABLE = "tags";
	public static final String TAG_EVENT_DATABASE_TABLE = "tagged_events";
	
	
    public static final String EVENT_DATABASE_CREATE =
        "create table " + EVENT_DATABASE_TABLE_NAME + 
        " (" + EventColumns._ID + " varchar primary key, " +
        EventColumns.EVENT_EXPERIENCEID + " varchar, " +
        EventColumns.EVENT_LOCATION_LAT + " varchar, " +
        EventColumns.EVENT_LOCATION_LNG + " varchar, " +
        EventColumns.EVENT_TITLE+" long not null, "+
        EventColumns.IS_SHARED+" INTEGER, "+
        EventColumns.CREATOR+ " varchar not null, "+
        EventColumns.MOODX + " varchar, "+
        EventColumns.MOODY + " varchar "+");";
    
    public static final String EVENT_TO_EVENT_ITEM_DATABASE_CREATE =
        "create table " + EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME + 
        " (" + EventColumns._ID + " varchar, " +
        EventItemsColumns.EVENT_ITEM_ID + " varchar, " +
        EventItemsColumns.EVENT_ITEM_TYPE + " INTEGER, " +
        EventItemsColumns.CREATED_DATE + " LONG, " +
        "primary key("+EventColumns._ID+" ,"+ EventItemsColumns.EVENT_ITEM_ID+"));";
    
    public static final String NOTES_DATABASE_CREATE = "CREATE TABLE " + NOTE_DATABASE_TABLE_NAME + " ("
		+ NoteColumns._ID + " VARCHAR PRIMARY KEY,"
		+ NoteColumns.TITLE + " TEXT,"
		+ NoteColumns.NOTE + " TEXT,"
		+ NoteColumns.CREATED_DATE + " LONG,"
		+EventItemsColumns.USERNAME+" VARCHAR,"
		+ NoteColumns.MODIFIED_DATE + " INTEGER"+");";
    
	public static final String PICTURE_DATABASE_CREATE = "CREATE TABLE " + PICTURE_DATABASE_TABLE_NAME + " ("
		+PictureColumns._ID +" VARCHAR PRIMARY KEY,"
		+PictureColumns.URI_PATH+" VARCHAR NOT NULL,"
		+PictureColumns.DESCRIPTION+" TEXT,"
		+EventItemsColumns.USERNAME+" VARCHAR,"
		+PictureColumns.FILENAME+" VARCHAR,"
		+PictureColumns.CREATED_DATE+" LONG"+");";
	
	public static final String RECORDING_DATABASE_CREATE = "CREATE TABLE " + RECORDINGS_DATABASE_TABLE_NAME + "("
		+RecordingColumns._ID +" VARCHAR PRIMARY KEY,"
		+RecordingColumns.FILE_URI +" VARCHAR NOT NULL,"
		+RecordingColumns.DESCRIPTION +" TEXT,"
		+EventItemsColumns.USERNAME+" VARCHAR,"
		+RecordingColumns.FILENAME+" VARCHAR,"
		+RecordingColumns.CREATED_DATE +" LONG"+");" ;
	
	public static final String VIDEO_DATABASE_CREATE = "CREATE TABLE " + VIDEO_DATABASE_TABLE_NAME +"("
		+VideoColumns._ID +" VARCHAR PRIMARY KEY,"
		+VideoColumns.FILE_PATH + " VARCHAR NOT NULL,"
		+VideoColumns.DESCRIPTION + " TEXT,"
		+EventItemsColumns.USERNAME+" VARCHAR,"
		+VideoColumns.FILENAME+" VARCHAR,"
		+VideoColumns.CREATED_DATE +" LONG"+");";
	
	public static final String EMOTIONS_DATABASE_CREATE = "CREATE TABLE " + EMOTIONS_DATABASE_TABLE_NAME+"("
		+EmotionColumns.EMOTION_ID+ " VARCHAR PRIMARY KEY ,"
		+EmotionColumns.EVENT_ID +" VARCHAR NOT NULL,"
		+EmotionColumns.EMOTION_TYPE +" INTEGER NOT NULL"+");";

	public static final String TIMELINES_DATABASE_CREATE = "CREATE TABLE " + TIMELINES_DATABASE_TABLE_NAME+"("
		+ExperienceColumns._ID+ " VARCHAR PRIMARY KEY,"
		+ExperienceColumns.EXPERIENCE_NAME+ " VARCHAR NOT NULL,"
		+ExperienceColumns.EXPERIENCE_SHARED+ " INTEGER,"
		+ExperienceColumns.EXPERIENCE_CREATOR+ " VARCHAR NOT NULL,"
		+ExperienceColumns.EXPERIENCE_LAST_MODIFIED+ " VARCHAR,"
		+ExperienceColumns.EXPERIENCE_SHARED_WITH+ " VARCHAR"+");";

	public static final String USER_DATABASE_CREATE = "CREATE TABLE " + USER_DATABASE_TABLE_NAME+"("
	    +UserColumns._ID+ " VARCHAR PRIMARY KEY,"
	    +UserColumns.USER_NAME+ " VARCHAR NOT NULL"+");";
	
	public static final String GROUP_DATABASE_CREATE = "CREATE TABLE " + GROUP_DATABASE_TABLE_NAME+"("
		+GroupColumns._ID+ " VARCHAR PRIMARY KEY,"
		+GroupColumns.GROUP_NAME+ " VARCHAR NOT NULL"+");";
	
	public static final String USER_GROUP_DATABASE_CREATE = "CREATE TABLE " + USER_GROUP_DATABASE_TABLE_NAME+"("
		+GroupColumns._ID+ " VARCHAR,"
		+UserColumns.USER_NAME+ " VARCHAR, "
		+"primary key("+GroupColumns._ID+" ,"+ UserColumns.USER_NAME+"));";
	
	public static final String TAG_DATABASE_CREATE = "CREATE TABLE " + TAG_DATABASE_TABLE+"("
		+TagColumns.TAG_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+TagColumns.TAG_NAME+ " VARCHAR "+");";
		
	public static final String TAGGED_EVENTS_DATABASE_CREATE = "CREATE TABLE " + TAG_EVENT_DATABASE_TABLE+"("
		+TagColumns.TAG_ID+ " INTEGER, "
		+EventColumns._ID+ " VARCHAR, "
		+"primary key("+TagColumns.TAG_ID+" ,"+ EventColumns._ID+"));";
}
