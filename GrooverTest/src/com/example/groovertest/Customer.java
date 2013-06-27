package com.example.groovertest;

public class Customer {
	
	private int id;
	private int account;
	private String type;
	private String name;
	
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
