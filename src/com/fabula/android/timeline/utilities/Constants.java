package com.fabula.android.timeline.utilities;

import java.io.File;

import org.apache.http.HttpHost;

import android.os.Environment;

public class Constants {

	public static final String ALL_TIMELINES_DATABASE_NAME = "allTimelinesDatabase.db";
	public static final String INTENT_ACTION_NEW_TIMELINE = "com.fabula.android.timeline.intent.NEW_TIMELINE";
	public static final String INTENT_ACTION_ADD_TO_TIMELINE = "com.fabula.android.timeline.intent.ADD_TIMELINE";
	public static final String INTENT_ACTION_OPEN_MAP_VIEW_FROM_TIMELINE = "com.fabula.android.intent.OPEN_MAP_TIMELINE";
	public static final String INTENT_ACTION_OPEN_MAP_VIEW_FROM_DASHBOARD = "com.fabula.android.intent.OPEN_MAP_DASHBOARD";
	public static final String INTENT_ACTION_NEW_TAG = "com.fabula.android.intent.NEWTAG";
	
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1;
    public static final int RECORD_AUDIO_ACTIVITY_REQUEST_CODE = 2;
    public static final int CREATE_NOTE_ACTIVITY_REQUEST_CODE = 3;
    public static final int ATTACHMENT_ACTIVITY_REQUEST_CODE = 4;
    public static final int MAP_VIEW_ACTIVITY_REQUEST_CODE = 14;
    public static final int ALL_EXPERIENCES_MAP_ACTIVITY_REQUEST_CODE = 15;
    
    public static final int SELECT_PICTURE = 5;
    public static final int SELECT_VIDEO = 6;
    public static final int CAPTURE_BARCODE = 0x0ba7c0de;
    public static final int BROWSE_FILES = 8;
    public static final int EDIT_NOTE = 9;
    
    
    public static final String REQUEST_CODE = "REQUEST_CODE";
    
    public static final String DATABASE_NAME = "timelineDB.db";
    public static final int DATABASE_VERSION = 1; //var 24
	public static final int USER_GROUP_DATABASE_VERSION = 1;
	
    public static final String DATABASENAME_REQUEST = "DATABASENAME";
    public static final String EXPERIENCEID_REQUEST = "EXPERIENCEID";
    public static final String EXPERIENCECREATOR_REQUEST = "EXPERIENCECREATOR";
    public static final String SHARED_REQUEST = "SHAREDEXPERIENCE";
    public static final String SHARED_WITH_REQUEST = "SHAREDWITH";

	public static final String USER_GROUP_DATABASE_NAME = "user_group_database.db";

	public static final String GOOGLE_APP_ENGINE_URL = "reflectapp.appspot.com";
	
	public static final String MEDIASTORE_URL = "http://folk.ntnu.no/andekr/upload/files/";
	
	public static final HttpHost targetHost = new HttpHost(Constants.GOOGLE_APP_ENGINE_URL, 80, "http");
    
    
    public static float HOUR_IN_MILLIS = 3600000;
    public static float DAY_IN_MILLIS = 86400000;
    public static float WEEK_IN_MILLIS = DAY_IN_MILLIS*7;
    
    public static int EVENT_TAG_KEY = 100;
    public static int ACTIVITY_TAG_KEY = 101;
    
    final public static int SHARED_FALSE = 0;
    final public static int SHARED_TRUE = 1;
    final public static int SHARED_ALL = 2;

	final public static int NEW_TAG_REQUESTCODE = 746;

    static File sdCardDirectory = Environment.getExternalStorageDirectory();
    public static String IMAGE_STORAGE_FILEPATH = sdCardDirectory.getPath()+"/data/com.fabula.android.timeline/images/";
    public static String VIDEO_STORAGE_FILEPATH = sdCardDirectory.getPath()+"/data/com.fabula.android.timeline/videos/";
    public static String RECORDING_STORAGE_FILEPATH = sdCardDirectory.getPath()+"/data/com.fabula.android.timeline/recordings/";

}
