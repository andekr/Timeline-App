package com.fabula.android.timeline;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.fabula.android.timeline.adapters.TagListAdapter;
import com.fabula.android.timeline.contentmanagers.TagManager;
import com.fabula.android.timeline.database.TimelineDatabaseHelper;

public class MyTagsActivity extends Activity {

	private Button addNewTagButton, showInTimelineButton;
	private ImageButton homeButton;
	private ArrayList <String> allTags;
	private TagManager tagManager;
	private ListView myTagsList;
	private TagListAdapter tagListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagactivitylayout);
		
		setupHelpers();
		setupViews();
		
		
		if(isNewTagIntent()){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			openNewTagNameInputDialog();
		}
		
	}

	private boolean isNewTagIntent() {
		return getIntent().getAction()!= null && getIntent().getAction().equals(Utilities.INTENT_ACTION_NEW_TAG);
	}
	
	/**
	 * Add a new group to the database
	 * @param groupName. The group name of the new group
	 */

	protected void addNewTag(String tagName) {
		tagManager.addTagToDatabase(tagName);
		Toast.makeText(MyTagsActivity.this.getApplicationContext(), "You have created the tag: " +tagName , Toast.LENGTH_SHORT).show();
		if(isNewTagIntent()){
	        setResult(RESULT_OK, getIntent());
			finish();
		}
			
		else
			setupViews();
	}
	


	/**
	 * Input dialog for the writing the name of a new group
	 */
	private void openNewTagNameInputDialog() {
		
		final AlertDialog.Builder tagNameInputDialog = new AlertDialog.Builder(
				this);
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.newgroupdialog, (ViewGroup) findViewById(R.id.newgroupdialogroot));
		tagNameInputDialog.setView(layout);
		
		final EditText inputTextField = (EditText)layout.findViewById(R.id.NewGroupeditText);
		inputTextField.setHint("Enter a tag name");

		tagNameInputDialog.setTitle("Enter a name for tag!");
		tagNameInputDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String inputName = inputTextField.getText().toString().trim();
				addNewTag(inputName);
				dialog.dismiss();
			}
		});

		tagNameInputDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		}).setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				
			}
		});
		
		tagNameInputDialog.show();
	}
	
	/**
	 * Confirmation dialog that pops when you tries to leave a group
	 */
	
	//listeners
	private android.view.View.OnClickListener newTagButtonListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			openNewTagNameInputDialog();
		}
	};
	
	private void setupHelpers() {
		new TimelineDatabaseHelper(this, Utilities.ALL_TIMELINES_DATABASE_NAME);
		tagManager = new TagManager(this);
	}
	
	
	
	/**
	 * Setup views and instansiate objects the activity is going to use
	 */
	private void setupViews() {
		myTagsList = (ListView) findViewById(R.id.tagListlistView);
		
		addNewTagButton = (Button) findViewById(R.id.tagsCreateButton);
		addNewTagButton.setOnClickListener(newTagButtonListener);
		
		
		showInTimelineButton = (Button) findViewById(R.id.tagShowInTimelineButton);
//		showInTimelineButton.setOnClickListener(newTagButtonListener);
		
		allTags = tagManager.getAllTags();
		System.out.println("Antall tags: "+allTags.size());
		tagListAdapter = new TagListAdapter(this, allTags, new ArrayList<String>());
		
		myTagsList.setAdapter(tagListAdapter);
		
		homeButton = (ImageButton)findViewById(R.id.TagHomeButton);
		homeButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
	}

}
