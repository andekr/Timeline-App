package com.fabula.android.timeline;

import java.util.ArrayList;

import com.fabula.android.timeline.adapters.GroupListAdapter;
import com.fabula.android.timeline.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.database.UserGroupDatabaseHelper;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MyGroupsActivity extends Activity {
	
	private Account userAccount;
	private ListView myGroupsList;
	private ImageButton addNewGroupButton, homeButton;
	private User applicationUser;
	private GroupListAdapter groupListAdapter;
	private Group selectedGroup;
	private ArrayList <Group> connectedGroups;
	private UserGroupManager uGManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupmenuscreen);
		uGManager = new UserGroupManager(this);
		setupViews();
	}
	
	/**
	 * Add a new group to the database
	 * @param groupName. The group name of the new group
	 */

	protected void addNewGroup(String groupName) {
		Group group = new Group(groupName);
		
		UserGroupDatabaseHelper helper = new UserGroupDatabaseHelper(this, Utilities.USER_GROUP_DATABASE_NAME);
		uGManager.addGroupToGroupDatabase(group);
		uGManager.addUserToAGroupInTheDatabase(group, applicationUser);
		group.addMembers(applicationUser);
		connectedGroups.add(group);
		helper.close();
		Toast.makeText(MyGroupsActivity.this.getApplicationContext(), "You have created the group: " +group.toString() , Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Get all groups from the database connected to the user using the application
	 * @param user. The user using the application
	 * @return a list of all the groups the user are a part of
	 */
	private ArrayList <Group> getAllGroupsConnectedToUser(Account user) {
		
		UserGroupDatabaseHelper helper = new UserGroupDatabaseHelper(this, Utilities.USER_GROUP_DATABASE_NAME);
		ArrayList <Group> allGroups = uGManager.getAllGroupsConnectedToAUser(applicationUser);
		helper.close();
		return allGroups;
	}
	
//	set the selected group
	private void setSelectedGroup(Group group) {
		this.selectedGroup = group;
	}
	
	/**
	 * Leaves the selected group
	 */
	protected void leaveGroup() {
		
		UserGroupDatabaseHelper helper = new UserGroupDatabaseHelper(this, Utilities.USER_GROUP_DATABASE_NAME);
		uGManager.removeUserFromAGroupInTheDatabase(selectedGroup, applicationUser);
		connectedGroups.remove(selectedGroup);
		selectedGroup.removeMember(applicationUser);
		
		//delete the group if it has no members
		if(selectedGroup.getMembers().isEmpty()) {
			deleteGroupFromDatabase(selectedGroup);
		}
		groupListAdapter.remove(selectedGroup);
		helper.close();
	}
	
	private void deleteGroupFromDatabase(Group group) {
		uGManager.deleteGroupFromDatabase(group);
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getGroupId()) {
		
		case R.id.MENU_DELETE_ITEM:
			leaveGroupConfirmationDialog();
			break;
		}
		return false;
	}
	
	private OnClickListener newGroupButtonListener = new OnClickListener() {
		public void onClick(View v) {
			openNewGroupNameInputDialog();
		}
	};
	
	/**
	 * Confirmation dialog that pops when you tries to leave a group
	 */
	
	private void leaveGroupConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.Leave_group_confirmation)
		.setPositiveButton(R.string.yes_label, leaveGroupConfirmationListener)
		.setNegativeButton(R.string.no_label, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			selectedGroup = null;
			dialog.cancel();
		}
	})
		.setOnCancelListener(new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			selectedGroup = null;
			dialog.cancel()	;					
		}
	});
		AlertDialog confirmation = builder.create();
		confirmation.show();
	}
	
	/**
	 * Confirmation dialog listener
	 */
	private android.content.DialogInterface.OnClickListener leaveGroupConfirmationListener = new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			leaveGroup();
			Toast.makeText(MyGroupsActivity.this.getApplicationContext(), "You have left group: "+selectedGroup.toString() , Toast.LENGTH_SHORT).show();
			selectedGroup = null;
			dialog.dismiss();
		}
	};

	/**
	 * Listener for a long click on an Item in the group list view
	 */
	private OnItemLongClickListener openItemLongClickMenuListener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> view, View arg1,
				int position, long arg3) {
			
			MyGroupsActivity.this.setSelectedGroup(groupListAdapter.getItem(position));
			
			view.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					menu.add(R.id.MENU_DELETE_ITEM, 0,0, R.string.Leave_group_label);
				}
			});
			return false;
		}
	};
	
	/**
	 * Input dialog for the writing the name of a new group
	 */
	private void openNewGroupNameInputDialog() {
		
		final AlertDialog.Builder groupNameInputDialog = new AlertDialog.Builder(
				this);
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
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
				dialog.cancel();

			}
		});
		
		groupNameInputDialog.show();
	}

	private void setupViews() {
		myGroupsList = (ListView) findViewById(R.id.groupsList);
		
		
		addNewGroupButton = (ImageButton) findViewById(R.id.my_groups);
		addNewGroupButton.setOnClickListener(newGroupButtonListener);
		
		userAccount = (Account) getIntent().getParcelableExtra("ACCOUNT");
		applicationUser = new User(userAccount.name);
		
		connectedGroups = getAllGroupsConnectedToUser(userAccount);
		groupListAdapter = new GroupListAdapter(this, connectedGroups);
		
		myGroupsList.setAdapter(groupListAdapter);
		myGroupsList.setOnItemLongClickListener(openItemLongClickMenuListener);

		
		homeButton = (ImageButton)findViewById(R.id.GroupHomeButto);
		homeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}

}
