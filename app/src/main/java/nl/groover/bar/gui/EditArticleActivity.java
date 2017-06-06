package nl.groover.bar.gui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import nl.groover.bar.R;
import nl.groover.bar.frame.Article;
import nl.groover.bar.frame.DBHelper;

public class EditArticleActivity extends Activity {

    private final int REQUEST_CODE = 123;

    private DBHelper DB;

    private EditText etName;
    private EditText etPrice;
    private Button btColor;

    private Article article;

    private int nArticles;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        DB = DBHelper.getDBHelper(this);

        Intent intent = getIntent();

        etName = (EditText) findViewById(R.id.edit_article_name);
        etPrice = (EditText) findViewById(R.id.edit_article_price);
        btColor = (Button) findViewById(R.id.edit_article_color);

        int id = intent.getIntExtra(ArticleActivity.ARTICLE_ID, -1);
        nArticles = intent.getIntExtra(ArticleActivity.N_ARTICLES, -1);

        if (id != -1){

            article = DB.getArticle(id);

            etName.setText(article.getName());
            etPrice.setText(df.format(article.getPrice()));
            btColor.setBackgroundColor(article.getColor());
        }else{
            article = new Article(-1, 0, null, true, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            Intent intent = getIntent();
            article.setColor(data.getIntExtra(ColorPickerActivity.COLOR, 0));
            btColor.setBackgroundColor(article.getColor());
        }
    }

    public void pickColor(View v) {
        Intent intent = new Intent(this, ColorPickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void save(View v) {

        boolean checks = true;
        double prijs = 0;

        if (etPrice.getText().toString().trim().equals("")) {

            etPrice.setError(getString(R.string.err_field_empty));
            etPrice.requestFocus();
            checks = false;
        } else {
            try {
                 prijs = df.parse(etPrice.getText().toString()).doubleValue();
                etPrice.setError(null);
            } catch (ParseException e) {
                etPrice.setError("Field must be a comma seperated decimal!");
                etPrice.requestFocus();
                checks = false;
            }
        }

        if (etName.getText().toString().trim().equals("")) {

            etName.setError(getString(R.string.err_field_empty));
            etName.requestFocus();
            checks = false;
        }else{
            etName.setError(null);
        }

        if (checks) {

            article.setName(etName.getText().toString());
            article.setPrice(prijs);

            ContentValues values = new ContentValues();
            values.put(DBHelper.ItemList.COLUMN_NAME_ITEM, article.getName());
            values.put(DBHelper.ItemList.COLUMN_NAME_PRICE, article.getPrice());
            values.put(DBHelper.ItemList.COLUMN_NAME_CAT, "all");
            values.put(DBHelper.ItemList.COLUMN_NAME_COLOR, article.getColor());

            if(article.getId()==-1){
                values.put(DBHelper.ItemList.COLUMN_NAME_ORDER, nArticles);
                DB.insertOrIgnore(DBHelper.ItemList.TABLE_NAME, values);
                Toast.makeText(this, "Artikel "+article.getName()+ " toegevoegd.", Toast.LENGTH_SHORT);
            }else{
                DB.updateOrIgnore(DBHelper.ItemList.TABLE_NAME, article.getId(), values);
                Toast.makeText(this, "Wijzigingen opgeslagen.", Toast.LENGTH_SHORT);
            }

            Intent intent = new Intent(this, ArticleActivity.class);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
