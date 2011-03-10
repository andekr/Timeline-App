package com.fabula.android.timeline.providers;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;

public class EventItemProvider extends BaseContentProvider {

	public static final String AUTHORITY = "com.fabula.android.timeline.providers.EventItemProvider";
	
	private static final int EVENT_ID = 1;
	private static final int EVENT_ITEM_ID = 2;
	
    public static final Uri CONTENT_URI = 
        Uri.parse("content://"+ AUTHORITY + "/eventItem");
    
      private static final UriMatcher uriMatcher;

      private static HashMap<String, String> eventsToEventItemsProjectionMap;

    
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		Long rowID = super.getDatabase().insert(SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME, "", values);
		
		if(rowID > 0){
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}
	
	@Override      
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
	      sqlBuilder.setTables(SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME);
	      	            	      
	      Cursor c = sqlBuilder.query(
	    		 super.getDatabase(), 
	 	         projection, 
	 	         selection, 
	 	         selectionArgs, 
	 	         null, 
	 	         null, 
	 	         sortOrder);

	      c.setNotificationUri(getContext().getContentResolver(), uri);
	      return c;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		
		//find all experiences
		case EVENT_ITEM_ID:
			return "vnd.android.cursor.item/vnd.com.fabula.android.eventToItemMapper";
		
		//find one particular experience
		case EVENT_ID:
			return "vnd.android.cursor.item/vnd.com.fabula.android.eventToItemMapper";
			
		default:
	          throw new IllegalArgumentException("Unsupported URI: " + uri); 
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case EVENT_ITEM_ID:
			String eventItemID = uri.getPathSegments().get(1);
			//update row where experienceItemID=experienceItemID
			count = super.getDatabase().update(SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME, values, eventItemID+"="+EventColumns.EVENT_ITEMS_ID
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			break;
		}
		return count;
	}

    static {
    	uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	uriMatcher.addURI(AUTHORITY, SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME+"/#", EVENT_ID);
    	uriMatcher.addURI(AUTHORITY, SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME+"/#", EVENT_ITEM_ID);
    	
    	eventsToEventItemsProjectionMap = new HashMap<String, String>();
    	eventsToEventItemsProjectionMap.put(EventColumns._ID, EventColumns._ID);
    	eventsToEventItemsProjectionMap.put(EventItemsColumns.EVENT_ITEM_ID, EventItemsColumns.EVENT_ITEM_ID);
    }
	
}
