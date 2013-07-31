package com.example.groovertest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class OrderListAdapter extends SimpleCursorAdapter implements PropertyChangeListener {
	
	private FilteredCursor base;
	private HashMap<Integer,Integer> m;
	private Context c;
	private NumPadAdapter numPad;

	public OrderListAdapter(Context context, int layout, FilteredCursor c,
			String[] from, int[] to, int flags, NumPadAdapter n) {
		super(context, layout, c, from, to, flags);
		Log.i("Count",1.1+"");

		base = c;
		Log.i("Count",1.1+"");
		numPad = n;
		Log.i("Count",1.1+"");
		numPad.addPropertyListener(this);
		Log.i("Count",1.1+"");
		this.c=context;
		Log.i("Count",1.1+"");
		m = new HashMap<Integer,Integer>();
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		
		Log.i("Count",1.1+"");

        View view = super.getView(position, convertView, parent);
        
		Log.i("Count",1.2+"");

        Button button = (Button) view.findViewById(R.orderRow.cancel);
        
		Log.i("Count",1.3+"");

        button.setOnClickListener(new deleteAdapter(position)); 
        
		Log.i("Count",1.4+"");

        return view;
    }

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		if(event.getSource().equals(numPad)){
			
		}
	}
	
	private class deleteAdapter implements OnClickListener{

		int p;
		
		public deleteAdapter(int pos){
			p = pos;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			base.filter(p);
            OrderListAdapter.this.notifyDataSetChanged();

		}
		
	}
}
