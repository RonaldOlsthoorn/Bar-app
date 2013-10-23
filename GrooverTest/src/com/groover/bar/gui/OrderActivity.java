package com.groover.bar.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.groover.bar.R;
import com.groover.bar.frame.ArticleFactory;
import com.groover.bar.frame.Customer;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.Order;
import com.groover.bar.frame.OrderListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

public class OrderActivity extends Activity implements OnItemClickListener, PropertyChangeListener{
	
	private TextView customerName;
	private Customer customer;
	
	private TextView order_amount;
	private int current_amount;
	private int current_article;

	private Spinner s_categories;
	
	private ListView l_artikelen;
	private ListView l_order;
	
	private SimpleCursorAdapter a_artikelen;
	private OrderListAdapter a_order;
	private ArrayAdapter<String> a_category;
	
	private DecimalFormat df;
	
	private String[] FROM_A = new String[]{DBHelper.ItemList.COLUMN_NAME_ITEM, DBHelper.ItemList.COLUMN_NAME_PRICE};
	private int[] TO_A = new int[]{R.articlerow2.naam,R.articlerow2.price};
		
	private DBHelper DB;
	
	private Cursor c_Articles;
	private Order c_Order;
	
	private ArrayList<String> category;
	private NumPadAdapter numPadAdapter;
	private TextView totaal_output;
	
	private ArrayList<String> order_Map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		// Show the Up button in the action bar.
		setupActionBar();
		
		current_article = -1;
		order_amount = (TextView) findViewById(R.order.amount);
		totaal_output = (TextView) findViewById(R.order.total);
		
		customer = new Customer(getIntent().getIntExtra("ID", -1),
									getIntent().getIntExtra("account", -1),
									getIntent().getStringExtra("type"),
									getIntent().getStringExtra("name"));
		
		customerName = (TextView) findViewById(R.order.custName);
		customerName.setText(customer.getName());
		
		DB = DBHelper.getDBHelper(this);
		
		s_categories = (Spinner) findViewById(R.order.catSpinner);
		l_artikelen = (ListView) findViewById(R.order.artList);
		l_order = (ListView) findViewById(R.order.orderList);		
		
		category = toArrayList(DB.getCategories());
		category.add("Alle");
		
		df = new DecimalFormat("0.00");
		
		c_Articles = DB.getArticles();
		ArticleFactory af = new ArticleFactory(c_Articles);
		c_Order = new Order(customer,af);
		
		a_category = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, category);
		a_artikelen = new SimpleCursorAdapter(this, R.layout.article_row2, c_Articles, FROM_A, TO_A, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

		numPadAdapter = new NumPadAdapter();
		
		a_order = new OrderListAdapter(this,R.layout.order_row, c_Order,this);

		s_categories.setAdapter(a_category);
		l_artikelen.setAdapter(a_artikelen);
		
		l_order.getPaddingLeft();
		a_order.hashCode();

		l_order.setAdapter(a_order);
		
		l_order.setOnItemClickListener(this);
		l_artikelen.setOnItemClickListener(this);
		
		numPadAdapter.addPropertyListener(this);
		
		findViewById(R.numPad.one).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.two).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.three).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.four).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.five).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.six).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.seven).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.eight).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.nine).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.zero).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.plus).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.minus).setOnClickListener(numPadAdapter);
		findViewById(R.numPad.clear).setOnClickListener(numPadAdapter);
				
	}

	private ArrayList<String> toArrayList(Cursor c) {
		// TODO Auto-generated method stub
		category = new ArrayList<String>();
		c.moveToFirst();

		while(c.getPosition()<c.getCount()){

			category.add(c.getString(1));
			c.moveToNext();
		}
		
		return category;
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg0.equals(l_artikelen)){
			
			if(!c_Order.contains((int) arg3)){
				c_Order.addUnspecified((int) arg3);
				a_order.notifyDataSetChanged();	
				setCurrentArticle(arg3);
			}
			
			
		}
		if(arg0.equals(l_order)){
			setCurrentArticle(arg3);
			updateTotal();
		}
		
	}

	private void setCurrentArticle(long arg3) {
		// TODO Auto-generated method stub
		current_article = (int) arg3;
		
		if(arg3 == -1){
			order_amount.setText("");

		}else{
			current_amount = c_Order.getUnitById(current_article).getAmount();
			numPadAdapter.setIntValue(current_amount);
			order_amount.setText(current_amount+"");
		}		
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
		if(current_article != -1 && event.getSource() == numPadAdapter){
			
			Integer res = (Integer) event.getNewValue();
			current_amount = res.intValue();
			c_Order.getUnitById(current_article).setAmount(current_amount);
			a_order.notifyDataSetChanged();
			order_amount.setText(current_amount+"");
			updateTotal();
			
		}	
		
		if(event.getNewValue().equals("TRUE")){
			current_article = -1;
			current_amount = 0;
			order_amount.setText("");
			updateTotal();
		}
	}
	
	public void goBack(View view){
		
		finish();
	}
	
	public void saveOrder(View view){
		
		c_Order.writeToDB(this);
		
		Intent intent = new Intent(this,TurfSelectCustomerActivity.class );
		
		if (getParent() == null) {
		    setResult(Activity.RESULT_OK, intent);
		    } else {
		        getParent().setResult(Activity.RESULT_OK, intent);
		    }
		finish();
		
	}
	
	public void updateTotal(){
		
		totaal_output.setText(df.format(c_Order.calculateTotal())+"");
	}
	
	public void setUnfocus(View view){
		
		setCurrentArticle(-1);
	}
}