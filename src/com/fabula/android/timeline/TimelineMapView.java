package com.fabula.android.timeline;

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

import com.fabula.android.timeline.contentmanagers.ContentLoader;
import com.fabula.android.timeline.models.*;


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
		drawable = this.getResources().getDrawable(R.drawable.icon);
		itemizedOverlay = new TimelineMapItemizedOverlay(drawable, this);
		

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
	
	private ArrayList <Event> loadEventsWithGeolocationFromDatabase() {
		contentLoader = new ContentLoader(this);
		return contentLoader.LoadAllEventsFromDatabase();
	}
	
	private void addEventsToMap(ArrayList<Event> allEvents) {
		
		for (Event event : allEvents) {
			OverlayItem overlayItem = new OverlayItem(event.getGeoPointLocation(), "", "");

			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}
	}
}
