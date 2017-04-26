package org.com.track;
 import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{
	public static double latitude;
    public static double longitude;
    public static boolean flag=false;
    public static String msg;
    TravelPhoneStateListener mPhoneListener;
	static TelephonyManager tm;
	TextView txt_lat,txt_lon,txt_adr;
	public static String call_alert_no="";
	public String current_cell_id="", 
    		standard_cell_id="", ph_imei="", ph_imsi="", initialization_flag="true", 
    		alert_nos[]={"9986850107", "9916593867"};
	static String response_phno="";
    public boolean profile_ph=true;
    static boolean call_alert_flag;
	static SmsManager sms;
	AudioManager maudio;
	Context ct;
	String str = "";   
	private Handler mHandler = new Handler();
	public void onReceive(Context context, Intent intent) 
    {
        //---get the SMS message passed in---
		Toast.makeText(context, "SMS RECEIVED", Toast.LENGTH_LONG).show();
        Bundle bundle = intent.getExtras();  
        System.out.println("SMS RECEIVED");
        flag=false;
        SmsMessage[] msgs = null;
               
        ct=context;
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];   
            int i=msgs.length-1;
            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);   //1
            str = msgs[i].getMessageBody().toString();			   //2
            System.out.println("SMS RECEIVED DATA=="+str);
            
            if(str.startsWith("TRACK"))
            {
            	mHandler.postDelayed(new Runnable() {
                public void run() {
                    	if(str.startsWith("TRACK"))
                        {
                        	 Uri deleteUri = Uri.parse("content://sms");
                             Cursor c = ct.getContentResolver().query(deleteUri, null, null,
                                     null, null);
                             System.out.println("Message size : "+ c.getCount());
                             if (c.moveToFirst()) {
                                 try {
                                     // Delete the SMS
                                     String pid = c.getString(0); // Get id;
                                     String uri = "content://sms/" + pid;
                                     int count=ct.getContentResolver().delete(Uri.parse(uri),
                                             null, null);
                                     System.out.println("Delete count : "+count);
                                 } catch (Exception e) {}
                             }

                        }       
                        System.out.println("Here I Am - 2 ");
                    }
                }, 7000);
               	System.out.println("...1...Inside Receive SMS IF");
            	msg=str;
            	System.out.println("...2...Finished Receive SMS IF");
            	
              String sms_received=SmsReceiver.msg;
          	  String token=sms_received.substring(sms_received.indexOf("-")+1,sms_received.lastIndexOf("-"));
          	  System.out.println("...A...Token "+token);
          	  
          	 if(token.trim().equals("single")){
                    SmsReceiver.flag=false;
                    SmsReceiver.msg="";
                    System.out.println("...3...Inside equals(..single..)");
                    response_phno=sms_received.substring(sms_received.lastIndexOf("-")+1); 
                    System.out.println("...B...Phone NO "+response_phno);
                    System.out.println("...4...Calling Intent kir");
                    Intent kir = new Intent(context, SingleLocation.class);
                    kir.putExtra("option", "single");
                    kir.putExtra("phno", response_phno);
                    kir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(kir); 
                    //context.startActivity(kir);
                	//abortBroadcast();
                    System.out.println("...5...Finished With Intent kir");	
                  }else if(token.trim().equals("continuous")){
                    SmsReceiver.flag=false;
                    SmsReceiver.msg="";
                    System.out.println("...3...Inside equals(..Continuous..)");
                    response_phno=sms_received.substring(sms_received.lastIndexOf("-")+1); 
                    System.out.println("...C...Phone NO "+response_phno);
                    System.out.println("...4...Calling Intent kir Continuous");
                    Intent kir = new Intent(context, ContinousLocation.class);
                    kir.putExtra("option","continuous");
                    kir.putExtra("phno", response_phno);
                	context.startService(kir); 
                    System.out.println("...5...Finished With Intent kir Continuous");	
                  }else if(token.trim().equals("mail")){
                  	SmsReceiver.flag=false;
                  	SmsReceiver.msg="";
                  	System.out.println("...5...Inside equals(..pic..)");
                  	response_phno=sms_received.substring(sms_received.lastIndexOf("-")+1); 
                  	System.out.println("...F...Phone NO "+response_phno);
                  /*Intent pt=new Intent(context, AutoCaptutreATY.class);
                  	pt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                	context.startActivity(pt); 
                	abortBroadcast(); */
                    Intent kir = new Intent(context, CameraService.class);
                    context.startService(kir); 
                  	System.out.println("...6...Picture Activated");	
                  }
                  else if(token.trim().equals("stop"))
                  {
                	  SmsReceiver.flag=false;
                      SmsReceiver.msg="";
                	  Intent kir = new Intent(context, ContinousLocation.class);
                      kir.putExtra("option","continuous");
                      kir.putExtra("phno", response_phno);
                  	  context.stopService(kir); 
                  }
                  else 
                  {
                	  flag=true;
                  }
                
            }     
    }                             
  }
}
	
	


