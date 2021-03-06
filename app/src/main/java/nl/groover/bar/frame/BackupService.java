package nl.groover.bar.frame;

import java.io.IOException;

import nl.groover.bar.frame.DBHelper.BackupLog;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

// BackupService class is used every hour to make backups of the database.
// If there is not internet connection to store.
public class BackupService extends IntentService {

	private static final String TAG = BackupService.class.getSimpleName();
	private DBHelper DB;
	private OrderExporter ex;

	public BackupService() {
		super("BackupService");
		DB = DBHelper.getDBHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.v(TAG,"onBackupService");
		// Check whether it needed to make a back up
		boolean check = DB.checkNeedToBackup();

		if (check) {

			SharedPreferences backupSP = getSharedPreferences(
					PrefConstants.BACKUP_PREFS, Context.MODE_PRIVATE);
			if (backupSP.getString(PrefConstants.BACKUP_TYPE,
					PrefConstants.BACKUP_TYPE_LOCAL).equals(
					PrefConstants.BACKUP_TYPE_LOCAL)) {
				
				try {
					// Make local backup
					ex = new OrderExporter(this);
					ex.backupSD();

				} catch (IOException e) {
					// Not able to produce backup. Log the error and log the event
					// in the database
					e.printStackTrace();
					ContentValues v = new ContentValues();
					v.put(BackupLog.COLUMN_TYPE, "backup SD");
					v.put(BackupLog.COLUMN_SUCCESS, false);
					DB.insertOrIgnore(BackupLog.TABLE_NAME, v);
				}
			}else{
				
				try {
					// Make local backup
					ex = new OrderExporter(this);
					ex.backupLocal();
					
					// try to upload the backup using UploadService
					Intent serviceintent = new Intent(this, UploadService.class);
					startService(serviceintent);

				} catch (IOException e) {
					// Not able to produce backup. Log the error and log the event
					// in the database
					e.printStackTrace();
					ContentValues v = new ContentValues();
					v.put(BackupLog.COLUMN_TYPE, "backup ftp");
					v.put(BackupLog.COLUMN_SUCCESS, false);
					DB.insertOrIgnore(BackupLog.TABLE_NAME, v);
				}				
			}			
		}
	}
}
