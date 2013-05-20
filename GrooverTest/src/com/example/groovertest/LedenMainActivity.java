package com.example.groovertest;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class LedenMainActivity extends Activity implements OnItemClickListener {
	
	private DBHelper DB;
	private ListView ledenlijst;

	private Button voegtoe;
	private Button wijzig;
	private Button verwijder;
	
	private EditText vtVoornaam;
	private EditText vtAchternaam;
	private EditText vtId;
	
	private EditText wVoornaam;
	private EditText wAchternaam;
	private EditText wId;

	
	private int current;

	private SimpleCursorAdapter adapter;
	private Cursor c;
	private String[] FROM = new String[]{DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			DBHelper.MemberTable.COLUMN_ID};
	private int[] TO = new int[]{R.ledenlijstrow.voornaam, R.ledenlijstrow.achternaam,R.ledenlijstrow.account};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leden_main);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		

		DB = DBHelper.getDBHelper(this);

		ledenlijst = (ListView) findViewById(R.leden.listview);
		
		
		voegtoe = (Button) findViewById(R.leden.voegtoe_button);
		wijzig = (Button) findViewById(R.leden.wijzig_button);
		verwijder = (Button) findViewById(R.leden.verwijder_button);
		
		vtVoornaam = (EditText) findViewById(R.leden.voegtoe_voornaam);
		vtAchternaam = (EditText) findViewById(R.leden.voegtoe_achternaam);
		vtId = (EditText) findViewById(R.leden.voegtoe_id);
		
		wVoornaam = (EditText) findViewById(R.leden.wijzig_voornaam);
		wAchternaam = (EditText) findViewById(R.leden.wijzig_achternaam);
		wId = (EditText) findViewById(R.leden.wijzig_id);
		
		c=DB.getMembers();
		
		adapter = new SimpleCursorAdapter(this,
				R.layout.ledenlijstrow, c, FROM,
				TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		ledenlijst.setOnItemClickListener(this);
		ledenlijst.setAdapter(adapter);
		
		DB.close();
		
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

	public void voegToeLid(View view){
		
		int id =Integer.parseInt(vtId.getText().toString());
		String voornaam = vtVoornaam.getText().toString();
		String achternaam = vtAchternaam.getText().toString();
		
		ContentValues v = new ContentValues();
		v.put(DBHelper.MemberTable.COLUMN_ID, id);
		v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
		v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);
		v.put(DBHelper.MemberTable.COLUMN_BALANCE,0);
		
		long b = DB.insertOrIgnore(DBHelper.MemberTable.TABLE_NAME, v);
		
		c.close();
		c=DB.getMembers();
		adapter.swapCursor(c);
			
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		c.moveToPosition(arg2);
		current = c.getInt(0);
		wId.setText(""+c.getInt(0));
		wVoornaam.setText(c.getString(1));
		wAchternaam.setText(c.getString(2));	
		wijzig.setEnabled(true);
		verwijder.setEnabled(true);
		
		Log.i("current", ""+current);
	}
	
	public void wijzigLid(View view){
		
		c.moveToPosition(current);
		
		String voornaam = wVoornaam.getText().toString();
		String achternaam = wAchternaam.getText().toString();

		int id = Integer.parseInt(wId.getText().toString());
		ContentValues v = new ContentValues();
		v.put(DBHelper.MemberTable.COLUMN_ID, id );
		v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
		v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);
		
		boolean b = DB.updateOrIgnore(DBHelper.MemberTable.TABLE_NAME, current, v);	
		c.close();
		c=DB.getMembers();
		adapter.swapCursor(c);
		
		wijzig.setEnabled(true);
		verwijder.setEnabled(true);
		
	}
	
	public void verwijderLid(View view){
		
		c.moveToPosition(current);
		Log.i("verwijder", ""+current);
		
		boolean b = DB.deleteOrIgnore(DBHelper.MemberTable.TABLE_NAME, current);
		
		c.close();
		c=DB.getMembers();
		adapter.swapCursor(c);
		
		wijzig.setEnabled(false);
		verwijder.setEnabled(false);
		
		wVoornaam.setText("");
		wAchternaam.setText("");
		
	}
}
