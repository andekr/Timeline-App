package com.fabula.android.timeline.sync;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Date;
//
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.SimplePicture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

public class Downloader {
	
	public static Experiences getAllSharedExperiencesFromServer(){
		
		try {
			Log.i("DOWNLOADER", "Json Parser started.. Getting all Experiences");
			GsonBuilder gsonB = new GsonBuilder();
			gsonB.registerTypeAdapter(EventItem.class, new EventItemDeserializer());
//			gsonB.registerTypeAdapter(EventItem.class, new EventItemInstanceCreator());
			gsonB.serializeNulls();
			
			Gson gson = gsonB.create();
			
			Reader r = new InputStreamReader(getJSONData("/rest/experiences/")); 
			Experiences experiences = gson.fromJson(r, Experiences.class);
			Log.i("DOWNLOADER", "Fetched "+experiences.getExperiences().size()+" experiences");
			return experiences;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
	 * Method that fetches JSON-data from a URL
	 * 
	 * @param url Address to service that provide JSON
	 * @return data as {@link InputStream}
	 */
	public static InputStream getJSONData(String url){
        DefaultHttpClient httpClient = new DefaultHttpClient();
//        URI uri;
        InputStream data = null;
        try {
//            uri = new URI(url);
            HttpHost targetHost = new HttpHost("reflectapp.appspot.com", 80, "http");
            HttpGet httpGet = new HttpGet(url);
         // Make sure the server knows what kind of a response we will accept
    		httpGet.addHeader("Accept", "application/json");
    		// Also be sure to tell the server what kind of content we are sending
    		httpGet.addHeader("Content-Type", "application/json");
            
            HttpResponse response = httpClient.execute(targetHost, httpGet);
            data = response.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return data;
    }
	
//	public static class EventItemInstanceCreator implements InstanceCreator<EventItem> {
//		  public EventItem createInstance(Type type) {
//		    if (type.getClass().getSimpleName().equals(SimplePicture.class)) {
//				return new SimplePicture();
//			}else
//				return null;
//		  }
//		}
	
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
			  
//		    return new DateTime(json.getAsJsonPrimitive().getAsString());
		  }
		}

}
