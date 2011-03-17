package com.fabula.android.timeline.providers;

import java.util.HashMap;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.Group.GroupColumns;
import com.fabula.android.timeline.models.User.UserColumns;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class GroupProvider extends BaseContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://com.fabula.android.timeline.providers.groupprovider");
	public static final String AUTHORITY = "com.fabula.android.timeline.providers.groupprovider";
	
	private static HashMap<String, String> groupColumnsMapping;
	
	@Override
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(SQLStatements.GROUP_DATABASE_TABLE_NAME);
		queryBuilder.setProjectionMap(groupColumnsMapping);
		
		Cursor cursorOnRetriewedRows = queryBuilder.query(
				super.getUserDatabase(), 
				columns, 
				where, 
				whereArgs, 
				null, 
				null, 
				null);
		
		cursorOnRetriewedRows.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursorOnRetriewedRows;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		
		int count = 0;
		count = super.getUserDatabase().delete(SQLStatements.GROUP_DATABASE_TABLE_NAME, where, whereArgs);
		return count;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        long rowId = super.getUserDatabase().insert(SQLStatements.GROUP_DATABASE_TABLE_NAME, "", values);
        
		if (rowId > 0) {
		    Uri groupUri = ContentUris.withAppendedId(GroupColumns.CONTENT_URI, rowId);
		    getContext().getContentResolver().notifyChange(groupUri, null);
		    return groupUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	static {
		groupColumnsMapping = new HashMap<String, String>();
		groupColumnsMapping.put(GroupColumns._ID, GroupColumns._ID);
		groupColumnsMapping.put(GroupColumns.GROUP_NAME, GroupColumns.GROUP_NAME);
	}
}
