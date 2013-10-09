package com.groover.bar.frame;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Xml;

public class MemberExporter {

	private DBHelper DB;
	private Context context;
	public MemberExporter(Context c) {

		context = c;
		DB = DBHelper.getDBHelper(context);
		
	}

	public void export() throws IOException {

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

		if (mExternalStorageAvailable && mExternalStorageWriteable) {

			File sdRoot = Environment.getExternalStorageDirectory();
			File mainFolder = new File(sdRoot,
					"Groover Bar/import export leden");
			mainFolder.mkdirs();

			File currentDB = context.getDatabasePath(DBHelper.DATABASE_NAME);
			File backupDB = new File(mainFolder, "DB.db");
			backupDB.createNewFile();
			FileChannel src = new FileInputStream(currentDB).getChannel();
			FileChannel dst = new FileOutputStream(backupDB).getChannel();
			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");

			File xml = new File(mainFolder, "ledenbestand "
					+ df1.format(c.getTime()) + ".xml");

			BufferedOutputStream buf = new BufferedOutputStream(
					new FileOutputStream(xml));
			extractFromDB(buf);
			buf.close();

		}
	}

	private void extractFromDB(BufferedOutputStream buf)
			throws IllegalArgumentException, IllegalStateException, IOException {

		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setOutput(buf, "UTF-8");
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		// open tag: <root>
		xmlSerializer.startTag(null, "root");

		
		Cursor c = DB.getMembers();
		c.moveToFirst();
		c.getInt(0);

		while (c.getPosition() < c.getCount()) {
			// open tag: <member>

			xmlSerializer.startTag(null, "member");

			xmlSerializer.attribute(null, "GR_ID", "" + c.getInt(1));

			xmlSerializer.attribute(null, "email",  c.getString(2));
			xmlSerializer.attribute(null, "first_name", c.getString(3));
			xmlSerializer.attribute(null, "last_name", "" + c.getString(4));
			xmlSerializer.attribute(null, "account_nr", "" + c.getInt(5));
			xmlSerializer.attribute(null, "balance", "" + c.getDouble(6));

			xmlSerializer.endTag(null, "member");

			c.moveToNext();

		}
		// end DOCUMENT
		xmlSerializer.endTag(null, "root");

		xmlSerializer.endDocument();
		xmlSerializer.flush();

	}
}
