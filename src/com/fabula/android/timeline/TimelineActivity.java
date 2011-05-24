package com.fabula.android.timeline;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.fabula.android.timeline.SimpleGestureFilter.SimpleGestureListener;
import com.fabula.android.timeline.adapters.TimelineGridAdapter;
import com.fabula.android.timeline.barcode.IntentIntegrator;
import com.fabula.android.timeline.barcode.IntentResult;
import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.database.contentmanagers.ContentAdder;
import com.fabula.android.timeline.database.contentmanagers.ContentDeleter;
import com.fabula.android.timeline.database.contentmanagers.ContentLoader;
import com.fabula.android.timeline.database.contentmanagers.ContentUpdater;
import com.fabula.android.timeline.dialogs.AttachmentAdder;
import com.fabula.android.timeline.dialogs.EventDialog;
import com.fabula.android.timeline.dialogs.MoodDialog;
import com.fabula.android.timeline.exceptions.MaxZoomedOutException;
import com.fabula.android.timeline.map.TimelineMapView;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.Zoom;
import com.fabula.android.timeline.models.MoodEvent.MoodEnum;
import com.fabula.android.timeline.sync.GoogleAppEngineHandler;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.MyLocation;
import com.fabula.android.timeline.utilities.Utilities;

/**
 * 
 * This is the main timeline {@linkplain Activity}.
 * 
 * This is where the {@link Event}s and {@link EventItem}s are accessed, and where new  
 * {@link Event}s and {@link EventItem}s can be added.
 * 
 * 
 * @author andekr
 *
 */
public class TimelineActivity extends Activity implements SimpleGestureListener {
	
	private LinearLayout cameraButton, videoCameraButton, audioRecorderButton, createNoteButton, attachmentButton, moodButton;
	private TextView screenTitle, timelineTitleTextView;
	private TimelineGridAdapter EventAdapter;
	private Event selectedEvent;
	private ContentAdder contentAdder;
	private ContentLoader contentLoader;
	private ContentUpdater contentUpdater;
	private ContentDeleter contentDeleter;
	private HorizontalScrollView scrollview;
	private GridView gridview;
	private ZoomControls zoomControls;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ImageButton homeButton;
	private int backCounter = 0;
	private String intentFilename;
	private QuickAction qa;

	public String databaseName, experienceID, experienceCreator;
	public boolean sharedExperience;
	private DatabaseHelper dbHelper;
	private ArrayList<BaseEvent> loadedEvents;
	private Experience timeline;
	private Uri imageUri;
	private Uri videoUri;
	private Uri audioUri;
	private MyLocation myLocation;
	private SimpleGestureFilter detector;
	private EventDialog eventDialog;
	private MoodDialog moodDialog;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timelinescreen);
        selectedEvent = null; //No event selected by default 
        detector = new SimpleGestureFilter(this,this); //Creates swipe gesture for the activity
    	setupZoom();
    	myLocation = MyLocation.getInstance(this);
    	
        createNewDatabaseIfTimeLineIsNew();
        
        //Instantiate content managers 
        contentAdder = new ContentAdder(getApplicationContext());
        contentUpdater  = new ContentUpdater(getApplicationContext());
        contentDeleter = new ContentDeleter(getApplicationContext());
       
        //Get intent content
        databaseName = getIntent().getExtras().getString(Constants.DATABASENAME_REQUEST);
        experienceID = getIntent().getExtras().getString(Constants.EXPERIENCEID_REQUEST);
        experienceCreator = getIntent().getExtras().getString(Constants.EXPERIENCECREATOR_REQUEST);
        sharedExperience = getIntent().getExtras().getBoolean(Constants.SHARED_REQUEST);
        
        Log.i(this.getClass().getSimpleName(), "****Got DB: "+databaseName+" from Intent****");
        Log.i(this.getClass().getSimpleName(), "Experience ID: "+experienceID);
        Log.i(this.getClass().getSimpleName(), "Created by: "+experienceCreator);
        Log.i(this.getClass().getSimpleName(), "Experience is shared: "+sharedExperience);
        Log.i(this.getClass().getSimpleName(), "*******************************************");
        
        //Instantiates database helper and loads events from database
        new DatabaseHelper(this, databaseName);
        new TimelineDatabaseHelper(this, Constants.ALL_TIMELINES_DATABASE_NAME);
        loadedEvents = loadEventItemsFromDatabase();
        TimelineDatabaseHelper.getCurrentTimeLineDatabase().close();
        
        //Creates the experience and loads it with events
        timeline = new Experience(experienceID, databaseName, sharedExperience, new Account(experienceCreator, "com.google"));
        
        timeline.setEvents(loadedEvents);
        System.out.println("ANTALL HENTEDE EVENTS! : " + timeline.getEvents().size());
        
        setupViews(); 
        setupMoodButtonQuickAction();
       
        //If the activity is started with a send-Intent(e.g. via share button in the Gallery), the item is added to the Timeline
        if(getIntent().getAction().equals(Constants.INTENT_ACTION_ADD_TO_TIMELINE)){
        	if(getIntent().getType().contains("image/")){
        			imageUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        			String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+".jpg";
    				Utilities.copyFile(Utilities.getRealPathFromURI(imageUri, this), Constants.IMAGE_STORAGE_FILEPATH, filename);
        			addPictureToTimeline(filename);
        	}
        	else if(getIntent().getType().contains("video/")){
        			videoUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        			String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+Utilities.getExtension(Utilities.getRealPathFromURI(videoUri, this));
    				Utilities.copyFile(Utilities.getRealPathFromURI(videoUri, this), Constants.VIDEO_STORAGE_FILEPATH, filename);
        			addVideoToTimeline(filename);
        	}else if(getIntent().getType().contains("audio/")){
        			audioUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        			String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+Utilities.getExtension(Utilities.getRealPathFromURI(audioUri, this));
    				Utilities.copyFile(Utilities.getRealPathFromURI(audioUri, this), Constants.RECORDING_STORAGE_FILEPATH, filename);
        			addAudioToTimeline(filename);
        	}
        	else if(getIntent().getType().contains("text/plain")){
        			addNoteToTimeline(getIntent());
        	}
        }
        
    }

	private void setupZoom() {
		Zoom.WEEK.setNext(Zoom.DAY);
    	Zoom.DAY.setNext(Zoom.HOUR);
    	Zoom.HOUR.setNext(Zoom.MONTH);
    	Zoom.MONTH.setNext(null);
	}
    
	private void createNewDatabaseIfTimeLineIsNew() {

	}
	
	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	private ArrayList<BaseEvent> loadEventItemsFromDatabase() {
		contentLoader = new ContentLoader(getApplicationContext());
		return contentLoader.LoadAllEventsFromDatabase();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	switch (requestCode) {
		case Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			  if (resultCode == RESULT_OK) {
				   Log.i(this.getClass().getSimpleName(), "********* PICTURE CREATED **************");
				   
				   Toast.makeText(this, "Picture created", Toast.LENGTH_SHORT).show();

	    	    	addPictureToTimeline(intentFilename);
	    	    	intentFilename="";

	    	    } else if (resultCode == RESULT_CANCELED) {
	    	        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
	    	    } else {
	    	        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
	    	    }
			break;
		case Constants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE:
			   if (resultCode == RESULT_OK) {
				Log.i(this.getClass().getSimpleName(), "********* VIDEO RECORDING CREATED **************");
				   
				Toast.makeText(this, "Video recording created", Toast.LENGTH_SHORT).show();
				   
	   	    	videoUri = data.getData();
    			String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+Utilities.getExtension(Utilities.getRealPathFromURI(videoUri, this));
				Utilities.copyFile(Utilities.getRealPathFromURI(videoUri, this), Constants.VIDEO_STORAGE_FILEPATH, filename);
				addVideoToTimeline(filename);
	   	    	
	    	    } else if (resultCode == RESULT_CANCELED) {
	    	        Toast.makeText(this, "Video was not taken", Toast.LENGTH_SHORT).show();
	    	    } else {
	    	        Toast.makeText(this, "Video was not taken", Toast.LENGTH_SHORT).show();
	    	    }
			break;
		case Constants.RECORD_AUDIO_ACTIVITY_REQUEST_CODE:
			   if (resultCode == RESULT_OK) {
				   Log.i(this.getClass().getSimpleName(), "********* AUDIO RECORDING CREATED **************");
				   
				   Toast.makeText(this, "Audio recording created", Toast.LENGTH_SHORT).show();
				  
				    audioUri = data.getData();
	       			String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+Utilities.getExtension(Utilities.getRealPathFromURI(audioUri, this));
	   				Utilities.copyFile(Utilities.getRealPathFromURI(audioUri, this), Constants.RECORDING_STORAGE_FILEPATH, filename);
	       			addAudioToTimeline(filename);

	    	    } else if (resultCode == RESULT_CANCELED) {
	    	        Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
	    	    } else {
	    	        Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
	    	    }
			break;
			
		case Constants.CREATE_NOTE_ACTIVITY_REQUEST_CODE:
			   if (resultCode == RESULT_OK) {
				   Log.i(this.getClass().getSimpleName(), "********* NOTE CREATED **************");
				   Log.i(this.getClass().getSimpleName(), "Title: "+data.getExtras().getString(Intent.EXTRA_SUBJECT));
				   Log.i(this.getClass().getSimpleName(), "Text: "+data.getExtras().getString(Intent.EXTRA_TEXT));
				   Log.i(this.getClass().getSimpleName(), "*************************************");
				   
				   Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show();
				
	    	    addNoteToTimeline(data);

	    	    } else if (resultCode == RESULT_CANCELED) {
	    	        Toast.makeText(this, "Note was not created", Toast.LENGTH_SHORT).show();
	    	    } else {
	    	        Toast.makeText(this, "Note was not created", Toast.LENGTH_SHORT).show();
	    	    }
			break;
			
		case Constants.EDIT_NOTE:
			   if (resultCode == RESULT_OK) {
	    	    	Toast.makeText(this, "Note edited" , Toast.LENGTH_SHORT).show();
	    	    
	    	    updateNote(data);

	    	    } else if (resultCode == RESULT_CANCELED) {
	    	        Toast.makeText(this, "Note was not edited", Toast.LENGTH_SHORT).show();
	    	    } else {
	    	        Toast.makeText(this, "Note was not edited", Toast.LENGTH_SHORT).show();
	    	    }
			break;
			
		case Constants.NEW_TAG_REQUESTCODE:
			   if (resultCode == RESULT_OK) {
	    	    
	    	    updateTags();

	    	    } else if (resultCode == RESULT_CANCELED) {
	    	        Toast.makeText(this, "Tag was not added", Toast.LENGTH_SHORT).show();
	    	    } else {
	    	        Toast.makeText(this, "Tag was not added", Toast.LENGTH_SHORT).show();
	    	    }
			break;
			case Constants.SELECT_PICTURE:
			if(resultCode == Activity.RESULT_OK) {
				Log.i(this.getClass().getSimpleName(), "********* PICTURE SELECTED **************");
				
				
				imageUri = (Uri) data.getData();
				String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+".jpg";
				Utilities.copyFile(Utilities.getRealPathFromURI(imageUri, this), Constants.IMAGE_STORAGE_FILEPATH, filename);
				addPictureToTimeline(filename);	
			}
			break;
			case Constants.SELECT_VIDEO:
			if(resultCode == Activity.RESULT_OK) {
				Log.i(this.getClass().getSimpleName(), "********* VIDEO SELECTED **************");
				Toast.makeText(this, "Video was selected", Toast.LENGTH_SHORT).show();
				videoUri = (Uri) data.getData();
    			String filename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+Utilities.getExtension(Utilities.getRealPathFromURI(videoUri, this));
				Utilities.copyFile(Utilities.getRealPathFromURI(videoUri, this), Constants.VIDEO_STORAGE_FILEPATH, filename);
    			addVideoToTimeline(filename);
				
			}
			case Constants.CAPTURE_BARCODE:
				Log.i(this.getClass().getSimpleName(), "********* BARCODE SCANNED **************");
				
				getBarcodeResults(requestCode, resultCode, data);
				break;
		
		case Constants.MAP_VIEW_ACTIVITY_REQUEST_CODE:
			
			if(resultCode == RESULT_OK) {
				
				String id = data.getExtras().getString("EVENT_ID");
				BaseEvent selectedEvent =  timeline.getEvent(id);
				
				if(selectedEvent != null) {
					if (selectedEvent instanceof Event) {
						eventDialog = new EventDialog(this, (Event)selectedEvent, this, true);
						eventDialog.show();
					}
					else {
						moodDialog = new MoodDialog(this, (MoodEvent) selectedEvent);
						moodDialog.show();
					}
				}
			}

		default:
			break;
		}    
    }
	
	private void getBarcodeResults(int requestCode, int resultCode, Intent intent) {
		if(resultCode == Activity.RESULT_OK) {
		    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		    if (scanResult != null) {
		    	 intent.putExtra(Intent.EXTRA_SUBJECT, "Barcode");
		    	 intent.putExtra(Intent.EXTRA_TEXT, scanResult.getContents());
		    	 
		    	   Log.i(this.getClass().getSimpleName(), "********* BARCODEINTENT CREATED **************");
				   Log.i(this.getClass().getSimpleName(), "Title: "+intent.getExtras().getString(Intent.EXTRA_SUBJECT));
				   Log.i(this.getClass().getSimpleName(), "Text: "+intent.getExtras().getString(Intent.EXTRA_TEXT));
				   Log.i(this.getClass().getSimpleName(), "*************************************");
				   
				   Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show();
			     addNoteToTimeline(intent);
		    }
		}
		else if(resultCode == Activity.RESULT_CANCELED) {
		    Toast.makeText(this, "Attachment was not added", Toast.LENGTH_SHORT).show();
		} else {
		    Toast.makeText(this, "Attachment was not added", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * The method that starts the camera.
	 * The method creates an {@link Intent} of the type {@linkplain MediaStore.ACTION_IMAGE_CAPTURE} which will start the camera,
	 * and deliver the result back to this activity by starting the intent with the {@link Activity.startActivityForResult()} method.
	 * 
	 * 
	 */
	public void startCamera() {
		    //create parameters for Intent
		    ContentValues values = new ContentValues();
		    values.put(MediaStore.Images.Media.TITLE, "TimelineImage");
		    values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		    
		    //Put some geolocation information into the image meta tag, if location is known
		    try {
		    	 values.put(MediaStore.Images.Media.LATITUDE, myLocation.getLocation().getLatitude());
				 values.put(MediaStore.Images.Media.LONGITUDE, myLocation.getLocation().getLongitude());
			} catch (NullPointerException e) {
				Log.e("startCamera", "Location unknown");
			}
		   
		    //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
		   try {
			  
			   intentFilename =(Utilities.getUserAccount(this).name+new Date().getTime()).hashCode()+".jpg";
			   imageUri = Uri.fromFile(new File(Constants.IMAGE_STORAGE_FILEPATH+intentFilename));
			   if(!(new File(Constants.IMAGE_STORAGE_FILEPATH)).exists()) {
					(new File(Constants.IMAGE_STORAGE_FILEPATH)).mkdirs();
				}
			    //create new Intent
			    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			    startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		} catch (Exception e) {
			Log.e("StartCamera", "Feil", e);
			Toast.makeText(this, "SD card not availiable", Toast.LENGTH_LONG).show();
		}
		    
	}
	
	/**
	 * The method that starts the camera.
	 * The method creates an {@link Intent} of the type {@linkplain MediaStore.ACTION_VIDEO_CAPTURE} which will start the video camera,
	 * and deliver the result back to this activity by starting the intent with the {@link Activity.startActivityForResult()} method.
	 * 
	 * 
	 */
	public void startVideoCamera() {
		//TODO: Må muligens legge til mer attributter		
		
		ContentValues values = new ContentValues();
        values.put(Video.Media.TITLE, "title");
        values.put(Video.Media.BUCKET_ID, "test");
        values.put(Video.Media.DESCRIPTION, "test Image taken");
        try {
        	Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
              
      	    //create new Intent
      	    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//      	    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//Did not work in Android 2.3
      	    intent.putExtra("output", uri.getPath());
      	    startActivityForResult(intent, Constants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
		} catch (Exception e) {
			Toast.makeText(this, "SD card not availiable", Toast.LENGTH_LONG).show();
		}
      
		
	}
	
	/**
	 * The method that starts the camera.
	 * The method creates an {@link Intent} of the type {@linkplain MediaStore.Audio.Media.RECORD_SOUND_ACTION} which will start the audio recorder,
	 * and deliver the result back to this activity by starting the intent with the {@link Activity.startActivityForResult()} method.
	 * 
	 */
	public void startAudioRecording(){
		Intent voiceIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		startActivityForResult(voiceIntent, Constants.RECORD_AUDIO_ACTIVITY_REQUEST_CODE);
	}
	
	/**
	 * The method that starts Note creation activity.
	 * The method creates an {@link Intent} for the {@linkplain NoteActivity} class which will open the Note Activity,
	 * and deliver the result back to this activity by starting the intent with the {@link Activity.startActivityForResult()} method.
	 * 
	 */
	public void createNote(){
		Intent noteIntent = new Intent(this, NoteActivity.class);
		noteIntent.putExtra(Constants.REQUEST_CODE, Constants.CREATE_NOTE_ACTIVITY_REQUEST_CODE);
		startActivityForResult(noteIntent, Constants.CREATE_NOTE_ACTIVITY_REQUEST_CODE);
	}
	
	/**
	 * Method that opens the map view activity
	 */
	
	public void openMapView() {
		if(Utilities.isConnectedToInternet(getApplicationContext())) {
			Intent mapViewIntent = new Intent(this, TimelineMapView.class);	
			mapViewIntent.setAction(Constants.INTENT_ACTION_OPEN_MAP_VIEW_FROM_TIMELINE);
			startActivityForResult(mapViewIntent, Constants.MAP_VIEW_ACTIVITY_REQUEST_CODE);
		}
		else {
			Toast.makeText(getApplicationContext(), "You have to be connected to the internett to use this functionality", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * The method that starts the Attacment dialog.
	 * 
	 */
	public void startAttachmentDialog() {
		new AttachmentAdder(this, this, timeline);
	}
 
	/**
	 * When the Activity is destroyed(ended) the database will be backed up to the SD-card(if availiable), 
	 * and the database closed.
	 * 
	 */
	@Override
	public void onDestroy() {
		DatabaseHelper.backupDBToSDcard(DatabaseHelper.getCurrentTimelineDatabase(), databaseName);
		DatabaseHelper.getCurrentTimelineDatabase().close();
		super.onDestroy();
	}
    
	/**
	 * 
	 * A method that sets all the view components from the layout-xml file.
	 * The methods also adds listeners and sets up the slide animations for the activity.
	 * 
	 * @author andekr
	 * 
	 */
    private void setupViews(){
    	int duration = 1000;
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftIn.setDuration(duration);
		slideLeftOut = AnimationUtils
				.loadAnimation(this, R.anim.slide_left_out);
		slideLeftOut.setDuration(duration);
		slideRightIn = AnimationUtils
				.loadAnimation(this, R.anim.slide_right_in);
		slideRightIn.setDuration(duration);
		slideRightOut = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_out);
		slideRightOut.setDuration(1000);
		
		homeButton = (ImageButton)findViewById(R.id.HomeButton);
		homeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		
		screenTitle = (TextView)findViewById(R.id.TimelineLabel);
		timelineTitleTextView = (TextView) findViewById(R.id.TimelineName);
		if(timeline.getTitle().contains(".db")) {
			timelineTitleTextView.setText(timeline.getTitle().substring(0, timeline.getTitle().indexOf(".db")));
		}else {
			timelineTitleTextView.setText(timeline.getTitle());
		}
		
		
    	cameraButton = (LinearLayout)findViewById(R.id.MenuCameraButton);
    	cameraButton.setOnClickListener(startCameraListener);
    	
    	videoCameraButton = (LinearLayout)findViewById(R.id.MenuVideoCameraButton);
    	videoCameraButton.setOnClickListener(startVideoCameraListener);
    	
    	audioRecorderButton = (LinearLayout)findViewById(R.id.MenuAudioButton);
    	audioRecorderButton.setOnClickListener(startAudioRecorderListener);
    	
    	createNoteButton = (LinearLayout)findViewById(R.id.MenuNoteButton);
    	createNoteButton.setOnClickListener(createNoteListener);
    		
    	attachmentButton = (LinearLayout)findViewById(R.id.MenuAttachmentButton);
    	attachmentButton.setOnClickListener(addAttachmentListener);
     	
    	scrollview = (HorizontalScrollView) findViewById(R.id.HorizontalScrollView01);
    	scrollview.setScrollContainer(true);
    	
    	gridview = (GridView) findViewById(R.id.GridView01);
    	
    	EventAdapter = new TimelineGridAdapter(this, this);
    	
    	
    	try {
    		Date[] dates = timeline.getMinAndMaxDate();
    		//Sets adapter to zoom to the type most appropriate to the timespan of events, and that the default view includes the last event.
    		EventAdapter.setZoomType(Utilities.convertTimeScopeInMillisToZoomType(dates), dates[1]);
		} catch (NullPointerException e) {
			//No experienceevents in TimeLine
//			Log.e(e.getStackTrace()[1].getMethodName(), e.getMessage());
			EventAdapter.setZoomType(Zoom.MONTH, null);
		}
		
		//Sets the adapter that does all the rendering of the activity
    	gridview.setAdapter(EventAdapter);
    	
    	
    	scrollRight(0);
    	
    	zoomControls = (ZoomControls)findViewById(R.id.ZoomControls01);
    	zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
					EventAdapter.zoomOut();
				} catch (MaxZoomedOutException e) {
					Toast.makeText(TimelineActivity.this, "You are max zoomed out", Toast.LENGTH_SHORT).show();
				}
			}
		});
    	
    	
    	
    }
    
    /**
     * This method scrolls the view one time unit(month, week, day, hour) forward.
     * The delay describes the length of the scroll animation.
     * 
     * @param int delay Delay in ms.
     */
    public void scrollRight(int delay) {
        // If we don't call fullScroll inside a Runnable, it doesn't scroll to
        // the bottom but to the (bottom - 1)
    	scrollview.postDelayed(new Runnable() {
            public void run() {
            	scrollview.scrollTo(350, 0);
            	scrollview.setAnimation(AnimationUtils.makeInAnimation(TimelineActivity.this, true));
            }
        }, delay);
    }
    
    /**
     * This method scrolls the view one time unit(month, week, day, hour) back.
     * The delay describes the length of the scroll animation.
     * 
     * @param int delay Delay in ms.
     */
    public void scrollLeftmost(int delay) {
        // If we don't call fullScroll inside a Runnable, it doesn't scroll to
        // the bottom but to the (bottom - 1)
    	scrollview.postDelayed(new Runnable() {
            public void run() {
            	scrollview.scrollTo(0, 0);
            	scrollview.setAnimation(AnimationUtils.makeInAnimation(TimelineActivity.this, true));
            }
        }, delay);
    }
    
    

    // LISTENERS
    
    
    
    private OnClickListener startCameraListener = new OnClickListener() {
		
		public void onClick(View v) {
			startCamera();
		}
	};
	
    private OnClickListener startVideoCameraListener = new OnClickListener() {
		
		public void onClick(View v) {
			startVideoCamera();
		}
	};
	
    private OnClickListener startAudioRecorderListener = new OnClickListener() {
		
		public void onClick(View v) {
			startAudioRecording();
		}
	};
	
	  private OnClickListener createNoteListener = new OnClickListener() {
			
			public void onClick(View v) {
				createNote();
			}
		};
	
	private OnClickListener addAttachmentListener = new OnClickListener() {
		
		public void onClick(View v) {
			startAttachmentDialog();
		}
	};
		
	/**
	 * Method that adds a picture to the timeline.
	 * Creates a {@link SimplePicture}.
	 * Adds it to an {@link Event} if fired from an {@linkplain EventDialog}, else
	 * the method creates a new {@link Event}.
	 * 
	 */
	private void addPictureToTimeline(String filename){
		
		SimplePicture picture = new SimplePicture(this);
		picture.setPictureUri(imageUri, Constants.MEDIASTORE_URL+filename);
		
		
    	if(selectedEvent!=null){
	    	addItemToExistingEvent(picture);
    	}else{
	    	createEventAndAddItem(picture);
    	}
	}
	
	/**
	 * Method that adds a video to the timeline.
	 * Creates a {@link SimpleVideo}.
	 * Adds it to an {@link Event} if fired from an {@linkplain EventDialog}, else
	 * the method creates a new {@link Event}.
	 * 
	 */
	private void addVideoToTimeline(String filename){
		
		SimpleVideo sVideo = new SimpleVideo(this);
		sVideo.setVideoUri(videoUri, Constants.MEDIASTORE_URL+filename);
			
    	if(selectedEvent!=null){
    		addItemToExistingEvent(sVideo);
    	}else{
    		createEventAndAddItem(sVideo);
	   }
	}

	/**
	 * Method that adds a audio to the timeline.
	 * Creates a {@link SimpleRecording}.
	 * Adds it to an {@link Event} if fired from an {@linkplain EventDialog}, else
	 * the method creates a new {@link Event}.
	 * 
	 */
	private void addAudioToTimeline(String filename) {
		 SimpleRecording sRecordring = new SimpleRecording(this);
		 sRecordring.setRecordingUri(audioUri, Constants.MEDIASTORE_URL+filename);
		
		if(selectedEvent!=null){
				addItemToExistingEvent(sRecordring);
		}else{
			createEventAndAddItem(sRecordring);
	   }
	}
	
	/**
	 * Method that adds a note to the timeline.
	 * Creates a {@link SimpleNote}.
	 * Adds it to an {@link Event} if fired from an {@linkplain EventDialog}, else
	 * the method creates a new {@link Event}.
	 * 
	 */
	private void addNoteToTimeline(Intent data) {
		SimpleNote note = new SimpleNote(this);
		if(data.getExtras().getString(Intent.EXTRA_SUBJECT) != null)
			note.setNoteTitle(data.getExtras().getString(Intent.EXTRA_SUBJECT));
		if(data.getExtras().getString(Intent.EXTRA_TEXT) != null)
			note.setNoteText(data.getExtras().getString(Intent.EXTRA_TEXT));
		if(data.getExtras().getString(Intent.EXTRA_TITLE) != null)
			note.setNoteText(data.getExtras().getString(Intent.EXTRA_TITLE));
		
		if(selectedEvent!=null){
			addItemToExistingEvent(note);
		}else{
			createEventAndAddItem(note);
		}
	}
	
	/**
	 * Creates a new {@link Event} and adds an {@link EventItem}.
	 * 
	 * @param evIt The {@link EventItem} to be added to the new {@link Event}
	 */
	private void createEventAndAddItem(EventItem evIt){
		final Event ev = new Event(timeline.getId(), myLocation.getLocation(), Utilities.getUserAccount(getApplicationContext()));
    	timeline.addEvent(ev);
    	ev.addEventItem(evIt);
    	EventAdapter.updateAdapter();
    	contentAdder.addEventToEventContentProvider(ev);
	}
	
	private void addMoodEventToTimeline(final MoodEvent moodEvent) {
		
 		Runnable SendMoodEventRunnable = new Runnable() {
			
			public void run() {
				GoogleAppEngineHandler.persistTimelineObject(moodEvent);
			}
		};
		
		timeline.addEvent(moodEvent);
		EventAdapter.updateAdapter();
		contentAdder.addEventToEventContentProvider(moodEvent);
		Thread sendMoodThread = new Thread(SendMoodEventRunnable, "shareThread");
		sendMoodThread.start();
		Toast.makeText(this, "Your mood is added to server!", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Adds an {@link EventItem} to an existing {@link Event}.
	 * 
	 * @param evIt The {@link EventItem} to be added to the existing {@link Event}
	 */
	private void addItemToExistingEvent(EventItem evIt){
		selectedEvent.addEventItem(evIt);
    	EventAdapter.notifyDataSetChanged();
    	EventAdapter.updateDialog();
    	contentAdder.addEventItemToEventContentProviderIfEventAlreadyExists(selectedEvent, evIt);
	}
	
	/**
	 * Updates an existing note.
	 * 
	 * @param data The {@link Intent} with note data
	 */
	private void updateNote(Intent data){
		 Log.i(this.getClass().getSimpleName(), "********* OPPDATERER NOTE **************");
		 Log.i(this.getClass().getSimpleName(), "Note item plass: "+data.getExtras().getInt("NOTE_ID"));
		 Log.i(this.getClass().getSimpleName(), "Title: "+data.getExtras().getString(Intent.EXTRA_SUBJECT));
		 Log.i(this.getClass().getSimpleName(), "Text: "+data.getExtras().getString(Intent.EXTRA_TEXT));
		 Log.i(this.getClass().getSimpleName(), "*************************************");
		SimpleNote note = (SimpleNote) selectedEvent.getEventItems().get(data.getExtras().getInt("NOTE_ID"));
		note.setNoteTitle(data.getExtras().getString(Intent.EXTRA_SUBJECT));
		note.setNoteText(data.getExtras().getString(Intent.EXTRA_TEXT));
		selectedEvent.getEventItems().set(data.getExtras().getInt("NOTE_ID"), note); 
		
		contentUpdater.updateNoteInDB(note);
		EventAdapter.updateDialog();
}
	private void updateTags(){
		EventAdapter.updateTagDialog();
	}
	
    public Event getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}
	
	public void addEvent(Event event){
		EventAdapter.addEvent(event);
	}
	
	public void removeEvent(Event event){
		timeline.removeEvent(event);
		EventAdapter.removeEvent(event);
		contentDeleter.deleteEventFromDB(event);
	}
	
	public TimelineGridAdapter getEventAdapter(){
		return EventAdapter;
	}

	public void delete(EventItem exItem) {
		contentDeleter.deleteEventItemFromDB(exItem);
		
	}
	
	/**
	 *  Handles click-event in context menu after long click on {@link Event}.
	 *  One element in this context menu: "Delete event".
	 *  
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getGroupId()) {
		case R.id.MENU_DELETE_EVENT:
			Log.v(this.getClass().getSimpleName()+" LONG-CLICK", "DELETE EVENT: "+ selectedEvent.getId());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.Delete_event_confirmation)
				.setPositiveButton(R.string.yes_label, deleteEventListener)
				.setNegativeButton(R.string.no_label, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					selectedEvent = null;
					dialog.cancel();
				}
			})
				.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					selectedEvent = null;
					dialog.cancel()	;					
				}
			});
				
				AlertDialog confirmation = builder.create();
				confirmation.show();
			return true;
			
	}
		return false;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.NEW_MAP_VIEW:
			openMapView();
			return true;
		case R.id.AVERAGE_TIMELINE_MOOD:
			double[] moodCoordinates = GoogleAppEngineHandler.getAverageMoodForExperience(timeline);
			MoodEvent averageMood = new MoodEvent(timeline.getId(), null, MoodEnum.getType(moodCoordinates[0], moodCoordinates[1]), timeline.getUser());
			averageMood.setAverage(true);
			new MoodDialog(this, averageMood).show();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	

	/**
	 * Click listener on delete Event yes button 
	 * 
	 */
	public android.content.DialogInterface.OnClickListener deleteEventListener = new DialogInterface.OnClickListener() {	
		public void onClick(DialogInterface dialog, int which) {
			removeEvent(selectedEvent);
			selectedEvent = null;
		}
	};
	
	public GridView getGridView(){
		return gridview;
	}
	
	public Experience getTimeline(){
		return timeline;
	}

	 @Override 
	 public boolean dispatchTouchEvent(MotionEvent me){ 
	   this.detector.onTouchEvent(me);
	  return super.dispatchTouchEvent(me); 
	 }
	 
	public void onDoubleTap() {
	}

	public void onSwipe(int direction) {
		String str = "";
		  
		  switch (direction) {
		  
		  case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
		  if(scrollview.getScrollX()==0){
		     gridview.startAnimation(slideLeftOut);
		  	 EventAdapter.minusOne();
		  	  str = "Månednummer:"+Utilities.getMonthNumberOfDate(EventAdapter.getZoomDate());
		  	  gridview.startAnimation(slideRightIn);
		  }
		                                           break;
		  case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
		  System.out.println(scrollview.getScrollX());
		  System.out.println(scrollview.getMeasuredWidth());
		  
		  //Hack for Galaxy tab
		  //TODO: THIS HAS TO BE REFACTORED TO ALLOW MULTIPLE SCREEN SIZES
		  if(scrollview.getMeasuredWidth()==1024){
			  if(scrollview.getScrollX()==(scrollview.getMeasuredWidth()-548) || scrollview.getScrollX()==0){
			  	  gridview.startAnimation(slideRightOut);
			  	  EventAdapter.plusOne();
			  	  str = "Månednummer:"+Utilities.getMonthNumberOfDate(EventAdapter.getZoomDate());
			  	  str += "Dag:"+EventAdapter.getZoomDate().getDate();
			  	  gridview.startAnimation(slideLeftIn);
			  	}
		  }
			
		  
		  	if(scrollview.getScrollX()==(scrollview.getMeasuredWidth()-100) || scrollview.getScrollX()==0){
		  	  gridview.startAnimation(slideRightOut);
		  	  EventAdapter.plusOne();
		  	  str = "Månednummer:"+Utilities.getMonthNumberOfDate(EventAdapter.getZoomDate());
		  	  str += "Dag:"+EventAdapter.getZoomDate().getDate();
		  	  gridview.startAnimation(slideLeftIn);
		  	}
		                                                 break;
		                                           
		  } 
	}
	
	public void setTitle(CharSequence title){
		screenTitle.setText(title);
	}
	
	
	public ContentAdder getContentAdder() {
		return contentAdder;
	}

	/**
	 * Overrides the click listener on the device BACK-button.
	 * The new behavior makes the back button zoom out until max zoomed out.
	 * When max zoomed out the user needs to click the back button twice to exit the timeline, and enter the menu.
	 * 
	 */
	@Override
	public void onBackPressed() {
		try {
			EventAdapter.zoomOut();
			backCounter=0;
		} catch (MaxZoomedOutException e) {
			if(backCounter==1){
				System.out.println("Resetting backcounter");
				backCounter=0;
				super.onBackPressed();
			}
			else{
				Toast.makeText(this, "You are max zoomed out, press back once more to go exit timeline mode", Toast.LENGTH_SHORT).show();
				backCounter++;
			}
				
		}
		
	}
	
	/**
	 * Overrides the long click listener on the device BACK-button.
	 * A long click on the back button takes you directly to the dashboard.
	 * 
	 */
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.out.println("Resetting backcounter");
			backCounter=0;
			super.onBackPressed();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * If another button than the back button is pressed the back counter is reseted, so you have to press BACK twice to return to dashboard.
	 * 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			return super.onKeyDown(keyCode, event);
	    }
		else if (keyCode != KeyEvent.KEYCODE_BACK) {
			System.out.println("Resetting backcounter");
			backCounter=0;
	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}

	public MyLocation getMyLocation(){
		return myLocation;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mapviewmenu, menu);
	    return true;
	}
	
	  @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	  
	private void setupMoodButtonQuickAction() {
		final ActionItem happy = new ActionItem();
		
		final Account user = Utilities.getUserAccount(getApplicationContext());
		happy.setIcon(this.getResources().getDrawable(MoodEnum.HAPPY.getIcon()));
		happy.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				addMoodEventToTimeline(new MoodEvent(timeline.getId(), myLocation.getLocation(), MoodEnum.HAPPY, user));
				qa.dismiss();
			}
		});
				
				
		final ActionItem nervous = new ActionItem();
		
		nervous.setIcon(this.getResources().getDrawable(MoodEnum.NERVOUS.getIcon()));
		nervous.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				addMoodEventToTimeline(new MoodEvent(timeline.getId(), myLocation.getLocation(), MoodEnum.NERVOUS, user));
				qa.dismiss();
			}
		});
		
		final ActionItem calm = new ActionItem();
		
		calm.setIcon(this.getResources().getDrawable(MoodEnum.CALM.getIcon()));
		calm.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				addMoodEventToTimeline(new MoodEvent(timeline.getId(), myLocation.getLocation(), MoodEnum.CALM, user));
				qa.dismiss();
			}
		});
		
		final ActionItem sad = new ActionItem();
		
		sad.setIcon(this.getResources().getDrawable(MoodEnum.SAD.getIcon()));
		sad.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				addMoodEventToTimeline(new MoodEvent(timeline.getId(), myLocation.getLocation(), MoodEnum.SAD, user));
				qa.dismiss();
			}
		});
        
    	moodButton = (LinearLayout) findViewById(R.id.MenuMoodButton);
		moodButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				qa = new QuickAction(v);
				
				qa.addActionItem(happy);
				qa.addActionItem(nervous);
				qa.addActionItem(calm);
				qa.addActionItem(sad);
				qa.setAnimStyle(QuickAction.ANIM_AUTO);
				qa.show();
				
			}
		});
		
	}

	public ContentLoader getContentLoader() {
		return contentLoader;
	}	
	
	
	
  
	
}