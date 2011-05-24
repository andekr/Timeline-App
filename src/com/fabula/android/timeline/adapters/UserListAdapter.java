package com.fabula.android.timeline.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;


/**
 * Adapter for the user list used to add users to groups.
 * 
 * 
 */
public class UserListAdapter extends ArrayAdapter<User> {

	
	private Context mContext;
	private ArrayList <User> users, checkedUsers;
	private LayoutInflater mInflater;

	public UserListAdapter(Context context, ArrayList <User> users, Group group) {
		 super(context, 0, users);
		 this.mContext = context;
		 this.users = users;
		 this.checkedUsers = new ArrayList<User>();
		 mInflater = LayoutInflater.from(mContext); 
		 setNotifyOnChange(true);
	 }

	public ArrayList<User> getSelectedUsers() {
		return checkedUsers;
	}
	
	public int getAmountOfSelectedUsers() {
		return checkedUsers.size();
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		
		if (convertView == null) {
			
			 convertView = mInflater.inflate(R.layout.list_users_view, null, false);
			 
			 holder = new ViewHolder();
			 holder.icon = (ImageView) convertView.findViewById(R.id.userlisticon);
			 holder.text = (TextView) convertView.findViewById(R.id.userlist_text);
			 holder.checkBox = (CheckBox) convertView.findViewById(R.id.user_list_checkbox);
			 holder.checkBox.setTag(position);
				
					OnClickListener l= new OnClickListener() {
						
						public void onClick(View v) {
							listClickAction(v);
						}
					};
				 
	  			holder.checkBox.setOnClickListener(l);
	  			convertView.setOnClickListener(l);
	  			convertView.setOnCreateContextMenuListener(null);
				convertView.setTag(holder);
			 
		 } else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.checkBox.setTag(position);
		 }
			 
			 
			 holder.icon.setImageResource(R.drawable.individual_timeline);
			 holder.text.setText(users.get(position).toString());
			 holder.checkBox.setChecked(checkedUsers.contains(users.get(position)));
				
			 return convertView;
	 }
	
	private void listClickAction(View v) {
		boolean checked = false;
		if(v instanceof RelativeLayout){
			v = ((ViewHolder)v.getTag()).checkBox;
			checked = !((CheckBox)v).isChecked();
		}else{
			checked = ((CheckBox)v).isChecked();
		}
		Integer myPosition = (Integer)v.getTag();
		
		if(checked){
			checkedUsers.add(getItem(myPosition));
			Log.i("User:", ""+ getItem(myPosition).toString());
			Log.i("Position:", ""+myPosition);
		}else{
			checkedUsers.remove(getItem(myPosition));
		}
		((CheckBox)v).setChecked(checked);
	}	
}	 

