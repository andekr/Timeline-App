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
package com.fabula.android.timeline.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.adapters.AttachmentAdapter;
import com.fabula.android.timeline.barcode.IntentIntegrator;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.utilities.Constants;

/**
 * The class that initiates actions for attaching items to the timeline.
 * 
 * 
 * @author andekr
 *
 */
public class AttachmentAdder {

	private Context timeLineActivityContext;
	private Activity timeLineActivity;
	private String[] attachmentTypes;

	public AttachmentAdder(Context context, Activity activity, Experience experience) {
		super();
		
		this.timeLineActivityContext = context;
		this.timeLineActivity = activity;
		
		attachmentTypes = timeLineActivityContext.getResources().getStringArray(R.array.attachment_types);
		
		
		//create a dialog where the user can choose between different attachment types
		Builder builder = new AlertDialog.Builder(timeLineActivityContext);
		builder.setTitle("Attachments");
		
		builder.setAdapter(new AttachmentAdapter(timeLineActivityContext), new DialogInterface.OnClickListener() {
			
		    public void onClick(DialogInterface dialog, int item) {
		    	
		        if(attachmentTypes[item].equals(timeLineActivityContext.getResources().getString(R.string.picture))) {
		        	openImageGallery();
		        }
		        else if(attachmentTypes[item].equals(timeLineActivityContext.getResources().getString(R.string.video))) { 
		        	openVideoGallery();
		        }
		        else if(attachmentTypes[item].equals(timeLineActivityContext.getResources().getString(R.string.audio))) {
		        	Toast.makeText(timeLineActivityContext, "Not implemented", Toast.LENGTH_SHORT).show();
		        }
		        else if(attachmentTypes[item].equals(timeLineActivityContext.getResources().getString(R.string.barcode))) {
		        	openBarcodeScanner();
		        }
		    }
		});
		
		builder.show();
	}
	

	private void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        timeLineActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.SELECT_PICTURE);
	}
	
	private void openVideoGallery() {
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		timeLineActivity.startActivityForResult(Intent.createChooser(intent, "Select Video"), Constants.SELECT_VIDEO);
	}
	
	private void openBarcodeScanner() {
		IntentIntegrator.initiateScan(timeLineActivity);
	}
	
}
