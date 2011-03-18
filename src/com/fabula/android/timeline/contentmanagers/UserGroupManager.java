package com.fabula.android.timeline.contentmanagers;

import java.util.ArrayList;

import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.Group.GroupColumns;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.models.User.UserColumns;
import com.fabula.android.timeline.providers.GroupProvider;
import com.fabula.android.timeline.providers.UserGroupProvider;
import com.fabula.android.timeline.providers.UserProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class UserGroupManager {

	private Context context;
	
	public UserGroupManager(Context context) {
		this.context = context;
	}
	
	public void addUserToUserDatabase(User user) {
		ContentValues values = new ContentValues();
		
		values.put(UserColumns._ID, user.getId());
		values.put(UserColumns.USER_NAME, user.getUserName());
		
		context.getContentResolver().insert(UserColumns.CONTENT_URI, values);
		
		Log.i("USER GROUP MANAGER", "User: "+ user.getUserName()+ " added to the database");
	}
	
	public void addGroupToGroupDatabase(Group group) {
		ContentValues values = new ContentValues();
		
		values.put(GroupColumns._ID, group.getId());
		values.put(GroupColumns.GROUP_NAME, group.getName());
		
		context.getContentResolver().insert(GroupColumns.CONTENT_URI, values);
		
		Log.i("USER GROUP MANAGER", "Group: "+ group.getName()+ " added to the database");
	}
	
	public void addUserToAGroupInTheDatabase(Group group, User user) {
		ContentValues values = new ContentValues();
		values.put(GroupColumns._ID, group.getId());
		values.put(UserColumns.USER_NAME, user.getUserName());
		
		System.out.println(userExistsInGroup(group, user));
		if(!userExistsInGroup(group, user)) {
			context.getContentResolver().insert(UserGroupProvider.CONTENT_URI, values);
		}
		
		
		
		Log.i("USER GROUP MANAGER", "User: "+ user.getUserName()+ " added to group: "+group.getName());
	}
	
	private boolean userExistsInGroup(Group group, User user) {
		
		ArrayList<Group> userConnectedGroups = getAllGroupsConnectedToAUser(user);
		System.out.println(userConnectedGroups.size());
		for (Group g : userConnectedGroups) {
			if(g.getId().equals(group)){
				return true;
			}
		}
		
		return false;
	}

	public void removeUserFromAGroupInTheDatabase(Group group, User user) {
		
		String where = GroupColumns._ID+ " = '" +group.getId()+"'"+ " AND " +UserColumns.USER_NAME+ " = '" +user.getUserName()+"'";
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, where , null);
		
		Log.i("USER GROUP MANAGER", "User: "+ user.getUserName()+ " removed from group: "+group.getName());
	}
	
	public void deleteGroupFromDatabase(Group group) {
		
		String where = GroupColumns._ID+ " = '" +group.getId()+"'";
		context.getContentResolver().delete(GroupProvider.CONTENT_URI, where, null);
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, where, null);
		Log.i("USER GROUP MANAGER", "Group: "+ group.toString()+ " deleted from database");
	}
	
	public void deleteUserFromDatabase(User user) {
		
		String where = UserColumns.USER_NAME+ " = " +user.getUserName();
		
		context.getContentResolver().delete(UserColumns.CONTENT_URI, where, null);
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, where, null);
	}
	
	public Boolean userExists(User user) {
		
		String [] userTableColumns = new String[] {UserColumns.USER_NAME};
		
		String where = UserColumns.USER_NAME+" = " +"'"+user.getUserName()+"'";
				
		Cursor c = context.getContentResolver().query(UserProvider.CONTENT_URI, userTableColumns, where, null, null);
		int numberOfRowsReturned = c.getCount();
		
		if(numberOfRowsReturned != 0) {
			return true;
		}else {
			return false;
		}
	}
		
	public ArrayList <Group> getAllGroupsConnectedToAUser(User user) {
		
		ArrayList<Group> allConnectedGroups = new ArrayList<Group>();
		
		String [] userGroupsTableColumns = new String[] {GroupColumns._ID, UserColumns.USER_NAME};
		
		String whereStatement = UserColumns.USER_NAME+" = '"+user.getUserName()+"'";
		
		Cursor c = context.getContentResolver().query(UserGroupProvider.CONTENT_URI, userGroupsTableColumns, whereStatement, null, null);
		
		if(c.moveToFirst()) {
			do{
				String groupID = c.getString(c.getColumnIndex(GroupColumns._ID));
				allConnectedGroups.add(getGroupFromDatabase(groupID));
				
			}while(c.moveToNext());
		}
		c.close();
		return allConnectedGroups;
	}

	private Group getGroupFromDatabase(String groupID) {
		
		String [] groupsTableColumns = new String[] {GroupColumns._ID, GroupColumns.GROUP_NAME};
		Group group = null;
		String whereStatement = GroupColumns._ID+ " = '" +groupID+"'";
		
		Cursor c = context.getContentResolver().query(GroupProvider.CONTENT_URI, groupsTableColumns, whereStatement, null, null);
		if(c.moveToFirst()) {
	
			group = new Group( c.getString(c.getColumnIndex(GroupColumns._ID)),c.getString(c.getColumnIndex(GroupColumns.GROUP_NAME)));
			group.setMembers(getUsersConnectedToAGroup(group));
		}
		c.close();
		return group;
	}
	
	private ArrayList <User> getUsersConnectedToAGroup(Group group) {
		
		String [] userGroupsTableColumns = new String[] {GroupColumns._ID, UserColumns.USER_NAME};
		ArrayList<User> users = new ArrayList<User>();
		
		String where = GroupColumns._ID+ " = '" +group.getId()+"'";
		
		Cursor c = context.getContentResolver().query(UserGroupProvider.CONTENT_URI, userGroupsTableColumns, where, null, null);
		
		if(c.moveToFirst()) {
			do {
				users.add(new User(c.getString(c.getColumnIndex(UserColumns.USER_NAME))));
			} while (c.moveToNext());
		}
		c.close();
		return users;
		
	}
	
	public ArrayList <User> getAllUsersFromDatabase() {
		
		String[] userTableColumns = new String[] {UserColumns.USER_NAME};
		ArrayList<User> users = new ArrayList<User>();
		Cursor c = context.getContentResolver().query(UserProvider.CONTENT_URI, userTableColumns, null, null, null);
		
		if(c.moveToFirst()) {
			do {
				users.add(new User(c.getString(c.getColumnIndex(UserColumns.USER_NAME))));
			} while (c.moveToNext());
		}
		c.close();
		return users;
	}
}
