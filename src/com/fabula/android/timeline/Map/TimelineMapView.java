package com.fabula.android.timeline.Map;
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.contentmanagers.ContentLoader;
import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.utilities.MyLocation;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * This class open a new mapview of the timeline that the application is running at a given time.
 * @author andrstor
 *
 */

public class TimelineMapView extends MapActivity {

	private MapView mapView;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private TimelineMapItemizedOverlay itemizedOverlay;
	private ContentLoader contentLoader;
	private MapController mapController;
	private DatabaseHelper eventDatabaseHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinemaplayout);
		setupViews();
		setUpMapControllers();
		
		if(getIntent().getAction().equals(Utilities.INTENT_ACTION_OPEN_MAP_VIEW_FROM_TIMELINE)) {
			addEventsToMap(loadEventsWithGeolocationFromDatabase());
		}
		else if(getIntent().getAction().equals(Utilities.INTENT_ACTION_OPEN_MAP_VIEW_FROM_DASHBOARD)) {
			addAllTimelineAppEventsToMap();
		}
	}

	private void setUpMapControllers() {
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(18);
		try {
			mapController.animateTo(MyLocation.getInstance(this).getGeoPointLocation());
		} catch (NullPointerException e) {
			Log.e(this.getClass().getSimpleName(), "Location not availiable");
		}
		
		mapOverlays = mapView.getOverlays();
	}
	
	/**
	 * Adds all the events saved in all the timeline to the map view
	 */
	private void addAllTimelineAppEventsToMap() {
		new TimelineDatabaseHelper(this, Utilities.ALL_TIMELINES_DATABASE_NAME);
		contentLoader = new ContentLoader(this);
		
		ArrayList<Experience> experiences = contentLoader.LoadAllExperiencesFromDatabase();
		TimelineDatabaseHelper.getCurrentTimeLineDatabase().close();
		
		for (Experience experience : experiences) {
			eventDatabaseHelper = new DatabaseHelper(this, experience.getTitle());
			addEventsToMap(loadEventsWithGeolocationFromDatabase());
			eventDatabaseHelper.close();
		}
	}
	
	private void setupViews() {
		mapView = (MapView) findViewById(R.id.mapview);
	}
	@Override	
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub 
		return false;
	}
	
	/**
	 * Loads all events in the timeline from the database
	 * @return 
	 */
	private ArrayList <BaseEvent> loadEventsWithGeolocationFromDatabase() {
		contentLoader = new ContentLoader(this);
		return contentLoader.LoadAllEventsFromDatabase();
	}
	
	/**
	 * Add events to the map overlay
	 * @param allEvents
	 */
	private void addEventsToMap(ArrayList<BaseEvent> allEvents) {
		
		for (BaseEvent event : allEvents) {
			OverlayItem overlayItem = new OverlayItem(event.getGeoPointLocation(), "", event.getId());

			drawable = this.getResources().getDrawable(Utilities.getMapImageIcon((Event)event)); //CASTED FROM BASEEVENT TO EVENT
			
			itemizedOverlay = new TimelineMapItemizedOverlay(drawable, this);
			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}
	}
}
