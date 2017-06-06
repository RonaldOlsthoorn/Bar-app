package nl.groover.bar.gui;

import nl.groover.bar.R;
import nl.groover.bar.frame.Article;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.ArticleAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

import java.util.ArrayList;

public class ArticleActivity extends Activity {

	public static final String ARTICLE_ID = "article_id";
	public static final String N_ARTICLES = "article_number";

	private static final String TAG = ArticleActivity.class.getSimpleName();
	private DBHelper DB;
	private ListView artikellijst;

	private Button voegToe;
	private ArticleAdapter adapter;
	private ArrayList<Article> listArticles;

	private static final int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		DB = DBHelper.getDBHelper(this);

		artikellijst = (ListView) findViewById(R.id.articles_activity_list);

		setupAdapter();

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


	public void setupAdapter(){

		listArticles = ConvertCursorToArray(DB.getArticlesWithTotalConsumptions());

		MoveUpListener moveUpListener = new MoveUpListener() {
			@Override
			public void moveUp(int pos) {

				ContentValues v = new ContentValues();
				v.put(DBHelper.ItemList.COLUMN_NAME_ORDER, pos-1);
				DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, listArticles.get(pos).getId(), v);

				v.clear();
				v.put(DBHelper.ItemList.COLUMN_NAME_ORDER, pos);
				DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, listArticles.get(pos-1).getId(), v);

				updateAdapter();
			}
		};

		MoveDownListener moveDownListener = new MoveDownListener() {
			@Override
			public void moveDown(int pos) {

				ContentValues v = new ContentValues();
				v.put(DBHelper.ItemList.COLUMN_NAME_ORDER, pos+1);
				DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, listArticles.get(pos).getId(), v);

				v.clear();
				v.put(DBHelper.ItemList.COLUMN_NAME_ORDER, pos);
				DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, listArticles.get(pos+1).getId(), v);

				updateAdapter();
			}
		};

		EditArticleListener editArticleListener = new EditArticleListener() {
			@Override
			public void editArticle(int pos) {

				Intent intent = new Intent(ArticleActivity.this, EditArticleActivity.class);
				intent.putExtra(ArticleActivity.ARTICLE_ID, listArticles.get(pos).getId());
				intent.putExtra(ArticleActivity.N_ARTICLES, listArticles.size());
				startActivityForResult(intent, ArticleActivity.REQUEST_CODE);
			}
		};

		RemoveArticleListener removeArticleListener = new RemoveArticleListener() {
			@Override
			public void removeArticle(int pos) {

				if (!listArticles.get(pos).getEditable()) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							ArticleActivity.this);

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

					DB.deleteOrIgnore(DBHelper.ItemList.TABLE_NAME, listArticles.get(pos).getId());
					updateAdapter();
				}
			}
		};

		adapter = new ArticleAdapter(this, listArticles,
				moveUpListener, moveDownListener, editArticleListener, removeArticleListener);
	}

	public interface MoveUpListener{

		void moveUp(int pos);
	}

	public interface MoveDownListener{

		void moveDown(int pos);
	}

	public interface EditArticleListener{

		void editArticle(int pos);
	}

	public interface RemoveArticleListener{

		void removeArticle(int pos);
	}

	public void updateAdapter(){

		listArticles = ConvertCursorToArray(DB.getArticlesWithTotalConsumptions());

		adapter.swapArrayList(listArticles);
		adapter.notifyDataSetChanged();
	}

	public ArrayList<Article> ConvertCursorToArray(Cursor c){

		ArrayList<Article> res = new ArrayList<Article>(c.getCount());

		for(c.moveToFirst(); c.getPosition()<c.getCount(); c.moveToNext()){

			res.add(new Article(c.getInt(0),
					c.getDouble(2), c.getString(1),
					c.getInt(3) < 1, c.getInt(4)));
		}

		return res;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

			updateAdapter();
		}
	}

	public void newArticle(View v){

		Intent intent = new Intent(ArticleActivity.this, EditArticleActivity.class);
		intent.putExtra(ArticleActivity.ARTICLE_ID, -1);
		intent.putExtra(ArticleActivity.N_ARTICLES, listArticles.size());
		startActivityForResult(intent, ArticleActivity.REQUEST_CODE);
	}
}
