package com.groover.bar.gui;

import java.text.DecimalFormat;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class ArticleActivity extends Activity implements OnItemClickListener {

	private DBHelper DB;
	private ListView artikellijst;

	private EditText et_naam;
	private EditText et_prijs;
	private View editPane;
	private Button voegToe;

	private int current;

	private SimpleCursorAdapter adapter;
	private Cursor c_articles;

	private String[] FROM = new String[] { DBHelper.ItemList.COLUMN_NAME_ITEM,
			DBHelper.ItemList.COLUMN_NAME_PRICE,
			DBHelper.ItemList.COLUMN_NAME_CAT };

	private int[] TO = new int[] { R.articlerow.naam, R.articlerow.price,
			R.articlerow.category };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		DB = DBHelper.getDBHelper(this);

		artikellijst = (ListView) findViewById(R.artikelen.listview);

		et_naam = (EditText) findViewById(R.artikelen.vt_naam);
		et_prijs = (EditText) findViewById(R.artikelen.vt_prijs);
		voegToe = (Button) findViewById(R.artikelen.voegtoe_button);

		editPane = findViewById(R.artikelen.editPane);

		c_articles = DB.getArticles();

		adapter = new SimpleCursorAdapter(this, R.layout.article_row,
				c_articles, FROM, TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		artikellijst.setOnItemClickListener(this);
		artikellijst.setAdapter(adapter);

		DB.close();
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_article, menu);
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

		c_articles.moveToPosition(arg2);
		current = c_articles.getInt(0);
		et_naam.setText(c_articles.getString(1));
		et_prijs.setText("" + c_articles.getDouble(2));

		voegToe.setVisibility(View.GONE);
		editPane.setVisibility(View.VISIBLE);

	}

	public void vt_article(View v) {

		boolean checks = true;
		// CHECKS

		if (et_prijs.getText().toString().trim().equals("")) {

			et_prijs.setError(getString(R.string.err_field_empty));
			et_prijs.requestFocus();
			checks = false;
		} else {
			try {
				double prijs = Double
						.parseDouble(et_prijs.getText().toString());
			} catch (NumberFormatException e) {
				et_prijs.setError("Field must be a number!");
				et_prijs.requestFocus();
				checks = false;
			}
		}
		
		if (et_naam.getText().toString().trim().equals("")) {

			et_naam.setError(getString(R.string.err_field_empty));
			et_naam.requestFocus();
			checks = false;
		}

		if(checks){
			
			String naam = et_naam.getText().toString();
			double prijs = Double.parseDouble(et_prijs.getText().toString());

			ContentValues values = new ContentValues();
			values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
			values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
			values.put(DBHelper.ItemList.COLUMN_NAME_CAT, "all");

			DB.insertOrIgnore(DBHelper.ItemList.TABLE_NAME, values);

			c_articles.close();
			c_articles = DB.getArticles();
			adapter.swapCursor(c_articles);

			setToDefault();
		}
	}

	public void w_article(View v) {
		
		boolean checks = true;
		// CHECKS

		if (et_prijs.getText().toString() == null) {

			et_prijs.setError(getString(R.string.err_field_empty));
			et_prijs.requestFocus();
			checks = false;
		} else {
			try {
				double prijs = Double
						.parseDouble(et_prijs.getText().toString());
			} catch (NumberFormatException e) {
				et_prijs.setError("Field must be a number!");
				et_prijs.requestFocus();
				checks = false;
			}
		}
		
		if (et_naam.getText().toString().trim().equals("")) {

			et_naam.setError(getString(R.string.err_field_empty));
			
			et_naam.requestFocus();
			checks = false;
		}

		if(checks){
			
			String naam = et_naam.getText().toString();
			double prijs = Double.parseDouble(et_prijs.getText().toString());

			ContentValues values = new ContentValues();
			values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
			values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);

			DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, current, values);

			et_naam.setError(null);
			et_prijs.setError(null);
			
			c_articles.close();
			c_articles = DB.getArticles();
			adapter.swapCursor(c_articles);
			
			
		}

	}

	public void v_article(View v) {

		DB.deleteOrIgnore(DBHelper.ItemList.TABLE_NAME, current);

		c_articles.close();
		c_articles = DB.getArticles();
		adapter.swapCursor(c_articles);

		setToDefault();

	}

	public void annuleren(View v) {

		setToDefault();
	}

	public void setToDefault() {

		editPane.setVisibility(View.GONE);
		et_naam.setText("");
		et_prijs.setText("");
		current = -1;
		voegToe.setVisibility(View.VISIBLE);
		
		et_naam.setError(null);
		et_prijs.setError(null);

	}
}
