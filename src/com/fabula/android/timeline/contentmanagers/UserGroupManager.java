package com.fabula.android.timeline.contentmanagers;

import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * Add a user to the database if the user doesn't exists from before
	 * @param user The user to be added
	 */
	public void addUserToUserDatabase(User user) {
		ContentValues values = new ContentValues();
		
		values.put(UserColumns._ID, user.getId());
		values.put(UserColumns.USER_NAME, user.getUserName());
		
		if (!userExists(user)) {
			context.getContentResolver().insert(UserColumns.CONTENT_URI, values);
			Log.i("USER GROUP MANAGER", "User: "+ user.getUserName()+ " added to the database");
		}
		else {
			Log.i("USER GROUP MANAGER", "User: "+ user.getUserName()+ " already exists");
		}
	}
	
	/**
	 * Add several users to the database
	 * @param users The list of users to be added
	 */
	public void addUsersToUserDatabase(List <User> users) {
		for (User u : users) {
			addUserToUserDatabase(u);
		}
	}
	
	/**
	 * Add a group to the database
	 * @param group The group to be added
	 */

	public void addGroupToGroupDatabase(Group group) {
		ContentValues values = new ContentValues();
		
		values.put(GroupColumns._ID, group.getId());
		values.put(GroupColumns.GROUP_NAME, group.getName());
		
		context.getContentResolver().insert(GroupColumns.CONTENT_URI, values);
		
		Log.i("USER GROUP MANAGER", "Group: "+ group.getName()+ " added to the database");
	}
	
	/**
	 * Add a user to a group in the database
	 * @param group The group the user should be added to
	 * @param user The user to be added
	 */
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
	
	/**
	 * Checks if a user already is in a group
	 * @param group The group to be checked
	 * @param user The user to be checked
	 * @return True if the user is in the group
	 */
	private boolean userExistsInGroup(Group group, User user) {
		
		ArrayList<Group> userConnectedGroups = getAllGroupsConnectedToAUser(user);
		System.out.println("Number of connected Groups :"+userConnectedGroups.size());
		for (Group g : userConnectedGroups) {
			if(g.getId().equals(group)){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Removes a user from a group in the database
	 * @param group The group the user should be removed from
	 * @param user The user to be removed
	 */
	public void removeUserFromAGroupInTheDatabase(Group group, User user) {
		
		String where = GroupColumns._ID+ " = '" +group.getId()+"'"+ " AND " +UserColumns.USER_NAME+ " = '" +user.getUserName()+"'";
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, where , null);
		
		Log.i("USER GROUP MANAGER", "User: "+ user.getUserName()+ " removed from group: "+group.getName());
	}
	
	/**
	 * Deletes a group from the database
	 * @param group The group to be deleted
	 */
	public void deleteGroupFromDatabase(Group group) {
		
		String where = GroupColumns._ID+ " = '" +group.getId()+"'";
		context.getContentResolver().delete(GroupProvider.CONTENT_URI, where, null);
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, where, null);
		Log.i("USER GROUP MANAGER", "Group: "+ group.toString()+ " deleted from database");
	}
	
	/**
	 * Deletes a user from the database
	 * @param user The user to be deleted
	 */
	
	public void deleteUserFromDatabase(User user) {
		
		String where = UserColumns.USER_NAME+ " = " +user.getUserName();
		
		context.getContentResolver().delete(UserColumns.CONTENT_URI, where, null);
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, where, null);
	}
	
	/**
	 * Checks if a user already exists in the database
	 * @param user The user to be checked
	 * @return True if the user already exists
	 */
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
	
	/**
	 * Gets all the groups in the database connected to a given user
	 * @param user The user connected to groups
	 * @return A list of the groups connected to the user
	 */
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

	/**
	 * Gets a group from the database based on an ID
	 * @param groupID The ID checked against the database
	 * @return The group with the given groupID
	 */
	public Group getGroupFromDatabase(String groupID) {
		
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
	
	/**
	 * Gets all users in a group
	 * @param group The group to be checked
	 * @return A list of all the users connected to the group
	 */
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
	
	/**
	 * Gets all the users in the user table of the database
	 * @return A list of all the users
	 */
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
	
	/**
	 * Deletes all content of the user group database
	 */
	public void truncateUserGroupDatabase() {
		context.getContentResolver().delete(GroupProvider.CONTENT_URI, null, null);
		context.getContentResolver().delete(UserGroupProvider.CONTENT_URI, null, null);
		context.getContentResolver().delete(UserProvider.CONTENT_URI, null, null);
	}
	
}
