package com.groover.bar.gui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.IllegalStateException;

import org.xmlpull.v1.XmlSerializer;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.FileDialog;

import com.groover.bar.frame.SelectionMode;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;

import android.database.Cursor;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class LedenMainActivity extends Activity implements OnItemClickListener {

	private DBHelper DB;
	private ListView ledenlijst;

	private Button voegtoe;

	private EditText vtVoornaam;
	private EditText vtAchternaam;
	private EditText vtEmail;

	private int current;

	private SimpleCursorAdapter adapter;
	private Cursor c;
	private String[] FROM = new String[] {
			DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			DBHelper.MemberTable.COLUMN_EMAIL };
	private int[] TO = new int[] { R.ledenlijstrow.voornaam,
			R.ledenlijstrow.achternaam, R.ledenlijstrow.account };

	private View editPane;
	private int REQUEST_FILE = 1;
	private String targetPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leden_main);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		DB = DBHelper.getDBHelper(this);

		ledenlijst = (ListView) findViewById(R.leden.listview);
		editPane = findViewById(R.leden.editPane);

		voegtoe = (Button) findViewById(R.leden.voegtoe_button);

		vtVoornaam = (EditText) findViewById(R.leden.voegtoe_voornaam);
		vtAchternaam = (EditText) findViewById(R.leden.voegtoe_achternaam);
		vtEmail = (EditText) findViewById(R.leden.email);

		c = DB.getMembers();

		adapter = new SimpleCursorAdapter(this, R.layout.ledenlijstrow, c,
				FROM, TO, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		ledenlijst.setOnItemClickListener(this);
		ledenlijst.setAdapter(adapter);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_leden_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void voegToeLid(View view) {

		String email = vtEmail.getText().toString();
		String voornaam = vtVoornaam.getText().toString();
		String achternaam = vtAchternaam.getText().toString();

		ContentValues v = new ContentValues();
		v.put(DBHelper.MemberTable.COLUMN_EMAIL, email);
		v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
		v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);
		v.put(DBHelper.MemberTable.COLUMN_BALANCE, 0);

		DB.insertOrIgnore(DBHelper.MemberTable.TABLE_NAME, v);

		c.close();
		c = DB.getMembers();
		adapter.swapCursor(c);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		c.moveToPosition(arg2);
		current = c.getInt(0);
		vtEmail.setText("" + c.getString(2));
		vtVoornaam.setText(c.getString(3));
		vtAchternaam.setText(c.getString(4));

		editPane.setVisibility(View.VISIBLE);
		voegtoe.setVisibility(View.GONE);
	}

	public void wijzigLid(View view) {

		c.moveToPosition(current);

		String voornaam = vtVoornaam.getText().toString();
		String achternaam = vtAchternaam.getText().toString();
		String email = vtEmail.getText().toString();

		ContentValues v = new ContentValues();
		v.put(DBHelper.MemberTable.COLUMN_EMAIL, email);
		v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
		v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);

		DB.updateOrIgnore(DBHelper.MemberTable.TABLE_NAME, current, v);

		c.close();
		c = DB.getMembers();
		adapter.swapCursor(c);

	}

	public void verwijderLid(View view) {

		c.moveToPosition(current);

		DB.deleteOrIgnore(DBHelper.MemberTable.TABLE_NAME, current);

		c.close();
		c = DB.getMembers();
		adapter.swapCursor(c);
		setToDefault();
	}

	public void annuleren(View view) {

		setToDefault();

	}

	public void setToDefault() {
		// TODO Auto-generated method stub
		editPane.setVisibility(View.GONE);
		voegtoe.setVisibility(View.VISIBLE);

		vtEmail.setText("");
		vtVoornaam.setText("");
		vtAchternaam.setText("");

	}

	public void exportMembers(View v) {

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

			try {

				File currentDB = this.getDatabasePath(DBHelper.DATABASE_NAME);
				File backupDB = new File(mainFolder, "DB.db");
				backupDB.createNewFile();
				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(getBaseContext(), backupDB.toString(),
						Toast.LENGTH_LONG).show();

			} catch (Exception e) {

				Toast.makeText(getBaseContext(), e.toString(),
						Toast.LENGTH_LONG).show();

			}

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yy hh.mm.ss");

			File xml = new File(mainFolder, "ledenbestand "
					+ df1.format(c.getTime()) + ".xml");

			try {
				
				BufferedOutputStream buf = new BufferedOutputStream(
						new FileOutputStream(xml));
				extractFromDB(buf);
				buf.close();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	private void extractFromDB(BufferedOutputStream buf) throws IllegalArgumentException,
			IllegalStateException, IOException {

		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setOutput(buf,"UTF-8");
		// start DOCUMENT
		xmlSerializer.startDocument("UTF-8", true);

		// open tag: <root>
		xmlSerializer.startTag(null, "root");

		
		c.close();
		c = DB.getMembers();
		c.moveToFirst();
		c.getInt(0);
			
		
		while (c.getPosition() < c.getCount()) {
			// open tag: <member>

			xmlSerializer.startTag(null, "member");

			xmlSerializer.attribute(null, "GR_ID", "" + c.getInt(1));

			xmlSerializer.attribute(null, "email", "" + c.getInt(2));
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
	
	public void importMembers(View v){
		
		Intent intent = new Intent(this,FileDialog.class);

		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] {"xml"});
		intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

		startActivityForResult(intent, REQUEST_FILE);
		
		
		
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data){
		
		if(requestCode ==REQUEST_FILE && resultCode ==FileDialog.RESULT_OK){
			
			targetPath = data.getStringExtra(FileDialog.RESULT_PATH);
		}
		
	}
	
	public class LoadData extends AsyncTask<File, Void, Void> {
	    ProgressDialog progressDialog;
	    MemberImporter importer;
	    //declare other objects as per your need
	    
	    public LoadData(){
	    	importer = new MemberImporter();
	    }
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog= ProgressDialog.show(LedenMainActivity.this, "importing...","Process Description Text", true);

	        //do initialization of required objects objects here                
	    };      
	       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
	        progressDialog.dismiss();
	    }
		@Override
		protected Void doInBackground(File... params) {
			// TODO Auto-generated method stub
			File f = params[0];
			importer.importMembers(f);
			
			return null;
		};
	 }
}
