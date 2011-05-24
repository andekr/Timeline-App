package com.fabula.android.timeline.database.providers;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.Experience.ExperienceColumns;

public class ExperienceProvider extends BaseContentProvider {
	
	public static final String AUTHORITY = "com.fabula.android.timeline.database.providers.ExperienceProvider";

	
    public static final Uri CONTENT_URI = 
        Uri.parse("content://"+ AUTHORITY + "/experiences");
      
	private static HashMap<String, String> experienceProjectionMap;
	
    static{
       experienceProjectionMap = new HashMap<String, String>();
       experienceProjectionMap.put(ExperienceColumns._ID, ExperienceColumns._ID);
       experienceProjectionMap.put(ExperienceColumns.EXPERIENCE_NAME, ExperienceColumns.EXPERIENCE_NAME);
       experienceProjectionMap.put(ExperienceColumns.EXPERIENCE_SHARED, ExperienceColumns.EXPERIENCE_SHARED);
       experienceProjectionMap.put(ExperienceColumns.EXPERIENCE_CREATOR, ExperienceColumns.EXPERIENCE_CREATOR);
       experienceProjectionMap.put(ExperienceColumns.EXPERIENCE_LAST_MODIFIED, ExperienceColumns.EXPERIENCE_LAST_MODIFIED);
       experienceProjectionMap.put(ExperienceColumns.EXPERIENCE_SHARED_WITH, ExperienceColumns.EXPERIENCE_SHARED_WITH);
    }
    
	//insert some element in the DB
	public Uri insert(Uri uri, ContentValues initialValues) { 

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        long rowId = super.getTimelinesDatabase().insertWithOnConflict(SQLStatements.TIMELINES_DATABASE_TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE); 
        
		if (rowId > 0) {
		    Uri exUri = ContentUris.withAppendedId(ExperienceColumns.CONTENT_URI, rowId);
		    getContext().getContentResolver().notifyChange(exUri, null);
		    return exUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	//get some element in the db
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(SQLStatements.TIMELINES_DATABASE_TABLE_NAME);
		
		queryBuilder.setProjectionMap(experienceProjectionMap);
		
		Cursor cursorOnRetriewedRows = queryBuilder.query(
				super.getTimelinesDatabase(), 
				columns, 
				where, 
				whereArgs, 
				null, 
				null, 
				null);
		
		cursorOnRetriewedRows.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursorOnRetriewedRows;
	}

	//delete some element in the DB
	public int delete(Uri uri, String id, String[] whereArgs) {
		return super.getTimelinesDatabase().delete(SQLStatements.TIMELINES_DATABASE_TABLE_NAME, ExperienceColumns._ID + "='"+id+"'", whereArgs);
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
	    count = super.getTimelinesDatabase().update(
	               SQLStatements.TIMELINES_DATABASE_TABLE_NAME, 
	               values,
	               selection, 
	               selectionArgs);
	    Log.d("Experience update", "antall endrede rader "+count);
	    
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	}
}
