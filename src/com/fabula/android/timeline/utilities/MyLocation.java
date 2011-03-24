package com.fabula.android.timeline.utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.fabula.android.timeline.Utilities;
import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Singelton class to access location services
 * 
 * @author andekr
 *
 */
public class MyLocation {

	public static MyLocation instance;
	LocationManager locationManager;
	LocationListener locationListener;
	String locationProvider;
	Location location = null;
	boolean gps_enabled, network_enabled;
	static Context context;
	
	protected MyLocation(final Context c) {
		context = c;
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		locationProvider = LocationManager.NETWORK_PROVIDER;
		

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLocationChanged(Location arg0) {
				location = arg0;
			}
		};
		
		//exceptions will be thrown if provider is not permitted.
        try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        
        if(gps_enabled)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
	}
	
	 public static MyLocation getInstance(Context c) {
	      if(instance == null) {
	         instance = new MyLocation(c);
	      }
	      return instance;
	   }
	
	public Location getLocation(){
		if(location==null)
			return locationManager.getLastKnownLocation(locationProvider);
		else
			return location;
	}
	
	public GeoPoint getGeoPointLocation() {
		return new GeoPoint((int)(getLocation().getLatitude() * 1E6) , (int) (getLocation().getLongitude() * 1E6));
	}
	

	/**
	 * Determines the the closest Address for a given Location.
	 *
	 * @param context the context
	 * @param location the location for which an Address needs to be found
	 * @return and Address or null if not found
	 * @throws IOException if the Geocoder fails
	 */
	public static Address getAddressForLocation(Context context, Location location) throws IOException{
		if(Utilities.isConnectedToInternet(context)){
		    if (location == null) {
		        return null;
		    }
	
		    double latitude = location.getLatitude();
		    double longitude = location.getLongitude();
		    int maxResults = 1;
	
		    Geocoder gc = new Geocoder(context, Locale.getDefault());
		    List<Address> addresses = null;
			try {
				addresses = gc.getFromLocation(latitude, longitude, maxResults);
			} catch (IOException e) {
				Log.e("getAddressForLocation", e.getMessage());
				throw new IOException();
			}
	
		    if (addresses.size() == 1) {
		        return addresses.get(0);
		    } else {
		        return null;
		    }
		}else
			return null;
	}

}
