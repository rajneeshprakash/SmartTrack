package org.com.track;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;


public class ContinousLocation extends Service{
	private LocationManager locManager1;
	private double LATITUDE;
	private double LONGITUDE;
	private Geocoder geocoder;
	final int maxResult =5;
	String addressList[] = new String[maxResult];
	String option;
	private String response_no="9845375610";
	String addr;
	long prvtime;
	

	public IBinder onBind(Intent arg0) {
        return null;
  }
  @Override
  public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Inside on create Continous", Toast.LENGTH_LONG).show();
        }
  
  public void onStart(Intent intent, int startid) {
	  Toast.makeText(this, "Inside on Start Continuous", Toast.LENGTH_LONG).show();
	  locManager1 = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
      locManager1.requestLocationUpdates(LocationManager.GPS_PROVIDER,60000, 5, locationListener);
      option=intent.getStringExtra("option");
	  option=option.trim();
	  response_no  =intent.getStringExtra("phno");
	  geocoder = new Geocoder(this, Locale.ENGLISH);
	  prvtime=0;
	  }
 
  @Override
  public void onDestroy() {
        super.onDestroy();
        locManager1.removeUpdates(locationListener);
        //Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
  }
  
  private final LocationListener locationListener = new LocationListener() {
      public void onLocationChanged(Location location) {
    	  if (location != null) {
    			LATITUDE = (double) (location.getLatitude());
    			LONGITUDE = (double) (location.getLongitude());
    			//geocoder = new Geocoder(this, Locale.ENGLISH);
    			long newtime=System.currentTimeMillis();
    			if((prvtime==0) || (newtime-prvtime)>=60000)
    			{   				
    			       String latlon=""+LATITUDE+" "+LONGITUDE;
                      try {
            	  		prvtime=newtime;
                	    List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, maxResult);
                	 
                	     if(addresses != null) {
                	    	  int j=0;
                		  Address returnedAddress=null;
                	      for (j=0; j<maxResult; j++){
                	      returnedAddress = addresses.get(j);
                	      StringBuilder strReturnedAddress = new StringBuilder();
                	      for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                	      strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                	       }
                	      addressList[j] = strReturnedAddress.toString();
                	     }
                	      addr="Latitude : "+LATITUDE+"\nLongitude : "+LONGITUDE+"\nAddress : "+addressList[0];
                	     }
                	     } catch (IOException e) {
                		 e.printStackTrace();
                	     } 
    			           Toast.makeText(ContinousLocation.this, latlon, Toast.LENGTH_LONG).show();
                	       sendSMS(response_no,addr);
                     }
    	        }
      }

      public void onProviderDisabled(String provider) {
          //updateWithNewLocation(null);
      }

      public void onProviderEnabled(String provider) {
      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }
  };
  
  public static void sendSMS(String phoneNumber, String message)
  { 
  	System.out.println("Inside Send SMS phone No"+phoneNumber+"Message "+message);
  	SmsManager sms = SmsManager.getDefault(); 
  	sms.sendTextMessage(phoneNumber, null, message, null, null);        
  }
}
