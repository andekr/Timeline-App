package com.fabula.android.timeline.providers;

import java.util.HashMap;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.Event.EventColumns;
import com.fabula.android.timeline.models.Experience.TagColumns;

public class TagProvider extends BaseContentProvider {

	public static final String AUTHORITY = "com.fabula.android.timeline.providers.tagprovider";
	private static final int TAG_ITEM = 1;
	private static final int TAGGED_EVENT_ITEM = 2;
	
	private static final UriMatcher uriMatcher;

	private static HashMap<String, String> tagColumnsMapping;
	private static HashMap<String, String> tagEventsColumnMapping;
	
	@Override
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Cursor cursorOnRetriewedRows = null;
		queryBuilder.setTables(SQLStatements.TAG_DATABASE_TABLE);
		
		switch (uriMatcher.match(uri)) {
		case TAG_ITEM:
			queryBuilder.setProjectionMap(tagColumnsMapping);
			
			cursorOnRetriewedRows = queryBuilder.query(
					super.getTimelinesDatabase(), 
					columns, 
					where, 
					whereArgs, 
					null, 
					null, 
					null);
			
			cursorOnRetriewedRows.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursorOnRetriewedRows;
		case TAGGED_EVENT_ITEM:
			queryBuilder.setProjectionMap(tagEventsColumnMapping);
			cursorOnRetriewedRows = queryBuilder.query(
					super.getTimelinesDatabase(), 
					columns, 
					where, 
					whereArgs, 
					null, 
					null, 
					null);
			
			cursorOnRetriewedRows.setNotificationUri(getContext().getContentResolver(), uri);
			
			return cursorOnRetriewedRows;
			
		default:
			return cursorOnRetriewedRows;
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        long rowId = 0;
        
        switch (uriMatcher.match(uri)) {
		case TAG_ITEM:
			rowId = super.getTimelinesDatabase().insertWithOnConflict(SQLStatements.TAG_DATABASE_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
			break;
		case TAGGED_EVENT_ITEM:
			rowId = super.getTimelinesDatabase().insertWithOnConflict(SQLStatements.TAG_EVENT_DATABASE_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		default:
			 System.out.println("Antall satt inn: "+rowId);
			break;
		}
        
        System.out.println("Antall satt inn: "+rowId);
		if (rowId > 0) {
		    getContext().getContentResolver().notifyChange(uri, null);
		    return uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case TAG_ITEM:
			return super.getTimelinesDatabase().delete(SQLStatements.TAG_DATABASE_TABLE, where, whereArgs);
		case TAGGED_EVENT_ITEM:
			return super.getTimelinesDatabase().delete(SQLStatements.TAGGED_EVENTS_DATABASE_CREATE, where, whereArgs);
		default:
			return count;
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues initialValues, String where,
			String[] whereArgs) {
		
		int count = 0;
		ContentValues values;
		
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        switch (uriMatcher.match(uri)) {
		case TAG_ITEM:
			count = super.getTimelinesDatabase().update(SQLStatements.TAG_DATABASE_TABLE, values, where, whereArgs);
			break;
		case TAGGED_EVENT_ITEM:
			count =  super.getTimelinesDatabase().update(SQLStatements.TAG_EVENT_DATABASE_TABLE, values, where, whereArgs);
		    break;
		}
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}
	
	static {
		tagColumnsMapping = new HashMap<String, String>();
		tagColumnsMapping.put(TagColumns.TAG_ID, TagColumns.TAG_ID);
		tagColumnsMapping.put(TagColumns.TAG_NAME, TagColumns.TAG_NAME);
		
		tagEventsColumnMapping = new HashMap<String, String>();
		tagEventsColumnMapping.put(TagColumns.TAG_ID, TagColumns.TAG_ID);
		tagEventsColumnMapping.put(EventColumns.EVENT_ID, EventColumns.EVENT_ID);
		
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, SQLStatements.TAG_DATABASE_TABLE, TAG_ITEM);
		uriMatcher.addURI(AUTHORITY, SQLStatements.TAG_EVENT_DATABASE_TABLE, TAGGED_EVENT_ITEM);
	}
}
