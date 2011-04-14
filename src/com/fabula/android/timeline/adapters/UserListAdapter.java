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

	public UserListAdapter(Context context, ArrayList <User> users, Group group) {
		 super(context, 0);
		 this.mContext = context;
		 this.users = users;
		 this.checkedUsers = new ArrayList<User>();
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
			 return convertView;
	 }
}	 

