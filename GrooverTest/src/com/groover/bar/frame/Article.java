package com.groover.bar.frame;

//Used as proxy class for articles in database
public class Article {
	
	private int art_id;
	private double price;
	private String name;
	
	public Article(int id, double p, String n){
		
		art_id = id;
		price = p;
		name = n;
		
	}
	
	public int getId(){return art_id;}
	public double getPrice(){return price;}
	public String getName(){return name;}

	public void setName(String n) {

		name = n;
	}
	public void setPrice(double p) {

		price = p;
	}
	
}
