package com.fabula.android.timeline.providers;

import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.SimpleVideo.VideoColumns;

public class VideoProvider extends BaseContentProvider {
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.fabula.android.timeline.providers.videoprovider");
	
	public static final String AUTHORITY = "com.fabula.android.timeline.providers.videoprovider";

	private static HashMap<String, String> videoColumnMapping;
	

	@Override
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(SQLStatements.VIDEO_DATABASE_TABLE_NAME);
		
		queryBuilder.setProjectionMap(videoColumnMapping);
		
		Cursor cursorOnRetriewedRows = queryBuilder.query(
				super.getDatabase(), 
				columns, 
				where, 
				whereArgs, 
				null, 
				null, 
				null);
		
		cursorOnRetriewedRows.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursorOnRetriewedRows;
	}

	static {
		videoColumnMapping = new HashMap<String, String>();
		videoColumnMapping.put(VideoColumns._ID, VideoColumns._ID);
		videoColumnMapping.put(VideoColumns.FILE_PATH, VideoColumns.FILE_PATH);
		videoColumnMapping.put(VideoColumns.CREATED_DATE, VideoColumns.CREATED_DATE);
		videoColumnMapping.put(VideoColumns.DESCRIPTION, VideoColumns.CREATED_DATE);
		videoColumnMapping.put(EventItemsColumns.USERNAME, EventItemsColumns.USERNAME);
	}
}
