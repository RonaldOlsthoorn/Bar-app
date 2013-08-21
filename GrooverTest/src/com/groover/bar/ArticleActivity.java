package com.groover.bar;

import com.groover.bar.R;
import com.groover.bar.DBHelper.ItemList;

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

public class ArticleActivity extends Activity implements OnItemClickListener, OnItemSelectedListener{
	
	private DBHelper DB;
	private ListView artikellijst;


	private Button wijzig;
	private Button verwijder;
	
	private EditText vtNaam;
	private EditText vtPrijs;
	private EditText vt_new_cat;
	private Spinner vtCat;
	private String vt_cat;
	
	private EditText wNaam;
	private EditText wPrijs;
	private EditText w_new_cat;
	private Spinner wCat;
	private String w_cat;
	
	private int current;
	
	private SimpleCursorAdapter adapter;
	private Cursor c_articles;
	private Cursor c_categories;
	private Cursor extendedCursor;
	private MatrixCursor extras;
	
	private String[] FROM = new String[]{DBHelper.ItemList.COLUMN_NAME_ITEM,
			DBHelper.ItemList.COLUMN_NAME_PRICE,
			DBHelper.ItemList.COLUMN_NAME_CAT};
	
	private int[] TO = new int[]{R.articlerow.naam, R.articlerow.price,R.articlerow.category};
	
	private SimpleCursorAdapter cat_adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		DB = DBHelper.getDBHelper(this);

		artikellijst = (ListView) findViewById(R.artikelen.listview);
			
		wijzig = (Button) findViewById(R.artikelen.wijzig_button);
		verwijder = (Button) findViewById(R.artikelen.delete_button);
		
		vtNaam = (EditText) findViewById(R.artikelen.vt_naam);
		vtPrijs = (EditText) findViewById(R.artikelen.vt_prijs);
		vt_new_cat = (EditText) findViewById(R.artikelen.vt_new_cat);
		vtCat = (Spinner) findViewById(R.artikelen.vt_categorie);
		vtCat.setOnItemSelectedListener(this);
		
		wNaam = (EditText) findViewById(R.artikelen.w_naam);
		wPrijs = (EditText) findViewById(R.artikelen.w_prijs);
		w_new_cat = (EditText) findViewById(R.artikelen.w_new_cat);
		wCat = (Spinner) findViewById(R.artikelen.w_categorie);
		wCat.setOnItemSelectedListener(this);
		
		c_articles=DB.getArticles();
		
		adapter = new SimpleCursorAdapter(this,
				R.layout.article_row, c_articles, FROM,
				TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		artikellijst.setOnItemClickListener(this);
		artikellijst.setAdapter(adapter);
		
		
		DB.close();
		
		c_categories = DB.getCategories();
		extras = new MatrixCursor(new String[] { ItemList.COLUMN_ID, ItemList.COLUMN_NAME_CAT });
		extras.addRow(new String[] { "-1", "Nieuwe Categorie" });
		Cursor[] cursors = { c_categories , extras };
		extendedCursor = new MergeCursor(cursors);
		
	    cat_adapter = new SimpleCursorAdapter(this, 
                android.R.layout.simple_spinner_item, 
                extendedCursor, new String[] { ItemList.COLUMN_NAME_CAT },  
                new int[] {android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

	    cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    
	    vtCat.setAdapter(cat_adapter);
	    
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
		wNaam.setText(c_articles.getString(1));
		wPrijs.setText(""+c_articles.getDouble(2));
		w_cat = c_articles.getString(3);
		c_categories.moveToFirst();
			
		while(!(c_categories.isLast() || c_categories.getString(1).equals(w_cat))){
			c_categories.moveToNext();
		}
		
		wCat.setClickable(true);
		wCat.setAdapter(cat_adapter);
		wCat.setSelection(c_categories.getPosition(), true);
		c_categories.moveToFirst();
		
		wijzig.setEnabled(true);
		verwijder.setEnabled(true);
		
		
	}
	
	public void vt_article(View v){
		
		String naam = vtNaam.getText().toString();
		double prijs = Double.parseDouble(vtPrijs.getText().toString());
		
		if(vt_cat.equals("Nieuwe Categorie")){
			vt_cat = vt_new_cat.getText().toString();
		}
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
		values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
		values.put(DBHelper.ItemList.COLUMN_NAME_CAT, vt_cat);
		
		long b = DB.insertOrIgnore(DBHelper.ItemList.TABLE_NAME, values);
		
		c_articles.close();
		c_articles=DB.getArticles();
		adapter.swapCursor(c_articles);
		
		c_categories.close();
		c_categories = DB.getCategories();
		Cursor[] cursors = { c_categories , extras };
		extendedCursor = new MergeCursor(cursors);
		
		cat_adapter.swapCursor(extendedCursor);
		
		vtNaam.setText("");
		vtPrijs.setText("");

	}
	
	public void w_article(View v){
		
		String naam = wNaam.getText().toString();
		double prijs = Double.parseDouble(wPrijs.getText().toString());
		
		if(w_cat.equals("Nieuwe Categorie")){
			w_cat = w_new_cat.getText().toString();
		}
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
		values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
		values.put(DBHelper.ItemList.COLUMN_NAME_CAT, w_cat);
		
		boolean b = DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME,current, values);
		
		c_articles.close();
		c_articles=DB.getArticles();
		adapter.swapCursor(c_articles);
		
		c_categories.close();
		c_categories = DB.getCategories();
		Cursor[] cursors = { c_categories , extras };
		extendedCursor = new MergeCursor(cursors);
		
		cat_adapter.swapCursor(extendedCursor);		
	}

	public void v_article(View v){
		
		boolean b = DB.deleteOrIgnore(DBHelper.ItemList.TABLE_NAME,current);
		
		c_articles.close();
		c_articles=DB.getArticles();
		adapter.swapCursor(c_articles);
		
		c_categories.close();
		c_categories = DB.getCategories();
		Cursor[] cursors = { c_categories , extras };
		extendedCursor = new MergeCursor(cursors);
		
		cat_adapter.swapCursor(extendedCursor);
		
		wNaam.setText("");
		wPrijs.setText("");
		wCat.setClickable(false);
		
		wijzig.setEnabled(false);
		verwijder.setEnabled(false);	
	}

	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if(arg0.equals(vtCat)){
			
			extendedCursor.moveToPosition(arg2);
			vt_cat = extendedCursor.getString(1);
			extendedCursor.moveToFirst();
			
			if(vt_cat.equals("Nieuwe Categorie")){
				vt_new_cat.setVisibility(View.VISIBLE);
			}else{
				vt_new_cat.setText("");
				vt_new_cat.setVisibility(View.GONE);
			}	
		}
		if(arg0.equals(wCat)){
			
			extendedCursor.moveToPosition(arg2);
			w_cat = extendedCursor.getString(1);
			extendedCursor.moveToFirst();
			
			if(w_cat.equals("Nieuwe Categorie")){
				w_new_cat.setVisibility(View.VISIBLE);
			}else{
				w_new_cat.setText("");
				w_new_cat.setVisibility(View.GONE);
			}				
		}	
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
