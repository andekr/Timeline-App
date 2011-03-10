package com.fabula.android.timeline.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fabula.android.timeline.Utilities;

/**
 * Database helper for the database of timelines.
 * 
 * @see SQLStatements
 * @see DatabaseHelper
 * @author andekr
 *
 */
public class TimelineDatabaseHelper extends SQLiteOpenHelper{
	
	private static SQLiteDatabase allTimelinesDatabase;
	private String databaseName;
	
	public TimelineDatabaseHelper(Context context, String databaseName){
		super(context, databaseName, null, Utilities.DATABASE_VERSION);
		this.databaseName = databaseName;
		
		allTimelinesDatabase = this.getWritableDatabase();
	}
	
	public static SQLiteDatabase getCurrentTimeLineDatabase() {
		if(allTimelinesDatabase == null) {
			Log.i("DATABASE PROBLEM", "THE DATABASE DONT EXIST!");
		}
		return allTimelinesDatabase;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQLStatements.TIMELINES_DATABASE_CREATE);
		DatabaseHelper.backupDBToSDcard(db, this.databaseName);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.TIMELINES_DATABASE_TABLE_NAME);
		onCreate(db);
		
	}

}
