package com.fabula.android.timeline.sync;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.SimplePicture;

/**
 * 
 * Handler for Google App Engine synchronization.
 * 
 */
public class GAEHandler {
	
	
	/**
	 * 
	 * 
	 * @param object The object to send
	 * @param a Needs {@link Activity} to get the path of the content?
	 */
	public static void send(Object object, Activity a){
		Serializer serializer = new Persister();
		File sdCardDirectory = Environment.getExternalStorageDirectory();
		File result = new File(sdCardDirectory.getPath()+"/experiences.xml");

		try {
			serializer.write(object, result);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
		 FileInputStream fis = null;
		    BufferedInputStream bis = null;
		    DataInputStream dis = null;

		
		 try {
		      fis = new FileInputStream(result);

		      // Here BufferedInputStream is added for fast reading.
		      bis = new BufferedInputStream(fis);
		      dis = new DataInputStream(bis);

		      // dis.available() returns 0 if the file does not have more lines.
		      while (dis.available() != 0) {

		      // this statement reads the line from the file and print it to
		        // the console.
		        System.out.println(dis.readLine());
		      }

		      // dispose all the resources after using them.
		      fis.close();
		      bis.close();
		      dis.close();

		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		
		    //Saving xml to server
		    System.out.println("Lagrer XML på server: "+result.getPath());
//		    Uploader.uploadFile(result.getPath(), result.getPath());
		    Uploader.putToGAE(object, result.getPath());
		    
		    //Saving pictures to server
		    System.out.println("Lagrer bilder på server");
		    if(object instanceof Experiences){
		    	for (Experience ex : ((Experiences) object).getExperiences()) {
		    		for (Event event : ex.getEvents()) {
			    		for (EventItem eventI : event.getEventItems()) {
					    	if(eventI instanceof SimplePicture){
					    		Uploader.uploadFile(Utilities.getRealPathFromURI(((SimplePicture)eventI).getPictureUri(), a), ((SimplePicture)eventI).getPictureFilename());
					    	}
						}
					}
				}
		    	
		    }else if(object instanceof Event){
		    	for (EventItem eventI : ((Event)object).getEventItems()) {
			    	if(eventI instanceof SimplePicture){
			    		Uploader.uploadFile(Utilities.getRealPathFromURI(((SimplePicture)eventI).getPictureUri(), a), ((SimplePicture)eventI).getPictureFilename());
			    	}
				}
		    }
		  
	}
	

}
