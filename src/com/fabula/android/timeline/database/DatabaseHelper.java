package com.fabula.android.timeline.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.fabula.android.timeline.utilities.Constants;

/**
 * 
 * Database Helper that handles opening, creation of tables, updating tables and backup of database;
 * 
 * @see SQLStatements
 * @author andekr
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static SQLiteDatabase timelineDatabase;
	private String databaseName;

	public DatabaseHelper(Context context, String databaseName) {
		super(context, (databaseName.trim().endsWith(".db"))? databaseName : databaseName+".db", null, Constants.DATABASE_VERSION);
		if(!databaseName.trim().endsWith(".db"))
			databaseName = databaseName+".db";
		this.databaseName = databaseName;
//		File sdCardDirectory = Environment.getExternalStorageDirectory();
	
		timelineDatabase = this.getWritableDatabase();
	}	
	
	public static SQLiteDatabase getCurrentTimelineDatabase() {
		if(timelineDatabase == null) {
			Log.i("DATABASE PROBLEM", "THE DATABASE DONT EXIST!");
		}
		return timelineDatabase;
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQLStatements.EVENT_DATABASE_CREATE);
		db.execSQL(SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_CREATE);
		db.execSQL(SQLStatements.NOTES_DATABASE_CREATE);
		db.execSQL(SQLStatements.PICTURE_DATABASE_CREATE);
		db.execSQL(SQLStatements.RECORDING_DATABASE_CREATE);	
		db.execSQL(SQLStatements.VIDEO_DATABASE_CREATE);
		db.execSQL(SQLStatements.EMOTIONS_DATABASE_CREATE);
		
		backupDBToSDcard(db, this.databaseName);
	}
	
	public static void backupDBToSDcard(SQLiteDatabase db, String databaseName) {
		
		System.out.println("BACKUP!");
		if(Environment.getExternalStorageState().equals("mounted")) {
			File sdCardDirectory = Environment.getExternalStorageDirectory();

				try {
					if(sdCardDirectory.canWrite()) {
						
						
						File backupDirectory = new File(sdCardDirectory.getPath()+"/data/com.fabula.android.timeline/databaseBackup/");
						File currentDBDirectory = new File(db.getPath());
						File backUpDB = new File(backupDirectory, databaseName);
						
						if(!backupDirectory.exists()) {
							backupDirectory.mkdirs();
						}
						
						FileChannel source = new FileInputStream(currentDBDirectory).getChannel();
						FileChannel destination = new FileOutputStream(backUpDB).getChannel();
						destination.transferFrom(source, 0, source.size());
						source.close();
						destination.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.EVENT_DATABASE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.EVENT_TO_EVENT_ITEM_DATABASE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.NOTE_DATABASE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.PICTURE_DATABASE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.RECORDINGS_DATABASE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.VIDEO_DATABASE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLStatements.EMOTIONS_DATABASE_TABLE_NAME);
		
        onCreate(db);
		
	}
	
	
}
