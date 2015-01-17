package com.groover.bar.gui;

import java.text.DecimalFormat;

import com.groover.bar.R;

import com.groover.bar.frame.Customer;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.Order;
import com.groover.bar.frame.OrderAdapter;
import com.groover.bar.frame.OrderFactory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EditOrderActivity extends Activity implements OnItemClickListener,
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
	private int orderId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_order);

		// Show the Up button in the action bar.
		setupActionBar();

		totaal_output = (TextView) findViewById(R.editOrder.total);

		customer = new Customer(getIntent().getIntExtra("ID", -1), getIntent()
				.getIntExtra("account", -1),
				getIntent().getStringExtra("type"), getIntent().getStringExtra(
						"name"));

		customerName = (TextView) findViewById(R.editOrder.custName);
		customerName.setText(customer.getName());

		DB = DBHelper.getDBHelper(this);

		c_Articles = DB.getArticles();

		orderId = getIntent().getIntExtra("order", -1);
		c_Order = OrderFactory.createExistingOrder(customer, c_Articles,
				DB.getOrder(orderId));
		updateTotal();
		l_order = (ListView) findViewById(R.editOrder.orderList);

		a_order = new OrderAdapter(this, R.layout.order_row, c_Order, this);

		l_order.setAdapter(a_order);
		l_order.setOnItemClickListener(this);
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
		getMenuInflater().inflate(R.menu.edit_order, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_place_order) {
			saveOrderDB();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		updateTotal();
	}

	public void goBack(View view) {

		finish();
	}

	public void saveOrder(){
		
		saveOrderDB();
	}
	
	public void saveOrderDB() {

		DB.deleteOrder(orderId);

		boolean res = c_Order.writeToDB(this);
		if (res) {
			Toast toast = Toast.makeText(this, "Wijzigingen opgeslagen",
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
		// TODO Auto-generated method stub
		totaal_output.setText(df.format(c_Order.calculateTotal()) + "");
	}
}
