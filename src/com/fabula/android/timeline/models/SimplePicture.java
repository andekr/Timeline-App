package com.fabula.android.timeline.models;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.providers.PictureProvider;

public class SimplePicture extends EventItem {

	private transient Uri pictureUri;
	
	public SimplePicture(){}
	
	public SimplePicture(Context c) {
		super(c);
		className = "SimplePicture";
	}
	
	public SimplePicture(String id, Uri uri, Account u, String filename) {
		super(id, u);
		className = "SimplePicture";
		this.pictureUri = uri;
		pictureFilename = filename;
	}
	
	public SimplePicture(String id, Account u, String filename) {
		super(id, u);
		className = "SimplePicture";
		File file = DownloadFromUrl(filename, Utilities.IMAGE_STORAGE_FILEPATH+filename);
		this.pictureUri = Uri.fromFile(file);
		pictureFilename = filename;
	}
	

	public Uri getPictureUri() {
		return pictureUri;
	}
	public void setPictureUri(Uri pictureUri, String filename) {
		this.pictureUri = pictureUri;
		pictureFilename = filename;
	}
	
	public String getPictureFilename() {
		return pictureFilename;
	}

	public void setPictureFilename(String pictureFilename) {
		this.pictureFilename = pictureFilename;
	}
	
	
	
	//For GSON serializing
	@SuppressWarnings("unused")
	public String getClassName() {
		return className;
	}

	@Override
	public View getView(Context context) {
        ImageView image = new ImageView(context);
       // image.setImageURI(getPictureUri());
        Bitmap bm = null;
        try {
			bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), getPictureUri());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        image.setImageBitmap(getThumbnail(bm));
        bm.recycle();
        image.setTag(this);

        image.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP));
        
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

    public File DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
                URL url = new URL("http://folk.ntnu.no/andekr/upload/files/" + imageURL); //you can write here any link
                File file = new File(fileName);
                System.out.println("THE FILENAME IS "+fileName);
                if(!file.exists()){
	                long startTime = System.currentTimeMillis();
	                Log.d("ImageManager", "download begining");
	                Log.d("ImageManager", "download url:" + url);
	                Log.d("ImageManager", "downloaded file name:" + fileName);
	                /* Open a connection to that URL. */
	                URLConnection ucon = url.openConnection();
	
	                /*
	                 * Define InputStreams to read from the URLConnection.
	                 */
	                InputStream is = ucon.getInputStream();
	                BufferedInputStream bis = new BufferedInputStream(is);
	
	                /*
	                 * Read bytes to the Buffer until there is nothing more to read(-1).
	                 */
	                ByteArrayBuffer baf = new ByteArrayBuffer(50);
	                int current = 0;
	                while ((current = bis.read()) != -1) {
	                        baf.append((byte) current);
	                }
	
	                /* Convert the Bytes read to a String. */
	                FileOutputStream fos = new FileOutputStream(file);
	               
	                fos.write(baf.toByteArray());
	                fos.close();
	                Log.d("ImageManager", "download ready in"
	                                + ((System.currentTimeMillis() - startTime) / 1000)
	                                + " sec");
                }else{
                	 Log.d("ImageManager", "file exists!");
                }
                return file;

        } catch (IOException e) {
                Log.d("ImageManager", "Error: " + e);
                return null;
        }

    }
}
