/*******************************************************************************
 * Copyright (c) 2011 Andreas Storlien and Anders Kristiansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Storlien and Anders Kristiansen - initial API and implementation
 ******************************************************************************/
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
			
			 convertView = mInflater.inflate(R.layout.group_list_view, null, false);
			 
			 holder = new ViewHolder();
			 holder.icon = (ImageView) convertView.findViewById(R.id.groupListIcon);
			 holder.text = (TextView) convertView.findViewById(R.id.titleOfGroupInList);		 
			 convertView.setTag(holder);
			 
		 } else {
			 holder = (ViewHolder) convertView.getTag();
		 }
			 
			 holder.icon.setImageResource(R.drawable.my_groups_small);
			 holder.text.setText(groups.get(position).toString());
			 return convertView;
	 }
}
