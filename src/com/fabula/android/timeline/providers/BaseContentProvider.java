package com.fabula.android.timeline.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.database.UserGroupDatabaseHelper;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;

public class BaseContentProvider extends ContentProvider{
	
	private static final int EVENT_ITEM = 1;
	private static final int NOTE_ITEM = 2;
	private static final int PICTURE_ITEM = 3;
	private static final int EVENT = 4;
	private static final int RECORDING_ITEM = 5;
	private static final int VIDEO_ITEM = 6;

	private static final UriMatcher uriMatcher;
	
	public SQLiteDatabase getDatabase() {
		return DatabaseHelper.getCurrentTimelineDatabase();
	}
	
	public SQLiteDatabase getTimelinesDatabase(){
		return TimelineDatabaseHelper.getCurrentTimeLineDatabase();
	}
	
	public SQLiteDatabase getUserDatabase() {
		return UserGroupDatabaseHelper.getUserDatabase();
	}
	
	
	@Override
	public int delete(Uri uri, String id, String[] whereArgs) {
		
		String itemID = "'"+id+"'";
		int count = 0;
        DatabaseHelper.getCurrentTimelineDatabase().delete(SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME, 
        		EventItemsColumns.EVENT_ITEM_ID + "=" + itemID, whereArgs);
           
		switch (uriMatcher.match(uri)) {
		case NOTE_ITEM:	
			count = DatabaseHelper.getCurrentTimelineDatabase().delete(SQLStatements.NOTE_DATABASE_TABLE_NAME, BaseColumns._ID + "="+ itemID, whereArgs);
			break;
		case PICTURE_ITEM:
            count = DatabaseHelper.getCurrentTimelineDatabase().delete(SQLStatements.PICTURE_DATABASE_TABLE_NAME, BaseColumns._ID + "=" + itemID, whereArgs);
            break;
		case RECORDING_ITEM:
			count = DatabaseHelper.getCurrentTimelineDatabase().delete(SQLStatements.RECORDINGS_DATABASE_TABLE_NAME, BaseColumns._ID + "=" + itemID, whereArgs);
			break;
		case VIDEO_ITEM:
			count = DatabaseHelper.getCurrentTimelineDatabase().delete(SQLStatements.VIDEO_DATABASE_TABLE_NAME, BaseColumns._ID + "="+itemID,whereArgs);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

        if (uriMatcher.match(uri) == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        long rowId = 0;
        
        switch (uriMatcher.match(uri)) {
        
		case NOTE_ITEM:
			rowId = DatabaseHelper.getCurrentTimelineDatabase().insertWithOnConflict(SQLStatements.NOTE_DATABASE_TABLE_NAME, NoteColumns.NOTE, values, SQLiteDatabase.CONFLICT_REPLACE);
	        return notifyContentResolver(uri, rowId);
	        
		case PICTURE_ITEM:
			rowId = DatabaseHelper.getCurrentTimelineDatabase().insertWithOnConflict(SQLStatements.PICTURE_DATABASE_TABLE_NAME,"", values, SQLiteDatabase.CONFLICT_REPLACE);
			return notifyContentResolver(uri, rowId);
			
		case RECORDING_ITEM:
			rowId = DatabaseHelper.getCurrentTimelineDatabase().insert(SQLStatements.RECORDINGS_DATABASE_TABLE_NAME, "", values);
			return notifyContentResolver(uri, rowId);
		case VIDEO_ITEM:
			rowId = DatabaseHelper.getCurrentTimelineDatabase().insert(SQLStatements.VIDEO_DATABASE_TABLE_NAME,"",values);
			return notifyContentResolver(uri, rowId);
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	private Uri notifyContentResolver(Uri uri, long rowId) {
		if (rowId > 0) {
		    Uri noteUri = ContentUris.withAppendedId(NoteColumns.CONTENT_URI, rowId);
		    getContext().getContentResolver().notifyChange(noteUri, null);
		    return noteUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(EventItemProvider.AUTHORITY, SQLStatements.EVENT_DATABASE_TABLE_NAME, EVENT_ITEM );
		uriMatcher.addURI(NoteProvider.AUTHORITY, SQLStatements.NOTE_DATABASE_TABLE_NAME, NOTE_ITEM);
		uriMatcher.addURI(PictureProvider.AUTHORITY, SQLStatements.PICTURE_DATABASE_TABLE_NAME, PICTURE_ITEM);
		uriMatcher.addURI(EventProvider.AUTHORITY, SQLStatements.EVENT_DATABASE_TABLE_NAME, EVENT);
		uriMatcher.addURI(RecordingProvider.AUTHORITY, SQLStatements.RECORDINGS_DATABASE_TABLE_NAME, RECORDING_ITEM);
		uriMatcher.addURI(VideoProvider.AUTHORITY, SQLStatements.VIDEO_DATABASE_TABLE_NAME, VIDEO_ITEM);
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
