package com.groover.bar.frame;


import java.io.IOException;


import com.groover.bar.frame.DBHelper.BackupLog;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import android.util.Log;

public class BackupService extends IntentService {

	private DBHelper DB;
	private OrderExporter ex;
	
	public BackupService() {
		super("BackupService");
		// TODO Auto-generated constructor stub
		DB = DBHelper.getDBHelper(this);		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		// Check whether it needed to make a back up
		boolean check = DB.checkNeedToUpdate();
		
		Log.i("backup",""+check);
		
		if(check){
				
			try {
				
				ex = new OrderExporter(this);
				ex.exportLocal();
				
				ContentValues v = new ContentValues();
				v.put(BackupLog.COLUMN_TYPE, "backup");
				v.put(BackupLog.COLUMN_SUCCESS, true);
				
				DB.insertOrIgnore(BackupLog.TABLE_NAME, v);
				
				Intent serviceintent = new Intent(this, UploadService.class);
				startService(serviceintent);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ContentValues v2 = new ContentValues();
				v2.put(BackupLog.COLUMN_TYPE, "backup");
				v2.put(BackupLog.COLUMN_SUCCESS, false);	
				DB.insertOrIgnore(BackupLog.TABLE_NAME, v2);
			}
		}
	}
}
