package com.groover.bar.frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import com.groover.bar.frame.DBHelper.BackupLog;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

public class UploadService extends IntentService {

	private DBHelper DB;
	
	public UploadService() {
		super("UploadService");
		// TODO Auto-generated constructor stub
		DB = DBHelper.getDBHelper(this);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		
		
		FTPClient client = new FTPClient();	
		FileInputStream fis = null;
	
		try{
			
			int reply;
			client.connect("ftp.grooverjazz.nl");
			client.enterLocalPassiveMode();

			Log.i("ftp","reply "+client.getReplyString());
			
			reply = client.getReplyCode();
			
			if(!FTPReply.isPositiveCompletion(reply)){
				client.disconnect();
				Log.i("ftp","disconnect");
			}
			
			client.login("grooverjazz.nl", "oMKtePGi");
			boolean res = client.changeWorkingDirectory("bar/backups");
			Log.i("ftp",""+res);
			
			if(!res){
				client.makeDirectory("bar");
				client.changeWorkingDirectory("bar");
				client.makeDirectory("backups");
				res = client.changeWorkingDirectory("backups");
				
			}
			
			File backupFolder = new File(this.getFilesDir(),"backups");
			
			if(!backupFolder.exists()){
				return;
			}
			
			
			for (File child : backupFolder.listFiles()){
				
				for(File grandchild : child.listFiles()){
					
					fis = new FileInputStream(grandchild);
					res = client.storeFile(grandchild.getName(),fis);
					Log.i("ftp",grandchild.getAbsolutePath()+" "+res+" "+client.getReplyString());

				}								
			}
			
			client.logout();
			
			ContentValues v = new ContentValues();
			v.put(BackupLog.COLUMN_TYPE, "upload");
			v.put(BackupLog.COLUMN_SUCCESS, true);
			
			DB.insertOrIgnore(BackupLog.TABLE_NAME, v);
			
		            
		}catch(IOException e){
			
			e.printStackTrace();
			
			ContentValues v = new ContentValues();
			v.put(BackupLog.COLUMN_TYPE, "upload");
			v.put(BackupLog.COLUMN_SUCCESS, false);
			DB.insertOrIgnore(BackupLog.TABLE_NAME, v);
			
		}finally{
			
			try{
				
				if(fis!=null){
					fis.close();
				}
				client.disconnect();
			}catch(IOException e){
				e.printStackTrace();
			}	
		}
	}
	
}
