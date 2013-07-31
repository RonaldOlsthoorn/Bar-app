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
		base = c;
		numPad = n;
		numPad.addPropertyListener(this);
		this.c=context;
		m = new HashMap<Integer,Integer>();
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		

        View view = super.getView(position, convertView, parent);        
        Button button = (Button) view.findViewById(R.orderRow.cancel);
        
        button.setOnClickListener(new deleteAdapter(position)); 
        
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
