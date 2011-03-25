package com.fabula.android.timeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.provider.MediaStore;

import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.SimpleAttachment;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.models.Zoom;

/**
 * Utility class with static constants and static utility methods.
 * 
 * @author andekr
 *
 */
public class Utilities {
	
	public static final String ALL_TIMELINES_DATABASE_NAME = "allTimelinesDatabase.db";
	
	public static final String INTENT_ACTION_NEW_TIMELINE = "com.fabula.android.timeline.intent.NEW_TIMELINE";
	public static final String INTENT_ACTION_ADD_TO_TIMELINE = "com.fabula.android.timeline.intent.ADD_TIMELINE";
	public static final String INTENT_ACTION_OPEN_MAP_VIEW_FROM_TIMELINE = "com.fabula.android.intent.OPEN_MAP_TIMELINE";
	public static final String INTENT_ACTION_OPEN_MAP_VIEW_FROM_DASHBOARD = "com.fabula.android.intent.OPEN_MAP_DASHBOARD";
	
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1;
    public static final int RECORD_AUDIO_ACTIVITY_REQUEST_CODE = 2;
    public static final int CREATE_NOTE_ACTIVITY_REQUEST_CODE = 3;
    public static final int ATTACHMENT_ACTIVITY_REQUEST_CODE = 4;
    public static final int MAP_VIEW_ACTIVITY_REQUEST_CODE = 14;
    public static final int ALL_EXPERIENCES_MAP_ACTIVITY_REQUEST_CODE = 15;
    
    public static final int SELECT_PICTURE = 5;
    public static final int SELECT_VIDEO = 6;
    public static final int CAPTURE_BARCODE = 0x0ba7c0de;
    public static final int BROWSE_FILES = 8;
    public static final int EDIT_NOTE = 9;
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final int DAY_MODE = 10;
    public static final int HOUR_MODE = 11;
    public static final int WEEK_MODE = 12;
    public static final int MONTH_MODE = 13;
    
    public static final String DATABASE_NAME = "timelineDB.db";
    public static final int DATABASE_VERSION = 1; //var 24
	public static final int USER_GROUP_DATABASE_VERSION = 1;
	
    public static final String DATABASENAME_REQUEST = "DATABASENAME";
    public static final String EXPERIENCEID_REQUEST = "EXPERIENCEID";
    public static final String EXPERIENCECREATOR_REQUEST = "EXPERIENCECREATOR";
    public static final String SHARED_REQUEST = "SHAREDEXPERIENCE";
    public static final String SHARED_WITH_REQUEST = "SHAREDWITH";

	public static final String USER_GROUP_DATABASE_NAME = "user_group_database.db";

	public static final String GOOGLE_APP_ENGINE_URL = "reflectapp.appspot.com";
    
    
    public static float HOUR_IN_MILLIS = 3600000;
    public static float DAY_IN_MILLIS = 86400000;
    public static float WEEK_IN_MILLIS = DAY_IN_MILLIS*7;
    
    public static int EVENT_TAG_KEY = 100;
    public static int ACTIVITY_TAG_KEY = 101;
    
    final public static int SHARED_FALSE = 0;
    final public static int SHARED_TRUE = 1;
    final public static int SHARED_ALL = 2;
    
    
    static File sdCardDirectory = Environment.getExternalStorageDirectory();
    public static String IMAGE_STORAGE_FILEPATH = sdCardDirectory.getPath()+"/data/com.fabula.android.timeline/images/";
    
	public static int getImageIcon(Event ex){
		
		if(ex.getEventItems().size()==1){
			if(ex.getEventItems().get(0) instanceof SimplePicture){
				return R.drawable.ic_menu_camera;
			}else if(ex.getEventItems().get(0) instanceof SimpleRecording){
				return R.drawable.ic_menu_audio;
			}else if(ex.getEventItems().get(0) instanceof SimpleVideo){
				return R.drawable.ic_menu_video;
			}else if(ex.getEventItems().get(0) instanceof SimpleAttachment){
				return R.drawable.ic_menu_attachment;
			}else if(ex.getEventItems().get(0) instanceof SimpleNote){
				return R.drawable.ic_menu_note;
			}
		}
		return R.drawable.ic_menu_archive;
	}
	
	public static int getMapImageIcon(Event ex) {
		
		if(ex.getEventItems().size()==1){
			if(ex.getEventItems().get(0) instanceof SimplePicture){
				return R.drawable.mapicon_photo;
			}else if(ex.getEventItems().get(0) instanceof SimpleRecording){
				return R.drawable.mapicon_audio;
			}else if(ex.getEventItems().get(0) instanceof SimpleVideo){
				return R.drawable.mapicon_video;
			}else if(ex.getEventItems().get(0) instanceof SimpleAttachment){
				return R.drawable.mapicon_note;
			}else if(ex.getEventItems().get(0) instanceof SimpleNote){
				return R.drawable.mapicon_note;
			}
		}
		return R.drawable.mapicon_note;
	}
	
	
	//DATEUTILITIES
	
	
	public static Zoom convertTimeScopeInMillisToZoomType(Date[] dates){
		System.out.println("Date 1 "+dates[0]);
		System.out.println("Date 2 "+dates[1]);
		if(isSameHour(dates[0], dates[1]))
			return Zoom.HOUR;
		else if(isSameDay(dates[0], dates[1]))
			return Zoom.DAY;
		else if(isSameWeek(dates[0], dates[1]))
			return Zoom.WEEK;
		else
			return Zoom.MONTH;
	}
	
	  public static boolean isSameHour(Date date1, Date date2) {
		  Calendar cal1 = Calendar.getInstance();
		  Calendar cal2 = Calendar.getInstance();
		  cal1.setTime(date1);
		  cal2.setTime(date2);
		  boolean sameHour = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
		                    cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY);
		  return sameHour;
	  }
	
	  public static boolean isSameDay(Date date1, Date date2) {
		  Calendar cal1 = Calendar.getInstance();
		  Calendar cal2 = Calendar.getInstance();
		  cal1.setTime(date1);
		  cal2.setTime(date2);
		  boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		  return sameDay;
	  }
	  
	  public static boolean isSameWeek(Date date1, Date date2) {
		  Calendar cal1 = Calendar.getInstance();
		  Calendar cal2 = Calendar.getInstance();
		  cal1.setTime(date1);
		  cal2.setTime(date2);
		  boolean sameWeek = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                    cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
		  return sameWeek;
	  }
	  
	  public static boolean isSameMonth(Date date1, Date date2) {
		  Calendar cal1 = Calendar.getInstance();
		  Calendar cal2 = Calendar.getInstance();
		  cal1.setTime(date1);
		  cal2.setTime(date2);
		  boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		  return sameDay;
	  }
	
	public static int convertZoomTypeAndDateToZoomValue(Zoom zoom, Date date){
		switch (zoom.getType()) {
		case HOUR_MODE:
			return date.getHours();
		case DAY_MODE:
			return date.getDate();
		case WEEK_MODE:
			return getWeekNumberOfDate(date);
		case MONTH_MODE:
			return getMonthNumberOfDate(date);
		default:
			return -1;
		}
		
	}
	
	public static Date getFirstDayOfWeek(Date dateInWeek){
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"), Locale.GERMANY);
		cal1.setTime(dateInWeek);
		int week = cal1.get(Calendar.WEEK_OF_YEAR);
		int year = cal1.get(Calendar.YEAR);
		cal1.clear();
		cal1.set(Calendar.WEEK_OF_YEAR, week);
		cal1.set(Calendar.YEAR, year);
//		cal1.add(Calendar.DATE, 1);//To make the week start on monday. Not needed when locale is set as above?
		
		return cal1.getTime();
	}
	
	public static Date getFirstDayOfMonth(Date dateInMonth){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(dateInMonth);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		
		return cal1.getTime();
	}
	
	public static Date getLastDayOfMonth(Date dateInMonth) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateInMonth);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.clear();
		calendar.set(Calendar.DATE, lastDate);	

		return calendar.getTime(); 

	  }
	
	public static int getWeekNumberOfDate(Date date){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		
		return cal1.get(Calendar.WEEK_OF_YEAR);
	}
	
	public static int getWeekOfDate(Date date){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		
		return cal1.get(Calendar.WEEK_OF_YEAR);
	}
	
	 /**
	   * Get default locale name of this day ("Monday", "Tuesday", etc.)
	   * Based on a zeroindexed array.
	   *
	   * @return  Name of day.
	   */
	public static String getDayName(int dayInWeek){
		 
		    switch (dayInWeek) {
		      case 0   : return "Monday";
		      case 1   : return "Tuesday";
		      case 2   : return "Wednesday";
		      case 3   : return "Thursday";
		      case 4   : return "Friday";
		      case 5   : return "Saturday";
		      case 6   : return "Sunday";
		      default :
		        assert false : "Invalid day of week: " + dayInWeek;
		    }

		    // This will never happen
		    return null;
	}


	public static int getMonthNumberOfDate(Date date) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		
		return cal1.get(Calendar.MONTH);
	}
	
	public static Date convertGridPositionToDate(int position, Date dateInMonth){
		int firstdayofmonth = getFirstDayOfMonth(dateInMonth).getDay();
		int firstSlotOfTheMonth = Zoom.MONTH.getColumns()+(firstdayofmonth-1);
		
		if(position<firstSlotOfTheMonth)
			return null;
		
		
		
		int dateNumberOnPosition = position-(firstSlotOfTheMonth-1);
		System.out.println("Date: "+dateNumberOnPosition+" on position"+(position));
		
		Calendar dateOnPosition = Calendar.getInstance();
		dateOnPosition.setTime(getFirstDayOfMonth(dateInMonth));
		dateOnPosition.set(Calendar.DATE, dateNumberOnPosition);
		
		if(dateNumberOnPosition<=getLastDayOfMonth(dateInMonth).getDate())
			return dateOnPosition.getTime();
		else
			return null;
	}


	public static Date adjustDate(Zoom zoom, Date zoomDate, int moveDirection) {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(zoomDate);
		 System.out.println("Before adjusting "+zoom+" "+getMonthNumberOfDate(zoomDate)+" "+moveDirection);
		switch (zoom.getType()) {
		case HOUR_MODE:
			cal.add(Calendar.HOUR_OF_DAY, moveDirection);
			System.out.println("Adjusting "+zoom+" "+cal.getTime()+" "+moveDirection);
			return cal.getTime();
		case DAY_MODE:
			cal.add(Calendar.DATE, moveDirection);
			return cal.getTime();
		case WEEK_MODE:
			cal.add(Calendar.WEEK_OF_YEAR, moveDirection);
			return cal.getTime();
		case MONTH_MODE:
			cal.add(Calendar.MONTH, moveDirection);
			System.out.println("After adjusting "+zoom+" "+getMonthNumberOfDate(cal.getTime()));
			return cal.getTime();
		default:
			return null;
		}
	}
	
	public static Account getUserAccount(Context c){
		 AccountManager manager = AccountManager.get(c); 
		 Account[] accounts = manager.getAccountsByType("com.google"); 

		    for (Account account : accounts) {
		    	return account;
		    }
		    
			return null;
	}
		
	public static String getExtension(String filename){
		int dot = filename.lastIndexOf(".");
		return filename.substring(dot);
	}
	
	public static String getRealPathFromURI(Uri contentUri, Activity a) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = a.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	public static void copyFile(String fromFile, String toPath, String toFilename) {
		
		System.out.println("COPY!");
		if(Environment.getExternalStorageState().equals("mounted")) {
			File sdCardDirectory = Environment.getExternalStorageDirectory();

				try {
					if(sdCardDirectory.canWrite()) {
						
						
						File destinationDirectory = new File(toPath);
						File sourceFile = new File(fromFile);
						File destinationFile = new File(destinationDirectory, toFilename);
						
						if(!destinationDirectory.exists()) {
							destinationDirectory.mkdirs();
						}
						
						FileChannel source = new FileInputStream(sourceFile).getChannel();
						FileChannel destination = new FileOutputStream(destinationFile).getChannel();
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
	
    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
	public static boolean isConnectedToInternet(Context c){
		ConnectivityManager connec =  (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
           
		return (connec.getNetworkInfo(0).isConnectedOrConnecting() ||  connec.getNetworkInfo(1).isConnectedOrConnecting())? true : false;
		 
	}

	
}
