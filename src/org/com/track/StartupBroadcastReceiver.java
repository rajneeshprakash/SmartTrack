package org.com.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartupBroadcastReceiver extends BroadcastReceiver 
{
	public void onReceive(Context context, Intent intent)
	{
		Toast.makeText(context, "Inside on Receive", Toast.LENGTH_LONG).show();
		System.out.println("Inside Broadcast Receiver");
		if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
		Intent kir = new Intent(context, MainService.class);
		kir.setFlags(32);
        kir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(kir);
		}
	}
}