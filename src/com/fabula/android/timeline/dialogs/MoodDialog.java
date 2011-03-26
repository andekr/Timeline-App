package com.fabula.android.timeline.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.MoodEvent;

public class MoodDialog extends Dialog{

	private TextView dateText;
	private TextView creatorText;
	private ImageView moodPicture;

	public MoodDialog(Context context, MoodEvent moodEvent) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.mooddialog);
		
		dateText = (TextView) findViewById(R.id.moodDialogTimestamp);
		creatorText = (TextView) findViewById(R.id.moodDialogUsername);
		moodPicture = (ImageView) findViewById(R.id.mooddialogpicture);
		
		dateText.setText(DateFormat.format
       		 ("dd MMMM yyyy "+DateFormat.HOUR_OF_DAY+":mm:ss",moodEvent.getDatetime()));
		if(moodEvent.isAverage())
			creatorText.setText("Current average");
		else
			creatorText.setText(moodEvent.getUser().name);
		moodPicture.setImageResource(moodEvent.getMood().getIcon());
	}
}
