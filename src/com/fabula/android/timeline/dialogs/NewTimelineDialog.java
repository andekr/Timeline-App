package com.fabula.android.timeline.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.TimelineActivity;
import com.fabula.android.timeline.adapters.GroupListAdapter;
import com.fabula.android.timeline.database.DatabaseHelper;
import com.fabula.android.timeline.database.contentmanagers.ContentAdder;
import com.fabula.android.timeline.database.contentmanagers.ContentUpdater;
import com.fabula.android.timeline.database.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.sync.GoogleAppEngineHandler;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.Utilities;

public class NewTimelineDialog extends Dialog {
	
	private static final String TAG = "NewTimelineDialog";
	
	private Context context; 
	private Experience experienceToEdit;
	private User user;
	private ListView groupList;
	private GroupListAdapter groupListAdapter;
	private UserGroupManager uGManager;
	private Button okButton, cancelButton, addGroupButton;
	private TextView selectedGroupText;
	private EditText inputTextField;
	private ToggleButton shareToggle;
	private Group selectedGroup;
	private Intent timelineIntent;

	private ContentAdder contentAdder;

	public NewTimelineDialog(final Context context, final Experience experienceToEdit, Intent timelineIntent) {
		super(context);
		setContentView(R.layout.newtimelinedialog);
		this.context = context;
		this.experienceToEdit = experienceToEdit;
		this.user = new User(Utilities.getUserAccount(context).name);
		this.timelineIntent = timelineIntent;
		
		contentAdder = new ContentAdder(context);
		
		if(experienceToEdit!=null)
			this.setTitle("Select a group to share with");
		else
			this.setTitle("Create a new timeline");
		uGManager = new UserGroupManager(this.context);
		
		groupList = (ListView) findViewById(R.id.sharedtimelinegroupslist);
		groupListAdapter = new GroupListAdapter(this.context, uGManager.getAllGroupsConnectedToAUser(user));
		groupList.setAdapter(groupListAdapter);
		
		addGroupButton = (Button) findViewById(R.id.newgroupbutton_in_timelinedialog);
		addGroupButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				openNewGroupNameInputDialog();
			}
		});
		

		
		selectedGroupText = (TextView) findViewById(R.id.selectedGroupText);
		selectedGroupText.setText("Select a group to share with:");
		
		inputTextField = (EditText)findViewById(R.id.TimelineNameEditText);
		shareToggle = (ToggleButton)findViewById(R.id.ShareTimelineToggleButton);
		shareToggle.setEnabled(Utilities.isConnectedToInternet(context));
		
		shareToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					selectedGroupText.setVisibility(View.VISIBLE);
					groupList.setVisibility(View.VISIBLE);
					addGroupButton.setVisibility(View.VISIBLE);
				}
				
				else {
					selectedGroupText.setVisibility(View.GONE);
					groupList.setVisibility(View.GONE);	
					addGroupButton.setVisibility(View.INVISIBLE);
					selectedGroup = null;
					selectedGroupText.setText("Select a group to share with");
				}
			}
		});
		
		if(experienceToEdit!=null){
			shareToggle.setChecked(true);
			shareToggle.setEnabled(false);
			inputTextField.setText(experienceToEdit.getTitle());
			inputTextField.setEnabled(false);
		}
		
		groupList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				selectedGroupText.setText("Share with group: " + groupList.getAdapter().getItem(position));
				selectedGroup = (Group) groupList.getAdapter().getItem(position);
			}
		});
				

		
		okButton = (Button) findViewById(R.id.okbutton);
		okButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if(shareToggle.isChecked() && selectedGroup == null) {
					Toast.makeText(context, "You have to select a group to share the timeline with!", Toast.LENGTH_SHORT).show();
				}
				else {
					String inputName = inputTextField.getText().toString().trim();
					boolean share = shareToggle.isChecked();
					if(experienceToEdit!=null){
						experienceToEdit.setShared(true);
						experienceToEdit.setSharingGroupObject(selectedGroup);
						ContentUpdater contentUpdater = new ContentUpdater(context);
						contentUpdater.updateExperience(experienceToEdit);
						Toast.makeText(context, "Timeline: "+experienceToEdit.toString()+" has been shared with "+selectedGroup.toString(), Toast.LENGTH_SHORT).show();
					}else
						createNewTimeline(inputName, share, selectedGroup);
					
					NewTimelineDialog.this.dismiss();
				}
			}
		});
		
		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				NewTimelineDialog.this.dismiss();
			}
		});
		
	}
	
					
	private void openNewGroupNameInputDialog() {
		
		final AlertDialog.Builder groupNameInputDialog = new AlertDialog.Builder(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.newgroupdialog, (ViewGroup) findViewById(R.id.newgroupdialogroot));
		groupNameInputDialog.setView(layout);
		
		final EditText inputTextField = (EditText)layout.findViewById(R.id.NewGroupeditText);

		groupNameInputDialog.setTitle("Enter a name for your group!");
		groupNameInputDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String inputName = inputTextField.getText().toString().trim();
				addNewGroup(inputName);
				dialog.dismiss();
			}
		});

		groupNameInputDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		}).setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				
			}
		});
		
		groupNameInputDialog.show();
	}
	
	/**
	 * Add a new group to the database
	 * @param groupName The name of the new group
	 */
	protected void addNewGroup(String groupName) {
		
		Group group = new Group(groupName);
		uGManager.addGroupToGroupDatabase(group);
		uGManager.addUserToAGroupInTheDatabase(group, user);
		group.addMembers(user);
		GoogleAppEngineHandler.addGroupToServer(group);
		Toast.makeText(context, "You have created the group: " +group.toString() , Toast.LENGTH_SHORT).show();
	}

	/**
	 * Creates a new timeline and starts the Timeline activity
	 * 
	 * @param timelineName String. Name of the new Timeline
	 * @param shared boolean If the Timeline should be shared
	 */
	private void createNewTimeline(String timelineName, boolean shared, Group group) {

		Experience timeLine = new Experience(timelineName, shared, Utilities.getUserAccount(context));
		
		String databaseName = timeLine.getTitle() + ".db";
		
		timelineIntent.putExtra(Constants.DATABASENAME_REQUEST, databaseName);
		timelineIntent.putExtra(Constants.SHARED_REQUEST, shared);
		timelineIntent.putExtra(Constants.EXPERIENCEID_REQUEST, timeLine.getId());
		timelineIntent.putExtra(Constants.EXPERIENCECREATOR_REQUEST, timeLine.getUser().name);
		
		if(shared) {
			timeLine.setSharingGroupObject(group);
			timelineIntent.putExtra(Constants.SHARED_WITH_REQUEST, timeLine.getSharingGroupObject().getId());
		}
		
		
		contentAdder.addExperienceToTimelineContentProvider(timeLine);
		new DatabaseHelper(context, databaseName);
		context.startActivity(timelineIntent);
		DatabaseHelper.getCurrentTimelineDatabase().close();
	}

	
}

