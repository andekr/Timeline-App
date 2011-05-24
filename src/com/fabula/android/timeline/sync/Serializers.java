package com.fabula.android.timeline.sync;

import java.lang.reflect.Type;

import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.MoodEvent;
import com.google.myjson.Gson;
import com.google.myjson.JsonElement;
import com.google.myjson.JsonParser;
import com.google.myjson.JsonSerializationContext;
import com.google.myjson.JsonSerializer;

public class Serializers {
	
	/**
	 * Custom serializer for {@link Gson} to remove empty lists, which Google App Engine can't handle right.
	 * GSON treats empty lists with two brackets [], while Google App Engine inserts an empty object to the database
	 * if two brackets with empty content is received.
	 * This method substitutes empty lists with null, so they are not serialized. 
	 * Not very good coding, and not very modifiable. 
	 * 
	 * So, if new lists are introduced, one have to implement the empty check for this list as well.
	 */
	protected static class ExperienceSerializer implements JsonSerializer<Experience> {
		  public JsonElement serialize(Experience src, Type typeOfSrc, JsonSerializationContext context) {
					 if(src.getEvents().size()==0)
						 src.setEvents(null);
			 
			Gson gson = new Gson();
			JsonElement je = new JsonParser().parse(gson.toJson(src));
		    return je;
		  }
		}

	

	/**
	 * Custom serializer for {@link Gson} to remove empty lists, which Google App Engine can't handle right.
	 * GSON treats empty lists with two brackets [], while Google App Engine inserts an empty object to the database
	 * if two brackets with empty content is received.
	 * This method substitutes empty lists with null, so they are not serialized. 
	 * Not very good coding, and not very modifiable. 
	 * 
	 * So, if new lists are introduced, one have to implement the empty check for this list as well.
	 */
	protected static class ExperiencesSerializer implements JsonSerializer<Experiences> {
		  public JsonElement serialize(Experiences src, Type typeOfSrc, JsonSerializationContext context) {
			  if(src.getExperiences().size()==0)
					 src.setExperiences(null);
			  else{
				  for (Experience ex: src.getExperiences()) {
					 if(ex.getEvents().size()==0)
						 ex.setEvents(null);
					  else{
						  try {
							  for (BaseEvent baseEvent : ex.getEvents()) {
									if(baseEvent instanceof Event){
										removeEmptyListsFromEvent((Event)baseEvent);
									}
							}
						} 
						  catch (Exception e) {
							e.printStackTrace();
						}
						
					  }
				  }
			  }
			 
			Gson gson = new Gson();
			JsonElement je = new JsonParser().parse(gson.toJson(src));
		    return je;
		  }
		}
	
	/**
	 * 
	 * Serializer for Events, as we haven't found a way to make GSON serialize subclasses. 
	 * We are therefore converting subclasses of BaseEvent to BaseEvent that will be sent by JSON to server.
	 * 
	 * Yet again, this isn't very modifiable, as new subclasses of BaseEvent will have to have a converter
	 * in this method. 
	 * New properties in BaseEvent and subclasses will also have to be added to the converters.
	 * 
	 * @author andekr
	 *
	 */
	protected static class EventSerializer implements JsonSerializer<BaseEvent> {
		  public JsonElement serialize(BaseEvent baseEvent, Type typeOfSrc, JsonSerializationContext context) {
			 Gson gson = new Gson();
			  if(baseEvent instanceof Event){
				  return new JsonParser().parse(gson.toJson(removeEmptyListsFromEvent((Event)baseEvent)));
			}else if (baseEvent instanceof MoodEvent){
				return new JsonParser().parse(gson.toJson((MoodEvent)baseEvent));
			}else {
				 return new JsonParser().parse(gson.toJson(baseEvent));
			}
			 
		  }

	}
	
	
	/**
	 * Helper class to remove empty lists from Event
	 * 
	 * @param event the {@linkplain Event} to remove empty lists from
	 * @return the {@link Event} with removed empty lists 
	 */
	private static Event removeEmptyListsFromEvent(Event event) {
		if(event.getEmotionList().size()==0)
			event.setEmotionList(null);
		
		if(event.getEventItems().size()==0)
			event.setEventItems(null);
		
		
		
		return event;
	}

}
