package com.fabula.android.timeline.database.providers;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.SimpleNote.NoteColumns;

public class NoteProvider extends BaseContentProvider {
	public static final String TAG = "NoteProvider";

    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;
    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sNotesProjectionMap;
    
    public static final String AUTHORITY = "com.fabula.android.timeline.providers.NoteProvider";

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(SQLStatements.NOTE_DATABASE_TABLE_NAME);
		
		switch (sUriMatcher.match(uri)) {
		//select all notes
		case NOTES:
			queryBuilder.setProjectionMap(sNotesProjectionMap);
			break;
			
		//select notes by ID
		case NOTE_ID: 
			queryBuilder.setProjectionMap(sNotesProjectionMap);
			queryBuilder.appendWhere(NoteColumns._ID + "=" + uri.getPathSegments().get(1));
			break;
			
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
        // Get the database and run the query

        Cursor c = queryBuilder.query(super.getDatabase(), projection, selection, selectionArgs, null, null, null);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}
	
	@Override
	public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case NOTES:
            return NoteColumns.CONTENT_TYPE;

        case NOTE_ID:
            return NoteColumns.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		
	        int count;
	        switch (sUriMatcher.match(uri)) {
	        case NOTES:
	            count = super.getDatabase().update(SQLStatements.NOTE_DATABASE_TABLE_NAME, values, where, whereArgs);
	            break;

	        case NOTE_ID:
	            String noteId = uri.getPathSegments().get(1);
	            count = super.getDatabase().update(SQLStatements.NOTE_DATABASE_TABLE_NAME, values, NoteColumns._ID + "=" + noteId
	                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
	            break;

	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	}
	  
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SQLStatements.NOTE_DATABASE_TABLE_NAME, NOTES);
        sUriMatcher.addURI(AUTHORITY, SQLStatements.NOTE_DATABASE_TABLE_NAME+"/#", NOTE_ID);
        
        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put(NoteColumns._ID, NoteColumns._ID);
        sNotesProjectionMap.put(NoteColumns.TITLE, NoteColumns.TITLE);
        sNotesProjectionMap.put(NoteColumns.NOTE, NoteColumns.NOTE);
        sNotesProjectionMap.put(NoteColumns.CREATED_DATE, NoteColumns.CREATED_DATE);
        sNotesProjectionMap.put(NoteColumns.MODIFIED_DATE, NoteColumns.MODIFIED_DATE);
        sNotesProjectionMap.put(EventItemsColumns.USERNAME, EventItemsColumns.USERNAME);
    }
}
