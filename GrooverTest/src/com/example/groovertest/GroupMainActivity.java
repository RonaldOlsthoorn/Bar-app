package com.example.groovertest;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class GroupMainActivity extends Activity implements OnItemClickListener{
	
	private DBHelper DB;

	private Button voegtoe;
	private Button wijzig;
	private Button verwijder;
	private Button opslaan;
	private Button anuleren;
	
	
	private LinearLayout editLayout;
	private ListView groepen;
	private ListView leden;
	private ListView groep;
	
	private EditText groepsNaam;
	private int current;
	
	private String[] FROM = new String[]{DBHelper.GroupTable.COLUMN_GROUP_NAME,
			"COUNT_MEMBERS"
			};
	
	private int[] TO = new int[]{R.groupRow.groupName, R.groupRow.memberNumber};

	private Cursor c_groups;
	private FilteredCursor c_members;
	private FilteredCursor c_group_members;
	
	private SimpleCursorAdapter mainAdapter;
	private SimpleCursorAdapter memberAdapter;
	private SimpleCursorAdapter groupMemberAdapter;
	
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
		anuleren = (Button) findViewById(R.groups.cancel);
		
		groepsNaam = (EditText) findViewById(R.groups.groepsNaam);
		
		groepen = (ListView) findViewById(R.groups.LV1);
		groep  = (ListView) findViewById(R.groups.LV2);
		leden = (ListView) findViewById(R.groups.LV3);
		editLayout = (LinearLayout) findViewById(R.groups.editLayout);
		
		groepen.setOnItemClickListener(this);
		groep.setOnItemClickListener(this);
		leden.setOnItemClickListener(this);
		
		c_groups = DB.getGroups();
		c_members = new FilteredCursor(DB.getMembers());
		c_group_members = new FilteredCursor(DB.getMembers());
		c_group_members.clearAllRows();
		
		mainAdapter = new SimpleCursorAdapter(this,
				R.layout.group_row, c_groups, FROM,
				TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		memberAdapter = new SimpleCursorAdapter(this,R.layout.group_row2, c_members,new String[]{DBHelper.MemberTable.COLUMN_FIRST_NAME,
				DBHelper.MemberTable.COLUMN_LAST_NAME,
				}, new int[] {R.groupRow2.first , R.groupRow2.last},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		groupMemberAdapter = new SimpleCursorAdapter(this,R.layout.group_row2, c_group_members,new String[]{DBHelper.MemberTable.COLUMN_FIRST_NAME,
				DBHelper.MemberTable.COLUMN_LAST_NAME,
				}, new int[] {R.groupRow2.first , R.groupRow2.last},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
	
		groepen.setAdapter(mainAdapter);
		leden.setAdapter(memberAdapter);
		groep.setAdapter(groupMemberAdapter);
		current = -1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_main, menu);
		return true;
	}
	
	public void voegToe1(View view){
		
		groepen.setVisibility(View.GONE);
		voegtoe.setVisibility(View.GONE);
		editLayout.setVisibility(View.VISIBLE);
		opslaan.setVisibility(View.VISIBLE);
		
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
			
			res[c.getPosition()] =  c.getString(1)+" "+c.getString(2);				
			c.moveToNext();
			
		}
		

		
		return res;
	}
	
	public void saveGroup(View view){

		String naam = groepsNaam.getText().toString();
		
		ContentValues v = new ContentValues();
		v.put(DBHelper.GroupTable.COLUMN_GROUP_NAME, naam);
		v.put(DBHelper.GroupTable.COLUMN_GROUP_BALANCE,0);
		
		long b = DB.insertOrIgnore(DBHelper.GroupTable.TABLE_NAME, v);
		
		c_groups.close();
		c_groups=DB.getGroups();
		mainAdapter.swapCursor(c_groups);
		
		v = new ContentValues();
		
		c_group_members.moveToFirst();
		
		while(c_group_members.getPosition() < c_group_members.getCount() ){
			
			v.put(DBHelper.GroupMembers.COLUMN_NAME_GROUP_ID, (int) b );
			v.put(DBHelper.GroupMembers.COLUMN_NAME_MEMBER_ID, c_group_members.getInt(0));
			DB.insertOrIgnore(DBHelper.GroupMembers.TABLE_NAME, v);
			
			c_group_members.moveToNext();
			
		}
			
		groepen.setVisibility(View.VISIBLE);
		editLayout.setVisibility(View.GONE);
		opslaan.setVisibility(View.GONE);
		anuleren.setVisibility(View.GONE);
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if(arg0.equals(groepen)){
			
			voegtoe.setVisibility(View.GONE);
			wijzig.setVisibility(View.VISIBLE);
			verwijder.setVisibility(View.VISIBLE);
			c_groups.moveToPosition(arg2);
			groepsNaam.setText(c_groups.getString(1));
			
		}
		
		if(arg0.equals(leden)){
			
			Log.i("Test","leden arg2: "+arg2+", arg3: "+arg3);

			c_members.moveToPosition(arg2);
			c_members.filter(arg2);
			c_group_members.addPos(arg2);
			
			groupMemberAdapter.notifyDataSetChanged();
			memberAdapter.notifyDataSetChanged();
		}
		
		if(arg0.equals(groep)){
			
			Log.i("Test","groep arg2: "+arg2+", arg3: "+arg3);
			
			c_group_members.moveToPosition(arg2);
			c_group_members.filter(arg2);
			c_members.addPos(arg2);
			
			groupMemberAdapter.notifyDataSetChanged();
			memberAdapter.notifyDataSetChanged();
		}
		
	}
}