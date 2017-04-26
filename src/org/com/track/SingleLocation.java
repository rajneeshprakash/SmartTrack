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

public class SingleLocation extends Service {
	
	private LocationManager locManager;
	private double LATITUDE;
	private double LONGITUDE;
	private Geocoder geocoder;
	final int maxResult =1;
	String addressList[] = new String[maxResult];
	String option;
	private String response_no="";
	String addr;
	public boolean flag=false;
	

	public IBinder onBind(Intent arg0) {
        return null;
  }
  @Override
  public void onCreate() 
  {
        super.onCreate();
        System.out.println("Inside Single location");
        Toast.makeText(this, "Inside oncreate Single", Toast.LENGTH_LONG).show();
     }
  
  public void onStart(Intent intent, int startid) {
	  System.out.println("Inside onstart Single");
	  Toast.makeText(this, "Inside on Start", Toast.LENGTH_LONG).show();
	  locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
      locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
      option=intent.getStringExtra("option");
	  option=option.trim();
	  response_no  =intent.getStringExtra("phno");
	  try {
		Thread.sleep(5000);
	} catch (InterruptedException e1) {
		e1.printStackTrace();
	}
      Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      	 
  		if (location != null) {
  			LATITUDE = (double) (location.getLatitude());
  			LONGITUDE = (double) (location.getLongitude());
  			String latlon=""+LATITUDE+" "+LONGITUDE;
  			geocoder = new Geocoder(this, Locale.ENGLISH);
              try {
              	  List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, maxResult);
              	 
              	  if(addresses != null) 
              	  {
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
              	  Toast.makeText(this, latlon, Toast.LENGTH_LONG).show(); 
              	  sendSMS(response_no,addr);
              	  /*flag=false;
              	  break;*/
  		}
  		else
  		{
  			sendSMS(response_no,"Location not Found...");
  			//locManager.removeUpdates(locationListener);
  		}
  				
	}
 
  @Override
  public void onDestroy() {
        super.onDestroy();
        locManager.removeUpdates(locationListener);
        Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
  }
  
  private final LocationListener locationListener = new LocationListener() {
      public void onLocationChanged(Location location) {
          //updateWithNewLocation(location);
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
