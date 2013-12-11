package com.groover.bar.frame;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;


import android.content.ContentValues;
import android.content.Context;
import android.util.SparseArray;

/*
 * proxy class representing consumptions.
 */
public class Order {
	
	private Customer customer;
	private OrderUnit[] orderUnits;
	private DecimalFormat df= new DecimalFormat("0.00");
		
	public Order(Customer c, OrderUnit[] o){
		
		customer = c;
		orderUnits = o;			
	}
	
	public int getCount() {
		return orderUnits.length;
	}
	

	public OrderUnit getUnit(int position) {
		return orderUnits[position];
	}
	
	public void setAmount(int position, int amount){
		
		getUnit(position).setAmount(amount);
	}
	
	public long getId(int position) {
		return getUnit(position).getArticle().getId();
	}

	public void deleteOrder(int position) {
		setAmount(position, 0);
	}
	
	public void addAmmountToOrderUnit(int position, int i) {
		
		setAmount(position, getUnit(position).getAmount()+i);
	}

	
	public double calculateTotal(){
		
		double total =0;	
		
		for(int i=0; i<orderUnits.length;i++){
			total = total + orderUnits[i].getSubtotal();
		}
		return total;
	}
	
	public boolean writeToDB(Context c){	
		
		DBHelper db = DBHelper.getDBHelper(c);
		ContentValues v = new ContentValues();
		
		for(int i=0; i<orderUnits.length;i++){
			
			if(orderUnits[i].getAmount()!=0){
				
				v.clear();
				v.put(DBHelper.Order.COLUMN_ACCOUNT, customer.getAccount());
				v.put(DBHelper.Order.COLUMN_TOTAL,orderUnits[i].getSubtotal());
				v.put(DBHelper.Order.COLUMN_TYPE, "consumption");
				
				long id = db.insertOrIgnore(DBHelper.Order.TABLE_NAME, v);
				
				if(id == -1){
					return false;
				}
				
				v.clear();
				v.put(DBHelper.Consumption.COLUMN_ID, id);
				v.put(DBHelper.Consumption.COLUMN_AMMOUNT, orderUnits[i].getAmount());
				v.put(DBHelper.Consumption.COLUMN_ARTICLE_NAME, orderUnits[i].getArticle().getName());
				v.put(DBHelper.Consumption.COLUMN_ARTICLE_PRICE, orderUnits[i].getArticle().getPrice());
					
				id =  db.insertOrIgnore(DBHelper.Consumption.TABLE_NAME, v);
				
				if(id == -1){
					return false;
				}		
			}		
		}
		return true;
	}
	
	public String toString(){
		
		String res="";	
		
		for(int i=0; i<orderUnits.length;i++){
			if(orderUnits[i].getAmount()!=0){
				res = res +orderUnits[i].getAmount()+" "+orderUnits[i].getArticle().getName()+",";
			}
		}
		return res.substring(0, res.length()-1)+" totaal: "+df.format(calculateTotal());	
	}
}