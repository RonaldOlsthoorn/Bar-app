package nl.groover.bar.gui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import nl.groover.bar.R;
import nl.groover.bar.frame.Article;
import nl.groover.bar.frame.DBHelper;

public class EditArticleActivity extends Activity {

    private final int REQUEST_CODE = 123;

    private DBHelper DB;

    private EditText et_naam;
    private EditText et_prijs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        DB = DBHelper.getDBHelper(this);

        et_naam = (EditText) findViewById(R.id.article_row_name);
        et_prijs = (EditText) findViewById(R.id.article_row_price);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            Intent intent = getIntent();
            ContentValues v = new ContentValues();
            v.put(DBHelper.ItemList.COLUMN_NAME_COLOR, data.getIntExtra(ColorPickerActivity.COLOR, 0));
            DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, data.getIntExtra(ArticleActivity.ARTICLE_ID,-1), v);
            finish();
            startActivity(intent);

        }
    }

    public void pickColor(Article a) {
        Intent intent = new Intent(this, ColorPickerActivity.class);
        intent.putExtra(ArticleActivity.ARTICLE_ID, a.getId());
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void edit(int pos, Article a) {

        ContentValues v = new ContentValues();
        v.put(DBHelper.ItemList.COLUMN_NAME_ITEM, a.getName());
        v.put(DBHelper.ItemList.COLUMN_NAME_PRICE, a.getPrice());
        DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, a.getId(), v);

    }

    public void vt_article(View v) {

        boolean checks = true;


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

            String naam = et_naam.getText().toString();
            double prijs = Double.parseDouble(et_prijs.getText().toString());

            ContentValues values = new ContentValues();
            values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, naam);
            values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, prijs);
            values.put(DBHelper.ItemList.COLUMN_NAME_CAT, "all");
            values.put(DBHelper.ItemList.COLUMN_NAME_ORDER, 1); //TODO: adapt!!


            DB.insertOrIgnore(DBHelper.ItemList.TABLE_NAME, values);

        }
    }
}
