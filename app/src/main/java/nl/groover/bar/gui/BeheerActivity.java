package nl.groover.bar.gui;

import nl.groover.bar.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class BeheerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beheer);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_beheer, menu);
		
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
	
	public void toLeden(View view){
		
		Intent intent = new Intent(this, LedenMainActivity.class);
		startActivity(intent);
	}

	public void toGroepen(View view){

		Intent intent = new Intent(this, GroupActivity.class);
		startActivity(intent);
	}

	public void toArtikelen(View view){
		
		Intent intent = new Intent(this, ArticleActivity.class);
		startActivity(intent);
	}
	
	public void editCredentials(View view){
		
		Intent intent = new Intent(this, EditCredentialsActivity.class);
		startActivity(intent);

	}
	
	public void toPenning(View view){
		
		Intent intent = new Intent(this, PenningActivity.class);
		startActivity(intent);
	}
	
	public void toBackups(View view){
		
		Intent intent = new Intent(this, BackupActivity.class);
		startActivity(intent);
	}
}
