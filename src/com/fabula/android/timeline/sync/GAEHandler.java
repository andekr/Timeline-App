package com.fabula.android.timeline.sync;

import java.lang.reflect.Type;

import android.app.Activity;
import android.util.Log;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
//		Serializer serializer = new Persister();
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(Experiences.class, new ExperiencesSerializer());
		
		Gson gson = gsonB.create();
//		File sdCardDirectory = Environment.getExternalStorageDirectory();
//		File result = new File(sdCardDirectory.getPath()+"/experiences.txt");
		String jsonString ="";

		try {
			jsonString = gson.toJson(object, object.getClass());
//			serializer.write(object, result);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
//		
//		 FileInputStream fis = null;
//		    BufferedInputStream bis = null;
//		    DataInputStream dis = null;
//
//		
//		 try {
//		      fis = new FileInputStream(result);
//
//		      // Here BufferedInputStream is added for fast reading.
//		      bis = new BufferedInputStream(fis);
//		      dis = new DataInputStream(bis);
//
//		      // dis.available() returns 0 if the file does not have more lines.
//		      while (dis.available() != 0) {
//
//		      // this statement reads the line from the file and print it to
//		        // the console.
//		        System.out.println(dis.readLine());
//		      }
//
//		      // dispose all the resources after using them.
//		      fis.close();
//		      bis.close();
//		      dis.close();
//
//		    } catch (FileNotFoundException e) {
//		      e.printStackTrace();
//		    } catch (IOException e) {
//		      e.printStackTrace();
//		    }
		
		    //Saving json to server
		    System.out.println("Lagrer JSON på Google App Engine: "+jsonString);
//		    Uploader.uploadFile(result.getPath(), result.getPath());
		    Uploader.putToGAE(object, jsonString);
		    
		    //Saving pictures to server
		    System.out.println("Lagrer bilder på server");
		    if(object instanceof Experiences){
		    	if(((Experiences) object).getExperiences()!=null){
			    	for (Experience ex : ((Experiences) object).getExperiences()) {
			    		if(((Experience) ex).getEvents()!=null){
				    		for (BaseEvent baseEvent : ex.getEvents()) {
				    			if(baseEvent.getEventItems()!=null){
						    		for (EventItem eventI : baseEvent.getEventItems()) {
								    	if(eventI instanceof SimplePicture){
								    		Uploader.uploadFile(Utilities.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), ((SimplePicture)eventI).getPictureFilename());
								    	}
									}
				    			}
							}
			    		}
					}
		    	}
		    	
		    }else if(object instanceof Event){
		    	for (EventItem eventI : ((Event)object).getEventItems()) {
			    	if(eventI instanceof SimplePicture){
			    		Uploader.uploadFile(Utilities.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), ((SimplePicture)eventI).getPictureFilename());
			    	}
				}
		    }
		  
	}
	
	public static void addGroupToServer(Group groupToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(groupToAdd, Group.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
	    System.out.println("Lagrer JSON på Google App Engine: "+jsonString);
	    Uploader.putGroupToGAE(jsonString);
	    
//	    for (User user : groupToAdd.getMembers()) {
//			addUserToGroupOnServer(groupToAdd, user);
//		}
	}
	

	public static void addUserToServer(User userToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(userToAdd, User.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
	    System.out.println("Lagrer JSON på Google App Engine: "+jsonString);
	    Uploader.putUserToGAE(jsonString);
	}
	
	public static void addUserToGroupOnServer(Group groupToGetNewMember, User userToAddToGroup) {
		System.out.println("Legger til "+ userToAddToGroup +"  til "+groupToGetNewMember.getName()+" på Google App Engine");
		Uploader.putUserToGroupToGAE(groupToGetNewMember, userToAddToGroup);
	}
	
	public static void removeUserFromGroupOnServer(Group groupToRemoveMember, User userToRemoveFromGroup) {
		Uploader.deleteUserFromGroupToGAE(groupToRemoveMember, userToRemoveFromGroup);
	}
	
	public static void removeGroupFromDatabase(Group selectedGroup) {
		Uploader.deleteUserFromGroupToGAE(selectedGroup);
	}
	
	
	//Custom serializer to remove empty lists, which Google App Engine can't handle right. Not good coding!
	
	private static class ExperiencesSerializer implements JsonSerializer<Experiences> {
		  public JsonElement serialize(Experiences src, Type typeOfSrc, JsonSerializationContext context) {
			  if(src.getExperiences().size()==0)
					 src.setExperiences(null);
			  else{
				  for (Experience ex: src.getExperiences()) {
					 if(ex.getEvents().size()==0)
						 ex.setEvents(null);
					  else{
						  for (BaseEvent baseEvent : ex.getEvents()) {
								if(baseEvent instanceof Event){
									BaseEvent bEvent = new BaseEvent(baseEvent.getId(), baseEvent.getExperienceid(), 
											baseEvent.getDatetime(), baseEvent.getLocation(), baseEvent.getUser());
									bEvent.setClassName(baseEvent.getClassName());
									if(((Event)baseEvent).getEmotionList().size()==0)
										bEvent.setEmotionList(null);
									else
										bEvent.setEmotionList(baseEvent.getEmotionList());
									
									if(((Event)baseEvent).getEventItems().size()==0)
										bEvent.setEventItems(null);
									else
										bEvent.setEventItems(((Event)baseEvent).getEventItems());
									
									ex.getEvents().add(bEvent);
									ex.getEvents().remove(baseEvent);
								}else if (baseEvent instanceof MoodEvent){
									baseEvent = (BaseEvent)baseEvent;
									baseEvent.setClassName(((MoodEvent)baseEvent).getClassName());
									baseEvent.setMood(((MoodEvent)baseEvent).getMood());
								}
						}
					  }
				  }
			  }
			 
			Gson gson = new Gson();
		    return new JsonParser().parse(gson.toJson(src));
		  }
		}
	
	
	




}
