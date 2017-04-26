package org.com.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver
{
	public static String outnum;
	public void onReceive(Context context, Intent intent) 
	{
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                Bundle bundle = intent.getExtras();
            
         if(bundle ==  null) return;
         String callie_name;
         outnum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
         System.out.println("Out Going Number : "+ outnum);
         
         if(MainService.call_alert_flag)
         {
        	 
         callie_name=TravelPhoneStateListener.getContactNameFromNumber(outnum);
         System.out.println("Callie : "+callie_name+" Number "+outnum);
         MainService.sendSMS(MainService.call_alert_no, 
           		"Their is an Out-going call To : "+callie_name+" - "+outnum);
         }
        }
     }
}
