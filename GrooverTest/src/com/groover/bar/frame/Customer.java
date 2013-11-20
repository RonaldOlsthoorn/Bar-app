package com.groover.bar.frame;

//Proxy class for customer in the database. Can be used to represent groups or members.
public class Customer {
	
	private int id;
	private int account;
	private String type;
	private String name;
	
	//pre: i is the identifier for the group/member (same as in db).
	//account is the account number in db, t is the type of customer (group/member).
	//in case of a member, the name is both first and lastname attached. 
	public Customer(int i, int a, String t, String n){
		
		id = i;
		account = a;
		type = t;
		name = n;
	}

	public int getId(){
		
		return id;
	}
	
	public int getAccount(){
		
		return account;
	}
	
	public String getType(){
		
		return type;
	}
	
	public String getName(){
		
		return name;
	}
}
