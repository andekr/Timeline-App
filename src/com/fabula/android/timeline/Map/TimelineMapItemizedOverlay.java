package com.fabula.android.timeline.Map;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.fabula.android.timeline.dialogs.EventDialog;
import com.fabula.android.timeline.models.Event;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class TimelineMapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> timelineMapOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private Event event;
	private EventDialog eventDialog;
	private Activity mActivity;
	
	public TimelineMapItemizedOverlay(Drawable defaultMarker, Context context, Activity activity) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		this.mActivity = activity;
	}
	
	public void addOverlay(OverlayItem overlay){
		timelineMapOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return timelineMapOverlays.get(i);
	}

	@Override
	public int size() {
		return timelineMapOverlays.size();
	}
	
//	@Override
//	public boolean onTap(GeoPoint p, MapView mapView) {
//		
//		mOverlayItem = 
//		openEventViewInTimeLineActivity();
//		return super.onTap(p, mapView);
//	}
	
	private void openEventViewInTimeLineActivity(OverlayItem item) {
		Intent eventTappedIntent = new Intent();
		eventTappedIntent.putExtra("EVENT_ID", item.getSnippet());
		mActivity.setResult(Activity.RESULT_OK, eventTappedIntent);
		mActivity.finish();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event, mapView);
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = timelineMapOverlays.get(index);
		openEventViewInTimeLineActivity(item);
		return true;
	}

}
