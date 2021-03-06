package nl.groover.bar.gui;

import java.io.File;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.FileDialog;

import nl.groover.bar.frame.MemberImporter;
import nl.groover.bar.frame.OrderExporter;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;

import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class LedenMainActivity extends FragmentActivity implements
		OnItemClickListener, ContinueDialogFragment.NoticeDialogListener {

	private DBHelper DB;
	private ListView ledenlijst;

	private Button voegtoe;

	private EditText vtVoornaam;
	private EditText vtTussenvoegsel;
	private EditText vtAchternaam;
	private EditText vtId;

	private int current;

	private NameCursorAdapter adapter;
	private Cursor c;


	private View editPane;
	private int REQUEST_FILE = 1;
	private String targetPath;
	private boolean mShowDialog;
	private String PROG_DIALOG_TAG = "continueornot";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leden_main);
		// Show the Up button in the action bar.
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		DB = DBHelper.getDBHelper(this);

		ledenlijst = (ListView) findViewById(R.id.leden_listview);
		editPane = findViewById(R.id.leden_editPane);

		voegtoe = (Button) findViewById(R.id.leden_voegtoe_button);

		vtVoornaam = (EditText) findViewById(R.id.leden_voegtoe_voornaam);
		vtTussenvoegsel = (EditText) findViewById(R.id.leden_voegtoe_tussenvoegsel);
		vtAchternaam = (EditText) findViewById(R.id.leden_voegtoe_achternaam);
		vtId = (EditText) findViewById(R.id.leden_id);

		c = DB.getMembers();

		adapter = new NameCursorAdapter(this, c, NameCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		ledenlijst.setOnItemClickListener(this);
		ledenlijst.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_leden_main, menu);
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

	public void voegToeLid(View view) {

		boolean checks = true;
		// CHECKS

		if (vtId.getText().toString() == null) {

			vtId.setError(getString(R.string.err_field_empty));
			vtId.requestFocus();
			checks = false;
		} else {

			try {
				int id = Integer.parseInt(vtId.getText().toString());
				if (id < 1) {

					vtId.setError("Field must be a non-negative number!");
					vtId.requestFocus();
					checks = false;
				} else {

					if (DB.checkIdInTable(DBHelper.MemberTable.TABLE_NAME, id)) {

						vtId.setError("ID already in use!");
						vtId.requestFocus();
						checks = false;
					}
				}

			} catch (NumberFormatException e) {
				vtId.setError("Field must be a non-negative number!");
				vtId.requestFocus();
				checks = false;
			}
		}

		if (vtVoornaam.getText().toString().trim().equals("")) {

			vtVoornaam.setError(getString(R.string.err_field_empty));
			vtVoornaam.requestFocus();
			checks = false;
		}

		if (vtAchternaam.getText().toString().trim().equals("")) {

			vtAchternaam.setError(getString(R.string.err_field_empty));
			vtAchternaam.requestFocus();
			checks = false;
		}

		if (checks) {

			int id = Integer.parseInt(vtId.getText().toString());
			String voornaam = vtVoornaam.getText().toString();
			String tussenvoegsel = vtTussenvoegsel.getText().toString();
			String achternaam = vtAchternaam.getText().toString();

			vtVoornaam.setError(null);
			vtAchternaam.setError(null);
			vtId.setError(null);

			ContentValues v = new ContentValues();
			v.put(DBHelper.MemberTable.COLUMN_GR_ID, id);
			v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
			v.put(DBHelper.MemberTable.COLUMN_PREFIX, tussenvoegsel);
			v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);
			v.put(DBHelper.MemberTable.COLUMN_BALANCE, 0);

			DB.insertOrIgnore(DBHelper.MemberTable.TABLE_NAME, v);

			c.close();
			c = DB.getMembers();
			adapter.swapCursor(c);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		c.moveToPosition(arg2);
		current = (int) arg3;

		vtVoornaam.setText(c.getString(1));
		vtTussenvoegsel.setText(c.getString(2));
		vtAchternaam.setText(c.getString(3));
		vtId.setText(current + "");

		editPane.setVisibility(View.VISIBLE);
		voegtoe.setVisibility(View.GONE);

		vtVoornaam.setError(null);
		vtAchternaam.setError(null);
		vtId.setError(null);
	}

	public void wijzigLid(View view) {

		boolean checks = true;
		// CHECKS
		if (vtId.getText().toString() == null) {

			vtId.setError(getString(R.string.err_field_empty));
			vtId.requestFocus();
			checks = false;
		} else {

			try {
				int id = Integer.parseInt(vtId.getText().toString());
				if (id < 1) {
					vtId.setError("Field must be a non-negative number!");
					vtId.requestFocus();
					checks = false;
				} else {

					// check whether the id already exists
					if (id != current
							&& DB.checkIdInTable(
									DBHelper.MemberTable.TABLE_NAME, id)) {

						vtId.setError("ID Already in use!");
						vtId.requestFocus();
						checks = false;
					}
				}

			} catch (NumberFormatException e) {
				vtId.setError("Field must be a non-negative number!");
				vtId.requestFocus();
				checks = false;
			}
		}

		if (vtVoornaam.getText().toString().trim().equals("")) {

			vtVoornaam.setError(getString(R.string.err_field_empty));
			vtVoornaam.requestFocus();
			checks = false;
		}

		if (vtAchternaam.getText().toString().trim().equals("")) {

			vtAchternaam.setError(getString(R.string.err_field_empty));
			vtAchternaam.requestFocus();
			checks = false;
		}

		if (checks) {

			c.moveToPosition(current);

			String voornaam = vtVoornaam.getText().toString();
			String tussenvoegsel = vtTussenvoegsel.getText().toString();
			String achternaam = vtAchternaam.getText().toString();
			int id = Integer.parseInt(vtId.getText().toString());

			vtVoornaam.setError(null);
			vtAchternaam.setError(null);
			vtId.setError(null);

			ContentValues v = new ContentValues();
			v.put(DBHelper.MemberTable.COLUMN_GR_ID, id);
			v.put(DBHelper.MemberTable.COLUMN_FIRST_NAME, voornaam);
			v.put(DBHelper.MemberTable.COLUMN_PREFIX, tussenvoegsel);
			v.put(DBHelper.MemberTable.COLUMN_LAST_NAME, achternaam);

			DB.updateOrIgnore(DBHelper.MemberTable.TABLE_NAME, current, v);

			c.close();
			c = DB.getMembers();

			adapter.swapCursor(c);
		}

	}

	public void verwijderLid(View view) {

		boolean res = DB.checkAlloweToRemoveMember(current);

		if(!res){

			BasicAlertDialogFragment dialog = new BasicAlertDialogFragment("Cannot remove. Still individual/group orders.");
			dialog.show(getSupportFragmentManager(), "cannot remove");

			return;
		}

		DB.deleteMember(current);

		c.close();
		c = DB.getMembers();
		adapter.swapCursor(c);
		setToDefault();
	}

	public void annuleren(View view) {

		setToDefault();
	}

	public void setToDefault() {

		editPane.setVisibility(View.GONE);
		voegtoe.setVisibility(View.VISIBLE);

		vtId.setText("");
		vtVoornaam.setText("");
		vtTussenvoegsel.setText("");
		vtAchternaam.setText("");

		vtVoornaam.setError(null);
		vtAchternaam.setError(null);
		vtId.setError(null);
	}

	// IMPORT
	public void importMembers(View v) {

		Intent intent = new Intent(this, FileDialog.class);

		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "xml" });
		intent.putExtra(FileDialog.START_PATH, getExternalFilesDir(null).getPath());
		intent.putExtra(FileDialog.KEY_ROOT, getExternalFilesDir(null).getPath());
		startActivityForResult(intent, REQUEST_FILE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_FILE && resultCode == FileDialog.RESULT_OK) {

			targetPath = data.getStringExtra(FileDialog.RESULT_PATH);

			Log.i("result", ""+DB.checkNeedToBackupSD());
			
			if (DB.checkNeedToBackupSD()) {
				mShowDialog = true;

			} else {
				LoadData memberLoader = new LoadData();
				memberLoader.execute(new File(targetPath));
				
				c.close();
				c = DB.getMembers();
				adapter.swapCursor(c);
			}

		} else {
			targetPath = null;
			mShowDialog = false;
		}

	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();

		// play with fragments here
		if (mShowDialog) {
			mShowDialog = false;

			// Show only if is necessary, otherwise FragmentManager will take
			// care
			if (getSupportFragmentManager().findFragmentByTag(PROG_DIALOG_TAG) == null) {
				new ContinueDialogFragment().show(getSupportFragmentManager(),
						PROG_DIALOG_TAG);
			}
		}
	}

	public class LoadData extends AsyncTask<File, Integer, Boolean> {
		
		ProgressDialog progressDialog;
		MemberImporter importer;
		OrderExporter exporter;

		public LoadData() {
			importer = new MemberImporter(LedenMainActivity.this);
			exporter = new OrderExporter(LedenMainActivity.this);
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(LedenMainActivity.this,
					"importing...", "Creating new member list.", true);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(File... params) {
			File f = params[0];
			return importer.importMembers(f);
		}
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		LoadData memberLoader = new LoadData();
		memberLoader.doInBackground(new File(targetPath));
		
		c.close();
		c = DB.getMembers();
		adapter.swapCursor(c);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
	}

}
