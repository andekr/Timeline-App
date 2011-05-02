package com.fabula.android.timeline.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.util.ByteArrayBuffer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.SimpleAttachment;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.Zoom;

/**
 * Utility class with static constants and static utility methods.
 * 
 * @author andekr
 *
 */
public class Utilities {
	
	
    public static final int DAY_MODE = 10;
    public static final int HOUR_MODE = 11;
    public static final int WEEK_MODE = 12;
    public static final int MONTH_MODE = 13;
	
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
	
	public static int getMapImageIcon(BaseEvent ex) {
		
		if(ex instanceof Event) {
			Event e= (Event)ex;
			if(e.getEventItems().size()==1){
				if(e.getEventItems().get(0) instanceof SimplePicture){
					return R.drawable.mapicon_photo;
				}else if(e.getEventItems().get(0) instanceof SimpleRecording){
					return R.drawable.mapicon_audio;
				}else if(e.getEventItems().get(0) instanceof SimpleVideo){
					return R.drawable.mapicon_video;
				}else if(e.getEventItems().get(0) instanceof SimpleAttachment){
					return R.drawable.mapicon_note;
				}else if(e.getEventItems().get(0) instanceof SimpleNote){
					return R.drawable.mapicon_note;
				}
			}
		}else {
			return R.drawable.ic_menu_emoticons;
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
		    
		    Account nonRegisteredAccount = new Account("test@timelineapp.no", "com.google");
			return nonRegisteredAccount;
	}
		
	public static String getExtension(String filename){
		int dot = filename.lastIndexOf(".");
		return filename.substring(dot);
	}
	
	public static String getFilenameFromURL(String url){
		int slashIndex = url.lastIndexOf('/');
		int dotIndex = url.lastIndexOf('.', slashIndex);
		if (dotIndex == -1)
		{
		  return url.substring(slashIndex + 1);
		}
		else
		{
		  return "";
		}
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
	
    public static File DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
                URL url = new URL(imageURL); //you can write here any link
                File file = new File(fileName);
                System.out.println("THE FILENAME IS "+fileName);
                if(!file.exists()){
	                long startTime = System.currentTimeMillis();
	                Log.d("ImageManager", "download begining");
	                Log.d("ImageManager", "download url:" + url);
	                Log.d("ImageManager", "downloaded file name:" + fileName);
	                /* Open a connection to that URL. */
	                URLConnection ucon = url.openConnection();
	
	                /*
	                 * Define InputStreams to read from the URLConnection.
	                 */
	                InputStream is = ucon.getInputStream();
	                BufferedInputStream bis = new BufferedInputStream(is);
	
	                /*
	                 * Read bytes to the Buffer until there is nothing more to read(-1).
	                 */
	                ByteArrayBuffer baf = new ByteArrayBuffer(50);
	                int current = 0;
	                while ((current = bis.read()) != -1) {
	                        baf.append((byte) current);
	                }
	
	                /* Convert the Bytes read to a String. */
	                FileOutputStream fos = new FileOutputStream(file);
	               
	                fos.write(baf.toByteArray());
	                fos.close();
	                Log.d("ImageManager", "download ready in"
	                                + ((System.currentTimeMillis() - startTime) / 1000)
	                                + " sec");
                }else{
                	 Log.d("ImageManager", "file exists!");
                }
                return file;

        } catch (IOException e) {
                Log.d("ImageManager", "Error: " + e);
                return null;
        }

    }

	
}
