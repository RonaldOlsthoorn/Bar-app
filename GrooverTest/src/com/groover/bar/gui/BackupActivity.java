package com.groover.bar.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.groover.bar.R;
import com.groover.bar.frame.BackupService;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.InfoDialog;
import com.groover.bar.frame.OrderExporter;
import com.groover.bar.frame.PrefConstants;
import com.groover.bar.frame.DBHelper.BackupLog;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class BackupActivity extends Activity {

	private static final String TAG = BackupActivity.class.getSimpleName();
	private TimePicker mTimePicker;
	private ToggleButton mBackupsEnabled;
	private EditText mUrl;
	private EditText mUname;
	private EditText mPassword;
	private Button mTestconnection;
	private RadioGroup mBackupType;
	private SharedPreferences backupSP;
	private DBHelper DB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);

		DB = DBHelper.getDBHelper(this);
		backupSP = getSharedPreferences(PrefConstants.BACKUP_PREFS,
				Context.MODE_PRIVATE);

		mBackupsEnabled = (ToggleButton) findViewById(R.backup.toggle);
		mBackupsEnabled.setChecked(backupSP.getBoolean(
				PrefConstants.BACKUP_ENABLED, false));
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
		if (backupSP.getString(PrefConstants.BACKUP_TYPE,
				PrefConstants.BACKUP_TYPE_LOCAL).equals(
				PrefConstants.BACKUP_TYPE_LOCAL)) {
			mBackupType.check(R.backup.radioLocal);
		} else {
			mBackupType.check(R.backup.radioInternet);
		}

		mUrl = (EditText) findViewById(R.backup.ftpUrl);
		mUrl.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_FTP_URL,
				"ftp.grooverjazz.nl"));

		mUname = (EditText) findViewById(R.backup.username);
		mUname.setText(backupSP.getString(PrefConstants.BACKUP_PREFS_UNAME, ""));

		mPassword = (EditText) findViewById(R.backup.password);
		mPassword.setText(backupSP.getString(
				PrefConstants.BACKUP_PREFS_PASSWORD, ""));

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

	public void onRadioButton(View radioButton) {

		if (((RadioButton) radioButton).getText().equals("Local backup")) {
			setInternetSettingsEnabled(false);
		}
		if (((RadioButton) radioButton).getText().equals("Internet backup")) {
			setInternetSettingsEnabled(true);
		}
	}
	
	public void onTestConnection(View view){
		
		if (mBackupType.getCheckedRadioButtonId() == R.backup.radioLocal) {
			TestLocalBackupTask task = new TestLocalBackupTask();
			task.doInBackground();
			
		} else {
			TestInternetBackupTask task = new TestInternetBackupTask();
			task.doInBackground();			
		}
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

		Log.v(TAG, "backup settings saved");
		saveBackupSettings();

		Intent intent = new Intent(this, BackupService.class);
		AlarmManager alarmMgr = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);

		if (backupSP.getBoolean(PrefConstants.BACKUP_ENABLED, false)) {
			alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + 5 * 1000, backupSP.getInt(
							PrefConstants.BACKUP_PREFS_INTERVAL, 3600 * 1000),
					pIntent);
		} else {
			alarmMgr.cancel(pIntent);
		}
	}

	public void setBackupSettingsEnabled(boolean enable) {

		mTimePicker.setEnabled(enable);

		for (int i = 0; i < mBackupType.getChildCount(); i++) {
			mBackupType.getChildAt(i).setEnabled(enable);
		}

		if (enable) {
			setInternetSettingsEnabled(backupSP.getString(
					PrefConstants.BACKUP_TYPE, PrefConstants.BACKUP_TYPE_LOCAL)
					.equals(PrefConstants.BACKUP_TYPE_INTERNET));
		} else {
			setInternetSettingsEnabled(false);
		}
	}

	public void setInternetSettingsEnabled(boolean enable) {

		mUrl.setEnabled(enable);
		mUname.setEnabled(enable);
		mPassword.setEnabled(enable);
		mTestconnection.setEnabled(enable);
	}

	private class TestInternetBackupTask extends AsyncTask<Void, Void, String> {

		public static final String RESULT_LOGIN_FAILED = "Backup test failed. Failed to log in";
		public static final String RESULT_OK = "Backup test successfull";
		public static final String RESULT_UPLOAD_FAILED = "Backup test failed. Failed upload.";
		public static final String RESULT_UNKNOWN_ERROR = "Backup test faild. Unknown error";

		@Override
		protected String doInBackground(Void... params) {
			String response = "";
			
			String url = mUrl.getText().toString();
			String uName = mUname.getText().toString();
			String passWord = mPassword.getText().toString();
			
			FTPClient client = new FTPClient();
			FileInputStream fis = null;
			
			try {
				int reply;
				client.connect(url);
				client.enterLocalPassiveMode();

				Log.i("ftp log in: ", "reply " + client.getReplyString());

				reply = client.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply)) {
					client.disconnect();
					Log.i("ftp", "disconnect");
					return RESULT_LOGIN_FAILED;
				}

				client.login(uName, passWord);
				boolean res = client.changeWorkingDirectory("bar/backups");

				if (!res) {
					client.makeDirectory("bar");
					client.changeWorkingDirectory("bar");
					client.makeDirectory("backups");
					res = client.changeWorkingDirectory("backups");

				}
				
				if(!res){
					
					client.logout();
					return RESULT_UPLOAD_FAILED;
				}

				res = client.storeFile("test", new InputStream() {
					
					@Override
					public int read() throws IOException {
						return 0;
					}
				});
				
				if(!res){
					
					client.logout();
					return RESULT_UPLOAD_FAILED;
				}
							
				client.logout();
				client.disconnect();
				return RESULT_OK;

			} catch (IOException e) {

				return RESULT_UNKNOWN_ERROR;

			} finally {

				try {
					client.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPostExecute(String result) {
			displayTestResult(result);
		}
	}

	private class TestLocalBackupTask extends AsyncTask<Void, Void, String> {

		public static final String RESULT_NO_SD = "Backup test failed. No SD card available";
		public static final String RESULT_SD_NOT_WRITABLE = "Backup test failed. SD not writable";
		public static final String RESULT_OK = "Backup test successfull";
		public static final String RESULT_UNKNOWN_ERROR = "Backup test failed. Unknown error";

		@Override
		protected String doInBackground(Void... params) {

			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				return RESULT_SD_NOT_WRITABLE;
			} else if (!Environment.MEDIA_MOUNTED.equals(state)) {
				return RESULT_NO_SD;
			}

			try {

				OrderExporter ex = new OrderExporter(BackupActivity.this);
				ex.backupSD();

				return RESULT_OK;

			} catch (IOException e) {

				ContentValues v = new ContentValues();
				v.put(BackupLog.COLUMN_TYPE, "backup SD");
				v.put(BackupLog.COLUMN_SUCCESS, false);
				DB.insertOrIgnore(BackupLog.TABLE_NAME, v);

				return RESULT_UNKNOWN_ERROR;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			displayTestResult(result);
		}
	}

	public void displayTestResult(String res) {
		InfoDialog id= new InfoDialog(this, "res");
		id.show();
	}

}