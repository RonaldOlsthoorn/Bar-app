package com.groover.bar.gui;

import java.text.DecimalFormat;

import com.groover.bar.R;
import com.groover.bar.frame.CustomListview;
import com.groover.bar.frame.Customer;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.Order;
import com.groover.bar.frame.OrderFactory;
import com.groover.bar.frame.OrderAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

public class OrderActivity extends Activity implements 
		OrderAdapter.UpdateListener {

	private TextView customerName;
	private Customer customer;
	private ListView l_order;
	private OrderAdapter a_order;
	private DecimalFormat df = new DecimalFormat("0.00");
	private DBHelper DB;
	private Cursor c_Articles;
	private Order c_Order;
	private TextView totaal_output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		// Show the Up button in the action bar.
		setupActionBar();

		totaal_output = (TextView) findViewById(R.order.total);

		customer = new Customer(getIntent().getIntExtra("ID", -1), getIntent()
				.getIntExtra("account", -1),
				getIntent().getStringExtra("type"), getIntent().getStringExtra(
						"name"));

		customerName = (TextView) findViewById(R.order.custName);
		customerName.setText(customer.getName());

		DB = DBHelper.getDBHelper(this);

		c_Articles = DB.getArticles();
		c_Order = OrderFactory.createEmptyOrder(customer, c_Articles);

		l_order = (ListView) findViewById(R.order.orderList);

		a_order = new OrderAdapter(this, R.layout.order_row, c_Order, this);

		l_order.setAdapter(a_order);
	}

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order, menu);
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

	public void toOrderOverview(View view) {

		Intent intent = new Intent(this, OrderOverviewActivity.class);
		intent.putExtra("custId", customer.getId());
		startActivity(intent);
	}

	public void saveOrder(View view) {

		boolean res = c_Order.writeToDB(this);
		if (res) {
			Toast toast = Toast.makeText(this, c_Order.toString(),
					Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Toast toast = Toast.makeText(this, "failed to order",
					Toast.LENGTH_SHORT);
			toast.show();
		}

		Intent intent = new Intent(this, TurfSelectCustomerActivity.class);

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, intent);
		} else {
			getParent().setResult(Activity.RESULT_OK, intent);
		}
		finish();

	}

	public void updateTotal() {
		totaal_output.setText(df.format(c_Order.calculateTotal()) + "");
	}

	@Override
	public void Update(Order o) {
		totaal_output.setText(df.format(c_Order.calculateTotal()) + "");
		//a_order.notifyDataSetChanged();
	}
}