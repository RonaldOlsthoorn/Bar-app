package com.groover.bar.frame;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class BackupService extends Service {
	
	private DBHelper DB;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private class BackupAttemptTask extends
			AsyncTask<Integer, Integer, Boolean> {

		protected Boolean doInBackground(Integer... integers) {

			OrderExporter ex = new OrderExporter(BackupService.this);

			return true;
		}

		protected void onPostExecute(Long result) {

		}

		private boolean checkNeedToUpdate() {

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");
			String ts_now = df1.format(c.getTime());
			
			return BackupService.this.DB.checkNeedToUpdate(ts_now);
		}
	}

}
