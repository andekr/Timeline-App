package com.fabula.android.timeline.adapters;

import java.util.ArrayList;
import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableGroupsListViewAdapter extends BaseExpandableListAdapter{

	private ArrayList <Group> groups;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public ExpandableGroupsListViewAdapter(Context context, ArrayList <Group> groups) {
		this.groups = groups;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext); 
	}
	
	public User getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).getMember(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	private TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(mContext);
        
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        textView.setPadding(50, 0, 0, 0);
        return textView;
	}

	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).getMembers().size();
	}

	public Group getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
        View textView = getParentView(groupPosition, convertView, parent);
        return textView;
	}
	
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
	 
		TextView textView = getGenericView();
		textView.setText(getChild(groupPosition, childPosition).toString());
		textView.setClickable(false);
		textView.setFocusable(false);
		return textView;
	}
	
	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	private View getParentView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		if (convertView == null) {
			
			 convertView = mInflater.inflate(R.layout.list_timeline_view, null, false);
			 convertView.setPadding(50, 0, 0, 10);
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
