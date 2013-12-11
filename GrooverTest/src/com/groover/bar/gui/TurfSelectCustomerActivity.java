package com.groover.bar.gui;

import java.text.DecimalFormat;
import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.SearchCursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;

public class TurfSelectCustomerActivity extends Activity implements
		OnItemClickListener {

	private int REQUEST_CODE = 123;
	private DBHelper DB;
	private SearchCursor c_leden;
	private SearchCursor c_leden_aanwezig;

	private FormatTextAdapter a_leden_aanwezig;
	private ListView l_leden_aanwezig;

	private Cursor c_filter_leden;
	private SimpleCursorAdapter a_leden;
	private DecimalFormat df = new DecimalFormat("0.00");
	private String[] FROM_LEDEN = new String[] {
			DBHelper.MemberTable.COLUMN_FIRST_NAME,
			DBHelper.MemberTable.COLUMN_LAST_NAME,
			DBHelper.MemberTable.COLUMN_BALANCE };
	private int[] TO_LEDEN = new int[] { R.ledenlijstrow.voornaam,
			R.ledenlijstrow.achternaam, R.ledenlijstrow.account };
	private ListView l_leden;
	private TextView customerNameTV;
	private int customerId;
	private String customerName;
	private String customerType;
	private int customerAcount;
	private Button nextButton;
	private AutoCompleteTextView search;
	private SimpleCursorAdapter autoCompleteAdapter;
	private boolean memberListClickable = true;

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
		MainSearchLayout searchLayout = new MainSearchLayout(this, null);
        setContentView(searchLayout);

		// Show the Up button in the action bar.
		setupActionBar();

		nextButton = (Button) findViewById(R.selectCustomer.nextButton);
		customerNameTV = (TextView) findViewById(R.selectCustomer.customer);

		DB = DBHelper.getDBHelper(this);
		c_leden = new SearchCursor(DB.getListMembers());
		c_leden_aanwezig = new SearchCursor(DB.getFrequentVisitors());

		a_leden = new FormatTextAdapter(this, R.layout.ledenlijstrow, c_leden,
				FROM_LEDEN, TO_LEDEN,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, df,
				R.ledenlijstrow.account);

		l_leden = (ListView) findViewById(R.selectCustomer.listViewleden);
		l_leden.setAdapter(a_leden);
		l_leden.setOnItemClickListener(this);

		a_leden_aanwezig = new FormatTextAdapter(this, R.layout.ledenlijstrow,
				c_leden_aanwezig, FROM_LEDEN, TO_LEDEN,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, df,
				R.ledenlijstrow.account);

		l_leden_aanwezig = (ListView) findViewById(R.selectCustomer.leden_aanwezig);
		l_leden_aanwezig.setAdapter(a_leden_aanwezig);
		l_leden_aanwezig.setOnItemClickListener(this);

		autoCompleteAdapter = new SimpleCursorAdapter(this,
				R.layout.autocomplete, c_filter_leden, new String[] {
						DBHelper.MemberTable.COLUMN_FIRST_NAME,
						DBHelper.MemberTable.COLUMN_LAST_NAME }, new int[] {
						R.groupRow2.first, R.groupRow2.last },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		search = (AutoCompleteTextView) findViewById(R.selectCustomer.search);
		search.setThreshold(2);
		search.setAdapter(autoCompleteAdapter);
		autoCompleteAdapter.setFilterQueryProvider(filterQueryProvider);

		Resources res = getResources();
		int color = res.getColor(android.R.color.black);
		search.setTextColor(color);

		search.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos,
			long id) {

		Log.i("Click",memberListClickable+"");
		if (adapterView.equals(l_leden) ) {
			if(memberListClickable){
				
				c_leden.moveToPosition(pos);
				customerId = c_leden.getInt(0);
				customerName = c_leden.getString(1) + " " + c_leden.getString(2);
				customerType = "individual";
				customerAcount = c_leden.getInt(3);
				customerNameTV.setText(customerName);
				nextButton.setEnabled(true);
			}
		}

		else {

			c_leden.moveToId((int) id);
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

	private FilterQueryProvider filterQueryProvider = new FilterQueryProvider() {
		public Cursor runQuery(CharSequence constraint) {
			// assuming you have your custom DBHelper instance
			// ready to execute the DB request
			return DB.getFilteredMember(constraint.toString());

		}
	};

	
	//gebeund!
	public class MainSearchLayout extends LinearLayout {

	    public MainSearchLayout(Context context, AttributeSet attributeSet) {
	        super(context, attributeSet);
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        inflater.inflate(R.layout.activity_turf_select_customer, this);
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        Log.d("Search Layout", "Handling Keyboard Window shown");

	        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
	        final int actualHeight = getHeight();

	        if (actualHeight > proposedheight){
	            // Keyboard is shown
	        	Log.i("measure","hello false");
	        	memberListClickable=false;

	        } else {
	            // Keyboard is hidden
	        	Log.i("measure","hello true");
	        	memberListClickable = true;
	        }
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    }
	}
}
