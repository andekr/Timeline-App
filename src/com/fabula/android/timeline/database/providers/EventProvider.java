package com.fabula.android.timeline.database.providers;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.Event.EventColumns;

public class EventProvider extends BaseContentProvider {
	
	public static final String AUTHORITY = "com.fabula.android.timeline.database.providers.EventProvider";

	
    public static final Uri CONTENT_URI = 
        Uri.parse("content://"+ AUTHORITY + "/events");
      
    private static final int EVENT = 1;
    private static final int EVENT_ID = 2; 
   
    private static final UriMatcher uriMatcher;


	private static HashMap<String, String> eventsProjectionMap;
	
    static{
       uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); 
       uriMatcher.addURI(AUTHORITY, SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME+"/", EVENT);
       uriMatcher.addURI(AUTHORITY, SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME+"/#", EVENT_ID);
       
       eventsProjectionMap = new HashMap<String, String>();
       eventsProjectionMap.put(EventColumns._ID, EventColumns._ID);
       eventsProjectionMap.put(EventColumns.EVENT_TITLE, EventColumns.EVENT_TITLE);
       eventsProjectionMap.put(EventColumns.EVENT_ITEMS_ID, EventColumns.EVENT_ITEMS_ID);
       eventsProjectionMap.put(EventColumns.EVENT_LOCATION_LAT, EventColumns.EVENT_LOCATION_LAT);
       eventsProjectionMap.put(EventColumns.EVENT_LOCATION_LNG, EventColumns.EVENT_LOCATION_LNG);
       eventsProjectionMap.put(EventColumns.IS_SHARED, EventColumns.IS_SHARED);
       eventsProjectionMap.put(EventColumns.CREATOR, EventColumns.CREATOR);
    }
    
	//insert some element in the DB
	public Uri insert(Uri uri, ContentValues values) {
		
	      long rowID = super.getDatabase().insertWithOnConflict(SQLStatements.EVENT_DATABASE_TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
	           
	      //---if added successfully---
	      if (rowID>0)
	      {
	         Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
	         getContext().getContentResolver().notifyChange(_uri, null);    
	         return _uri;                
	      }        
	      throw new SQLException("Failed to insert row into " + uri);
	}
	
	//get some element in the db
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
	      sqlBuilder.setTables(SQLStatements.EVENT_DATABASE_TABLE_NAME);
	       
	      if (uriMatcher.match(uri) == EVENT_ID)
	         
	         sqlBuilder.appendWhere(
	            EventColumns._ID + " = " + uri.getPathSegments().get(1));                
	       
	      if (sortOrder==null || sortOrder=="")
	         sortOrder = EventColumns.EVENT_TITLE;
	   
	      Cursor c = sqlBuilder.query(
	         super.getDatabase(), 
	         projection, 
	         selection, 
	         selectionArgs, 
	         null, 
	         null, 
	         sortOrder);
	   
	      //---register to watch a content URI for changes---
	      c.setNotificationUri(getContext().getContentResolver(), uri);
	      return c;
	}

	//delete some element in the DB
	public int delete(Uri uri, String id, String[] whereArgs) {
		return super.getDatabase().delete(SQLStatements.EVENT_DATABASE_TABLE_NAME, EventColumns._ID + "='"+id+"'", whereArgs);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		
		//find all experiences
		case EVENT:
			return "vnd.android.cursor.dir/vnd.com.fabula.android.timelines";
		
		//find one particular experience
		case EVENT_ID:
			return "vnd.android.cursor.item/vnd.com.fabula.android.timelines";
			
		default:
	          throw new IllegalArgumentException("Unsupported URI: " + uri); 
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
	    count = super.getDatabase().update(
	               SQLStatements.EVENT_DATABASE_TABLE_NAME, 
	               values,
	               selection, 
	               selectionArgs);
	    
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	}
}
