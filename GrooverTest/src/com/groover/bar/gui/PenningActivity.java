package com.groover.bar.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import au.com.bytecode.opencsv.CSVWriter;

public class PenningActivity extends Activity {
	
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

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		Log.i("available", "available: " + mExternalStorageAvailable
				+ " writable: " + mExternalStorageWriteable);

		if (mExternalStorageAvailable && mExternalStorageWriteable) {

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");

			File sdRoot = Environment.getExternalStorageDirectory();
			File mainFolder = new File(sdRoot,
					"Groover Bar/Afrekeningen/Afrekening "
							+ df1.format(c.getTime()));
			boolean res1 = mainFolder.mkdirs();

			try {

				File currentDB = this.getDatabasePath(DBHelper.DATABASE_NAME);
				File backupDB = new File(mainFolder, "DB.db");
				backupDB.createNewFile();
				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(getBaseContext(), backupDB.toString(),
						Toast.LENGTH_LONG).show();

			} catch (Exception e) {

				Toast.makeText(getBaseContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			}

			File cvs = new File(mainFolder, "afrekening.csv");

			CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter(cvs.getAbsolutePath()));
				writer.writeNext(new String[] {"Afrekening"});
				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public String[] memberOrders(){
		
		Cursor c = DB.getMemberOrders();
		
		return null;
	}

	public void toStatistics(View view) {

	}
}
