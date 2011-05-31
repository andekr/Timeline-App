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
package com.fabula.android.timeline.models;

import java.io.File;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.database.providers.VideoProvider;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.Utilities;



public class SimpleVideo extends EventItem {

	public SimpleVideo(Context c) {
		super(c);
		className = "SimpleVideo";
	}
	public SimpleVideo(String id, Uri uri, Account u, String videoUrl) {
		super(id, u);
		className = "SimpleVideo";
		this.videoURI = uri;
		url = videoUrl;
	}
	
	public SimpleVideo(String id, Account u, String videoUrl) {
		super(id, u);
		className = "SimpleVideo";
		url = videoUrl;
		File file = Utilities.DownloadFromUrl(videoUrl, Constants.VIDEO_STORAGE_FILEPATH+getVideoFilename());
		this.videoURI = Uri.fromFile(file);
		
	}
	
	
	public Uri videoURI;
	

	public String getVideoFilename() {
		return Utilities.getFilenameFromURL(url);
	}
	public void setVideoUrl(String videoUrl) {
		this.url = videoUrl;
	}
	
	public String getVideoUrl() {
		return this.url;
	}
	

	public Uri getVideoUri() {
		return videoURI;
	}
	public void setVideoUri(Uri video, String videoUrl) {
		this.videoURI = video;
		url = videoUrl;
	}
	@Override
	public View getView(Context context) {
		
		RelativeLayout playButton = new RelativeLayout(context);
		LayoutInflater inflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		playButton = (RelativeLayout)inflater.inflate(R.layout.imageandtextbutton, null);
		ImageView icon = (ImageView)playButton.findViewById(R.id.CustomButtonIcon);
		icon.setImageResource(R.drawable.ic_menu_video);
		
		playButton.setTag(this);
		
        playButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        return playButton;
	}
	
	public Intent getIntent(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(videoURI, "video/*");
		
		return intent;
	}
	
	@Override
	public Uri getUri() {
		return VideoColumns.CONTENT_URI;
	}
	
	public static final class VideoColumns implements BaseColumns {
		
		private VideoColumns(){
		}
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+VideoProvider.AUTHORITY+"/videos");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.videos";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fabula.videos";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String FILE_PATH = "file_path";
        
        public static final String FILENAME = "filename";
        
        public static final String DESCRIPTION = "description";

        public static final String CREATED_DATE = "created";

	}
	

}
