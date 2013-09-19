package com.groover.bar.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.groover.bar.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class EditCredentialsActivity extends Activity {

	private TextView oldU;
	private TextView oldP;
	private TextView newU;
	private TextView newP;
	private TextView newPRepeat;
	private Button save;

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

		String oldUsername = oldU.getText().toString();
		String oldPassword = oldP.getText().toString();
		String newUsername = newU.getText().toString();
		String newPassword = newP.getText().toString();
		String newPasswordRepeat = newPRepeat.getText().toString();

		String credentials[] = getCreds();

		if (!newUsername.equals(null) && !newPassword.equals(null)
				&& newPasswordRepeat.equals(newPassword)) {

			if (credentials[0].equals(oldUsername)
					&& credentials[1].equals(oldPassword)) {
				saveNewCredentials(newUsername, newPassword);
			}
		}
	}

	private boolean saveNewCredentials(String newUsername, String newPassword) {
		// TODO Auto-generated method stub
		File f = new File(this.getFilesDir(), "sec");

		if (!f.exists()) {
			f.mkdirs();
		}

		f = new File(this.getFilesDir(), "sec/main");
		FileOutputStream outputStream;

		try {
			outputStream = new FileOutputStream(f);
			String out = newUsername + "\n" + newPassword;
			byte[] buffer = out.getBytes();
			outputStream.write(buffer);
			outputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public String[] getCreds() {

		String credentials[] = new String[2];
		File f = new File(this.getFilesDir(), "sec/main");

		if (f.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				InputStreamReader in = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(in);
				credentials[0] = br.readLine();
				credentials[1] = br.readLine();
				fis.close();
				return credentials;

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return credentials;
		} else {
			return credentials;
		}
	}
}
