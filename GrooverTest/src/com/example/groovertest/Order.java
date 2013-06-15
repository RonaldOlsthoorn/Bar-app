package com.example.groovertest;

import android.content.Context;

public class Order {
	
	String custType;
	String custName;
	int custId;
	int account;
	DBHelper DB;
	
	public Order(String ct, String cn, int ci, int ac, Context c){
		
		custType = ct;
		custName = cn;
		custId = ci;
		account = ac;
		
		DB = DBHelper.getDBHelper(c);
		
	}
	
	
	
	public void writeToDB(){	
		
	}
}
