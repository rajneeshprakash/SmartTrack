package org.com.track;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TravelPhoneStateListener extends PhoneStateListener{

	String callie_name;
    static MainService parent;
    AudioManager maudio;
    boolean priority_flag;
    
    public TravelPhoneStateListener(MainService  parent) {
    	TravelPhoneStateListener.parent=parent;
	}
    
    @Override
    public void onCallStateChanged(int state, String incoming_number) {
    System.out.println("switch : " +state);
   // boolean ringing=false, offhook=false;

     switch (state) 
      {
        case TelephonyManager.CALL_STATE_OFFHOOK:
        	System.out.println("I Am In CALL_STATE_OFFHOOK");
        	//callie_name=getContactNameFromNumber(incoming_number);
        	//System.out.println("Callie : "+callie_name+" Number "+incoming_number);
        	//offhook=true;
        	break;

        case TelephonyManager.CALL_STATE_RINGING:
        	 System.out.println("I Am In CALL_STATE_RINGING");
        	 if(MainService.call_alert_flag){
        		 callie_name=getContactNameFromNumber(incoming_number);
        		 System.out.println("Callie : "+callie_name+" Number "+incoming_number);
        		 MainService.sendSMS(MainService.call_alert_no, 
              		"Their is an In-coming call From : "+callie_name+" - "+incoming_number);
        	 }
             //ringing=true;
             break;
            
        case TelephonyManager.CALL_STATE_IDLE:
        	 System.out.println("I Am In CALL_STATE_IDLE");
             //callie_name=getContactNameFromNumber(incoming_number);
             //System.out.println("Callie : "+callie_name+" Number "+incoming_number);
             break;
      }
   }

    public static String getContactNameFromNumber(String number) {
    	
    	ContentResolver cr = parent.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor= cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);//(uri, new String[]{PhoneLookup.DISPLAY_NAME} .....)
        
        System.out.println("Cursor cursor : "+cursor);
        if (cursor.moveToFirst()) 
        {
          System.out.println("Cursor Has Recordes");
          do{
        	  String name =cursor.getString(0);
        	  cursor.close();
              return name;     
          }while (cursor.moveToNext());
        	
        	  
        }
        cursor.close();  
        
        return "New";
	}
}
