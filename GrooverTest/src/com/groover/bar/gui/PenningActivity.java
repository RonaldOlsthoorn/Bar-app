package com.groover.bar.gui;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.IOReport;
import com.groover.bar.frame.OrderExporter;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class PenningActivity extends FragmentActivity {
	
	DBHelper DB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_penning);
		// Show the Up button in the action bar.
		setupActionBar();
		
		DB = DBHelper.getDBHelper(this);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.penning, menu);
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

	public void processDB(View v) {

		OrderExporter ex = new OrderExporter(this);	
		IOReport report = ex.exportSD();
		
		if(report.getSucces()){
			Toast toast = Toast.makeText(this, "Succesfully made afrekening", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}if(report.getCause().equals(IOReport.CAUSE_NO_SD_MOUNTED)){			
			BasicAlertDialogFragment dialog = new BasicAlertDialogFragment("No sd card mounted. Check if there is an sd card in the slot. Tablet must not be connect to a pc using USB!");
			dialog.show(getSupportFragmentManager(), "error_sd");
			return;
		}else{
			BasicAlertDialogFragment dialog = new BasicAlertDialogFragment("An error occured during the process. No backup made");
			dialog.show(getSupportFragmentManager(), "error_sd");
		}
	}

	public void toStatistics(View view) {

	}
}
