package com.groover.bar.gui;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.OrderListAdapter;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class OrderOverviewActivity extends Activity implements
		OrderListAdapter.ListActionListener {

	private final int REQUEST_CODE = 123;
	private DBHelper DB;
	private ListView list;
	private OrderListAdapter adapter;
	private int orderId;
	private Cursor c;
	private int custId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_overview);

		custId = getIntent().getIntExtra("custId", -1);

		DB = DBHelper.getDBHelper(this);
		
		if(custId==-1){
			c = DB.getAllOrders();
		}else{
			c = DB.getOrdersCust(custId);
			Log.d("id",custId+"");
		}
				
		adapter = new OrderListAdapter(this, R.layout.order_overview_row, c,
				this);
		list = (ListView) findViewById(R.orderOverview.list);
		list.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_overview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
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

	@Override
	public void edit(int id, int pos) {
		c.moveToPosition(pos);

		Intent intent = new Intent(this, EditOrderActivity.class);
		intent.putExtra("order", c.getInt(0));
		intent.putExtra("type", "individual");
		intent.putExtra("ID", c.getString(1));
		intent.putExtra("account", c.getInt(4));
		intent.putExtra("name", c.getString(2) +" " +c.getString(3));

		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	public void delete(int i) {

		orderId = i;

		DialogInterface.OnClickListener pos = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// Delete the sucker!!!
				DB.deleteOrder(orderId);
				Toast toast = Toast.makeText(OrderOverviewActivity.this,
						"Bestelling verwijderd", Toast.LENGTH_SHORT);
				toast.show();
				c = DB.getAllOrders();
				adapter.changeCursor(c);
				adapter.notifyDataSetChanged();
			}
		};

		DialogInterface.OnClickListener neg = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		};

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Bestelling verwijderen?");
		alertDialogBuilder.setPositiveButton(R.string.dialog_continue, pos);
		alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, neg);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//
//		Log.d("itemclick", "click");
//
//		if ((view.equals(findViewById(R.orderOverViewRow.edit)))) {
//			Log.d("itemclick", "found item");
//		}
//	}
}
