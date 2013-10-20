package com.groover.bar.gui;

import java.io.File;
import java.io.IOException;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.FileDialog;
import com.groover.bar.frame.MemberExporter;
import com.groover.bar.frame.MemberImporter;

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
	private EditText vtId;

	private int current;

	private SimpleCursorAdapter adapter;
	private Cursor c;
	private String[] FROM = new String[] {
			DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			DBHelper.MemberTable.COLUMN_GR_ID };
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
		vtId = (EditText) findViewById(R.leden.id);

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

		int id = Integer.parseInt(vtId.getText().toString());
		String voornaam = vtVoornaam.getText().toString();
		String achternaam = vtAchternaam.getText().toString();

		ContentValues v = new ContentValues();
		v.put(DBHelper.MemberTable.COLUMN_GR_ID, id);
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
		current = (int) arg3;

		vtVoornaam.setText(c.getString(1));
		vtAchternaam.setText(c.getString(2));
		vtId.setText(current+"");
		
		editPane.setVisibility(View.VISIBLE);
		voegtoe.setVisibility(View.GONE);
		
	}

	public void wijzigLid(View view) {

		c.moveToPosition(current);
		
		String voornaam = vtVoornaam.getText().toString();
		String achternaam = vtAchternaam.getText().toString();
		int id = Integer.parseInt(vtId.getText().toString());
		
		ContentValues v = new ContentValues();
		v.put(DBHelper.MemberTable.COLUMN_GR_ID, id);
		v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
		v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);
		
		System.out.println("hello 1"+current);

		DB.updateOrIgnore(DBHelper.MemberTable.TABLE_NAME, current, v);

		System.out.println("hello 2"+current);
		
		c.close();
		c = DB.getMembers();
		
		System.out.println("hello 3"+current);
		
		adapter.swapCursor(c);

	}

	public void verwijderLid(View view) {
	
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

		vtId.setText("");
		vtVoornaam.setText("");
		vtAchternaam.setText("");

	}

	public void exportMembers(View v) {
		
		MemberExporter ex = new MemberExporter(this);
		
		try {
			ex.export();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public class LoadData extends AsyncTask<File, Void, Boolean> {
	    ProgressDialog progressDialog;
	    MemberImporter importer;
	    //declare other objects as per your need
	    
	    public LoadData(){
	    	importer = new MemberImporter(LedenMainActivity.this);
	    }
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog= ProgressDialog.show(LedenMainActivity.this, "importing...","Process Description Text", true);
              
	    };      
	       
	    @Override
	    protected void onPostExecute(Boolean result)
	    {
	        super.onPostExecute(result);
	        progressDialog.dismiss();
	    }
		@Override
		protected Boolean doInBackground(File... params) {
			// TODO Auto-generated method stub
			File f = params[0];
			return importer.importMembers(f);	
		};
	 }
}
