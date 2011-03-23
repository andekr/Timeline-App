package com.fabula.android.timeline.sync;

import android.app.ProgressDialog;
import android.content.Context;

import com.fabula.android.timeline.ProgressDialogActivity;
import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.Groups;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.models.Users;

public class UserAndGroupServiceHandler {

	private Context mContext;
	private ProgressDialog progressDialog;
	private Runnable getUsersAndGroupsRunnable;
	private UserGroupManager uGManager;
	private ProgressDialogActivity activity;

	public UserAndGroupServiceHandler(ProgressDialogActivity activity, Context context) {
		this.mContext = context;
		this.activity = activity;
		progressDialog = new ProgressDialog(mContext);
		
		uGManager = new UserGroupManager(mContext);
		getUsersAndGroupsRunnable = new Runnable(){
            public void run() {
            	getUsersAndGroupsAddToDatabase();
            }
        };
        
        startDownloadUsersAndGroups();
	}
    /**
     * Metoden som starter tråden som henter serier fra serveren
     * Kan vises uten progressDialog, som er hendig når man trykker seg tilbake til startskjermen.
     * Evt. endring vil da reflekteres, men uten å hindre brukereren i se på lista. 
     * 
     * @param showProgressDialog Gir mulighet til å vise/ikke vise progressbar ved lasting av nye elementer.
     */
	private void startDownloadUsersAndGroups() {
		Thread thread =  new Thread(null, getUsersAndGroupsRunnable, "getUsersAndGroups");
        thread.start();
        progressDialog = ProgressDialog.show(mContext,    
              "", "", true);
	}
	
	private void getUsersAndGroupsAddToDatabase() {
		uGManager.truncateUserGroupDatabase();
		getUsersAndAddToDatabase();
    	getGroupsAndAddToDatabase();
    	activity.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.dismiss();
				activity.callBack();
			}
		});
	}
	
	private void getUsersAndAddToDatabase() {
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				progressDialog.setMessage("Loading all users ...");
			}
		});
		
		Users selectableUsersFromServer = Downloader.getUsersFromServer();
		if(selectableUsersFromServer!=null){
			for (User user : selectableUsersFromServer.getUsers()) {
				uGManager.addUserToUserDatabase(user);
			}
		}	
	}	
	
	private void getGroupsAndAddToDatabase() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.setMessage("Loading my groups ...");
			}
		});
		Groups groupsUserIsConnectedToFromServer = Downloader.getGroupsFromServer(new User(Utilities.getUserAccount(mContext).name));
		
		if(groupsUserIsConnectedToFromServer!=null){
			for (Group groupToAddToDatabase : groupsUserIsConnectedToFromServer.getGroups()) {
				uGManager.addGroupToGroupDatabase(groupToAddToDatabase);
				for (User user : groupToAddToDatabase.getMembers()) {
					uGManager.addUserToAGroupInTheDatabase(groupToAddToDatabase, user);
				}
			}
		}
	}
}
