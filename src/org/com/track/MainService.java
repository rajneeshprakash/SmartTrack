package org.com.track;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class MainService extends Service implements Runnable
{
	TravelPhoneStateListener mPhoneListener;
	static TelephonyManager tm;
	//TextView txt_lat,txt_lon,txt_adr;    
	public static String call_alert_no="";
    public String response_phno="", current_cell_id="", 
    		standard_cell_id="", ph_imei="", ph_imsi="", initialization_flag="false", 
    		alert_nos[]={"8892527311","7676416672"}; 
    public boolean profile_ph=true;
    static boolean call_alert_flag;  
	static SmsManager sms;
	AudioManager maudio;

	public IBinder onBind(Intent arg0)
	{        return null;   }
	
	public void onCreate()
	{
        super.onCreate();
        System.out.println("Inside oncreate MainService");
        Toast.makeText(this, "Inside on create MainService", Toast.LENGTH_LONG).show();
        
	}
	
	public void onStart(Intent intent, int startid)
	{
		  System.out.println("Inside onstart MainService");
		  Toast.makeText(this, "Inside onStart MainService", Toast.LENGTH_LONG).show();
		  mPhoneListener = new TravelPhoneStateListener(this);
	      MainService.tm = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
	      MainService.tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); 
	      System.out.println("Inside Onstart MainService");  
	      sms = SmsManager.getDefault();   
	      Properties properties = new Properties();
	      try
	      {
	        	properties.load(new FileInputStream("system_file.properties"));
	        	initialization_flag=properties.getProperty("initialization_flag");
	        	 Toast.makeText(this, "Flag= "+initialization_flag, Toast.LENGTH_LONG).show();
	        	 	        	 
	      }catch(Exception ex){
	    	  Toast.makeText(this, "Initialising application ", Toast.LENGTH_LONG).show();
	    	  initialization_flag="false";
	    	  }
	      
	      FindIMEI_IMSI();
	      if(initialization_flag.trim().equals("false"))
	      {
	    	  Toast.makeText(this, "Flag 1 = "+initialization_flag, Toast.LENGTH_LONG).show();
	          try{
	        	properties.setProperty("initialization_flag","true");
	        	standard_cell_id=ph_imei+ph_imsi;
	        	properties.setProperty("standard_cell_identity",standard_cell_id);
	        	properties.store(new FileOutputStream("system_file.properties"),null);
	          }catch(Exception ex){ex.printStackTrace();}
	      }
	      else
	      {
	    	  Toast.makeText(this, "Flag 2 = "+initialization_flag, Toast.LENGTH_LONG).show();
	        	standard_cell_id=properties.getProperty("standard_cell_identity");;
	      }
	       
	        current_cell_id=ph_imei+ph_imsi;
	        System.out.println("standard_cell_identity : "+standard_cell_id);
	        System.out.println("current_cell_identity  : "+current_cell_id);
	        
	        if(!standard_cell_id.trim().equals(current_cell_id.trim()))
	        {
	        	for(int i=0;i<alert_nos.length;i++)
	        	{
	        		
	        		sendSMS(alert_nos[i],
	        				"SIM changed. Note IMEI number  => "+ph_imei);
	        		try{Thread.sleep(5000);}catch(Exception e){}
	        	}
	        }		
	              
	        Thread th=new Thread(this);
	        th.start();
	        System.out.println("After Thread Started");
	}
	
    
    public void run()
    {
    	System.out.println("Inside Run method");
      while(true)
      {
    	if(SmsReceiver.flag)
    	{
    	  String sms_received=SmsReceiver.msg;
    	  String token=sms_received.substring(sms_received.indexOf("-")+1,sms_received.lastIndexOf("-"));
    	  if(token.trim().equals("INout"))
    	  {
          	SmsReceiver.flag=false;
          	profile_ph=false;
          	SmsReceiver.msg="";
          	call_alert_no=sms_received.substring(sms_received.lastIndexOf("-")+1); 
          	call_alert_flag=true;
          }
    	  else if(token.trim().equals("profile"))
    	  {    		  	  
    	    	  profile_ph=false;
    	    	  String mode="";
    	    	  SmsReceiver.flag=false;
    	    	  SmsReceiver.msg="";
    	    	  mode=sms_received.substring(sms_received.lastIndexOf("-")+1,sms_received.indexOf("@"));
    	    	  response_phno=sms_received.substring(sms_received.indexOf("@")+1);
    	    	  if(mode.trim().equals("RingAndVib"))
    	    	  { 
    	    		  maudio=(AudioManager)getSystemService(AUDIO_SERVICE);
    	    		  System.out.println("...C...Profile ring-and-vib");
    	    		  maudio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    	    		  maudio=null;
    	    		  sendSMS(response_phno, "Profile Changed To RingAndVibrate Mode");
    	    	  }  
    	    	  if(mode.trim().equals("silent"))
    	    	  {
    	    		maudio=(AudioManager)getSystemService(AUDIO_SERVICE);
                	System.out.println("...C...Profile silent");
                	maudio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                	maudio=null;
                	sendSMS(response_phno, "Profile Changed To Silent Mode");
                  }
    	   }
    	   else
    	   {
                	if(profile_ph)
                	{
                		response_phno=sms_received.substring(sms_received.lastIndexOf("-")+1); 
                		SmsReceiver.flag=false;
                		SmsReceiver.msg="";
                		sendSMS(response_phno,"Invalid Request");
                	}
           }
    	  			SmsReceiver.flag=false;
    		 } 
    	 
    	 }
      }
    
   
	
    public static void sendSMS(String phoneNumber, String message)
    {        
    	System.out.println("Inside Send SMS phone No"+phoneNumber+"Message "+message);
        sms.sendTextMessage(phoneNumber, null, message, null, null);        
    }  
      
    public void FindIMEI_IMSI()
    {
    	TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    	String p_imei = manager.getDeviceId();
    	String p_imsi = manager.getSubscriberId();
    	if(p_imei!=null && p_imei.length()==15 ){
    		 ph_imei=p_imei;
    		 //txt_lat.setText(ph_imei);
    	 	 ph_imsi=p_imsi;
    	 	// txt_lon.setText(ph_imsi);
    	 }else{
    		 ph_imei=p_imei;
    	 	 ph_imsi=p_imsi; 
    	 }
    }
}