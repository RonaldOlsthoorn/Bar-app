package com.groover.bar.gui;

import java.text.DecimalFormat;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;

public class TurfSelectCustomerActivity extends Activity implements
		OnItemClickListener {

	private int REQUEST_CODE = 123;

	private DBHelper DB;
	private Cursor c_leden;
	// private Cursor c_groepen;
	private SimpleCursorAdapter a_leden;
	private SimpleCursorAdapter a_groepen;
	private DecimalFormat df;

	private String[] FROM_LEDEN = new String[] {
			DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			DBHelper.MemberTable.COLUMN_BALANCE };

	// private String[] FROM_GROUPS = new String[] {
	// DBHelper.GroupTable.COLUMN_GROUP_NAME,
	// DBHelper.GroupTable.COLUMN_GROUP_BALANCE };

	private int[] TO_LEDEN = new int[] { R.ledenlijstrow.voornaam,
			R.ledenlijstrow.achternaam, R.ledenlijstrow.account };
	// private int[] TO_GROUPS = new int[] { R.grouprow1.naam,
	// R.grouprow1.balance };

	private ListView l_leden;
	// private ListView l_groepen;

	private TextView customerNameTV;
	private int customerId;
	private String customerName;
	private String customerType;
	private int customerAcount;
	private Button nextButton;

	private AutoCompleteTextView search;

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.turf_select_customer, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_turf_select_customer);
		// Show the Up button in the action bar.
		setupActionBar();

		df = new DecimalFormat("0.00");

		nextButton = (Button) findViewById(R.selectCustomer.nextButton);
		customerNameTV = (TextView) findViewById(R.selectCustomer.customer);

		DB = DBHelper.getDBHelper(this);
		c_leden = DB.getListMembers();

		// c_groepen = DB.getListGroups();

		a_leden = new FormatTextAdapter(this, R.layout.ledenlijstrow, c_leden,
				FROM_LEDEN, TO_LEDEN,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, df,
				R.ledenlijstrow.account);

		// a_groepen = new FormatTextAdapter(this, R.layout.grouprow, c_groepen,
		// FROM_GROUPS, TO_GROUPS,
		// CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, df,
		// R.grouprow1.balance);

		l_leden = (ListView) findViewById(R.selectCustomer.listViewleden);

		// TO PUT GOUPS BACK UNCOMMENT THIS LIN
		findViewById(R.selectCustomer.listViewgroepen).setVisibility(View.GONE);

		l_leden.setAdapter(a_leden);

		// l_groepen.setAdapter(a_groepen);

		l_leden.setOnItemClickListener(this);
		// l_groepen.setOnItemClickListener(this);

		search = (AutoCompleteTextView) findViewById(R.selectCustomer.search);
		search.setThreshold(2);
		search.setAdapter(new SimpleCursorAdapter(this, R.layout.group_row2,
				c_leden, 
				new String[] { DBHelper.MemberTable.COLUMN_FIRST_NAME,
				DBHelper.MemberTable.COLUMN_LAST_NAME }, 
				new int[] {R.groupRow2.first, R.groupRow2.last },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		
		Resources res = getResources();
		int color = res.getColor(android.R.color.black);
		search.setTextColor(color);
		
		search.setOnItemClickListener(this);

	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg0.equals(l_leden)) {
			c_leden.moveToPosition(arg2);
			customerId = c_leden.getInt(0);
			customerName = c_leden.getString(1) + " " + c_leden.getString(2);
			customerType = "individual";
			customerAcount = c_leden.getInt(3);
			customerNameTV.setText(customerName);
			nextButton.setEnabled(true);
		}
		// if (arg0.equals(l_groepen)) {
		// c_groepen.moveToPosition(arg2);
		// customerId = c_groepen.getInt(0);
		// customerName = c_groepen.getString(1);
		// customerType = "group";
		// customerAcount = c_groepen.getInt(2);
		// customerNameTV.setText(customerName);
		// nextButton.setEnabled(true);
		//
		// }
		
		
		else{
			
			c_leden.moveToPosition(arg2);
			customerId = c_leden.getInt(0);
			customerName = c_leden.getString(1) + " " + c_leden.getString(2);
			customerType = "individual";
			customerAcount = c_leden.getInt(3);
			customerNameTV.setText(customerName);
			nextButton.setEnabled(true);	
			
			search.setText(c_leden.getString(1) + " " + c_leden.getString(2));
		}
	}

	public void annuleren(View view) {
		this.finish();
	}

	public void next(View view) {

		Intent intent = new Intent(this, OrderActivity.class);
		intent.putExtra("type", customerType);
		intent.putExtra("ID", customerId);
		intent.putExtra("account", customerAcount);
		intent.putExtra("name", customerName);

		startActivityForResult(intent, REQUEST_CODE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 123) {
			if (resultCode == RESULT_OK) {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		}
	}
}
