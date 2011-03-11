package com.fabula.android.timeline;

import java.util.ArrayList;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fabula.android.timeline.contentmanagers.ContentAdder;
import com.fabula.android.timeline.contentmanagers.ContentLoader;
import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.dialogs.TimelineBrowserDialog;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.sync.GAEHandler;
import com.fabula.android.timeline.utilities.MyLocation;


/**
 * The starting activity for the application.
 * The application is mainly a collection of button listeners for the different menu buttons.
 * 
 * Implemented buttons:
 * -New timeline
 * -My timelines(all timelines - private and shared)
 * -Shared timelines
 * -Profile(shows the username)
 * -Syncronize(collects all shared experiences and sends to a server)
 * 
 * @author andekr
 *
 */
public class DashboardActivity extends Activity {

	private ImageButton newTimeLineButton;
	private ImageButton browseMyTimelinesButton;
	private ImageButton profileButton;
	private ImageButton browseSharedTimelinesButton;
	private ImageButton syncronizeButton;
	private Intent timelineIntent;
	private Intent profileIntent;
	private ContentAdder contentAdder;
	private ContentLoader contentLoader;
	Runnable syncThread;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		MyLocation.getInstance(this);//Starts the LocationManager right away so a location will be available as soon as possible
		
		//Initializes the content managers
		contentAdder = new ContentAdder(getApplicationContext());
		contentLoader = new ContentLoader(getApplicationContext());
		
		
		profileIntent = new Intent(this, ProfileActivity.class);
		timelineIntent = new Intent(this, TimelineActivity.class);
		timelineIntent.setAction(Utilities.INTENT_ACTION_NEW_TIMELINE); //Default Intent action for TimelineActivity is to create/open a timeline.

		setupViews();

		//If the application is started with a SEND- or share Intent, change the Intent to add to a timeline
		if (getIntent().getAction().equals(Intent.ACTION_SEND)
				|| getIntent().getAction().equals("share")) {
			timelineIntent = getIntent();
			timelineIntent.setAction(Utilities.INTENT_ACTION_ADD_TO_TIMELINE);
			timelineIntent.setClass(this, TimelineActivity.class); //Changes the class to start
			browseAllTimelines(false);
		}
		
 		syncThread = new Runnable() {
			
			public void run() {
				syncTimelines();
			}
		};
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		timelineIntent = new Intent(this, TimelineActivity.class);
		timelineIntent.setAction("NEW");
	}

	/**
	 * Sets up the views by getting the views from layout XML and attaching listeners to buttons. 
	 * 
	 */
	private void setupViews() {
		newTimeLineButton = (ImageButton) findViewById(R.id.dash_new_timeline);
		newTimeLineButton.setOnClickListener(newTimeLineListener);
		browseMyTimelinesButton = (ImageButton) findViewById(R.id.dash_my_timelines);
		browseMyTimelinesButton.setOnClickListener(browseTimeLineListener);
		browseSharedTimelinesButton = (ImageButton) findViewById(R.id.dash_shared_timelines);
		browseSharedTimelinesButton.setOnClickListener(browseSharedTimeLinesListener);
		profileButton = (ImageButton) findViewById(R.id.dash_profile);
		profileButton.setOnClickListener(viewProfileListener);
		syncronizeButton = (ImageButton)findViewById(R.id.dash_sync);
		syncronizeButton.setOnClickListener(syncListener);
	}

	private OnClickListener newTimeLineListener = new OnClickListener() {

		public void onClick(View v) {
			openDialogForTimelineNameInput();
		}
	};

	private OnClickListener browseTimeLineListener = new OnClickListener() {

		public void onClick(View v) {
			browseAllTimelines(false);
		}
	};
	
	private OnClickListener browseSharedTimeLinesListener = new OnClickListener() {

		public void onClick(View v) {
			browseAllTimelines(true);		
		}

	};
	
	
	private OnClickListener viewProfileListener = new OnClickListener() {
		public void onClick(View v) {
			startActivity(profileIntent);
		}
	};
	
	private OnClickListener syncListener = new OnClickListener() {
		public void onClick(View v) {
			Toast.makeText(DashboardActivity.this, "Syncronizing shared experiences with server...", Toast.LENGTH_SHORT).show();
				Thread shareThread = new Thread(syncThread, "shareThread");
				shareThread.start();
		}
	};

	/**
	 * Opens the dialog for creating a new timeline.
	 * Input is name and if the timeline should be shared.
	 *
	 */
	private void openDialogForTimelineNameInput() {
		final AlertDialog.Builder timelineNameInputDialog = new AlertDialog.Builder(
				this);
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.newtimelinedialog, (ViewGroup) findViewById(R.id.layout_root));
		timelineNameInputDialog.setView(layout);
		
		final EditText inputTextField = (EditText)layout.findViewById(R.id.TimelineNameEditText);
		final ToggleButton shareToggle = (ToggleButton)layout.findViewById(R.id.ShareTimelineToggleButton);

		timelineNameInputDialog.setTitle("Enter a name for your timeline!");
		timelineNameInputDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String inputName = inputTextField.getText().toString().trim();
				boolean share = shareToggle.isChecked();
				createNewTimeline(inputName, share);
				dialog.dismiss();
			}
		});

		timelineNameInputDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		timelineNameInputDialog.show();
	}
	
	/**
	 * Creates a new timeline and starts the Timeline activity
	 * 
	 * @param timelineName String. Name of the new Timeline
	 * @param shared boolean If the Timeline should be shared
	 */
	private void createNewTimeline(String timelineName, boolean shared) {

		Account creator = Utilities.getUserAccount(this);
		Experience timeLine = new Experience(timelineName, shared, creator);

		String databaseName = timeLine.getTitle() + ".db";
		addNewTimelineToTimelineDatabase(timeLine); 
		new DatabaseHelper(this, databaseName);
		timelineIntent.putExtra(Utilities.DATABASENAME_REQUEST, timelineName);
		timelineIntent.putExtra(Utilities.SHARED_REQUEST, shared);
		timelineIntent.putExtra(Utilities.EXPERIENCEID_REQUEST, timeLine.getId());
		timelineIntent.putExtra(Utilities.EXPERIENCECREATOR_REQUEST, timeLine.getCreator().name);
		startActivity(timelineIntent);

	}

	/**
	 * Adds the new timeline to the database containing all the timelines.
	 * 
	 * 
	 * @param experience The experience to add to database
	 */
	private void addNewTimelineToTimelineDatabase(Experience experience) {
		new TimelineDatabaseHelper(this, Utilities.ALL_TIMELINES_DATABASE_NAME);
		contentAdder.addExperienceToTimelineContentProvider(experience);
		TimelineDatabaseHelper.getCurrentTimeLineDatabase().close();
	}

	private void browseAllTimelines(boolean shared) {
		TimelineBrowserDialog dialog = new TimelineBrowserDialog(this,
				timelineIntent, shared);

		if (dialog.getNumberOfTimelinesSaved() != 0) {
			dialog.show();
		} else {
			Toast.makeText(this,
					"No timelines exists yet. Create a new one first!",
					Toast.LENGTH_SHORT).show();
		}
	}
	

	/**
	 * Synchronize shared timelines with database.
	 * 
	 */
	private void syncTimelines() {
		//Hente ned fra server og merge
		// Hente inn experiencer som er delt - DONE
		// Hente ut alle events i alle delte experiencer (kun de som ikke er låst) - DONE
		new TimelineDatabaseHelper(this, Utilities.ALL_TIMELINES_DATABASE_NAME);
		ArrayList<Experience> sharedExperiences = contentLoader.LoadAllSharedExperiencesFromDatabase();
		for (Experience experience : sharedExperiences) {
			new DatabaseHelper(this, experience.getTitle());
			experience.setEvents(contentLoader.LoadAllEventsFromDatabase());//TODO: Hente ut creator i contentLoader
			DatabaseHelper.getCurrentTimelineDatabase().close();
		}
		
		Experiences experiences = new Experiences(sharedExperiences);
		GAEHandler.send(experiences, this);
		
//		for (Experience experience : sharedExperiences) {
//			GAEHandler.send(experience, this);
//		}
	
		TimelineDatabaseHelper.getCurrentTimeLineDatabase().close();
		// Serialisere til XML el. JSON og sende opp til server(med REST?)
		
			
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
