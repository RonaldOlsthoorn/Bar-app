package com.groover.bar.gui;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.DBHelper.ItemList;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class ArticleActivity extends Activity implements OnItemClickListener,
		OnItemSelectedListener {

	private DBHelper DB;
	private ListView artikellijst;

	private EditText et_naam;
	private EditText et_prijs;
	private EditText et_cat;
	private Spinner sp_cat;
	private String cat;
	private View editPane;
	private Button voegToe;
	
	private int current;

	private SimpleCursorAdapter adapter;
	private Cursor c_articles;
	private Cursor c_categories;
	private Cursor extendedCursor;
	private MatrixCursor extras;

	private String[] FROM = new String[] { DBHelper.ItemList.COLUMN_NAME_ITEM,
			DBHelper.ItemList.COLUMN_NAME_PRICE,
			DBHelper.ItemList.COLUMN_NAME_CAT };

	private int[] TO = new int[] { R.articlerow.naam, R.articlerow.price,
			R.articlerow.category };

	private SimpleCursorAdapter cat_adapter;

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
		et_cat = (EditText) findViewById(R.artikelen.vt_new_cat);
		sp_cat = (Spinner) findViewById(R.artikelen.vt_categorie);
		voegToe = (Button) findViewById(R.artikelen.voegtoe_button);
		sp_cat.setOnItemSelectedListener(this);
		
		editPane = findViewById(R.artikelen.editPane);

		c_articles = DB.getArticles();

		adapter = new SimpleCursorAdapter(this, R.layout.article_row,
				c_articles, FROM, TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		artikellijst.setOnItemClickListener(this);
		artikellijst.setAdapter(adapter);

		DB.close();

		c_categories = DB.getCategories();
		extras = new MatrixCursor(new String[] { ItemList.COLUMN_ID,
				ItemList.COLUMN_NAME_CAT });
		extras.addRow(new String[] { "-1", "Nieuwe Categorie" });
		Cursor[] cursors = { c_categories, extras };
		extendedCursor = new MergeCursor(cursors);

		cat_adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, extendedCursor,
				new String[] { ItemList.COLUMN_NAME_CAT },
				new int[] { android.R.id.text1 },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		cat_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp_cat.setAdapter(cat_adapter);

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
		cat = c_articles.getString(3);

		c_categories.moveToFirst();

		while (!(c_categories.isLast() || c_categories.getString(1).equals(cat))) {
			c_categories.moveToNext();
		}

		sp_cat.setClickable(true);
		sp_cat.setAdapter(cat_adapter);
		sp_cat.setSelection(c_categories.getPosition(), true);
		
		voegToe.setVisibility(View.GONE);
		editPane.setVisibility(View.VISIBLE);
		
		c_categories.moveToFirst();

	}

	public void vt_article(View v) {

		String naam = et_naam.getText().toString();
		double prijs = Double.parseDouble(et_prijs.getText().toString());

		if (cat.equals("Nieuwe Categorie")) {
			cat = et_cat.getText().toString();
		}

		ContentValues values = new ContentValues();
		values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
		values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
		values.put(DBHelper.ItemList.COLUMN_NAME_CAT, cat);

		long b = DB.insertOrIgnore(DBHelper.ItemList.TABLE_NAME, values);

		c_articles.close();
		c_articles = DB.getArticles();
		adapter.swapCursor(c_articles);

		c_categories.close();
		c_categories = DB.getCategories();
		Cursor[] cursors = { c_categories, extras };
		extendedCursor = new MergeCursor(cursors);

		cat_adapter.swapCursor(extendedCursor);

		setToDefault();

	}

	public void w_article(View v) {

		String naam = et_naam.getText().toString();
		double prijs = Double.parseDouble(et_prijs.getText().toString());

		if (cat.equals("Nieuwe Categorie")) {
			cat = et_cat.getText().toString();
		}

		ContentValues values = new ContentValues();
		values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
		values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
		values.put(DBHelper.ItemList.COLUMN_NAME_CAT, cat);

		boolean b = DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, current,
				values);

		c_articles.close();
		c_articles = DB.getArticles();
		adapter.swapCursor(c_articles);

		c_categories.close();
		c_categories = DB.getCategories();
		Cursor[] cursors = { c_categories, extras };
		extendedCursor = new MergeCursor(cursors);

		cat_adapter.swapCursor(extendedCursor);
	}

	public void v_article(View v) {

		boolean b = DB.deleteOrIgnore(DBHelper.ItemList.TABLE_NAME, current);

		c_articles.close();
		c_articles = DB.getArticles();
		adapter.swapCursor(c_articles);

		c_categories.close();
		c_categories = DB.getCategories();
		Cursor[] cursors = { c_categories, extras };
		extendedCursor = new MergeCursor(cursors);

		cat_adapter.swapCursor(extendedCursor);

		setToDefault();

	}
	
	public void annuleren(View v){
		
		setToDefault();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		extendedCursor.moveToPosition(arg2);
		cat = extendedCursor.getString(1);
		extendedCursor.moveToFirst();

		if (cat.equals("Nieuwe Categorie")) {
			et_cat.setVisibility(View.VISIBLE);
		} else {
			et_cat.setText("");
			et_cat.setVisibility(View.GONE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
	
	public void setToDefault(){
		
		editPane.setVisibility(View.GONE);
		et_naam.setText("");
		et_prijs.setText("");
		current = -1;
		voegToe.setVisibility(View.VISIBLE);
		
	}
}
