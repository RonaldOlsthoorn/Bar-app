package com.groover.bar.frame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class MemberImporter {

	private final String DOCUMENT_ROOT = "memberimport";
	private final String LIST = "memberlist";

	private DBHelper DB;
	private File file;
	private Context context;

	public MemberImporter(Context c) {

		context = c;
		DB = DBHelper.getDBHelper(context);
	}

	public boolean importMembers() {

		DB.deleteAllMembers();
		
		boolean res = false;
		Document doc = getDomElement(file.getAbsolutePath());

		if (doc == null) {

			return res;
		}

		res = true ; 
		
		NodeList createList = doc.getElementById(LIST)
				.getElementsByTagName("member");
		Element n;
		ContentValues v = new ContentValues();

		for (int i = 0; i < createList.getLength(); i++) {

			n = (Element) createList.item(i);
			v.clear();
			v.put(DBHelper.MemberTable.COLUMN_GR_ID, n.getAttribute("gr_id"));
			v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME,
					n.getAttribute("firstname"));
			v.put(DBHelper.MemberTable.COLUMN_LAST_NAME,
					n.getAttribute("lastname"));

			long result = DB.insertOrIgnore(DBHelper.MemberTable.TABLE_NAME, v);
			
			if(result==-1){
				res = false;
			}

		}
		
		DB.deleteAllOrders();

		return res;
	}

	public boolean importMembers(File f) {

		file = f;

		return importMembers();
	}

	public Document getDomElement(String path) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(path));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}
}
