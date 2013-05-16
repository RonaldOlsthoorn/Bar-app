package com.example.groovertest;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class GroupMainActivity extends Activity {
	
	private DBHelper DB;

	private Button voegtoe;
	private Button wijzig;
	private Button verwijder;
	
	private ListView groepen;
	private ListView leden;
	private ListView groep;
	
	private EditText groepsNaam;
	private int current;
	
	private String[] memberPool;
	private String[] groupMembers;
	
	private String[] FROM = new String[]{DBHelper.GroupTable.COLUMN_GROUP_NAME,
			"COUNT_MEMBERS"
			};
	private int[] TO = new int[]{R.groupRow.groupName, R.groupRow.memberNumber};

	private Cursor c_groups;
	private Cursor c_members;
	private Cursor c_group_members;
	
	private SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_main);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		DB = DBHelper.getDBHelper(this);
		
		voegtoe = (Button) findViewById(R.groups.voegtoe);
		wijzig = (Button) findViewById(R.groups.edit);
		verwijder = (Button) findViewById(R.groups.delete);
		
		groepsNaam = (EditText) findViewById(R.groups.groepsNaam);
		
		groepen = (ListView) findViewById(R.groups.LV1);
		
		c_groups = DB.getGroups();
		Log.i("hello", "helloooo");
		c_members = DB.getMembers();
		Log.i("hello", "helloooo");
		String[] members = membersToArray(c_members);
		
		for(int i=0;i<members.length;i++){
			Log.i("hello", members[i]);
		}

		
		adapter = new SimpleCursorAdapter(this,
				R.layout.group_row, c_groups, FROM,
				TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		groepen.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_main, menu);
		return true;
	}
	
	public void voegToe1(View view){

		String naam = groepsNaam.getText().toString();
		

		ContentValues v = new ContentValues();
		v.put(DBHelper.GroupTable.COLUMN_GROUP_NAME, naam);
		v.put(DBHelper.GroupTable.COLUMN_GROUP_BALANCE,0);
		
		boolean b = DB.insertOrIgnore(DBHelper.GroupTable.TABLE_NAME, v);
		
		c_groups.close();
		c_groups=DB.getGroups();
		adapter.swapCursor(c_groups);
		
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
	
	public String[] membersToArray(Cursor c){
		
		String[] res = new String[c.getCount()];
		c.moveToFirst();
		
		while(c.getPosition()<c.getCount()){
			
			res[c.getPosition()] = c.getString(1)+" "+c.getString(2);
			c.moveToNext();
		}
		
		return res;
	}
}