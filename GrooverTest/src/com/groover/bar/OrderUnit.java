package com.groover.bar;

public class OrderUnit {
	
	private Article subject;
	private int amount;
	private double subtotal;
	
	public OrderUnit(Article a){
		
		subject = a;
		amount = 0;
		subtotal = 0;
		
	}
	
	public OrderUnit(Article a, int n){
		
		subject = a;
		amount = n;
		subtotal = amount*subject.getPrice();
	}
	
	public void setAmount(int n){
		
		amount = n;
		subtotal = subject.getPrice()*amount;
	}
	
	public Article getArticle(){return subject;}
	public int getAmount(){return amount;}
	public double getSubtotal(){return subtotal;}

}