package com.groover.bar.gui;


import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.FilteredCursor;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.Button;
import android.widget.EditText;

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
	
	
	private LinearLayout groepen_lo;
	private LinearLayout leden_lo;
	private LinearLayout groepsLeden_lo;
	
	private ListView groepen;
	private ListView leden;
	private ListView groep;
	
	private EditText groepsNaam;
	private int current=-1;
	
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
		
		groepsLeden_lo = (LinearLayout) findViewById(R.groups.groepsLeden_lo);
		leden_lo = (LinearLayout) findViewById(R.groups.leden_lo);
		groepen_lo = (LinearLayout) findViewById(R.groups.groepen_lo);
		
		groepen.setOnItemClickListener(this);
		groep.setOnItemClickListener(this);
		leden.setOnItemClickListener(this);
		
		c_groups = DB.getGroupsFancy();
		
		Cursor m = DB.getMembers();

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
		
		groep.setEnabled(false);
		leden.setEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_main, menu);
		return true;
	}
	
	public void voegToe1(View view){
		
		c_group_members.clearAllRows();
		c_members.setAll();
		
		groepen.setEnabled(true);
		leden.setEnabled(true);
		
		groepen_lo.setVisibility(View.GONE);
		voegtoe.setVisibility(View.GONE);
		leden_lo.setVisibility(View.VISIBLE);
		groepsLeden_lo.setVisibility(View.VISIBLE);
		
		opslaan.setVisibility(View.VISIBLE);
		anuleren.setVisibility(View.VISIBLE);
		
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
	
	public void saveGroup(View view){

		String naam = groepsNaam.getText().toString();
		
		if(current == -1){
			
			ContentValues v = new ContentValues();
			v.put(DBHelper.GroupTable.COLUMN_GROUP_NAME, naam);
			v.put(DBHelper.GroupTable.COLUMN_GROUP_BALANCE,0);
			
			long b = DB.insertOrIgnore(DBHelper.GroupTable.TABLE_NAME, v);
			
			v = new ContentValues();
			
			c_group_members.moveToFirst();
			
			while(c_group_members.getPosition() < c_group_members.getCount() ){
				
				v.put(DBHelper.GroupMembers.COLUMN_NAME_GROUP_ID, (int) b );
				v.put(DBHelper.GroupMembers.COLUMN_NAME_MEMBER_ID, c_group_members.getString(2));
				DB.insertOrIgnore(DBHelper.GroupMembers.TABLE_NAME, v);
				
				c_group_members.moveToNext();
				
			}
			
		}else{
			
			DB.PayOffGroupOrIgnore(current);
			ContentValues v = new ContentValues();
			v.put(DBHelper.GroupTable.COLUMN_GROUP_NAME, naam);
			DB.updateOrIgnore(DBHelper.GroupTable.TABLE_NAME, current, v);
			DB.deleteGroupMembers(current);
			v = new ContentValues();
			
			c_group_members.moveToFirst();
			
			while(c_group_members.getPosition() < c_group_members.getCount() ){
				
				v.put(DBHelper.GroupMembers.COLUMN_NAME_GROUP_ID, current );
				v.put(DBHelper.GroupMembers.COLUMN_NAME_MEMBER_ID, c_group_members.getString(2));
				DB.insertOrIgnore(DBHelper.GroupMembers.TABLE_NAME, v);
				c_group_members.moveToNext();
				
			}
		}
		
		setToDefault();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if(arg0.equals(groepen)){
						
			voegtoe.setVisibility(View.GONE);
			wijzig.setVisibility(View.VISIBLE);
			verwijder.setVisibility(View.VISIBLE);
			anuleren.setVisibility(View.VISIBLE);
			
			c_groups.moveToPosition(arg2);
			current=c_groups.getInt(0);
			
			groepsNaam.setText(c_groups.getString(1));
			groepsNaam.setEnabled(false);
			
			c_group_members.clearAllRows();
			c_members.setAll();
			
			Cursor c = DB.getGroupMembers(c_groups.getInt(0));
			c.moveToFirst();

			while(c.getPosition()<c.getCount()){

				c_group_members.addId(c.getInt(0));	
				c_members.filterId(c.getInt(0));
				
				c.moveToNext();
				
			}

			c.close();

			memberAdapter.notifyDataSetChanged();
			groupMemberAdapter.notifyDataSetChanged();
							
			groepsLeden_lo.setVisibility(View.VISIBLE);
			
		}
		
		if(arg0.equals(leden)){
			
			c_members.moveToPosition(arg2);
			
			int res = c_members.getUnfilteredPosition();
			
			c_group_members.addPos(c_members.getUnfilteredPosition());
			c_members.filter(arg2);
			
			groupMemberAdapter.notifyDataSetChanged();			
			memberAdapter.notifyDataSetChanged();
		}
		
		if(arg0.equals(groep)){
			
			c_group_members.moveToPosition(arg2);
			
			int res = c_group_members.getUnfilteredPosition();

			c_members.addPos(res);	
			c_group_members.filter(arg2);
			
			groupMemberAdapter.notifyDataSetChanged();			
			memberAdapter.notifyDataSetChanged();
		}		
	}
	
	public void deleteGroup(View view){
		
		boolean res = DB.PayOffGroupOrIgnore(current);		
		
		DB.deleteOrIgnore(DBHelper.GroupTable.TABLE_NAME, current);
		Cursor members = DB.getGroupMembers(current);
		
		setToDefault();
		
	}
	
	public void cancel(View view){
		
		setToDefault();
	}
	
	public void edit(View view){
		
		groepen_lo.setVisibility(View.GONE);
		leden_lo.setVisibility(View.VISIBLE);
		
		wijzig.setVisibility(View.GONE);
		opslaan.setVisibility(View.VISIBLE);
		
		groep.setEnabled(true);
		leden.setEnabled(true);
		
		groepsNaam.setEnabled(true);
	}
	
	public void setToDefault(){
		
		
		c_groups.close();
		
		c_groups = DB.getGroupsFancy();
		c_members.setAll();
		c_group_members.clearAllRows();
		
		groep.setEnabled(false);
		leden.setEnabled(false);
		
		mainAdapter.swapCursor(c_groups);
		mainAdapter.notifyDataSetChanged();
		memberAdapter.notifyDataSetChanged();
		groupMemberAdapter.notifyDataSetChanged();
		
		voegtoe.setVisibility(View.VISIBLE);
		wijzig.setVisibility(View.GONE);
		verwijder.setVisibility(View.GONE);
		opslaan.setVisibility(View.GONE);
		anuleren.setVisibility(View.GONE);
		
		groepen_lo.setVisibility(View.VISIBLE);
		leden_lo.setVisibility(View.GONE);
		groepsLeden_lo.setVisibility(View.GONE);
		
		groepsNaam.setText("");
		groepsNaam.setEnabled(true);
		
		current = -1;
	}
}