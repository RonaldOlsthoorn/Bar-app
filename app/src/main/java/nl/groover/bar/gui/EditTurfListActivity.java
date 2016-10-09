package nl.groover.bar.gui;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.FilteredCursor;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class EditTurfListActivity extends Activity implements
OnItemClickListener {

	private DBHelper DB;

	private FilteredCursor c_leden1;
	private FilteredCursor c_leden2;

	private SimpleCursorAdapter a_leden1;
	private SimpleCursorAdapter a_leden2;

	private ListView l_leden1;
	private ListView l_leden2;

	private String[] FROM1 = new String[] {
			DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			};
	
	private int[] TO1 = new int[] { R.ledenlijstrow2.voornaam,
			R.ledenlijstrow2.achternaam };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_turf_list);
		
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		DB = DBHelper.getDBHelper(this);
		
		c_leden1 = new FilteredCursor(DB.getMembers());
		c_leden1.setAll();	
		c_leden1 = FilterList(c_leden1);
		c_leden2 = c_leden1.mirrorCursor();		
			
		a_leden1 = new SimpleCursorAdapter(this, R.layout.ledenlijstrow2,
				c_leden1, FROM1, TO1,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		a_leden2 = new SimpleCursorAdapter(this, R.layout.ledenlijstrow2,
				c_leden2, FROM1, TO1,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		l_leden1 = (ListView) findViewById(R.editTurf.listView1);
		l_leden2 = (ListView) findViewById(R.editTurf.listView2);
		
		l_leden1.setAdapter(a_leden1);
		l_leden2.setAdapter(a_leden2);
		
		l_leden1.setOnItemClickListener(this);
		l_leden2.setOnItemClickListener(this);
				
	}

	private FilteredCursor FilterList(FilteredCursor c) {
		
		int bool = c.getColumnIndex(DBHelper.MemberTable.COLUMN_ACTIVE);
		c.moveToFirst();
				
		while (c.getPosition() < c.getCount()) {	
			if (c.getInt(bool) == 0) {
				c.filter(c.getPosition());		
			}else{
				c.moveToNext();
			}			
		}		
		return c;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_turf_list, menu);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if (arg0.equals(l_leden1)) {

			c_leden1.moveToPosition(arg2);
			c_leden2.addId(c_leden1.getInt(0));
			c_leden1.filter(arg2);
			a_leden1.notifyDataSetChanged();
			a_leden2.notifyDataSetChanged();
		}
		if (arg0.equals(l_leden2)) {

			c_leden2.moveToPosition(arg2);
			c_leden1.addId(c_leden2.getInt(0));			
			c_leden2.filter(arg2);
			a_leden1.notifyDataSetChanged();
			a_leden2.notifyDataSetChanged();
		}
	}
	
	public void annuleren(View view){
		
		this.finish();
	}
	
	public void opslaan(View view){
		
		c_leden1.moveToFirst();
		ContentValues c = new ContentValues();
		c.put(DBHelper.MemberTable.COLUMN_ACTIVE, 1);
		
		while(c_leden1.getPosition()<c_leden1.getCount()){
			
			DB.updateOrIgnore(DBHelper.MemberTable.TABLE_NAME, c_leden1.getInt(0), c);
			c_leden1.moveToNext();
		}
		
		c_leden2.moveToFirst();
		c = new ContentValues();
		c.put(DBHelper.MemberTable.COLUMN_ACTIVE, 0);
		
		while(c_leden2.getPosition()<c_leden2.getCount()){
			
			DB.updateOrIgnore(DBHelper.MemberTable.TABLE_NAME, c_leden2.getInt(0), c);
			c_leden2.moveToNext();
		}
	}
}
