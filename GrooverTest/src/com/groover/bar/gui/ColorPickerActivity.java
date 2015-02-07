package com.groover.bar.gui;

import com.groover.bar.R;


import com.groover.bar.color.ColorPicker;
import com.groover.bar.color.ColorPicker.OnColorChangedListener;
import com.groover.bar.color.OpacityBar;
import com.groover.bar.color.SVBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ColorPickerActivity extends Activity implements OnColorChangedListener {

	public static final String COLOR = "color";
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_picker);
		
		picker = (ColorPicker) findViewById(R.colorpicker.picker);
		svBar = (SVBar) findViewById(R.colorpicker.svbar);
		opacityBar = (OpacityBar) findViewById(R.colorpicker.opacitybar);
		button = (Button) findViewById(R.colorpicker.button1);
		text = (TextView) findViewById(R.colorpicker.textView1);
		
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				text.setTextColor(picker.getColor());
				picker.setOldCenterColor(picker.getColor());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.color_picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onSelectColor(View view){
		
		Intent intent = new Intent(this, ArticleActivity.class);
		intent.putExtra(COLOR, picker.getColor());
		
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, intent);
		} else {
			getParent().setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

	@Override
	public void onColorChanged(int color) {
		
	}
}
