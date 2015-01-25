package com.groover.bar.gui;

import com.groover.bar.R;
import com.groover.bar.R.id;
import com.groover.bar.R.layout;
import com.groover.bar.R.menu;
import com.groover.bar.frame.BackupService;
import com.groover.bar.frame.PrefConstants;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

public class BackupActivity extends Activity {

	private TimePicker mTimePicker;
	private ToggleButton mBackupsEnabled;
	private EditText mUrl;
	private EditText mUname;
	private EditText mPassword;
	private Button mTestconnection;
	private RadioGroup mBackupType;
	private Intent intent = new Intent(this, BackupService.class);

	private SharedPreferences backupSP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);

		backupSP = getSharedPreferences(PrefConstants.BACKUP_PREFS,
				Context.MODE_PRIVATE);

		mBackupsEnabled = (ToggleButton) findViewById(R.backup.toggle);
		mBackupsEnabled
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						setBackupSettingsEnabled(isChecked);
					}
				});

		mTimePicker = (TimePicker) findViewById(R.backup.timePicker);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(backupSP.getInt(
				PrefConstants.BACKUP_PREFS_INTERVAL, 0) / (3600 * 1000));
		mTimePicker.setCurrentMinute(backupSP.getInt(
				PrefConstants.BACKUP_PREFS_INTERVAL, 0)
				/ (60 * 1000)
				- 60
				* mTimePicker.getCurrentHour());

		mBackupType = (RadioGroup) findViewById(R.backup.backuptype);

		mUrl = (EditText) findViewById(R.backup.ftpUrl);
		mUrl.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_FTP_URL,
				"ftp://"));

		mUname = (EditText) findViewById(R.backup.username);
		mUrl.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_UNAME, ""));

		mUname = (EditText) findViewById(R.backup.password);
		mUrl.setText(backupSP
				.getString(PrefConstants.BACKUP_PREFS_PASSWORD, ""));
		mTestconnection = (Button) findViewById(R.backup.testconnection);

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

	public void onRadioButtonClicked(View radioButton) {

		Editor backupSettingsEditor = backupSP.edit();

		if (((RadioButton) radioButton).getText().equals("Local backup")) {
			backupSettingsEditor.putString(PrefConstants.BACKUP_TYPE,
					PrefConstants.BACKUP_TYPE_LOCAL);
		}
		if (((RadioButton) radioButton).getText().equals("Internet backup")) {
			backupSettingsEditor.putString(PrefConstants.BACKUP_TYPE,
					PrefConstants.BACKUP_TYPE_INTERNET);
		}

		backupSettingsEditor.commit();
	}

	public void saveBackupSettings() {

		Editor backupSettingsEditor = backupSP.edit();

		backupSettingsEditor.putBoolean(PrefConstants.BACKUP_ENABLED,
				mBackupsEnabled.isChecked());

		backupSettingsEditor.putInt(PrefConstants.BACKUP_PREFS_INTERVAL,
				(mTimePicker.getCurrentHour() * 3600 + mTimePicker
						.getCurrentMinute() * 60) * 1000);

		String type;
		if (mBackupType.getCheckedRadioButtonId() == R.backup.radioLocal) {
			type = PrefConstants.BACKUP_TYPE_LOCAL;
		} else {
			type = PrefConstants.BACKUP_TYPE_INTERNET;
		}
		backupSettingsEditor.putString(PrefConstants.BACKUP_TYPE, type);

		backupSettingsEditor.putString(PrefConstants.BACKUP_PREFS_FTP_URL, mUrl
				.getText().toString());
		backupSettingsEditor.putString(PrefConstants.BACKUP_PREFS_UNAME, mUname
				.getText().toString());
		backupSettingsEditor.putString(PrefConstants.BACKUP_PREFS_PASSWORD,
				mPassword.getText().toString());

		backupSettingsEditor.commit();
	}

	@Override
	protected void onStop() {
		super.onStop(); // Always call the superclass method first

		saveBackupSettings();

		AlarmManager alarmMgr = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);

		if (backupSP.getBoolean(PrefConstants.BACKUP_ENABLED, false)) {
			alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + 5 * 1000, backupSP.getLong(
							PrefConstants.BACKUP_PREFS_INTERVAL, 3600 * 1000),
					pIntent);
		}else{
			alarmMgr.cancel(pIntent);
		}
	}

	public void setBackupSettingsEnabled(boolean enable) {

		mTimePicker.setEnabled(enable);
		mBackupType.setEnabled(enable);

		setInternetSettingsEnabled(backupSP.getString(
				PrefConstants.BACKUP_TYPE, PrefConstants.BACKUP_TYPE_LOCAL)
				.equals(PrefConstants.BACKUP_TYPE_INTERNET));
	}

	public void setInternetSettingsEnabled(boolean enable) {

		mUrl.setEnabled(enable);
		mUname.setEnabled(enable);
		mPassword.setEnabled(enable);
	}
}