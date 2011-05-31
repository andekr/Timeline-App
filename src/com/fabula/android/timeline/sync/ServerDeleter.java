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
package com.fabula.android.timeline.sync;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.util.Log;

import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.utilities.Constants;
import com.fabula.android.timeline.utilities.Utilities;

public class ServerDeleter {
	
	protected static void deleteUserFromGroupToGAE(Group groupToRemoveMember,
			User userToRemoveFromGroup) {
		final HttpDelete httpDelete = new HttpDelete("/rest/group/"+groupToRemoveMember.getId()+"/user/"+userToRemoveFromGroup.getUserName()+"/");
		httpDelete.addHeader(CoreProtocolPNames.USER_AGENT, "TimelineAndroid");
		sendDeleteRequestTOGAEServer("", Constants.targetHost, httpDelete);
	}
	
	protected static void deleteUserFromGroupToGAE(Group selectedGroup) {
		final HttpDelete httpDelete = new HttpDelete("/rest/group/"+selectedGroup.getId()+"/");
		
		sendDeleteRequestTOGAEServer("", Constants.targetHost, httpDelete);
		
	}

	private static void sendDeleteRequestTOGAEServer(String string,
			final HttpHost targetHost, final HttpDelete httpDelete) {
	Runnable sendRunnable = new Runnable() {
			
			public void run() {
				try
				{
					DefaultHttpClient httpClient = new DefaultHttpClient();
					
				    HttpResponse response = httpClient.execute(targetHost, httpDelete);

				    Log.v("Delete to GAE", Utilities.convertStreamToString(response.getEntity().getContent()));
				}
				catch (Exception ex)
				{
				        ex.printStackTrace();
				}
					}
		};
		
		Thread thread =  new Thread(null, sendRunnable, "deleteToGAE");
        thread.start();
		
	}
}
