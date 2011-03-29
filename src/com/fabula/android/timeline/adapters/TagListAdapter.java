package com.fabula.android.timeline.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.fabula.android.timeline.MyGroupsActivity;
import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;

public class TagListAdapter extends ArrayAdapter<String> {

	
	private Context mContext;
	private List <String> tags, checkedTags;
	private LayoutInflater mInflater;

	public TagListAdapter(Context context, List<String> allTags, List<String> checkedTags) {
		 super(context, 0);
		 this.mContext = context;
		 this.tags = allTags;
		 this.checkedTags = checkedTags;
		 Log.i(this.getClass().getSimpleName(), "checked tags init: "+this.checkedTags.size());
		 mInflater = LayoutInflater.from(mContext); 
		 setNotifyOnChange(true);
	 }

	public int getCount() {
		return tags.size();
	}

	public String getItem(int position) {
		return tags.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public List<String> getCheckedTags() {
		return checkedTags;
	}

	public void setCheckedTags(List<String> checkedTags) {
		this.checkedTags = checkedTags;
	}

	public int getAmountOfSelectedTags() {
		return checkedTags.size();
	}
	

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		
		if (convertView == null) {
			 convertView = mInflater.inflate(R.layout.list_tags_view, null, false);
			 holder = new ViewHolder();
			 holder.text = (TextView) convertView.findViewById(R.id.taglist_text);
			 holder.checkBox = (CheckBox) convertView.findViewById(R.id.tag_list_checkbox);
			 holder.checkBox.setTag(position);
			 
				
		
			holder.checkBox.setOnClickListener(l);
			convertView.setTag(holder);
			 
		 } else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.checkBox.setTag(position);
		 }
			 
			 holder.text.setText(tags.get(position).toString());
//			 holder.checkBox.setChecked(isAlreadyPartOfGroup(users.get(position), group));
			holder.checkBox.setChecked(checkedTags.contains(tags.get(position)));
			

			 Log.i(this.getClass().getSimpleName(), "checked tags getView: "+this.checkedTags.size());
			 return convertView;
	 }
	
	public OnClickListener l= new OnClickListener() {
		
		public void onClick(View v) {
			listClickAction(v);

		}

	};
	
	public OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			Toast.makeText(mContext, "YO", Toast.LENGTH_SHORT).show();
			listClickAction(v);
			
		}
	};
	
	private void listClickAction(View v) {
		if(v instanceof RelativeLayout){
			v = ((ViewHolder)v.getTag()).checkBox;
		}
		Integer myPosition = (Integer)v.getTag();
		boolean checked = ((CheckBox)v).isChecked();
		if(checked){
			checkedTags.add(getItem(myPosition));
		}else{
			checkedTags.remove(getItem(myPosition));
		}
		((CheckBox)v).setChecked(checked);
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

