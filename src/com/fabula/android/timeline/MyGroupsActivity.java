package com.fabula.android.timeline;

import java.util.ArrayList;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.fabula.android.timeline.adapters.ExpandableGroupsListViewAdapter;
import com.fabula.android.timeline.adapters.UserListAdapter;
import com.fabula.android.timeline.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.database.UserGroupDatabaseHelper;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.sync.Downloader;
import com.fabula.android.timeline.sync.GAEHandler;
import com.fabula.android.timeline.sync.UserAndGroupServiceHandler;

public class MyGroupsActivity extends Activity implements ProgressDialogActivity {
	
	private Account userAccount;
	private ImageButton addNewGroupButton, homeButton;
	private User applicationUser;
	private UserListAdapter userlistAdapter;
	private Group selectedGroup;
	private ArrayList <Group> connectedGroups;
	private UserGroupManager uGManager;
	private ExpandableListView myGroupsList;
	private ExpandableGroupsListViewAdapter groupListAdapter;
	private UserGroupDatabaseHelper helper;
	private Runnable getUsersAndGroupsRunnable;
	private ProgressDialog progressDialog;
	private UserAndGroupServiceHandler userAndGroupServiceHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupmenuscreen);
		
		setupHelpers();
		
//		progressDialog = new ProgressDialog(this);
		
		userAndGroupServiceHandler = new UserAndGroupServiceHandler(this, this);
		userAndGroupServiceHandler.startDownloadUsersAndGroups();
//		getUsersAndGroupsRunnable = new Runnable(){
//            public void run() {
//            	getUsersAndGroupsAddToDatabase();
//            }
//        };
        
        
//        startDownloadUsersAndGroups();
			
	}

	
//    /**
//     * Metoden som starter tråden som henter serier fra serveren
//     * Kan vises uten progressDialog, som er hendig når man trykker seg tilbake til startskjermen.
//     * Evt. endring vil da reflekteres, men uten å hindre brukereren i se på lista. 
//     * 
//     * @param showProgressDialog Gir mulighet til å vise/ikke vise progressbar ved lasting av nye elementer.
//     */
//	private void startDownloadUsersAndGroups() {
//		Thread thread =  new Thread(null, getUsersAndGroupsRunnable, "getUsersAndGroups");
//        thread.start();
//        progressDialog = ProgressDialog.show(MyGroupsActivity.this,    
//              "", "", true);
//	}
//	
//	private void getUsersAndGroupsAddToDatabase() {
//		uGManager.truncateUserGroupDatabase();
//		getUsersAndAddToDatabase();
//    	getGroupsAndAddToDatabase();
//    	runOnUiThread(new Runnable() {
//			public void run() {
//				setupViews();
//			}
//		});
//	}
//	
//	private void getUsersAndAddToDatabase() {
//		runOnUiThread(new Runnable() {
//			
//			public void run() {
//				progressDialog.setMessage("Loading all users ...");
//			}
//		});
//		
//		Users selectableUsersFromServer = Downloader.getUsersFromServer();
//		if(selectableUsersFromServer!=null){
//			for (User user : selectableUsersFromServer.getUsers()) {
//				uGManager.addUserToUserDatabase(user);
//			}
//		}
//		
//		
//	}	
//	
//	private void getGroupsAndAddToDatabase() {
//		runOnUiThread(new Runnable() {
//			public void run() {
//				progressDialog.setMessage("Loading my groups ...");
//			}
//		});
//		Groups groupsUserIsConnectedToFromServer = Downloader.getGroupsFromServer(applicationUser);
//		
//		if(groupsUserIsConnectedToFromServer!=null){
//			for (Group groupToAddToDatabase : groupsUserIsConnectedToFromServer.getGroups()) {
//				uGManager.addGroupToGroupDatabase(groupToAddToDatabase);
//				for (User user : groupToAddToDatabase.getMembers()) {
//					uGManager.addUserToAGroupInTheDatabase(groupToAddToDatabase, user);
//				}
//			}
//		}
//		
//		
//	}
	
	/**
	 * Add a new group to the database
	 * @param groupName. The group name of the new group
	 */

	protected void addNewGroup(String groupName) {
		Group group = new Group(groupName);
		uGManager.addGroupToGroupDatabase(group);
		uGManager.addUserToAGroupInTheDatabase(group, applicationUser);
		group.addMembers(applicationUser);
		connectedGroups.add(group);
		groupListAdapter.notifyDataSetChanged();
		Toast.makeText(MyGroupsActivity.this.getApplicationContext(), "You have created the group: " +group.toString() , Toast.LENGTH_SHORT).show();
		GAEHandler.addGroupToServer(group);
		
	}
	
	/**
	 * Add new user to a group if they don't already are there
	 * @param selectedUsers
	 */
	protected void addUsersToGroup(ArrayList<User> selectedUsers) {
		for (User user : selectedUsers) {
//			if(!isAlreadyPartOfGroup(user, selectedGroup)) {
				uGManager.addUserToAGroupInTheDatabase(selectedGroup, user);
				GAEHandler.addUserToGroupOnServer(selectedGroup, user);
				selectedGroup.addMembers(user);
//			}
		}
		Toast.makeText(this, "New users has been added to group "+selectedGroup+"!", Toast.LENGTH_SHORT).show();
		userlistAdapter.notifyDataSetChanged();
	}
	
//	set the selected group
	private void setSelectedGroup(Group group) {
		this.selectedGroup = group;
	}
	
	public Group getSelectedGroup() {
		return selectedGroup;
	}
	
	/**
	 * Leaves the selected group
	 */
	protected void leaveGroup() {
		
		GAEHandler.removeUserFromGroupOnServer(selectedGroup, applicationUser);
		uGManager.removeUserFromAGroupInTheDatabase(selectedGroup, applicationUser);
		connectedGroups.remove(selectedGroup);
		selectedGroup.removeMember(applicationUser);
		
		//delete the group if it has no members
		if(selectedGroup.getMembers().isEmpty()) {
			deleteGroupFromDatabase(selectedGroup);
			GAEHandler.removeGroupFromDatabase(selectedGroup);
		}
		
		groupListAdapter.notifyDataSetChanged();
	}
	
	private void deleteGroupFromDatabase(Group group) {
		uGManager.deleteGroupFromDatabase(group);
	}
	
	/**
	 * Get all groups from the database connected to the user using the application
	 * @param user. The user using the application
	 * @return a list of all the groups the user are a part of
	 */
	private ArrayList <Group> getAllGroupsConnectedToUser(Account user) {
		
		//TODO move this to appropriate place
		uGManager.addUsersToUserDatabase(Downloader.getUsersFromServer().getUsers());
		ArrayList <Group> allGroups = uGManager.getAllGroupsConnectedToAUser(applicationUser);
		return allGroups;
	}
	
	/**
	 * Get all users from the database
	 * @return ArrayList with all the users
	 */
	private ArrayList<User> getAllUsers() {
		ArrayList<User> users = uGManager.getAllUsersFromDatabase();
		return users;
	}
	
	/**
	 * Checks what item type that has been selected in a long press context menu
	 */

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getGroupId()) {
		
		case R.id.MENU_DELETE_ITEM:
			leaveGroupConfirmationDialog();
			break;
		case R.id.MENU_ADD_USER:
			openSelectUserToAddDialog();
			break;
		}
		return false;
	}
	/**
	 * Closes the database when the back-button is pressed
	 */
	@Override
	public void onBackPressed() {
		helper.close();
		super.onBackPressed();
	}
	/**
	 * Checks if a username already is part of a group (the user name is unique by default)
	 * @param user the user to be checked
	 * @param group the group to be checked
	 * @return true if the user already is a part of the group, false otherwise
	 */
	private boolean isAlreadyPartOfGroup(User user, Group group) {
		
		for (User u : group.getMembers()) {
			if(user.getUserName().equals(u.getUserName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Dialog for selecting wich user to add to the group
	 */
	private void openSelectUserToAddDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		ListView userList = new ListView(this);
		userList.setBackgroundColor(this.getResources().getColor(R.color.White));
		userList.setCacheColorHint(this.getResources().getColor(android.R.color.transparent));
		userlistAdapter = new UserListAdapter(this, getAllUsersNotInGroupAlready(selectedGroup) , selectedGroup);
		userList.setAdapter(userlistAdapter);
				
		builder.setView(userList);
		
		builder.setMessage("Select users to add:")
		.setPositiveButton("Add users", addUserDialogListener)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	})
		.setOnCancelListener(new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();					
		}
	});
		
		AlertDialog confirmation = builder.create();
		if(userlistAdapter.getCount()>0)
			confirmation.show();
		else
			Toast.makeText(this, "No more users to add", Toast.LENGTH_SHORT).show();
	}
	
	private ArrayList<User> getAllUsersNotInGroupAlready(Group selectedGroup) {
		ArrayList<User> usersNotInGroup = new ArrayList<User>();
		
		for (User user : getAllUsers()) {
			if(!isAlreadyPartOfGroup(user, selectedGroup))
				usersNotInGroup.add(user);
		}
	return usersNotInGroup;
}


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
	 * Confirmation dialog that pops when you tries to leave a group
	 */
	
	private void leaveGroupConfirmationDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you really want to leave group " +selectedGroup.toString()+"?")
		.setPositiveButton(R.string.yes_label, leaveGroupConfirmationListener)
		.setNegativeButton(R.string.no_label, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			setSelectedGroup(null);
			dialog.dismiss();
		}
	})
		.setOnCancelListener(new OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			setSelectedGroup(null);
			dialog.dismiss();					
		}
	});
		AlertDialog confirmation = builder.create();
		confirmation.show();
	}
	
	//listeners
	/**
	 * Add user dialog listener
	 */
	private OnClickListener addUserDialogListener = new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			addUsersToGroup(userlistAdapter.getSelectedUsers());
			setSelectedGroup(null);
			dialog.dismiss();
		}
	};
	
	/**
	 * Confirmation dialog listener
	 */
	private android.content.DialogInterface.OnClickListener leaveGroupConfirmationListener = new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			
			leaveGroup();
			Toast.makeText(MyGroupsActivity.this.getApplicationContext(), "You have left group: "+selectedGroup.toString() , Toast.LENGTH_SHORT).show();
			setSelectedGroup(null);
			dialog.dismiss();
		}
	};

	/**
	 * Listener for a long click on an Item in the group list view
	 */
	private OnItemLongClickListener openItemLongClickMenuListener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> view, View arg1,
				int position, long arg3) {
					
			view.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					
					//make sure that the selectedGroup is not out of bounds:
					ExpandableListView.ExpandableListContextMenuInfo info =
						(ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
					MyGroupsActivity.this.setSelectedGroup(groupListAdapter.getGroup(ExpandableListView.getPackedPositionGroup(info.packedPosition)));
					
					menu.add(R.id.MENU_ADD_USER,0,0, R.string.Add_user_label);
					menu.add(R.id.MENU_DELETE_ITEM, 0,0, R.string.Leave_group_label);
				}
			});
			return false;
		}
	};
	
	private android.view.View.OnClickListener newGroupButtonListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			openNewGroupNameInputDialog();
		}
	};
	
	private void setupHelpers() {
		uGManager = new UserGroupManager(this);
		helper = new UserGroupDatabaseHelper(this, Utilities.USER_GROUP_DATABASE_NAME);
		userAccount = (Account) getIntent().getParcelableExtra("ACCOUNT");
		applicationUser = new User(userAccount.name);
	}
	
	/**
	 * Setup views and instansiate objects the activity is going to use
	 */
	private void setupViews() {
		myGroupsList = (ExpandableListView) findViewById(R.id.groupsList);
		
		addNewGroupButton = (ImageButton) findViewById(R.id.my_groups);
		addNewGroupButton.setOnClickListener(newGroupButtonListener);
		
		connectedGroups = getAllGroupsConnectedToUser(userAccount);
		groupListAdapter = new ExpandableGroupsListViewAdapter(this, connectedGroups);
		
		myGroupsList.setAdapter(groupListAdapter);
		this.registerForContextMenu(myGroupsList);
		myGroupsList.setOnItemLongClickListener(openItemLongClickMenuListener);

		
		homeButton = (ImageButton)findViewById(R.id.GroupHomeButto);
		homeButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				helper.close();
				finish();
			}
		});
		
	}

	public void callBack() {
		setupViews();
		
	}

}
