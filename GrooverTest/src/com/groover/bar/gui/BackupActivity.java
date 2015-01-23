package com.groover.bar.gui;

import com.groover.bar.R;
import com.groover.bar.R.id;
import com.groover.bar.R.layout;
import com.groover.bar.R.menu;
import com.groover.bar.frame.PrefConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class BackupActivity extends Activity {

	private TimePicker mTimePicker;
	private EditText mUrl;
	private EditText mUname;
	private EditText mPassword;
	
	private SharedPreferences backupSP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);

		backupSP = getSharedPreferences(
				PrefConstants.BACKUP_PREFS, Context.MODE_PRIVATE);

		mTimePicker = (TimePicker) findViewById(R.backup.timePicker);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(backupSP.getInt(
				PrefConstants.BACKUP_PREFS_INTERVAL, 0) / (3600 * 1000));
		mTimePicker.setCurrentMinute(backupSP.getInt(
				PrefConstants.BACKUP_PREFS_INTERVAL, 0)
				/ (60 * 1000)
				- 60
				* mTimePicker.getCurrentHour());
		
		mUrl = (EditText) findViewById(R.backup.ftpUrl);
		mUrl.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_FTP_URL, "ftp://"));
		
		mUname = (EditText) findViewById(R.backup.username);
		mUrl.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_UNAME, ""));
		
		mUname = (EditText) findViewById(R.backup.password);
		mUrl.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_PASSWORD, ""));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	public void saveBackupSettings() {

		Editor backupSettingsEditor = backupSP.edit();

		backupSettingsEditor.putInt(PrefConstants.BACKUP_PREFS_INTERVAL,
				(mTimePicker.getCurrentHour() * 3600 + mTimePicker.getCurrentMinute() * 60) * 1000);
		backupSettingsEditor.putString(PrefConstants.BACKUP_PREFS_FTP_URL,
				mUrl.getText().toString());
		backupSettingsEditor.putString(PrefConstants.BACKUP_PREFS_UNAME,
				mUname.getText().toString());
		backupSettingsEditor.putString(PrefConstants.BACKUP_PREFS_UNAME,
				mPassword.getText().toString());
		
		backupSettingsEditor.commit();
	}
}