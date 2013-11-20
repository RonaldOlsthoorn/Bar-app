package com.groover.bar.frame;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;


import android.content.ContentValues;
import android.content.Context;

/*
 * proxy class representing consumptions.
 */
public class Order {
	
	private Customer customer;
	private HashMap<Integer,OrderUnit> map;
	private Stack<Integer> stack;
	private ArticleFactory af;
	private DecimalFormat df= new DecimalFormat("0.00");
	
	
	public Order(Customer c, ArticleFactory af){
		
		this.af=af;
		customer = c;
		map = new HashMap<Integer,OrderUnit>();
		stack = new Stack<Integer>();
		
	}
	
	public void addOrderUnit(OrderUnit u){
		
		map.put(Integer.valueOf(u.getArticle().getId()), u);
		stack.push(Integer.valueOf(u.getArticle().getId()));
	}
	
	public void addSpecified(int art_id, int amount){
		addOrderUnit(new OrderUnit(af.getArticle(art_id),amount));
	}
	
	public void addUnspecified(int art_id){
		addSpecified(art_id,0);
	}
	
	public void deleteOrderKey(int key){
		
		map.remove(Integer.valueOf(key));
		stack.remove(Integer.valueOf(key));
	}
	
	public double calculateTotal(){
		
		double total =0;
		Iterator<OrderUnit> it = map.values().iterator();
		while(it.hasNext()){
			total = total + it.next().getSubtotal();
		}
		
		return total;
	}
	
	public boolean writeToDB(Context c){	
		
		DBHelper db = DBHelper.getDBHelper(c);
		ContentValues v = new ContentValues();
		
		for(int i=0; i<stack.size();i++){
			
			v.clear();
			v.put(DBHelper.Order.COLUMN_ACCOUNT, customer.getAccount());
			v.put(DBHelper.Order.COLUMN_TOTAL,map.get(stack.get(i)).getSubtotal());
			v.put(DBHelper.Order.COLUMN_TYPE, "consumption");
			
			long id = db.insertOrIgnore(DBHelper.Order.TABLE_NAME, v);
			
			if(id == -1){
				return false;
			}
			
			v.clear();
			v.put(DBHelper.Consumption.COLUMN_ID, id);
			v.put(DBHelper.Consumption.COLUMN_AMMOUNT, map.get(stack.get(i)).getAmount());
			v.put(DBHelper.Consumption.COLUMN_ARTICLE_NAME, map.get(stack.get(i)).getArticle().getName());
			v.put(DBHelper.Consumption.COLUMN_ARTICLE_PRICE, map.get(stack.get(i)).getArticle().getPrice());
				
			id =  db.insertOrIgnore(DBHelper.Consumption.TABLE_NAME, v);
			
			if(id == -1){
				return false;
			}
		}
		
		return true;
		
	}

	public int getCount() {
		// TODO Auto-generated method stub
		
		return map.size();
	}
	
	public long getId(int pos){
		
		return stack.get(pos).longValue();
	}

	public OrderUnit getUnit(int position) {
		// TODO Auto-generated method stub
		return map.get(stack.get(position));
	}
	
	public OrderUnit getUnitById(int id){
		
		return map.get(Integer.valueOf(id));
	}
	
	public boolean contains(int i){
		
		return stack.contains(Integer.valueOf(i));
			
	}
	
	public String toString(){
		
		String res="";
		Iterator<OrderUnit> it = map.values().iterator();
		while(it.hasNext()){
			OrderUnit unit = it.next();
			res = res + unit.getAmount()+" "+unit.getArticle().getName()+",";
		}
		
		return res.substring(0, res.length()-1)+" totaal: "+df.format(calculateTotal());	
	}
}


