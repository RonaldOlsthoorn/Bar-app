package com.example.groovertest;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Stack;
import android.database.Cursor;

public class OrderCursor extends FilteredCursor{

	private HashMap<Integer,Integer> m;
	
	public OrderCursor(Cursor cursor) {
		super(cursor);
		// TODO Auto-generated constructor stub
		m = new HashMap<Integer,Integer>();
		
	}
	
	

}
