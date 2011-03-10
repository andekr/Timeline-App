package com.fabula.android.timeline.contentmanagers;

import android.content.Context;

import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.providers.EmotionsProvider;
import com.fabula.android.timeline.providers.EventProvider;
import com.fabula.android.timeline.providers.ExperienceProvider;

/**
 * Helper class to delete models from SQLite database using content providers.
 * 
 * @author andrstor
 *
 */
public class ContentDeleter {
	
	
	private Context context;

	public ContentDeleter(Context context){
		this.context = context;
	}
	
	public void deleteEventItemFromDB(EventItem item){
		context.getContentResolver().delete(item.getUri(), item.getId(), null);
	}
	
	public void deleteEventFromDB(Event event) {
		
		for (EventItem item : event.getEventItems()) {
			deleteEventItemFromDB(item);
		}
		
		deleteEmotionFromDB(event);
		context.getContentResolver().delete(EventProvider.CONTENT_URI, event.getId(), null);
	}
	
	public void deleteExperienceFromDB(Experience experience){
		context.getContentResolver().delete(ExperienceProvider.CONTENT_URI, experience.getId(), null);
	}

	private void deleteEmotionFromDB(Event event) {
		context.getContentResolver().delete(EmotionsProvider.CONTENT_URI, event.getId(), null);
	}

}
