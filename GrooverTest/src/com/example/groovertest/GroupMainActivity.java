package com.example.groovertest;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class GroupMainActivity extends Activity {
	
	private DBHelper DB;

	private Button voegtoe;
	private Button wijzig;
	private Button verwijder;
	private Button opslaan;
	
	
	private LinearLayout editLayout;
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
	
	private SimpleCursorAdapter mainAdapter;
	private ArrayAdapter<String> memberAdapter;
	private ArrayAdapter<String> groupMemberAdapter;
		
	//private Filter filter;
	
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
		opslaan = (Button) findViewById(R.groups.save);
		
		groepsNaam = (EditText) findViewById(R.groups.groepsNaam);
		
		groepen = (ListView) findViewById(R.groups.LV1);
		groep  = (ListView) findViewById(R.groups.LV2);
		leden = (ListView) findViewById(R.groups.LV3);
		editLayout = (LinearLayout) findViewById(R.groups.editLayout);
		
		c_groups = DB.getGroups();
		c_members = DB.getMembers();
		memberPool = membersToArray(c_members);
		groupMembers = new String[0];
				
		mainAdapter = new SimpleCursorAdapter(this,
				R.layout.group_row, c_groups, FROM,
				TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		memberAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, memberPool);
		groupMemberAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, groupMembers);
		
		groepen.setAdapter(mainAdapter);
		leden.setAdapter(memberAdapter);
			
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
		mainAdapter.swapCursor(c_groups);
		
		groepen.setVisibility(View.GONE);
		editLayout.setVisibility(View.VISIBLE);
		
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