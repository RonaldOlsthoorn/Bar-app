package com.groover.bar.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.groover.bar.R;
import com.groover.bar.frame.DBHelper;
import com.groover.bar.frame.MD5Hash;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;

public class EditCredentialsActivity extends Activity {

	private TextView oldU;
	private TextView oldP;
	private TextView newU;
	private TextView newP;
	private TextView newPRepeat;
	private Button save;
	private DBHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_credentials);
		// Show the Up button in the action bar.
		setupActionBar();

		oldU = (TextView) findViewById(R.editCredentials.oldU);
		oldP = (TextView) findViewById(R.editCredentials.oldP);
		newU = (TextView) findViewById(R.editCredentials.newU);
		newP = (TextView) findViewById(R.editCredentials.newP);
		newPRepeat = (TextView) findViewById(R.editCredentials.newPRepeat);
		save = (Button) findViewById(R.editCredentials.save);
		
		db = DBHelper.getDBHelper(this);

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
		getMenuInflater().inflate(R.menu.edit_credentials, menu);
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

	public void saveOrDont(View view) {
		
		

		Cursor c = db.getCredentials();
		c.moveToFirst();
		
		String oldUsername = oldU.getText().toString();
		String oldPassword = oldP.getText().toString();
		String newUsername = newU.getText().toString();
		String newPassword = newP.getText().toString();
		String newPasswordRepeat = newPRepeat.getText().toString();

		boolean checks = true;
		
		if(!oldUsername.equals(c.getString(1))){
			
			oldU.setError("Wrong username");
			checks = false;
			
		}
		c.moveToNext();
		
		if(!c.getString(1).equals(MD5Hash.md5(oldPassword))){
			oldP.setError("Wrong password");
			checks = false;
		}
		
		if(!newPassword.equals(newPasswordRepeat)){
			
			newPRepeat.setError("no match");
			checks = false;
		}
		
		if(checks){
			
			boolean res = db.updateCredentials(newUsername, newPassword);				
			Log.d("editCreds",res+"");
			oldP.setText("");
			oldP.setError(null);
			newP.setText("");
			newP.setError(null);
			oldU.setText("");
			oldU.setError(null);
			newU.setText("");
			newU.setError(null);
			newPRepeat.setText("");
			newPRepeat.setError(null);
			Toast t =Toast.makeText(this, "Wijzigingen opgeslagen", Toast.LENGTH_LONG);
			t.show();
		}		
	}
}
