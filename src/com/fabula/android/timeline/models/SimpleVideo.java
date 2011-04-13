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
	public SimpleVideo(String id, Uri uri, Account u, String videoFilename) {
		super(id, u);
		className = "SimplePicture";
		this.videoURI = uri;
		filename = videoFilename;
	}
	
	public SimpleVideo(String id, Account u, String videoFilename) {
		super(id, u);
		className = "SimpleVideo";
		if(!(new File(Constants.VIDEO_STORAGE_FILEPATH)).exists()) {
			(new File(Constants.VIDEO_STORAGE_FILEPATH)).mkdirs();
		}
		File file = Utilities.DownloadFromUrl(videoFilename, Constants.VIDEO_STORAGE_FILEPATH+videoFilename);
		this.videoURI = Uri.fromFile(file);
		filename = videoFilename;
	}
	
	
	public Uri videoURI;
	

	public String getVideoFilename() {
		return filename;
	}
	public void setVideoFilename(String videoFileName) {
		this.filename = videoFileName;
	}

	public Uri getVideoUri() {
		return videoURI;
	}
	public void setVideoUri(Uri video, String videoFilename) {
		this.videoURI = video;
		filename = videoFilename;
	}
	@Override
	public View getView(Context context) {
		
		RelativeLayout playButton = new RelativeLayout(context);
		LayoutInflater inflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		playButton = (RelativeLayout)inflater.inflate(R.layout.imageandtextbutton, null);
		ImageView icon = (ImageView)playButton.findViewById(R.id.CustomButtonIcon);
		icon.setImageResource(R.drawable.ic_menu_video);
		
		
//		ImageButton playButton = new ImageButton(context);
//		playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_play_clip));
		
		playButton.setTag(this);
		
//        VideoView videoView = new VideoView(context);
//        videoView.setTag(this);
//        videoView.setVideoURI(videoURI);
//        Log.v("VIDEOVIEW", "VideoURL: "+videoURI+" spiller: "+videoView.isPlaying());
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
