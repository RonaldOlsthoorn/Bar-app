package nl.groover.bar.gui;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.SearchCursor;
import nl.groover.bar.frame.ViewGroupListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.widget.TabHost;

public class TurfSelectCustomerActivity extends Activity implements
		OnItemClickListener, TextWatcher {

	private final int REQUEST_CODE = 123;
	private DBHelper DB;
	private SearchCursor cMembers;
	private SearchCursor cGroups;
	private SearchCursor cFrequentMembers;
	private SearchCursor cFrequentGroups;

	private TabHost tabHost;

	private MemberListAdapter aFrequentMembers;
	private ViewGroupListAdapter aFrequentGroups;

	private ListView lFrequentMembers;
	private ListView lFrequentGroups;

	private MemberListAdapter aMembers;
	private ViewGroupListAdapter aGroups;

	private ListView lMembers;
	private ListView lGroups;

	private EditText searchMembers;
	private EditText searchGroups;

	private int height;
	private boolean softkeyHidden;

	private int TAB;
	private static final int TAB_MEMBERS = 1;
	private static final int TAB_GROUPS = 2;

	public static final String CUSTOMER_TYPE = "customer_type";
	public static final String CUSTOMER_TYPE_INDIVIDUAL = "individual";
	public static final String CUSTOMER_TYPE_GROUP = "group";


	private FilterQueryProvider filterMembers = new FilterQueryProvider() {
		public Cursor runQuery(CharSequence constraint) {
			// assuming you have your custom DBHelper instance
			// ready to execute the DB request

			cMembers = new SearchCursor(DB.getFilteredMember(constraint
					.toString()));

			return cMembers;
		}
	};

	private FilterQueryProvider filterGroups = new FilterQueryProvider() {
		public Cursor runQuery(CharSequence constraint) {
			// assuming you have your custom DBHelper instance
			// ready to execute the DB request

			cGroups = new SearchCursor(DB.getFilterGroups(constraint.toString()));

			return cGroups;
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
		cMembers = new SearchCursor(DB.getMembers());
		cGroups = new SearchCursor(DB.getAllGroups());
		cFrequentMembers = new SearchCursor(DB.getFrequentVisitors());
		cFrequentGroups = new SearchCursor(DB.getFrequentGroups());

		aMembers = new MemberListAdapter(this, cMembers, MemberListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		aMembers.setFilterQueryProvider(filterMembers);

		aGroups = new ViewGroupListAdapter(this, cGroups,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		aGroups.setFilterQueryProvider(filterGroups);

		lMembers = (ListView) findViewById(R.id.select_customer_activity_list_all_members);
		lMembers.setAdapter(aMembers);
		lMembers.setOnItemClickListener(this);

		lGroups = (ListView) findViewById(R.id.select_customer_activity_list_all_groups);
		lGroups.setAdapter(aGroups);
		lGroups.setOnItemClickListener(this);

		aFrequentMembers = new MemberListAdapter(this, cFrequentMembers,
				 MemberListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		aFrequentGroups = new ViewGroupListAdapter(this, cFrequentGroups,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		lFrequentMembers = (ListView) findViewById(R.id.select_customer_activity_list_frequent_members);
		lFrequentMembers.setAdapter(aFrequentMembers);
		lFrequentMembers.setOnItemClickListener(this);

		lFrequentGroups = (ListView) findViewById(R.id.select_customer_activity_list_frequent_groups);
		lFrequentGroups.setAdapter(aFrequentGroups);
		lFrequentGroups.setOnItemClickListener(this);

		searchMembers = (EditText) findViewById(R.id.select_customer_activity_search_member);
		searchGroups = (EditText) findViewById(R.id.select_customer_activity_search_groups);


		Resources res = getResources();
		int color = res.getColor(android.R.color.black);
		searchMembers.setTextColor(color);
		searchMembers.addTextChangedListener(this);

		searchGroups.setTextColor(color);
		searchGroups.addTextChangedListener(this);

		TabHost tabHost = (TabHost)findViewById(R.id.select_customer_activity_root);
		tabHost.setup();

		//Tab 1
		TabHost.TabSpec spec = tabHost.newTabSpec("Leden");
		spec.setContent(R.id.Leden);
		spec.setIndicator("Leden");
		tabHost.addTab(spec);

		//Tab 2
		spec = tabHost.newTabSpec("Groepen");
		spec.setContent(R.id.Groepen);
		spec.setIndicator("Groepen");
		tabHost.addTab(spec);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos,
			long id) {

		int customerId;
		String customerName;
		String customerType;
		int customerAcount;

		if (adapterView.equals(lMembers) || adapterView.equals(lFrequentMembers)) {

			cMembers.moveToId((int) id);
			customerId = cMembers.getInt(0);
			customerName = cMembers.getString(1) + " ";

			String prefix = cMembers.getString(2);

			if (prefix != null){
				customerName = customerName+prefix+" ";
			}
			customerName = customerName+ cMembers.getString(3);
			customerType = CUSTOMER_TYPE_INDIVIDUAL;
			customerAcount = cMembers.getInt(4);
		} else if (adapterView.equals(lGroups) || adapterView.equals(lFrequentGroups)) {

			cGroups.moveToId((int) id);
			customerId = cGroups.getInt(0);
			customerName = cGroups.getString(1);
			customerType = CUSTOMER_TYPE_GROUP;
			customerAcount = cGroups.getInt(2);
		}else{
			return;
		}

		Intent intent = new Intent(this, OrderActivity.class);
		intent.putExtra(CUSTOMER_TYPE, customerType);
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

	public class MainSearchLayout extends TabHost {

		public MainSearchLayout(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.activity_turf_select_customer, this);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			Log.d("Search Layout", "Handling Keyboard Window shown");

			final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);

			if (height > proposedHeight) {
				// Keyboard is shown
				Log.d("Search Layout", "Keyboard shown");
				softkeyHidden = false;
				height = proposedHeight;

				findViewById(R.id.select_customer_activity_box_all_members).setVisibility(
						VISIBLE);
				findViewById(R.id.select_customer_activity_box_frequent_visitiors)
						.setVisibility(GONE);

				findViewById(R.id.select_customer_activity_box_all_groups).setVisibility(
						VISIBLE);
				findViewById(R.id.select_customer_activity_box_frequent_groups)
						.setVisibility(GONE);
			}
			if (height < proposedHeight) {

				height = proposedHeight;
				// Keyboard is hidden
				softkeyHidden = true;
				Log.d("Search Layout", "Keyboard hidden");
				findViewById(R.id.select_customer_activity_box_all_members)
						.setVisibility(GONE);
				findViewById(R.id.select_customer_activity_box_frequent_visitiors)
						.setVisibility(VISIBLE);

				findViewById(R.id.select_customer_activity_box_all_groups)
						.setVisibility(GONE);
				findViewById(R.id.select_customer_activity_box_frequent_groups)
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

			findViewById(R.id.select_customer_activity_box_all_members).setVisibility(
					View.VISIBLE);
			findViewById(R.id.select_customer_activity_box_frequent_visitiors).setVisibility(
					View.GONE);

			findViewById(R.id.select_customer_activity_box_all_groups).setVisibility(
					View.VISIBLE);
			findViewById(R.id.select_customer_activity_box_frequent_groups).setVisibility(
					View.GONE);
		}
		if (s.length() == 0 && softkeyHidden == true) {
			findViewById(R.id.select_customer_activity_box_all_members).setVisibility(
					View.GONE);
			findViewById(R.id.select_customer_activity_box_frequent_visitiors).setVisibility(
					View.VISIBLE);

			findViewById(R.id.select_customer_activity_box_all_groups).setVisibility(
					View.GONE);
			findViewById(R.id.select_customer_activity_box_frequent_groups).setVisibility(
					View.VISIBLE);
		}

		aMembers.getFilter().filter(s);
		aMembers.notifyDataSetChanged();

		aGroups.getFilter().filter(s);
		aGroups.notifyDataSetChanged();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
