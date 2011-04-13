package com.fabula.android.timeline.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fabula.android.timeline.utilities.Constants;

public class UserGroupDatabaseHelper extends SQLiteOpenHelper {

	
	private static SQLiteDatabase userGroupDatabase;
	private String databaseName;
	
	public UserGroupDatabaseHelper(Context context, String databaseName) {
		super(context, databaseName, null, Constants.USER_GROUP_DATABASE_VERSION);
		
		this.databaseName = databaseName;
		userGroupDatabase = getWritableDatabase();
	}
	
	public static SQLiteDatabase getUserDatabase() {
		if(userGroupDatabase == null) {
			Log.i("DATABASE PROBLEM", "THE DATABASE DONT EXIST!");
		}
		return userGroupDatabase;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQLStatements.USER_DATABASE_CREATE);
		db.execSQL(SQLStatements.USER_GROUP_DATABASE_CREATE);
		db.execSQL(SQLStatements.GROUP_DATABASE_CREATE);
		DatabaseHelper.backupDBToSDcard(db, this.databaseName);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.USER_DATABASE_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.GROUP_DATABASE_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.USER_GROUP_DATABASE_CREATE);
		onCreate(db);
	}

}
