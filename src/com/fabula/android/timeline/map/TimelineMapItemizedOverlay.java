/*******************************************************************************
 * Copyright (c) 2011 Andreas Storlien and Anders Kristiansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Storlien and Anders Kristiansen - initial API and implementation
 ******************************************************************************/
package com.fabula.android.timeline.map;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TimelineMapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> timelineMapOverlays = new ArrayList<OverlayItem>();
	private Activity mActivity;
	
	public TimelineMapItemizedOverlay(Drawable defaultMarker,  Activity activity) {
		super(boundCenterBottom(defaultMarker));

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
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = timelineMapOverlays.get(index);
		openEventViewInTimeLineActivity(item);
		return true;
	}
	
	private void openEventViewInTimeLineActivity(OverlayItem item) {
		Intent eventTappedIntent = new Intent();
		eventTappedIntent.putExtra("EVENT_ID", item.getSnippet());
		mActivity.setResult(Activity.RESULT_OK, eventTappedIntent);
		mActivity.finish();
	}




}
