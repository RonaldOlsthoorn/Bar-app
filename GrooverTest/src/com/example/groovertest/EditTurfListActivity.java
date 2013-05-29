package com.example.groovertest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class EditTurfListActivity extends Activity {
	
	private DBHelper DB;
	
	private FilteredCursor c_groepen1;
	private FilteredCursor c_groepen2;
	private FilteredCursor c_leden1;
	private FilteredCursor c_leden2;

	private SimpleCursorAdapter a_groepen1;
	private SimpleCursorAdapter a_groepen2;
	private SimpleCursorAdapter a_leden1;
	private SimpleCursorAdapter a_leden2;
	
	private String[] FROM1 = new String[]{DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			DBHelper.MemberTable.COLUMN_ID};
	private int[] TO1 = new int[]{R.ledenlijstrow.voornaam, R.ledenlijstrow.achternaam,R.ledenlijstrow.account};

	private String[] FROM2 = new String[]{DBHelper.GroupTable.COLUMN_GROUP_NAME,
			};
	
	private int[] TO2 = new int[]{R.groupRow3.first};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_turf_list);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		DB = DBHelper.getDBHelper(this);
		
		c_leden1 = new FilteredCursor(DB.getListMembers());
		c_leden2 = c_leden1.mirrorCursor();
		c_groepen1 = new FilteredCursor(DB.getListGroups());
		c_groepen2 = c_groepen1.mirrorCursor();
		
		a_leden1 = new SimpleCursorAdapter(this,
				R.layout.ledenlijstrow, c_leden1, FROM1,
				TO1,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		a_leden2 = new SimpleCursorAdapter(this,
				R.layout.ledenlijstrow, c_leden2, FROM1,
				TO1,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		a_groepen1 = new SimpleCursorAdapter(this,
				R.layout.group_row3, c_groepen1,
				FROM2,
				TO2,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		a_groepen2 = new SimpleCursorAdapter(this,
				R.layout.group_row3, c_groepen1,
				FROM2,
				TO2,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

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

}
