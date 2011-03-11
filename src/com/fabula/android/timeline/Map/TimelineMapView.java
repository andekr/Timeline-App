package com.fabula.android.timeline.Map;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.R.drawable;
import com.fabula.android.timeline.R.id;
import com.fabula.android.timeline.R.layout;
import com.fabula.android.timeline.contentmanagers.ContentLoader;
import com.fabula.android.timeline.models.*;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinemaplayout);
		setupViews();
		
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();

		addEventsToMap(loadEventsWithGeolocationFromDatabase());
		
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
			
			itemizedOverlay = new TimelineMapItemizedOverlay(drawable, this, this);
			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}
	}
}
