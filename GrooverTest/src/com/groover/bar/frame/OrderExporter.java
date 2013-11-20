package com.groover.bar.frame;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xmlpull.v1.XmlSerializer;

import com.groover.bar.frame.DBHelper.BackupLog;
import com.groover.bar.frame.DBHelper.Order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class OrderExporter {

	private DBHelper DB;
	private Context context;
	private String ts_settled;
	private DecimalFormat df= new DecimalFormat("0.00");
	
	public OrderExporter(Context c) {

		context = c;
		DB = DBHelper.getDBHelper(context);
		
	}

	public IOReport exportSD(){

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
		
		if(!mExternalStorageAvailable){
			return new IOReport(false,IOReport.CAUSE_NO_SD_MOUNTED);			
		}
		if(!mExternalStorageWriteable){
			return new IOReport(false,IOReport.CAUSE_WRITING_EXCEPTION);			
		}
		
		else {
			
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");
			ts_settled = df1.format(c.getTime());
			
			File sdRoot = Environment.getExternalStorageDirectory();
			File mainFolder = new File(sdRoot,
					"Groover Bar/afrekeningen/afrekening "+ts_settled);
			mainFolder.mkdirs();
			
			
			try{
				
				File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
				File backupDB = new File(mainFolder, "DB.db");
				backupDB.createNewFile();
				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

				File xml = new File(mainFolder, "afrekening "
						+ ts_settled + ".xml");

				BufferedOutputStream buf = new BufferedOutputStream(
						new FileOutputStream(xml));
				extractFromDB(buf);
				buf.close();
				
				//backup success. note to database.
				ContentValues v = new ContentValues();
				v.put(DBHelper.BackupLog.COLUMN_SUCCESS, true);
				v.put(DBHelper.BackupLog.COLUMN_TYPE, "SD");
				
				DB.insertOrIgnore(DBHelper.BackupLog.TABLE_NAME, v);
				DB.deleteAllOrders();
				
				return new IOReport(true,null);
				
			}catch(IOException e){
				
				return new IOReport(false,IOReport.CAUSE_WRITING_EXCEPTION);

			}
		}
	}
	
	public void exportLocal() throws IOException{
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");
		ts_settled = df1.format(c.getTime());
		
		File internalRoot = context.getFilesDir();
		File dir = new File(internalRoot,"backups");
		
		if (dir.isDirectory()){
			for (File child : dir.listFiles()){
				DeleteRecursive(child);
			}
		}
		
		File mainFolder = new File(internalRoot,
				"backups/backup "+ts_settled);
		mainFolder.mkdirs();
		
		File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
		File backupDB = new File(mainFolder, "backup "+ts_settled+".db");
		backupDB.createNewFile();
		FileChannel src = new FileInputStream(currentDB).getChannel();
		FileChannel dst = new FileOutputStream(backupDB).getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();

		File xml = new File(mainFolder, "backup "
				+ ts_settled + ".xml");

		BufferedOutputStream buf = new BufferedOutputStream(
				new FileOutputStream(xml));
		extractFromDB(buf);
		buf.close();
	}

	private void extractFromDB(BufferedOutputStream buf)
			throws IllegalArgumentException, IllegalStateException, IOException {
		
		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setOutput(buf, "UTF-8");
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		// open tag: <root>
		xmlSerializer.startTag(null, "afrekening");
		xmlSerializer.attribute(null, "datum_afrekening", ts_settled);
		
		
		xmlSerializer.startTag(null, "list");
		xmlSerializer.attribute(null, "id", "members");
		
		Cursor c = DB.getMembers();
		c.moveToFirst();

		while (c.getPosition() < c.getCount()) {
			// open tag: <member>
			
			Cursor orders = DB.getConsumptions(c.getInt(3));
			
			
			xmlSerializer.startTag(null, "member");

			xmlSerializer.attribute(null, "GR_ID", "" + c.getInt(0));
			xmlSerializer.attribute(null, "first_name", c.getString(1));
			xmlSerializer.attribute(null, "last_name", "" + c.getString(2));
			xmlSerializer.attribute(null, "total", df.format(c.getDouble(4)));
			
			orders.moveToFirst();
			
			while(orders.getPosition() < orders.getCount()){
				
				xmlSerializer.startTag(null, "consumption");

				xmlSerializer.attribute(null, "ts_created", orders.getString(1));
				xmlSerializer.attribute(null, "ts_settled", ts_settled);
				xmlSerializer.attribute(null, "article", orders.getString(3));
				xmlSerializer.attribute(null, "amount", "" + orders.getInt(4));
				xmlSerializer.attribute(null, "price", df.format(orders.getDouble(5)));

				xmlSerializer.attribute(null, "subtotal", df.format(orders.getDouble(0)));

				xmlSerializer.endTag(null, "consumption");
				
				orders.moveToNext();
				
			}

			orders.close();
			xmlSerializer.endTag(null, "member");

			c.moveToNext();

		}
		
		xmlSerializer.endTag(null,"list");
		
		xmlSerializer.endTag(null, "afrekening");

		xmlSerializer.endDocument();
		xmlSerializer.flush();

	}
	
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
}
