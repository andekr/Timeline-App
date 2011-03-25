package com.fabula.android.timeline.models;

import java.util.Date;

import com.fabula.android.timeline.R;

import android.accounts.Account;
import android.location.Location;

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
	
	public enum MoodEnum{
		VERY_HAPPY(R.drawable.emo_im_happy, 5), HAPPY(R.drawable.emo_im_cool,4), LIKEWISE(R.drawable.emo_im_dislike,3), SAD(R.drawable.emo_im_sad,2), VERY_SAD(R.drawable.emo_im_sad, 1);

		private int icon;
		private int type;
		
		MoodEnum(int icon, int type){
			this.icon = icon;
			this.type = type;
		}
		
		public int getIcon(){
			return icon;
		}
		public int getMoodInt() {
			return type;
		}
		
		public String getName(){
			return name();
		}
	}
	
	

}
