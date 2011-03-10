package com.fabula.android.timeline.models;

import java.io.IOException;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.providers.RecordingProvider;

public class SimpleRecording extends EventItem{

	private String recordingDescription;
	private Uri recordingUri;
	MediaPlayer mp;
	
	
	public SimpleRecording(Context c) {
		super(c);
	}
	
	public SimpleRecording(String id, Uri uri, Account u){
		super(id,u);
		this.recordingUri = uri;
	}
	
	public void setRecordingDescription(String recordingDescription) {
		this.recordingDescription = recordingDescription;
	}
	
	public String getRecordingDescription() {
		return recordingDescription;
	}
	
	public Uri getRecordingUri() {
		return recordingUri;
	}

	public void setRecordingUri(Uri recordingUri) {
		this.recordingUri = recordingUri;
	}

	public MediaPlayer getMp() {
		return mp;
	}
	
	@Override
	public View getView(Context context) {
		
		RelativeLayout playButton = new RelativeLayout(context);
		LayoutInflater inflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		playButton = (RelativeLayout)inflater.inflate(R.layout.imageandtextbutton, null);
		ImageView icon = (ImageView)playButton.findViewById(R.id.CustomButtonIcon);
		icon.setImageResource(R.drawable.ic_menu_audio);
		
//		ImageButton playButton = new ImageButton(context);
//		playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_play_clip));
        playButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		
 	   mp = new MediaPlayer();
	    try {
			mp.setDataSource(context, recordingUri);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		playButton.setTag(this);
		playButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				System.out.println("START");
				((SimpleRecording)v.getTag()).getMp().start();
			}
		});
		
		return playButton;
	}

	@Override
	public Intent getIntent() {
		return null;
	}
	
	@Override
	public Uri getUri() {
		return RecordingColumns.CONTENT_URI;
	}
	
public static final class RecordingColumns implements BaseColumns {
		
		private RecordingColumns(){
		}
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+RecordingProvider.AUTHORITY+"/recordings");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.recordings";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fabula.recordings";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String FILE_URI = "uri_path";
        
        public static final String DESCRIPTION = "description";

        public static final String CREATED_DATE = "created";

	}		
}
