package com.fabula.android.timeline;

import java.util.ArrayList;

import com.fabula.android.timeline.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.database.UserGroupDatabaseHelper;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class MyGroupsActivity extends Activity {
	
	private Account userAccount;
	private ListView myGroupsList;
	private ImageButton addNewGroupButton;
	private User applicationUser;
	private ArrayAdapter<Group> groupListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupmenuscreen);
		
		myGroupsList = (ListView) findViewById(R.id.groupsList);
		
		
		addNewGroupButton = (ImageButton) findViewById(R.id.my_groups);
		addNewGroupButton.setOnClickListener(newGroupButtonListener);
		
		userAccount = (Account) getIntent().getParcelableExtra("ACCOUNT");
		applicationUser = new User(userAccount.name);
		
		groupListAdapter = new ArrayAdapter<Group>(this, R.layout.mygroupslist, getAllGroupsConnectedToUser((userAccount)));
		myGroupsList.setAdapter(groupListAdapter);
		
	}
	
	private OnClickListener newGroupButtonListener = new OnClickListener() {
		public void onClick(View v) {
			openNewGroupNameInputDialog();
		}
	};

	
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

	protected void addNewGroup(String groupName) {
		Group group = new Group(groupName);
		
		UserGroupDatabaseHelper helper = new UserGroupDatabaseHelper(this, Utilities.USER_GROUP_DATABASE_NAME);
		UserGroupManager uGManager = new UserGroupManager(this);
		uGManager.addGroupToGroupDatabase(group);
		uGManager.addUserToAGroupInTheDatabase(group, applicationUser);
		groupListAdapter.add(group);
		helper.close();
	}
	
	private ArrayList <Group> getAllGroupsConnectedToUser(Account user) {
		
		UserGroupDatabaseHelper helper = new UserGroupDatabaseHelper(this, Utilities.USER_GROUP_DATABASE_NAME);
		UserGroupManager uGManager = new UserGroupManager(this);
		ArrayList <Group> connectedGroups = uGManager.getAllGroupsConnectedToAUser(new User(user.name));
		
		helper.close();
		return connectedGroups;
	}

}
