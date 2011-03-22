package com.fabula.android.timeline;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import com.fabula.android.timeline.sync.GAEHandler;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * A simple activity to show the username.
 * The username is fetched from the Google Account on the device.
 * 
 * 
 */
public class ProfileActivity extends Activity {
	
	AccountManager manager;
	DefaultHttpClient http_client = new DefaultHttpClient();
	Account[] accounts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilescreen);
		
		TextView googleAcc = (TextView)findViewById(R.id.UsernameTextView);
		manager = AccountManager.get(this);
		googleAcc.setText(getUsername());
		
	}
	
	public String getUsername(){
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	    	possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        String email = possibleEmails.get(0);
	        return email;
	    }else
	        return null;
	}
	
	public Account getUserAccount(){
		 manager = AccountManager.get(this); 
		    this.accounts = manager.getAccountsByType("com.google"); 

		    for (Account account : accounts) {
		    	return account;
		    }
		    
			return null;
	}

}
