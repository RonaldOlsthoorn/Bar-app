package nl.groover.bar.gui;

import java.text.DecimalFormat;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.SearchCursor;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;

public class TurfSelectCustomerActivity extends Activity implements
		OnItemClickListener, TextWatcher {

	private final int REQUEST_CODE = 123;
	private DBHelper DB;
	private SearchCursor c_leden;
	private SearchCursor c_leden_aanwezig;
	private SearchCursor c_filtered;

	private FormatTextAdapter a_leden_aanwezig;
	private ListView l_leden_aanwezig;

	private FormatTextAdapter a_leden;

	private ListView l_leden;
	private EditText search;
	private int height;
	private boolean softkeyHidden;

	private FilterQueryProvider filterQueryProvider = new FilterQueryProvider() {
		public Cursor runQuery(CharSequence constraint) {
			// assuming you have your custom DBHelper instance
			// ready to execute the DB request

			c_filtered = new SearchCursor(DB.getFilteredMember(constraint
					.toString()));

			return c_filtered;
		}
	};

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

		DB = DBHelper.getDBHelper(this);
		c_leden = new SearchCursor(DB.getMembers());
		c_leden_aanwezig = new SearchCursor(DB.getFrequentVisitors());

		a_leden = new FormatTextAdapter(this, c_leden, FormatTextAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		a_leden.setFilterQueryProvider(filterQueryProvider);

		l_leden = (ListView) findViewById(R.selectCustomer.listAllMembers);
		l_leden.setAdapter(a_leden);
		l_leden.setOnItemClickListener(this);

		a_leden_aanwezig = new FormatTextAdapter(this, c_leden_aanwezig,
				 FormatTextAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		l_leden_aanwezig = (ListView) findViewById(R.selectCustomer.listFrequentMembers);
		l_leden_aanwezig.setAdapter(a_leden_aanwezig);
		l_leden_aanwezig.setOnItemClickListener(this);

		search = (EditText) findViewById(R.selectCustomer.search);

		Resources res = getResources();
		int color = res.getColor(android.R.color.black);
		search.setTextColor(color);
		search.addTextChangedListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos,
			long id) {

		int customerId;
		String customerName;
		String customerType;
		int customerAcount;

		if (adapterView.equals(l_leden)) {
			if (!c_leden.isClosed()) {
				c_leden.moveToPosition(pos);
				customerId = c_leden.getInt(0);
				customerName = c_leden.getString(1) + " ";

				String prefix = c_leden.getString(2);

				if (prefix != null){
					customerName = customerName+prefix+" ";
				}
				customerName = customerName+ c_leden.getString(3);
				customerType = "individual";
				customerAcount = c_leden.getInt(4);
			} else {
				c_filtered.moveToId((int) id);
				customerId = c_filtered.getInt(0);
				customerName = c_leden.getString(1) + " ";

				String prefix = c_leden.getString(2);

				if (prefix != null){
					customerName = customerName+prefix+" ";
				}
				customerName = customerName+ c_leden.getString(3);
				customerType = "individual";
				customerAcount = c_filtered.getInt(4);
			}
		} else {

			c_leden.moveToId((int) id);
			customerId = c_leden.getInt(0);
			customerName = c_leden.getString(1) + " ";

			String prefix = c_leden.getString(2);

			if (prefix != null){
				customerName = customerName+prefix+" ";
			}
			customerName = customerName+ c_leden.getString(3);
			customerType = "individual";
			customerAcount = c_leden.getInt(4);

		}

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

	public class MainSearchLayout extends LinearLayout {

		public MainSearchLayout(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.activity_turf_select_customer, this);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			Log.d("Search Layout", "Handling Keyboard Window shown");

			final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);

			if (height > proposedheight) {
				// Keyboard is shown
				Log.d("Search Layout", "Keyboard shown");
				softkeyHidden = false;
				height = proposedheight;
				findViewById(R.selectCustomer.boxAllMembers).setVisibility(
						VISIBLE);
				findViewById(R.selectCustomer.boxFrequentVisitiors)
						.setVisibility(GONE);

			}
			if (height < proposedheight) {

				height = proposedheight;
				// Keyboard is hidden
				softkeyHidden = true;
				Log.d("Search Layout", "Keyboard hidden");
				findViewById(R.selectCustomer.boxAllMembers)
						.setVisibility(GONE);
				findViewById(R.selectCustomer.boxFrequentVisitiors)
						.setVisibility(VISIBLE);
			}

			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0 && softkeyHidden == true) {
			findViewById(R.selectCustomer.boxAllMembers).setVisibility(
					View.VISIBLE);
			findViewById(R.selectCustomer.boxFrequentVisitiors).setVisibility(
					View.GONE);
		}
		if (s.length() == 0 && softkeyHidden == true) {
			findViewById(R.selectCustomer.boxAllMembers).setVisibility(
					View.GONE);
			findViewById(R.selectCustomer.boxFrequentVisitiors).setVisibility(
					View.VISIBLE);
		}

		a_leden.getFilter().filter(s);
		a_leden.notifyDataSetChanged();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
