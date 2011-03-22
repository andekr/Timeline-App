package com.fabula.android.timeline.sync;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;

public class Uploader {
	
	public static void uploadFile(String locationFilename, String saveFilename){
		System.out.println("saving "+locationFilename+"!! ");
		if(!saveFilename.contains("."))
			saveFilename = saveFilename+Utilities.getExtension(locationFilename);
		
//		if(exists("http://folk.ntnu.no/andekr/upload/files/"+saveFilename)){
		
			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
	
			String pathToOurFile = locationFilename;
			String urlServer = "http://folk.ntnu.no/andekr/upload/upload.php";
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary =  "*****";
	
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;
	
			try
			{
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
	
			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
	
			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
	
			// Enable POST method
			connection.setRequestMethod("POST");
	
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + saveFilename +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);
	
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
	
			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
			while (bytesRead > 0)
			{
			outputStream.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
	
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			
	
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			
			System.out.println("Server response: "+serverResponseCode+" Message: "+serverResponseMessage);
			}
			catch (Exception ex)
			{
			//Exception handling
			}
//		}
//		else{
//			System.out.println("image exists on server");
//		}
	}
	
	public static void putToGAE(Object o, String jsonString){       
		HttpHost targetHost = new HttpHost(Utilities.GOOGLE_APP_ENGINE_URL, 80, "http");
		// Using PUT here
		HttpPut httpPut = makeHttpPutBasedOnObjectType(o);
		
		makeHttpPutContentTypeHeader(httpPut);
		       
		sendJSONTOGAEServer(jsonString, targetHost, httpPut);
	}

	private static HttpPut makeHttpPutBasedOnObjectType(Object o) {
		HttpPut httpPut = null;
		if(o instanceof Experiences)
			httpPut = new HttpPut("/rest/experiences/");
		else if(o instanceof Experience)
			httpPut = new HttpPut("/rest/experience/");
		else if(o instanceof Event)
			httpPut = new HttpPut("/rest/event/");
		return httpPut;
	}
	
	
	public static void putGroupToGAE(final String jsonString){  
		final HttpHost targetHost = new HttpHost(Utilities.GOOGLE_APP_ENGINE_URL, 80, "http");
		// Using PUT here
		final HttpPut httpPut = new HttpPut("/rest/group/");
		makeHttpPutContentTypeHeader(httpPut);
		
		sendJSONTOGAEServer(jsonString, targetHost, httpPut);
		
	}
	
	public static void putUserToGAE(final String jsonString){  
		final HttpHost targetHost = new HttpHost(Utilities.GOOGLE_APP_ENGINE_URL, 80, "http");
		// Using PUT here
		final HttpPut httpPut = new HttpPut("/rest/user/");
		makeHttpPutContentTypeHeader(httpPut);
		
		sendJSONTOGAEServer(jsonString, targetHost, httpPut);
		
	}

	private static void makeHttpPutContentTypeHeader(HttpPut httpPut) {
		// Make sure the server knows what kind of a response we will accept
		httpPut.addHeader("Accept", "application/json");
		// Also be sure to tell the server what kind of content we are sending
		httpPut.addHeader("Content-Type", "application/json");
	}
	
	private static void sendJSONTOGAEServer(final String jsonString,
			final HttpHost targetHost, final HttpPut httpPut) {
		Runnable sendRunnable = new Runnable() {
			
			public void run() {
				try
				{
					DefaultHttpClient httpClient = new DefaultHttpClient();
					
				    StringEntity entity = new StringEntity(jsonString, "UTF-8");
				    entity.setContentType("application/json");
				    httpPut.setEntity(entity);
				   
				        // execute is a blocking call, it's best to call this code in a thread separate from the ui's
				    HttpResponse response = httpClient.execute(targetHost, httpPut);

				    Log.v("Put to GAE", response.getStatusLine().toString());
				}
				catch (Exception ex)
				{
				        ex.printStackTrace();
				}
					}
		};
		
		Thread thread =  new Thread(null, sendRunnable, "merkSettTraad");
        thread.start();
	
	}


	
	
	
	/**
	 * Convert XML to String
	 * 
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	@SuppressWarnings("unused")
	private static String readFileAsString(String filePath) throws java.io.IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}
	
	public static boolean exists(String URLName){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      // note : you may also need
	      //        HttpURLConnection.setInstanceFollowRedirects(false)
	      HttpURLConnection con =
	         (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestMethod("HEAD");
	      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	}
}
