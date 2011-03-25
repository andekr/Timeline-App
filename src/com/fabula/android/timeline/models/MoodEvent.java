package com.fabula.android.timeline.models;

import java.util.Date;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.providers.EventProvider;

import android.accounts.Account;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Xml.Encoding;

public class MoodEvent extends BaseEvent{
	
	private MoodEnum mood;

	public MoodEvent(String experienceID, Location location, MoodEnum mood, Account user) {
		super(experienceID, location, user);
		this.mood = mood;
	}
	
	public MoodEvent(String id, String experienceID, Date dateTime, Location location, MoodEnum mood, Account user) {
		super(id, experienceID, dateTime, location, user);
		this.mood = mood;
	}

	public MoodEnum getMood() {
		return mood;
	}

	public void setMood(MoodEnum mood) {
		this.mood = mood;
	}
	
	public enum MoodEnum {
		VERY_HAPPY(2), HAPPY(1), LIKEWISE(0), SAD(-1), VERY_SAD(-2);

		private int type;
		
		MoodEnum(int type){
			this.type = type;
		}
		
		public int getIcon(){
			
			switch (this) {
			case VERY_HAPPY:
				 return R.drawable.emo_im_happy;
			case HAPPY:
				return R.drawable.emo_im_cool;
			case LIKEWISE:
				 return R.drawable.emo_im_dislike;
			case SAD:
				 return R.drawable.emo_im_sad;
			case VERY_SAD:
				return R.drawable.emo_im_sad;
			default:
				return R.drawable.emo_im_happy;
			}
		}
		
		public int getMoodInt() {
			return type;
		}
		
		public String getName(){
			return name();
		}
		
		public static MoodEnum getType(int type) {
			
			switch (type) {
			case 2:
				return MoodEnum.VERY_HAPPY;
			case 1:
				return MoodEnum.HAPPY;
			case 0: 
				return MoodEnum.LIKEWISE;
			case -1:
				return MoodEnum.SAD;
			case -2:
				return MoodEnum.VERY_SAD;
			default:
				return MoodEnum.VERY_HAPPY;
			}
		}
		
	}
	
}
