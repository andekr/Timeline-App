package com.fabula.android.timeline;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.database.UserGroupDatabaseHelper;
import com.fabula.android.timeline.database.contentmanagers.ContentAdder;
import com.fabula.android.timeline.database.contentmanagers.ContentLoader;
import com.fabula.android.timeline.database.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.dialogs.NewTimelineDialog;
import com.fabula.android.timeline.dialogs.TimelineBrowserDialog;
import com.fabula.android.timeline.map.TimelineMapView;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.sync.GoogleAppEngineHandler;
import com.fabula.android.timeline.sync.UserAndGroupServiceHandler;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.MyLocation;
import com.fabula.android.timeline.utilities.Utilities;


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
public class DashboardActivity extends Activity implements ProgressDialogActivity {

	private ImageButton newTimeLineButton;
	private ImageButton browseMyTimelinesButton;
	private ImageButton profileButton;
	private ImageButton browseSharedTimelinesButton;
	private ImageButton syncronizeButton;
	private ImageButton myGroupsButton;
	private TextView lastSyncedTextView;
	private Intent timelineIntent;
	private Intent myGroupsIntent;
	private Intent tagsIntent;
	private ContentAdder contentAdder;
	private ContentLoader contentLoader;
	private Account creator;
	private User user;
	Runnable syncThread, addGroupToServerThread, checkUserRunnable;
	private long lastSynced=0;
	boolean registered=false;
	private UserGroupDatabaseHelper userGroupDatabaseHelper;
	private UserGroupManager uGManager;
	private ProgressDialog progressDialog;
	private TimelineDatabaseHelper timelineDatabaseHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		MyLocation.getInstance(this);//Starts the LocationManager right away so a location will be available as soon as possible
		
		creator = Utilities.getUserAccount(this);
		user = new User(creator.name);
		
		//Initializes the content managers
		setupHelpers();
		
		uGManager.addUserToUserDatabase(user);
		
		setupIntents();

		//Check for Internet
		
		progressDialog = new ProgressDialog(this);
		
		//Check if user is registered
		checkUserRunnable = new Runnable() {
			public void run() {
				checkIfUserIsRegisteredOnServer();
			}
		};
		
		if(Utilities.isConnectedToInternet(this)){
			Thread checkUserThread = new Thread(checkUserRunnable, "checkUserThread");
			checkUserThread.start();
			progressDialog = ProgressDialog.show(DashboardActivity.this,    
		              "", "", true);
		}else{
			Toast.makeText(this, "You are not connected to Internet. Some functions will not be availiable. But you can still collect your experiences!", Toast.LENGTH_LONG).show();
		}
		
		
		try {
			lastSynced = getLastSynced();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "Couldn't retrieve last synced time");
		}
		
		setupViews();
		
		//If the application is started with a SEND- or share Intent, change the Intent to add to a timeline
		if (getIntent().getAction().equals(Intent.ACTION_SEND)
				|| getIntent().getAction().equals("share")) {
			timelineIntent = getIntent();
			timelineIntent.setAction(Constants.INTENT_ACTION_ADD_TO_TIMELINE);
			timelineIntent.setClass(this, TimelineActivity.class); //Changes the class to start
			browseAllTimelines(Constants.SHARED_ALL);
		}
		
 		syncThread = new Runnable() {
			
			public void run() {
				syncTimelines();
			}
		};
		
	}

	private void checkIfUserIsRegisteredOnServer() {
		runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.setMessage("Checking user against server ...");
			}
		});
		registered = GoogleAppEngineHandler.IsUserRegistered(user.getUserName());
		//Register user if not registered
		if(!registered){
			GoogleAppEngineHandler.addUserToServer(user);
			runOnUiThread(new Runnable() {
				
				public void run() {
					progressDialog.setMessage("Not registered. Registering user.");
				}
			});
		}else{
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(DashboardActivity.this, "Already registered!", Toast.LENGTH_LONG).show();
				}
			});
		}
		progressDialog.dismiss();
	}
	
	private void setLastSyncedTextView() {
		if(lastSynced!=0){
			String lastSyncedFormattedString = DateFormat.format
   		 ("dd MMMM yyyy "+DateFormat.HOUR_OF_DAY+":mm:ss", new Date(lastSynced)).toString();
			lastSyncedTextView.setText(getResources().getString(R.string.Last_synced_label).toString()+" "+lastSyncedFormattedString);
		}else{
			lastSyncedTextView.setText(getResources().getString(R.string.Last_synced_label).toString()+" Never");
		}
	}

	/**
	 * Adds the new timeline to the database containing all the timelines.
	 * 
	 * 
	 * @param experience The experience to add to database
	 */
	private void addNewTimelineToTimelineDatabase(Experience experience) {
		contentAdder.addExperienceToTimelineContentProvider(experience);
	}


	private void browseAllTimelines(int shared) {
		TimelineBrowserDialog dialog = new TimelineBrowserDialog(this,
				timelineIntent, shared);

		if (dialog.getNumberOfTimelinesSaved() != 0) {
			dialog.show();
		} else {
			switch (shared) {
			case Constants.SHARED_TRUE:
				Toast.makeText(this,
						"No shared timelines exists yet. Synchronize, create new or share a private timeline!",
						Toast.LENGTH_LONG).show();
				break;
			case Constants.SHARED_FALSE:
				Toast.makeText(this,
						"No private timelines exists yet. Create a new one first or look in \"my shared timelines\"",
						Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(this,
						"No timelines exists yet. Create a new one or synchronize!",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}
	}
	
	/**
	 * Synchronize shared timelines with database.
	 * First all local objects are persisted on server, then all content for this user on the server is downloaded.
	 * 
	 */
	private void syncTimelines() {
		UserAndGroupServiceHandler ugHandler =  new UserAndGroupServiceHandler(this, this);
		ugHandler.downloadUsersAndGroups();
		ArrayList<Experience> sharedExperiences = contentLoader.LoadAllSharedExperiencesFromDatabase();
		for (Experience experience : sharedExperiences) {
			new DatabaseHelper(this, experience.getTitle()+".db");
			DatabaseHelper.getCurrentTimelineDatabase().beginTransaction();
			experience.setEvents(contentLoader.LoadAllEventsFromDatabase());
			DatabaseHelper.getCurrentTimelineDatabase().setTransactionSuccessful();
			DatabaseHelper.getCurrentTimelineDatabase().endTransaction();
			DatabaseHelper.getCurrentTimelineDatabase().close();
		}
		
		Experiences experiences = new Experiences(sharedExperiences);
		GoogleAppEngineHandler.persistTimelineObject(experiences);
		
		Experiences exps = GoogleAppEngineHandler.getAllSharedExperiences(user);
		if(exps!=null){
			for (Experience e : exps.getExperiences()) {
				e.setSharingGroupObject(uGManager.getGroupFromDatabase(e.getSharingGroup()));
				contentAdder.addExperienceToTimelineContentProvider(e);
				addNewTimelineToTimelineDatabase(e);

			}
		}
		runOnUiThread(confirmSync);
	}
	
	private void storeLastSynced(long lastSyncedInMillis){
		String FILENAME = "lastSynced";
		String lastSynced = String.valueOf(lastSyncedInMillis);
		this.lastSynced = lastSyncedInMillis;
		setLastSyncedTextView();
		
		try {
			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(lastSynced.getBytes());
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private long getLastSynced(){
		String FILENAME = "lastSynced";
		int ch;
	    StringBuffer strContent = new StringBuffer("");
		
		try {
			FileInputStream fis = openFileInput(FILENAME);
			while( (ch = fis.read()) != -1)
		        strContent.append((char)ch);
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Long.valueOf(strContent.toString());
	}
	
	/**
	 * Thread to notify user that timelines have been synced. Intended run on UI thread.
	 * 
	 */
    private Runnable confirmSync = new Runnable() {
        public void run(){
        	try {
        		storeLastSynced(new Date().getTime());
        		progressDialog.dismiss();
        		Toast.makeText(DashboardActivity.this, "Timelines synced!",Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
			}
        }
      };
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDatabaseHelpers();
	}
	
	@Override
	public void finish() {
		super.finish();
		closeDatabaseHelpers();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		closeDatabaseHelpers();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setupDatabaseHelpers();
	}
	
	@Override
	protected void onStop() {
		closeDatabaseHelpers();
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		timelineIntent = new Intent(this, TimelineActivity.class);
		timelineIntent.setAction("NEW");
	}
	
	private void setupDatabaseHelpers() {
		userGroupDatabaseHelper = new UserGroupDatabaseHelper(this, Constants.USER_GROUP_DATABASE_NAME);
		timelineDatabaseHelper = new TimelineDatabaseHelper(this, Constants.ALL_TIMELINES_DATABASE_NAME);
	}
	
	private void closeDatabaseHelpers() {
		userGroupDatabaseHelper.close();
		timelineDatabaseHelper.close();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dashboard_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.SEND_EMAIL_WITH_CONTENT:
			//Check if user is registered
			Runnable sendEmailRunnable = new Runnable() {
				public void run() {
					GoogleAppEngineHandler.sendEmailWithActivity(user);
				}
			};
			Thread sendEmailThread = new Thread(sendEmailRunnable);
			sendEmailThread.start();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Open the map view where all the events in every timeline is showed on the map.
	 */
	
	public void openMapView() {
		Intent mapViewIntent = new Intent(this, TimelineMapView.class);
		mapViewIntent.setAction(Constants.INTENT_ACTION_OPEN_MAP_VIEW_FROM_DASHBOARD);
		startActivityForResult(mapViewIntent, Constants.ALL_EXPERIENCES_MAP_ACTIVITY_REQUEST_CODE);
	}
	
	//listeners
	private OnClickListener newTimeLineListener = new OnClickListener() {
		public void onClick(View v) {
				if(Utilities.isConnectedToInternet(getApplicationContext())) {
					UserAndGroupServiceHandler ugHandler =  new UserAndGroupServiceHandler(DashboardActivity.this, DashboardActivity.this);
					ugHandler.startDownloadUsersAndGroups();
				}
				else {
					Toast.makeText(getApplicationContext(), "You can only create private timelines in offline mode", Toast.LENGTH_SHORT).show();
					NewTimelineDialog newTimelineDialog = new NewTimelineDialog(DashboardActivity.this, null,timelineIntent);
					newTimelineDialog.show();
				}
			
		}
	};

	private OnClickListener browseTimeLineListener = new OnClickListener() {

		public void onClick(View v) {
			browseAllTimelines(Constants.SHARED_FALSE);
		}
	};
	
	private OnClickListener browseSharedTimeLinesListener = new OnClickListener() {

		public void onClick(View v) {
			browseAllTimelines(Constants.SHARED_TRUE);		
		}

	};
	
	private OnClickListener openMyGroupsListener = new OnClickListener() {
		public void onClick(View v) {
			if(Utilities.isConnectedToInternet(getApplicationContext())) {
				startActivity(myGroupsIntent);
				closeDatabaseHelpers();
			}
			else {
				Toast.makeText(getApplicationContext(), "You have to be connected to Internet to use this functionality", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener viewProfileListener = new OnClickListener() {
		public void onClick(View v) {
			startActivity(tagsIntent);
		}
	};
	
	private OnClickListener syncListener = new OnClickListener() {
		public void onClick(View v) {
			
			if(Utilities.isConnectedToInternet(getApplicationContext())) {
				progressDialog = ProgressDialog.show(DashboardActivity.this,    
			              "", "", true);
				progressDialog.setMessage("Syncronizing timelines");
				Thread shareThread = new Thread(syncThread, "shareThread");
				shareThread.start();
			}
			else {
				Toast.makeText(getApplicationContext(), "You have to be connected to Internet to use this functionaltiy", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private void setupIntents() {
		myGroupsIntent = new Intent(this, MyGroupsActivity.class);
		myGroupsIntent.putExtra("ACCOUNT", creator);
		
		timelineIntent = new Intent(this, TimelineActivity.class);
		timelineIntent.setAction(Constants.INTENT_ACTION_NEW_TIMELINE); //Default Intent action for TimelineActivity is to create/open a timeline.
		tagsIntent = new Intent(this, MyTagsActivity.class);
	}

	private void setupHelpers() {
		setupDatabaseHelpers();
		contentAdder = new ContentAdder(getApplicationContext());
		contentLoader = new ContentLoader(getApplicationContext());
		uGManager = new UserGroupManager(getApplicationContext());
	}

	public void callBack() {
		NewTimelineDialog newTimelineDialog = new NewTimelineDialog(DashboardActivity.this, null, timelineIntent);
		newTimelineDialog.show();
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
		myGroupsButton = (ImageButton) findViewById(R.id.dash_my_groups);
		myGroupsButton.setOnClickListener(openMyGroupsListener);
		profileButton = (ImageButton) findViewById(R.id.dash_profile);
		profileButton.setOnClickListener(viewProfileListener);
		syncronizeButton = (ImageButton)findViewById(R.id.dash_sync);
		syncronizeButton.setOnClickListener(syncListener);
		lastSyncedTextView = (TextView)findViewById(R.id.DashLastSyncedTextView);
		setLastSyncedTextView();	
	}
}
