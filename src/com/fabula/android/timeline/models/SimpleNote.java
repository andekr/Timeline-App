package com.fabula.android.timeline.models;


import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.database.providers.NoteProvider;

public class SimpleNote extends EventItem{
	
	public SimpleNote() {}
	
	
	public SimpleNote(Context c) {
		super(c);
		className = "SimpleNote";
	}
	
	public SimpleNote(String noteText, Context c) {
		super(c);
		className = "SimpleNote";
		this.noteText = noteText;
	}
	
	public SimpleNote(String id, String title, String noteText, Account u){
		super(id, u);
		className = "SimpleNote";
		this.noteText = noteText;
		this.noteTitle = title;
	}
	
	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	
	public String getNoteTitle() {
		return noteTitle;
	}

	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}
	
	@Override
	public Uri getUri() {
		return NoteColumns.CONTENT_URI;
	}

	@Override
	public View getView(Context context) {
		LinearLayout textLayout = new LinearLayout(context);
        TextView noteTextView = new TextView(context);
        noteTextView.setTag(this);
        noteTextView.setText(noteText);
        noteTextView.setTextSize(20);
        noteTextView.setTextColor(context.getResources().getColor(R.color.Black));
        noteTextView.setLinkTextColor(context.getResources().getColor(R.color.Black));
        Linkify.addLinks( noteTextView, Linkify.ALL);
        
        TextView noteTitleTextView = new TextView(context);
        noteTitleTextView.setTag(this);
        noteTitleTextView.setText(noteTitle);
        noteTitleTextView.setTextSize(14);
        noteTitleTextView.setTextColor(context.getResources().getColor(R.color.Black));
        

        textLayout.setClickable(true);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.addView(noteTitleTextView);
        textLayout.addView(noteTextView);
        textLayout.setTag(this);
        textLayout.setPadding(10, 10, 0, 0);
        
        return textLayout;
	}
	
	@Override
	public Intent getIntent() {
		return null;
	}
	
	public static final class NoteColumns implements  BaseColumns {
		
		// This class cannot be instantiated
        private NoteColumns() {}

        public static final Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.AUTHORITY + "/notes");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.notes";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fabula.note";
        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String TITLE = "title";
        public static final String NOTE = "note";
        public static final String CREATED_DATE = "created";
        public static final String MODIFIED_DATE = "modified";
    }
}



