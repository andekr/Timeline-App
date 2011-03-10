package com.fabula.android.timeline.models;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class SimpleAttachment extends EventItem{

	public SimpleAttachment(Context c) {
		super(c);
	}
	
	private File attachment;
	private String attachmentDescription;
	
	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}
	public File getAttachment() {
		return attachment;
	}
	@Override
	public View getView(Context context) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri getUri() {
		// TODO Auto-generated method stub
		return null;
	}	
}
