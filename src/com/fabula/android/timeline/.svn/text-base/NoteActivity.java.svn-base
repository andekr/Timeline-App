package com.fabula.android.timeline;

import com.fabula.android.timeline.models.SimpleNote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * The Activity for creation of a {@linkplain SimpleNote}.
 * 
 * @author andekr
 *
 */
public class NoteActivity extends Activity {
	
	
	private Button saveButton, discardButton;
	private EditText noteTitle, noteText;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notescreen);
		
		setupViews();

	}

	/**
	 * Sets up the views by getting elements from layout XML and setting listeners for the buttons.
	 * Sets up the text fields if the Activity is started with Utilities.EDIT_NOTE as request code.
	 * 
	 */
	private void setupViews() {
		saveButton = (Button)findViewById(R.id.SaveNoteButton);
		discardButton = (Button)findViewById(R.id.DiscardNoteButton);
		
		noteTitle = (EditText)findViewById(R.id.NoteTitleEditText);
		noteText = (EditText)findViewById(R.id.NoteTextEditText);
		
		if(Utilities.EDIT_NOTE == getIntent().getExtras().getInt(Utilities.REQUEST_CODE)){
			noteTitle.setText(getIntent().getExtras().getString(Intent.EXTRA_SUBJECT));
			noteText.setText(getIntent().getExtras().getString(Intent.EXTRA_TEXT));
		}
		
		saveButton.setOnClickListener(saveNoteListener);
		discardButton.setOnClickListener(discardNoteListener);
	}
	
	private OnClickListener saveNoteListener = new OnClickListener() {
		
		public void onClick(View v) {
			saveNote();
            finish();		
		}
	};
	
	private OnClickListener discardNoteListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent discardNoteIntent = new Intent();
            setResult(RESULT_CANCELED, discardNoteIntent);
            finish();					
		}
	};
	
	/**
	 * Saves the note by putting the data in an Intent and sending back to the calling activity.
	 * 
	 */
	private void saveNote(){
		Intent saveNoteIntent = new Intent();
		saveNoteIntent.putExtra("NOTE_ID", getIntent().getExtras().getInt("NOTE_ID")); 
        saveNoteIntent.putExtra(Intent.EXTRA_SUBJECT, noteTitle.getText().toString()); 
        saveNoteIntent.putExtra(Intent.EXTRA_TEXT, noteText.getText().toString()); 
        setResult(RESULT_OK, saveNoteIntent);
	}
}
