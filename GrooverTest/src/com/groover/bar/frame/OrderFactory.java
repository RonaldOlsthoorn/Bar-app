package com.groover.bar.frame;

import android.database.Cursor;

//Constructor class to make an order
public class OrderFactory {
		
	public static Order createEmptyOrder(Customer customer, Cursor cursor){
		
		cursor.moveToFirst();
		OrderUnit[] units = new OrderUnit[cursor.getCount()];
		int i =0;		
		OrderUnit u;
		Article a;
		
		while(cursor.getPosition()<cursor.getCount()){
			
			a = new Article(cursor.getInt(0), cursor.getDouble(2), cursor.getString(1));
			u = new OrderUnit(a,0);
			units[i] = u;
			cursor.moveToNext();
			i++;
		}
		
		return new Order(customer,units);
	}
}
