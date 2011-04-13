package com.fabula.android.timeline.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;

public class UserListAdapter extends ArrayAdapter<User> {

	
	private Context mContext;
	private ArrayList <User> users, checkedUsers;
	private LayoutInflater mInflater;
	private Group group;
	private Group selectedGroup;

	public UserListAdapter(Context context, ArrayList <User> users, Group group) {
		 super(context, 0);
		 this.mContext = context;
		 this.users = users;
		 this.group = group;
		 this.checkedUsers = new ArrayList<User>();
		 this.selectedGroup = group;
		 mInflater = LayoutInflater.from(mContext); 
		 setNotifyOnChange(true);
	 }

	public int getCount() {
		return users.size();
	}

	public User getItem(int position) {
		return users.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public ArrayList<User> getSelectedUsers() {
		return checkedUsers;
	}
	
	public int getAmountOfSelectedUsers() {
		return checkedUsers.size();
	}
	
	public void setSelectedGroup(Group group) {
		selectedGroup = group;
	}
	

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
			
			 convertView = mInflater.inflate(R.layout.list_users_view, null, false);
			 
			 holder = new ViewHolder();
			 holder.icon = (ImageView) convertView.findViewById(R.id.userlisticon);
			 holder.text = (TextView) convertView.findViewById(R.id.userlist_text);
			 holder.checkBox = (CheckBox) convertView.findViewById(R.id.user_list_checkbox);
			 holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked) {
						checkedUsers.add(getItem(position));
					}
					else {
						checkedUsers.remove(getItem(position));
					}
				}
			});
			 
			 convertView.setTag(holder);
			 
		 } else {
			 holder = (ViewHolder) convertView.getTag();
		 }
			 holder.checkBox.setTag(position);
			 
			 holder.icon.setImageResource(R.drawable.individual_timeline);
			 holder.text.setText(users.get(position).toString());
//			 holder.checkBox.setChecked(isAlreadyPartOfGroup(users.get(position), group));
			 return convertView;
	 }

//	private boolean isAlreadyPartOfGroup(User user, Group group) {
//	
//			for (User u : group.getMembers()) {
//				if(user.getUserName().equals(u.getUserName())) {
//					return true;
//				}
//			}
//		return false;
//	}
	
}	 

