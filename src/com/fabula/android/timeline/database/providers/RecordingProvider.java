/*******************************************************************************
 * Copyright (c) 2011 Andreas Storlien and Anders Kristiansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Storlien and Anders Kristiansen - initial API and implementation
 ******************************************************************************/
package com.fabula.android.timeline.database.providers;

import java.util.HashMap;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.fabula.android.timeline.database.SQLStatements;
import com.fabula.android.timeline.models.EventItem.EventItemsColumns;
import com.fabula.android.timeline.models.SimpleRecording.RecordingColumns;

public class RecordingProvider extends BaseContentProvider {
	public static String TAG = "RecordingProvider";
	
	private static HashMap<String, String> recordingColumnMapping;
	private static final UriMatcher recordingUriMatcher;
	
	public static final String AUTHORITY = "com.fabula.android.timeline.database.providers.recordingprovider";

	private static final int RECORDINGS = 1;
	private static final int RECORDINGS_ID = 2;  
	
	@Override
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(SQLStatements.RECORDINGS_DATABASE_TABLE_NAME);
		
		switch (recordingUriMatcher.match(uri)) {
		case RECORDINGS:
			queryBuilder.setProjectionMap(recordingColumnMapping);
			break;
		
		case RECORDINGS_ID:
			queryBuilder.setProjectionMap(recordingColumnMapping);
			queryBuilder.appendWhere(RecordingColumns._ID+"="+uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
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

	@Override
	public String getType(Uri uri) {
		switch (recordingUriMatcher.match(uri)) {
		case RECORDINGS:
			return RecordingColumns.CONTENT_TYPE;
		case RECORDINGS_ID:
			return RecordingColumns.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	static{
		recordingUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		recordingUriMatcher.addURI(AUTHORITY, SQLStatements.RECORDINGS_DATABASE_TABLE_NAME, RECORDINGS);
		recordingUriMatcher.addURI(AUTHORITY, SQLStatements.RECORDINGS_DATABASE_TABLE_NAME+"/#", RECORDINGS_ID);
		
		recordingColumnMapping = new HashMap<String, String>();
		recordingColumnMapping.put(RecordingColumns._ID, RecordingColumns._ID);
		recordingColumnMapping.put(RecordingColumns.DESCRIPTION, RecordingColumns.DESCRIPTION);
		recordingColumnMapping.put(RecordingColumns.FILE_URI, RecordingColumns.FILE_URI);
		recordingColumnMapping.put(RecordingColumns.CREATED_DATE, RecordingColumns.CREATED_DATE);
		recordingColumnMapping.put(RecordingColumns.FILENAME, RecordingColumns.FILENAME);
		recordingColumnMapping.put(EventItemsColumns.USERNAME, EventItemsColumns.USERNAME);
	}
}
