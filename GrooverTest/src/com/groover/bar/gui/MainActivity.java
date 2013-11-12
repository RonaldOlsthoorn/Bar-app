package com.groover.bar.gui;
import com.groover.bar.R;
import com.groover.bar.frame.BackupService;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	private int REQUEST_CODE=123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setupBackupService();
	}

	private void setupBackupService() {
		// TODO Auto-generated method stub
			
		alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, BackupService.class);
		PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
		        20 * 1000, AlarmManager.INTERVAL_HOUR , pIntent);
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void toBeheer(View view){
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent,REQUEST_CODE);
	}
	
	public void toBarBazz(View view){
		
		Intent intent = new Intent(this, BarBazzActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.i("main","hello");
		
		if (requestCode == 123) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, BeheerActivity.class);
				startActivity(intent);
			}
		}
	}
}
