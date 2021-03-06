package nl.groover.bar.frame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class NetworkChangeReceiver extends BroadcastReceiver {
	
	/**
	 * Called whenever a change of status occurs in the network.
	 * Try to upload backups if there are any.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("Bar","Noticed network change");
		Intent myIntent = new Intent(context, UploadService.class);
		context.startService(myIntent);

	}
} 
