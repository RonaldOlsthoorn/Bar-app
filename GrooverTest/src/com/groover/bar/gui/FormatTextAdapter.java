package com.groover.bar.gui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class FormatTextAdapter extends SimpleCursorAdapter{
	
	private boolean childrenEnabled=true;
	private DecimalFormat df;
	private int id;
	
	public FormatTextAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, DecimalFormat df, int id) {
		super(context, layout, c, from, to, flags);
		// TODO Auto-generated constructor stub
		this.df = df;
		this.id = id;
	}
	
	@Override
	public boolean isEnabled(int position) {
	    return childrenEnabled;
	}
	
	public void setAllChildrenEnabled(boolean enabled){
		childrenEnabled = enabled;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		
		View v = super.getView(position, convertView, parent);
		
		TextView tv = (TextView) v.findViewById(id);
		tv.setText(df.format(Double.parseDouble(tv.getText().toString())));
		
		return v;
		
	}

}