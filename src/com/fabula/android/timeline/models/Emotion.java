package com.fabula.android.timeline.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import android.net.Uri;
import android.provider.BaseColumns;

import com.fabula.android.timeline.R;

@Root
public enum Emotion {
	
	LIKE(R.drawable.emo_im_happy, 1), COOL(R.drawable.emo_im_cool,2), DISLIKE(R.drawable.emo_im_dislike,3), SAD(R.drawable.emo_im_sad,4);

	private int icon;
	
	private int type;
	
	Emotion(int icon, int type){
		this.icon = icon;
		this.type = type;
	}
	
	public int getIcon(){
		return icon;
	}
	@Attribute
	public int getType() {
		return type;
	}
	
	@Attribute
	public String getName(){
		return name();
	}
	
	public static final class EmotionColumns implements BaseColumns {
		
		public static final Uri CONTENT_URI = Uri.parse("content://com.fabula.android.timeline.providers.emotionsprovider");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.emotions";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fabula.emotions";
        public static final String EVENT_ID = "exp_id";
        public static final String EMOTION_TYPE = "type";
	}

}

