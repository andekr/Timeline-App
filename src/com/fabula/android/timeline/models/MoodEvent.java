package com.fabula.android.timeline.models;

import java.util.Date;

import android.accounts.Account;
import android.location.Location;

import com.fabula.android.timeline.R;

public class MoodEvent extends BaseEvent{
	
	private MoodEnum mood;
	private boolean average;
	
	public MoodEvent(){
		className = this.getClass().getSimpleName();
		setShared(true);
		setAverage(false);
	}

	public MoodEvent(String experienceID, Location location, MoodEnum mood, Account user) {
		super(experienceID, location, user);
		this.mood = mood;
		setMoodInt(mood.getMoodInt());
		className = this.getClass().getSimpleName();
		setShared(true);
		setAverage(false);
	}
	
	public MoodEvent(String id, String experienceID, Date dateTime, Location location, MoodEnum mood, Account user) {
		super(id, experienceID, dateTime, location, user);
		this.mood = mood;
		className = this.getClass().getSimpleName();
		setShared(true);
		setAverage(false);
		setMoodInt(mood.getMoodInt());
	}

	public MoodEnum getMood() {
		return mood;
	}

	public void setMood(MoodEnum mood) {
		this.mood = mood;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	public boolean isAverage() {
		return average;
	}

	public void setAverage(boolean average) {
		this.average = average;
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
				 return R.drawable.mood_very_happy;
			case HAPPY:
				return R.drawable.mood_happy;
			case LIKEWISE:
				 return R.drawable.mood_likewise;
			case SAD:
				 return R.drawable.mood_sad;
			case VERY_SAD:
				return R.drawable.mood_very_sad;
			default:
				return R.drawable.mood_happy;
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
