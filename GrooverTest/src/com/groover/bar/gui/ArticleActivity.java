package com.groover.bar.gui;

import com.groover.bar.R;
import com.groover.bar.frame.Article;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.ArticleAdapter;
import com.groover.bar.frame.InfoDialog;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class ArticleActivity extends Activity implements
		ArticleAdapter.UpdateListener {

	private DBHelper DB;
	private ListView artikellijst;

	private EditText et_naam;
	private EditText et_prijs;
	private View addPane;
	private Button voegToe;
	private ArticleAdapter adapter;
	private Cursor c_articles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		DB = DBHelper.getDBHelper(this);

		artikellijst = (ListView) findViewById(R.articles.listview);

		et_naam = (EditText) findViewById(R.articles.name);
		et_prijs = (EditText) findViewById(R.articles.price);
		voegToe = (Button) findViewById(R.articles.add_button);

		addPane = findViewById(R.articles.addPane);

		c_articles = DB.getArticles();

		adapter = new ArticleAdapter(this, DB.getArticles(), this);
		artikellijst.setAdapter(adapter);
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

	public void vt_article(View v) {

		boolean checks = true;
		Button b = (Button) v;
		if (b.getText().equals("Voeg artikel toe")) {
			addPane.setVisibility(View.VISIBLE);
			b.setText("Artikel opslaan");
			return;
		}

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

		if (checks) {

			Log.d("add", c_articles.isClosed() + "");

			String naam = et_naam.getText().toString();
			double prijs = Double.parseDouble(et_prijs.getText().toString());

			ContentValues values = new ContentValues();
			values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
			values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
			values.put(DBHelper.ItemList.COLUMN_NAME_CAT, "all");
			values.put(DBHelper.ItemList.COLUMN_ORDER,
					c_articles.getCount() + 1);

			DB.insertOrIgnore(DBHelper.ItemList.TABLE_NAME, values);

			c_articles = DB.getArticles();
			adapter.swapCursor(c_articles);

			setToDefault();
		}
	}

	public void setToDefault() {

		addPane.setVisibility(View.GONE);
		et_naam.setText("");
		et_prijs.setText("");
		voegToe.setText("Voeg artikel toe");

		et_naam.setError(null);
		et_prijs.setError(null);
	}

	@Override
	public void delete(Article a) {

		if (!a.getEditable()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder.setTitle("Warning");

			// set dialog message
			alertDialogBuilder
					.setMessage("Kan artikel niet verwijderen want er zijn al bestellingen geplaatst. Eerst afrekenen!");

			// set dialog message
			alertDialogBuilder.setPositiveButton("Ok", null);
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

		} else {
			DB.deleteOrIgnore(DBHelper.ItemList.TABLE_NAME, a.getId());
			adapter.swapCursor(DB.getArticles());
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void swap(int id1, int id2, int pos1, int pos2) {
		ContentValues v = new ContentValues();
		v.put(DBHelper.ItemList.COLUMN_ORDER, pos2);
		DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, id1, v);
		v.clear();
		v.put(DBHelper.ItemList.COLUMN_ORDER, pos1);
		DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, id2, v);
		adapter.swapCursor(DB.getArticles());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void edit(int pos, Article a) {

		ContentValues v = new ContentValues();
		v.put(DBHelper.ItemList.COLUMN_NAME_ITEM, a.getName());
		v.put(DBHelper.ItemList.COLUMN_NAME_PRICE, a.getPrice());
		DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, a.getId(), v);
		adapter.swapCursor(DB.getArticles());
		adapter.notifyDataSetChanged();
		setEditable(-1, null);
	}

	@Override
	public void setEditable(int pos, Article a) {
		
		if (pos == -1) {
			for (int i = 0; i < adapter.getCount(); i++) {
				artikellijst.getChildAt(i).findViewById(R.articleRow.delete)
						.setEnabled(true);
				artikellijst.getChildAt(i).findViewById(R.articleRow.edit)
						.setEnabled(true);
				artikellijst.getChildAt(i).findViewById(R.articleRow.up)
						.setEnabled(true);
				artikellijst.getChildAt(i).findViewById(R.articleRow.down)
						.setEnabled(true);
			}
			return;
		}

		if (!a.getEditable()) {
			Log.d("article", "not editable");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder.setTitle("Warning");
			// set dialog message
			alertDialogBuilder
					.setMessage("Kan artikel niet aanpassen want er zijn al bestellingen geplaatst. Eerst afrekenen!");
			// set dialog message
			alertDialogBuilder.setPositiveButton("Ok", null);
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			
			return;

		} else {

			for (int i = 0; i < adapter.getCount(); i++) {
				if (i != pos) {
					artikellijst.getChildAt(i)
							.findViewById(R.articleRow.delete)
							.setEnabled(false);
					artikellijst.getChildAt(i).findViewById(R.articleRow.edit)
							.setEnabled(false);
					artikellijst.getChildAt(i).findViewById(R.articleRow.up)
							.setEnabled(false);
					artikellijst.getChildAt(i).findViewById(R.articleRow.down)
							.setEnabled(false);
				} else {
					artikellijst.getChildAt(i).findViewById(R.articleRow.up)
							.setEnabled(false);
					artikellijst.getChildAt(i).findViewById(R.articleRow.down)
							.setEnabled(false);
				}
			}
		}
	}
}
