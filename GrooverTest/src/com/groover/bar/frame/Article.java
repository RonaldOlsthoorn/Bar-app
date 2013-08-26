package com.groover.bar.frame;

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
	
}
