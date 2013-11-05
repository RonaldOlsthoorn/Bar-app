package com.groover.bar.frame;

import org.apache.commons.net.ftp.FTPClient;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends IntentService {

	public UploadService() {
		super("UploadService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i("hello","hello");
		
		
		
	}
}
