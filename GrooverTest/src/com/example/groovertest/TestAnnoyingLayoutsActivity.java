package com.example.groovertest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TestAnnoyingLayoutsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_annoying_layouts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_annoying_layouts, menu);
		return true;
	}

}
