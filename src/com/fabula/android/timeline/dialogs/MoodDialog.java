package com.fabula.android.timeline.dialogs;

import com.fabula.android.timeline.models.MoodEvent;

import com.fabula.android.timeline.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MoodDialog extends Dialog{

	private Context mContext;
	private MoodEvent moodEvent;
	private TextView titleText;
	private TextView creatorText;
	private ImageView moodPicture;

	public MoodDialog(Context context, MoodEvent moodEvent) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.mooddialog);
		
		titleText = (TextView) findViewById(R.id.moodidalogTitle);
		creatorText = (TextView) findViewById(R.id.mooddialogcreator);
		moodPicture = (ImageView) findViewById(R.id.mooddialogpicture);
		
		titleText.setText("Your Mood");
		moodPicture.setImageResource(moodEvent.getMood().getIcon());
		creatorText.setText("Added by: " +moodEvent.getUser().name);
		this.mContext = context;
		this.moodEvent = moodEvent;
	}
}
