package com.fabula.android.timeline.Map;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.R.drawable;
import com.fabula.android.timeline.R.id;
import com.fabula.android.timeline.R.layout;
import com.fabula.android.timeline.contentmanagers.ContentLoader;
import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.models.*;
import com.fabula.android.timeline.utilities.MyLocation;

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
		
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(18);
		mapController.animateTo(MyLocation.getInstance(this).getGeoPointLocation());
		mapOverlays = mapView.getOverlays();
		
		if(getIntent().getAction().equals(Utilities.INTENT_ACTION_OPEN_MAP_VIEW_FROM_TIMELINE)) {
			addEventsToMap(loadEventsWithGeolocationFromDatabase());
			
		}else if(getIntent().getAction().equals(Utilities.INTENT_ACTION_OPEN_MAP_VIEW_FROM_DASHBOARD)) {
			
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
	private ArrayList <Event> loadEventsWithGeolocationFromDatabase() {
		contentLoader = new ContentLoader(this);
		return contentLoader.LoadAllEventsFromDatabase();
	}
	
	/**
	 * Add events to the map overlay
	 * @param allEvents
	 */
	private void addEventsToMap(ArrayList<Event> allEvents) {
		
		for (Event event : allEvents) {
			OverlayItem overlayItem = new OverlayItem(event.getGeoPointLocation(), "", event.getId());

			drawable = this.getResources().getDrawable(Utilities.getMapImageIcon(event));
			
			itemizedOverlay = new TimelineMapItemizedOverlay(drawable, this);
			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}
	}
}
