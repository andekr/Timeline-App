package com.fabula.android.timeline.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.Group;

public class GroupListAdapter extends ArrayAdapter<Group> {

	private Context mContext;
	private ArrayList <Group> groups;
	private LayoutInflater mInflater;

	public GroupListAdapter(Context context, ArrayList <Group> groups) {
		 super(context, 0);
		 this.mContext = context;
		 this.groups = groups;
		 mInflater = LayoutInflater.from(mContext); 
		 setNotifyOnChange(true);
	 }

	public int getCount() {
		return groups.size();
	}

	public Group getItem(int position) {
		return groups.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
			
			 convertView = mInflater.inflate(R.layout.list_timeline_view, null, false);
			 
			 holder = new ViewHolder();
			 holder.icon = (ImageView) convertView.findViewById(R.id.listIcon);
			 holder.text = (TextView) convertView.findViewById(android.R.id.text1);		 
			 convertView.setTag(holder);
			 
		 } else {
			 holder = (ViewHolder) convertView.getTag();
		 }
			 
			 holder.icon.setImageResource(R.drawable.my_groups_small);
			 holder.text.setText(groups.get(position).toString());
			 return convertView;
	 }
}
