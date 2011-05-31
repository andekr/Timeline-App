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
package com.fabula.android.timeline.models;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.fabula.android.timeline.database.providers.PictureProvider;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.Utilities;

public class SimplePicture extends EventItem {

	private transient Uri pictureUri;
	
	public SimplePicture(){}
	
	public SimplePicture(Context c) {
		super(c);
		className = "SimplePicture";
	}
	
	public SimplePicture(String id, Uri uri, Account u, String pictureUrl) {
		super(id, u);
		className = "SimplePicture";
		this.pictureUri = uri;
		this.url = pictureUrl;
	}
	
	public SimplePicture(String id, Account u, String pictureUrl) {
		super(id, u);
		className = "SimplePicture";
		this.url = pictureUrl;
		File file = Utilities.DownloadFromUrl(pictureUrl, Constants.IMAGE_STORAGE_FILEPATH+getPictureFilename());
		this.pictureUri = Uri.fromFile(file);
		
	}
	

	public Uri getPictureUri() {
		return pictureUri;
	}
	public void setPictureUri(Uri pictureUri, String pictureUrl) {
		this.pictureUri = pictureUri;
		this.url = pictureUrl;
	}
	
	public String getPictureFilename() {
		return Utilities.getFilenameFromURL(url);
	}

	public void setPictureUrl(String pictureUrl) {
		this.url = pictureUrl;
	}
	
	public String getPictureUrl() {
		return this.url; 
	}
	
	
	//For GSON serializing
	public String getClassName() {
		return className;
	}

	@Override
	public View getView(Context context) {
        ImageView image = new ImageView(context);
        Bitmap bm = null;
        try {
			bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), getPictureUri());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        image.setImageBitmap(getThumbnail(bm));
        bm.recycle();
        image.setTag(this);

        image.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP));
        image.setPadding(10, 10, 0, 0);
        return image;
	}
	
	@Override
	public Intent getIntent(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(getPictureUri(), "image/*");
		
		return intent;
	}
	
	public Bitmap getThumbnail(Bitmap bm){
        //actual width of the image (img is a Bitmap object)
        int width = bm.getWidth();
        int height = bm.getHeight();
     
        //new width
        int newWidth = 400;

        // calculate the scale
        float scaleWidth = (float)((float)newWidth /(float)width);

        //new height
        int newHeight = (int) ((int)height*scaleWidth);


        // recreate the new Bitmap and set it back
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
	}
	
	@Override
	public Uri getUri() {
		return PictureColumns.CONTENT_URI;
	}
	
	public static final class PictureColumns implements BaseColumns {
		
		private PictureColumns(){
		}
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+PictureProvider.AUTHORITY+"/pictures");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.pictures";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fabula.pictures";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String TITLE = "title";
        
        public static final String URI_PATH = "uri_path";
        
        public static final String FILENAME = "filename";
        
        public static final String DESCRIPTION = "description";
        
        public static final String CREATED_DATE = "created";
        

	}


}
