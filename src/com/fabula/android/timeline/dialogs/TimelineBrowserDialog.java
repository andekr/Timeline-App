package com.fabula.android.timeline.dialogs;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.TimelineActivity;
import com.fabula.android.timeline.TimelineDirectory;
import com.fabula.android.timeline.adapters.TimelineListAdapter;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;
import com.fabula.android.timeline.database.contentmanagers.ContentDeleter;
import com.fabula.android.timeline.database.contentmanagers.ContentLoader;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.utilities.Constants;

public class TimelineBrowserDialog extends Dialog {

		private Context context;
		private Experience selectedTimeline;
		private TimelineDirectory directory;
		private TimelineListAdapter timelineAdapter;
		private Intent receivedIntent;
		
/**
 * {@link Dialog} to browse list of timelines. Sets up handlers for opening and deleting experiences(timelines).
 * Takes an {@link Intent} as parameter since the dialog is opened when attaching events from outside of the application.
 * 
 * @param context the {@link Context} to add the {@link Dialog}
 * @param receivedIntent the {@link Intent} that will start {@link TimelineActivity}
 * @param shared Shows shared timelines if true, else all timelines are showed
 */
public TimelineBrowserDialog(Context context, Intent receivedIntent, int shared) {
		super(context);
		this.context = context;
		ListView view = new ListView(context);
		directory = new TimelineDirectory();
		this.receivedIntent = receivedIntent;
		ContentLoader contentLoader = new ContentLoader(context);
		ArrayList<Experience> allExperiences =null;
		switch (shared) {
		case Constants.SHARED_ALL:
			allExperiences = contentLoader.LoadAllExperiencesFromDatabase();
			break;
		case Constants.SHARED_TRUE:
			allExperiences = contentLoader.LoadAllSharedExperiencesFromDatabase();
			break;
		case Constants.SHARED_FALSE:
			allExperiences = contentLoader.LoadPrivateExperiencesFromDatabase();
			break;

		default:
			allExperiences = contentLoader.LoadAllExperiencesFromDatabase();
			break;
		}
		for (Experience experience : allExperiences) {
			Log.i("Alle experiences", experience.getTitle()+" Delt: "+experience.isShared());
		}	
		timelineAdapter = new TimelineListAdapter(context, allExperiences); 
		

		this.setTitle(context.getResources().getString(R.string.Select_Timeline_label));
		
		view.setBackgroundColor(context.getResources().getColor(R.color.White));
		view.setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
		view.setAdapter(timelineAdapter);
		view.setOnItemClickListener(timelineListListener);
		view.setOnItemLongClickListener(openOrDeleteTimelineListener);
		this.setContentView(view);
	}
	
	public int getNumberOfTimelinesSaved() {
		return timelineAdapter.getCount();
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getGroupId()) {
		
		case R.id.MENU_DELETE_TIMELINE:
			openDeleteTimelineConfirmationDialog();
			this.dismiss();
			break;
		case R.id.MENU_OPEN_TIMELINE:
			openExperience(selectedTimeline);
			this.dismiss();
			break;
		}
		return false;
	}
	
	public android.content.DialogInterface.OnClickListener deleteTimelineListener = new DialogInterface.OnClickListener() {	
		
		public void onClick(DialogInterface dialog, int which) {
			directory.deleteTimeline(selectedTimeline.getTitle()+".db");
			new TimelineDatabaseHelper(context, Constants.ALL_TIMELINES_DATABASE_NAME);
			ContentDeleter contentDeleter = new ContentDeleter(context);
			contentDeleter.deleteExperienceFromDB(selectedTimeline);
			
			Toast.makeText(context, "Timeline: "+selectedTimeline.toString()+" has been deleted from the phone", Toast.LENGTH_SHORT);
			selectedTimeline = null;
			dialog.dismiss();
		}
	};
	
	protected void openExperience(Experience experience) {
		String databaseName = experience.getTitle() + ".db";
		boolean shared = experience.isShared();
		String id = experience.getId();
		
		receivedIntent.putExtra(Constants.DATABASENAME_REQUEST, databaseName);
		receivedIntent.putExtra(Constants.SHARED_REQUEST, shared);
		receivedIntent.putExtra(Constants.EXPERIENCEID_REQUEST, id);
		receivedIntent.putExtra(Constants.EXPERIENCECREATOR_REQUEST, experience.getUser().name);
		
		context.startActivity(receivedIntent);
		this.dismiss();
	}

	private OnItemClickListener timelineListListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			Experience timeline = timelineAdapter.getItem(position);
			openExperience(timeline);
			Toast.makeText(context, timeline.getTitle(), Toast.LENGTH_SHORT).show();
		}
	};
	
	private OnItemLongClickListener openOrDeleteTimelineListener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long id) {
			
			TimelineBrowserDialog.this.setSelectedTimeline(timelineAdapter.getItem(position));
			arg0.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					if(receivedIntent.getAction().equals(Constants.INTENT_ACTION_ADD_TO_TIMELINE))
						menu.add(R.id.MENU_OPEN_TIMELINE, 0, 0, R.string.Add_to_timeline_label);
					else
						menu.add(R.id.MENU_OPEN_TIMELINE, 0, 0, R.string.Open_timeline_label);
					menu.add(R.id.MENU_DELETE_TIMELINE, 0,0, R.string.Delete_timeline_label);
				}
			});
			
			return false;
		}
	};

	
	public void setSelectedTimeline(Experience timeline) {
		this.selectedTimeline = timeline;
	}
	
	public Experience getSelectedTimeline() {
		
		return this.selectedTimeline;
	}
	
	private boolean openDeleteTimelineConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(R.string.Delete_timeline_confirmation)
			.setPositiveButton(R.string.yes_label, deleteTimelineListener)
			.setNegativeButton(R.string.no_label, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				selectedTimeline = null;
				dialog.cancel();
			}
		})
			.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				selectedTimeline = null;
				dialog.cancel()	;					
			}
		});

			AlertDialog confirmation = builder.create();
			confirmation.show();
		return true;
	}
}
