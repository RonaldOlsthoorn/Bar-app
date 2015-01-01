package com.groover.bar.frame;

//Used as proxy class for articles in database
public class Article {
	
	private int art_id;
	private double price;
	private String name;
	private boolean editable;
	
	public Article(int id, double p, String n, boolean b){
		
		art_id = id;
		price = p;
		name = n;
		editable = b;
		
	}
	
	public Article(int id, double p, String n) {
		art_id = id;
		price = p;
		name = n;
		editable = false;
	}

	public int getId(){return art_id;}
	public double getPrice(){return price;}
	public String getName(){return name;}
	public boolean getEditable(){return editable;}
	

	public void setName(String n) {

		name = n;
	}
	public void setPrice(double p) {

		price = p;
	}
	
	public void setEditable(boolean b){
		
		editable = b;
	}
	
}
