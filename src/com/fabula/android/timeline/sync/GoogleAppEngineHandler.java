package com.fabula.android.timeline.sync;

import java.util.List;

import android.util.Log;

import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Handler for Google App Engine synchronization.
 * 
 * All actions to the Google App Engine is routed through this method.
 * 
 * 
 */
public class GoogleAppEngineHandler {
	private static final String TAG = "Google App Engine Handler";
	

	//ADDERS
	/**
	 * Sends an entire object to persist on server
	 * 
	 * @param object The object to send. Experiences, experience or event
	 */
	public static void persistTimelineObject(Object object){
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(BaseEvent.class, new Serializers.EventSerializer());
		gsonB.registerTypeAdapter(Event.class, new Serializers.EventSerializer());
		gsonB.registerTypeAdapter(Experience.class, new Serializers.ExperienceSerializer());
		gsonB.registerTypeAdapter(Experiences.class, new Serializers.ExperiencesSerializer());
		
		Gson gson = gsonB.create();
		String jsonString ="";
 
		try {
			jsonString = gson.toJson(object, object.getClass());
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
		    Log.i(TAG, "Saving TimelineObject-JSON to Google App Engine "+jsonString);
		    ServerUploader.putToGAE(object, jsonString);
		    
		    Log.i(TAG, "Saving files on server");
		    storeFilesOnServer(object);
		  
	}
	
	public static void addGroupToServer(Group groupToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(groupToAdd, Group.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
	    System.out.println();
	    Log.i(TAG, "Saving group-JSON on Google App Engine: "+jsonString);
	    ServerUploader.putGroupToGAE(jsonString);
	    
	}
	

	public static void addUserToServer(User userToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(userToAdd, User.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
		Log.i(TAG, "Saving user-JSON on Google App Engine: "+jsonString);
	    ServerUploader.putUserToGAE(jsonString);
	}
	
	public static void addUserToGroupOnServer(Group groupToGetNewMember, User userToAddToGroup) {
		Log.i(TAG,"Adding "+ userToAddToGroup +"  to "+groupToGetNewMember.getName()+" on Google App Engine");
		ServerUploader.putUserToGroupToGAE(groupToGetNewMember, userToAddToGroup);
	}
	
	
	//REMOVERS
	public static void removeUserFromGroupOnServer(Group groupToRemoveMember, User userToRemoveFromGroup) {
		ServerDeleter.deleteUserFromGroupToGAE(groupToRemoveMember, userToRemoveFromGroup);
	}
	
	public static void removeGroupFromDatabase(Group selectedGroup) {
		ServerDeleter.deleteUserFromGroupToGAE(selectedGroup);
	}
	
	//GETTERS
	
	public static double[] getAverageMoodForExperience(Experience experience){
		return ServerDownloader.getAverageMoodForExperience(experience);
	}
	
	public static Experiences getAllSharedExperiences(User user){
		return ServerDownloader.getAllSharedExperiencesFromServer(user);
	}
	
	public static List<User> getUsers(){
		return ServerDownloader.getUsersFromServer().getUsers();
	}
	
	public static boolean IsUserRegistered(String username) {
		return ServerDownloader.IsUserRegistered(username);
	}
	

	
	//HELPERS
	
	//Saving pictures to server. 
	//TODO: Any better way to do this than the almighty nesting going on here?
	private static void storeFilesOnServer(Object object) {
		if(object instanceof Experiences){
			if(((Experiences) object).getExperiences()!=null){
		    	for (Experience ex : ((Experiences) object).getExperiences()) {
		    		if(((Experience) ex).getEvents()!=null){
			    		for (BaseEvent baseEvent : ex.getEvents()) {
			    			if(baseEvent instanceof Event){
			    				Event event = (Event)baseEvent;
			    			if(event.getEventItems()!=null && event.isShared()){
						    		for (EventItem eventI : event.getEventItems()) {
								    	if(eventI instanceof SimplePicture){
								    		ServerUploader.uploadFile(Constants.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureUrl(), 
								    				((SimplePicture)eventI).getPictureFilename());
								    	}else if(eventI instanceof SimpleVideo){
								    		ServerUploader.uploadFile(Constants.VIDEO_STORAGE_FILEPATH+((SimpleVideo)eventI).getVideoUrl(), 
								    				((SimpleVideo)eventI).getVideoFilename());
								    	}else if(eventI instanceof SimpleRecording){
								    		ServerUploader.uploadFile(Constants.RECORDING_STORAGE_FILEPATH+((SimpleRecording)eventI).getRecordingFilename(), 
								    				((SimpleRecording)eventI).getRecordingUrl());
								    	}
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
		    		ServerUploader.uploadFile(Constants.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), 
		    				((SimplePicture)eventI).getPictureFilename());
		    	}else if(eventI instanceof SimpleVideo){
		    		ServerUploader.uploadFile(Constants.VIDEO_STORAGE_FILEPATH+((SimpleVideo)eventI).getVideoFilename(), 
		    				((SimpleVideo)eventI).getVideoFilename());
		    	}else if(eventI instanceof SimpleRecording){
		    		ServerUploader.uploadFile(Constants.RECORDING_STORAGE_FILEPATH+((SimpleRecording)eventI).getRecordingFilename(), 
		    				((SimpleRecording)eventI).getRecordingFilename());
		    	}
			}
		}
	}

}
