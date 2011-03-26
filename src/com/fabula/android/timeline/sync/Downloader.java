package com.fabula.android.timeline.sync;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.location.Location;
import android.util.Log;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.Groups;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.models.Users;
import com.fabula.android.timeline.models.MoodEvent.MoodEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Downloader {
	
	public static Experiences getAllSharedExperiencesFromServer(User user){
		
		try {
			Log.i("DOWNLOADER", "Json Parser started.. Getting all Experiences");
			GsonBuilder gsonB = new GsonBuilder();
			gsonB.registerTypeAdapter(EventItem.class, new EventItemDeserializer());
			gsonB.serializeNulls();
			
			Gson gson = gsonB.create();
			
			Reader r = new InputStreamReader(getJSONData("/rest/experiences/"+user.getUserName()+"/")); 
			Experiences experiences = gson.fromJson(r, Experiences.class);
			List<BaseEvent> baseEvents = new ArrayList<BaseEvent>();
			for (Experience experience : experiences.getExperiences()) {
				for (BaseEvent be : experience.getEvents()) {
					
					 Location location = new Location("");
					 location.setLatitude(be.getLatitude());
					 location.setLongitude(be.getLongitude());
					if(be.getClassName().equals(Event.class.getSimpleName())){
						BaseEvent bEvent = new BaseEvent(be.getId(), be.getExperienceid(), 
								new Date(be.getDatetimemillis()),location, be.getUser());
						bEvent.setEmotionList(be.getEmotionList());
						bEvent.setEventItems(be.getEventItems());
						bEvent.setShared(be.isShared());
						baseEvents.add(bEvent);
					}else if(be.getClassName().equals(MoodEvent.class.getSimpleName())){
						MoodEvent me = new MoodEvent(be.getId(), be.getExperienceid(), 
								new Date(be.getDatetimemillis()), location, MoodEnum.getType(be.getMoodInt()) , be.getUser());
						me.setShared(true);
						me.setAverage(be.isAverage());
						baseEvents.add(me);
					}
				}
				experience.setEvents(baseEvents);
			}
			Log.i("DOWNLOADER", "Fetched "+experiences.getExperiences().size()+" experiences");
			return experiences;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	 public static boolean IsUserRegistered(String username){
		 boolean registered = true; 
		 InputStream is = getJSONData("/rest/user/"+username+"/");
		 JSONObject json = null;
		 if(is!=null){
			 String response = Utilities.convertStreamToString(is);
			 
			 try {
				json = new JSONObject(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 
		 
		 registered = json==null ? false : true;
		 
		 return registered;
	 }
	 
	 public static Users getUsersFromServer(){
				try {
					Log.i("DOWNLOADER", "Json Parser started.. Getting all users");
					Gson gson = new Gson();
					
					Reader r = new InputStreamReader(getJSONData("/rest/users/")); 
					Users users = gson.fromJson(r, Users.class);
					Log.i("DOWNLOADER", "Fetched "+users.getUsers().size()+" users");
					return users;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
	 }
	 
	 public static Groups getGroupsFromServer(User user){
			try {
				Log.i("DOWNLOADER", "Json Parser started.. Getting all groups for the user "+user.getUserName());
				Gson gson = new Gson();
				
				Reader r = new InputStreamReader(getJSONData("/rest/groups/"+user.getUserName()+"/")); 
				Groups groups = null;
				groups = gson.fromJson(r, Groups.class);
				try {
					Log.i("DOWNLOADER", "Fetched "+groups.getGroups().size()+" groups");
				} catch (NullPointerException e) {
					Log.i("DOWNLOADER", "No groups on server!");
				}
				return groups;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	 }
	 

	public static int getAverageMoodForExperience(Experience experience) {
		 InputStream is = getJSONData("/rest/mood/experience/id/"+experience.getId()+"/");
		 int average = 0;
		 if(is!=null){
			 average = Integer.valueOf(Utilities.convertStreamToString(is));
		 }
		return average;
	}
	
	/**
	 * Method that fetches JSON-data from a URL
	 * 
	 * @param url Address to service that provide JSON
	 * @return data as {@link InputStream}
	 */
	public static InputStream getJSONData(String url){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        InputStream data = null;
        try {
            HttpHost targetHost = new HttpHost(Utilities.GOOGLE_APP_ENGINE_URL, 80, "http");
            HttpGet httpGet = new HttpGet(url);
         // Make sure the server knows what kind of a response we will accept
    		httpGet.addHeader("Accept", "application/json");
    		// Also be sure to tell the server what kind of content we are sending
    		httpGet.addHeader("Content-Type", "application/json");
            
            HttpResponse response = httpClient.execute(targetHost, httpGet);
            data = null; 
            try {
            	 data = response.getEntity().getContent();
			} catch (NullPointerException e) {
			}
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return data;
    }
	

	public static class EventItemDeserializer implements JsonDeserializer<EventItem> {
		  public EventItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		      throws JsonParseException {
			  
			 String className = json.getAsJsonObject().get("className").getAsString();
			 String id = json.getAsJsonObject().get("id").getAsString();
			 Account creator = new Account(json.getAsJsonObject().get("creator").getAsString(), "com.google");
			 EventItem ei;
			if(className.equals("SimplePicture")){
				 String filename = json.getAsJsonObject().get("pictureFilename").getAsString();
				ei = new SimplePicture(id, creator, filename);
				  return ei;
			}else if(className.equals("SimpleNote")){
				String noteTitle = json.getAsJsonObject().get("noteTitle").getAsString();
				String noteText = json.getAsJsonObject().get("noteText").getAsString();
				ei = new SimpleNote(id, noteTitle, noteText, creator);
				  return ei;
			} else 
				  return null;
			  
		  }
		}

//	public static class EventDeserializer implements JsonDeserializer<BaseEvent> {
//		  public BaseEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//		      throws JsonParseException {
//			  
//			 String className = json.getAsJsonObject().get("className").getAsString();
//			 String id = json.getAsJsonObject().get("id").getAsString();
//			 String exID = json.getAsJsonObject().get("experienceid").getAsString();
//			 double latitude = json.getAsJsonObject().get("latitude").getAsDouble();
//			 double longitude = json.getAsJsonObject().get("longitude").getAsDouble();
//			 Date dateTime = new Date(json.getAsJsonObject().get("datetimemillis").getAsLong());
//			 Location location = new Location("");
//			 location.setLatitude(latitude);
//			 location.setLongitude(longitude);
//			 Account user = new Account(json.getAsJsonObject().get("creator").getAsString(), "com.google");
//			 json.getAsJsonObject().getAsJsonArray("datetimemillis");
//			 
//			 BaseEvent be;
//			if(className.equals("Event")){
//				be = new Event(id, exID, dateTime, location, user);
//				be.setEmotionList(emotionList)
//				be.setEventItems(eventItems)
//				  return ei;
//			}else if(className.equals("SimpleNote")){
//				String noteTitle = json.getAsJsonObject().get("noteTitle").getAsString();
//				String noteText = json.getAsJsonObject().get("noteText").getAsString();
//				ei = new SimpleNote(id, noteTitle, noteText, creator);
//				  return ei;
//			} else 
//				  return null;
//			  
//		  }
//		}


}