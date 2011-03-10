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
		
	}
	
	public static void putToGAE(Object o, String xmlLocation){
	   
	
		DefaultHttpClient httpClient = new DefaultHttpClient();
		       
		HttpHost targetHost = new HttpHost("reflectapp.appspot.com", 80, "http");
		// Using PUT here
		HttpPut httpPut = null;
		if(o instanceof Experiences)
			httpPut = new HttpPut("/rest/experiences/");
		else if(o instanceof Experience)
			httpPut = new HttpPut("/rest/experience/");
		else if(o instanceof Event)
			httpPut = new HttpPut("/rest/event/");
		// Make sure the server knows what kind of a response we will accept
		httpPut.addHeader("Accept", "application/xml");
		// Also be sure to tell the server what kind of content we are sending
		httpPut.addHeader("Content-Type", "application/xml");
		       
		try
		{
		    StringEntity entity = new StringEntity(readFileAsString(xmlLocation), "UTF-8");
		    System.out.println(readFileAsString(xmlLocation));
		    entity.setContentType("application/xml");
		    httpPut.setEntity(entity);
		   
		        // execute is a blocking call, it's best to call this code in a thread separate from the ui's
		    HttpResponse response = httpClient.execute(targetHost, httpPut);

		    Log.v("PutExperiences to GAE", response.getStatusLine().toString());
		}
		catch (Exception ex)
		{
		        ex.printStackTrace();
		}
	}
	
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

}
