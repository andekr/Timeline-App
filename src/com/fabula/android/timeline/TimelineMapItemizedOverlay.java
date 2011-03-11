package com.fabula.android.timeline;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TimelineMapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> timelineMapOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public TimelineMapItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
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

}
