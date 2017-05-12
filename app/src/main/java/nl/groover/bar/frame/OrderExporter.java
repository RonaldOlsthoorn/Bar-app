package nl.groover.bar.frame;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xmlpull.v1.XmlSerializer;

import nl.groover.bar.frame.DBHelper.BackupLog;
import nl.groover.bar.frame.DBHelper.Order;

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
	private DecimalFormat df = new DecimalFormat("0.00");

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
			
			File sdRoot = context.getExternalFilesDir(null);
			File mainFolder = new File(sdRoot,
					"afrekeningen/afrekening "+ts_settled);
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

				File xml1 = new File(mainFolder, "bestellingen "
						+ ts_settled + ".xml");

				BufferedOutputStream buf1 = new BufferedOutputStream(
						new FileOutputStream(xml1));
				extractOrdersFromDB(buf1);
				buf1.close();
				
				File xml2 = new File(mainFolder, "afrekening "
						+ ts_settled + ".xml");

				BufferedOutputStream buf2 = new BufferedOutputStream(
						new FileOutputStream(xml2));
				extractReceiptFromDB(buf2);
				buf2.close();
				
				//backup success. note to database.
				ContentValues v = new ContentValues();
				v.put(DBHelper.BackupLog.COLUMN_SUCCESS, true);
				v.put(DBHelper.BackupLog.COLUMN_TYPE, "SD");
				
				DB.insertOrIgnore(DBHelper.BackupLog.TABLE_NAME, v);
				DB.deleteAllOrders();
				
				return new IOReport(true,null);
				
			}catch(IOException e){
				
				return new IOReport(false,IOReport.CAUSE_WRITING_EXCEPTION);
			}catch(DBHelper.InvalidGroupException e){

				return new IOReport(false,IOReport.CAUSE_ZERO_GROUP, e.getGroupName());
			}

		}
	}

	public void backupSD() throws IOException {

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

		if (!mExternalStorageAvailable || !mExternalStorageWriteable) {
			throw new IOException();
		} else {

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");
			ts_settled = df1.format(c.getTime());

			File sdRoot = context.getExternalFilesDir(null);
			File mainFolder = new File(sdRoot,
					"Groover Bar/backup " + ts_settled);
			mainFolder.mkdirs();
			
			if (mainFolder.isDirectory()) {
				for (File child : mainFolder.listFiles()) {
					DeleteRecursive(child);
				}
			}

			File xml1 = new File(mainFolder, "bestellingen " + ts_settled
					+ ".xml");

			BufferedOutputStream buf1 = new BufferedOutputStream(
					new FileOutputStream(xml1));
			extractOrdersFromDB(buf1);
			buf1.close();

			// backup success. note to database.
			ContentValues v = new ContentValues();
			v.put(DBHelper.BackupLog.COLUMN_SUCCESS, true);
			v.put(DBHelper.BackupLog.COLUMN_TYPE, "SD");

			DB.insertOrIgnore(DBHelper.BackupLog.TABLE_NAME, v);
		}
	}

	public void backupLocal() throws IOException {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");
		ts_settled = df1.format(c.getTime());

		File internalRoot = context.getFilesDir();
		File dir = new File(internalRoot, "backups");

		if (dir.isDirectory()) {
			for (File child : dir.listFiles()) {
				DeleteRecursive(child);
			}
		}

		File mainFolder = new File(internalRoot, "backups/backup " + ts_settled);
		mainFolder.mkdirs();

		File xml1 = new File(mainFolder, "bestellingen " + ts_settled
				+ ".xml");

		BufferedOutputStream buf1 = new BufferedOutputStream(
				new FileOutputStream(xml1));
		extractOrdersFromDB(buf1);
		buf1.close();

	}

	private void extractOrdersFromDB(BufferedOutputStream buf)
			throws IllegalArgumentException, IllegalStateException, IOException {

		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setOutput(buf, "UTF-8");
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		// open tag: <root>
		xmlSerializer.startTag(null, "bestellingen");
		xmlSerializer.attribute(null, "datum_afrekening", ts_settled);

		xmlSerializer.startTag(null, "list");
		xmlSerializer.attribute(null, "id", "members");

		Cursor c = DB.getMembers();
		c.moveToFirst();

		while (c.getPosition() < c.getCount()) {
			// open tag: <member>

			Cursor orders = DB.getConsumptionsByMember(c.getInt(3));

			xmlSerializer.startTag(null, "member");

			xmlSerializer.attribute(null, "GR_ID", "" + c.getInt(0));
			xmlSerializer.attribute(null, "first_name", c.getString(1));

			String prefix = c.getString(2);

			if (prefix == null){

				xmlSerializer.attribute(null, "prefix", "");
			}else{

				xmlSerializer.attribute(null, "prefix", prefix);
			}

			xmlSerializer.attribute(null, "last_name", c.getString(3));
			xmlSerializer.attribute(null, "total", df.format(c.getDouble(5)));

			orders.moveToFirst();

			while (orders.getPosition() < orders.getCount()) {

				xmlSerializer.startTag(null, "consumption");

				xmlSerializer
						.attribute(null, "ts_created", orders.getString(1));
				xmlSerializer.attribute(null, "ts_settled", ts_settled);
				xmlSerializer.attribute(null, "article", orders.getString(3));
				xmlSerializer.attribute(null, "amount", "" + orders.getInt(4));
				xmlSerializer.attribute(null, "price",
						df.format(orders.getDouble(5)));

				xmlSerializer.attribute(null, "subtotal",
						df.format(orders.getDouble(0)));

				xmlSerializer.endTag(null, "consumption");

				orders.moveToNext();
			}

			orders.close();
			xmlSerializer.endTag(null, "member");

			c.moveToNext();
		}

		xmlSerializer.endTag(null, "list");
		xmlSerializer.endTag(null, "bestellingen");
		xmlSerializer.endDocument();
		xmlSerializer.flush();
	}

	private void extractReceiptFromDB(BufferedOutputStream buf)
			throws IllegalArgumentException, IllegalStateException, IOException, DBHelper.InvalidGroupException {

		DB.settleAllGroups();

		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setOutput(buf, "UTF-8");
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		// open tag: <root>
		xmlSerializer.startTag(null, "afrekening");
		xmlSerializer.attribute(null, "datum_afrekening", ts_settled);

		Cursor articles = DB.getArticles();
		articles.moveToFirst();

		xmlSerializer.startTag(null, "list");
		xmlSerializer.attribute(null, "id", "articles");

		while (articles.getPosition() < articles.getCount()) {

			xmlSerializer.startTag(null, "article");
			xmlSerializer.attribute(null, "id", "" + articles.getInt(0));
			xmlSerializer.attribute(null, "name", articles.getString(1));
			xmlSerializer.attribute(null, "price",
					df.format(articles.getDouble(2)));
			xmlSerializer.endTag(null, "article");

			articles.moveToNext();
		}

		xmlSerializer.endTag(null, "list");

		xmlSerializer.startTag(null, "list");
		xmlSerializer.attribute(null, "id", "members");

		Cursor members = DB.getMembers();
		members.moveToFirst();

		while (members.getPosition() < members.getCount()) {
			// open tag: <member>
			xmlSerializer.startTag(null, "member");

			xmlSerializer.attribute(null, "GR_ID", "" + members.getInt(0));
			xmlSerializer.attribute(null, "first_name", members.getString(1));

			String prefix = members.getString(3);

			if (prefix == null){
				xmlSerializer.attribute(null, "prefix", "");
			}else{
				xmlSerializer.attribute(null, "prefix", prefix);
			}

			xmlSerializer.attribute(null, "last_name", members.getString(3));
			xmlSerializer.attribute(null, "total",
					df.format(members.getDouble(5)));

			Cursor consumptions = DB.getTotalConsumptionsByMember(members
					.getInt(4));

			consumptions.moveToFirst();

			while (consumptions.getPosition() < consumptions.getCount()) {

				xmlSerializer.startTag(null, "article");
				xmlSerializer
						.attribute(null, "id", "" + consumptions.getInt(0));
				xmlSerializer
						.attribute(null, "name", consumptions.getString(1));
				xmlSerializer.attribute(null, "price",
						df.format(consumptions.getDouble(2)));
				xmlSerializer.attribute(null, "amount",
						"" + consumptions.getInt(3));
				xmlSerializer.endTag(null, "article");
				consumptions.moveToNext();
			}
			consumptions.close();

			Cursor settlements = DB.getGroupSettlementsByMember(members.getInt(0));

			settlements.moveToFirst();

			while (settlements.getPosition() < settlements.getCount()) {

				xmlSerializer.startTag(null, "group settlement");
				xmlSerializer
						.attribute(null, "group name", "" + settlements.getString(1));
				xmlSerializer.attribute(null, "total",
						df.format(settlements.getDouble(2)));

				xmlSerializer.endTag(null, "group settlement");
				consumptions.moveToNext();
			}
			settlements.close();

			xmlSerializer.endTag(null, "member");

			members.moveToNext();
		}

		xmlSerializer.endTag(null, "list");

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