package nl.groover.bar.frame;

//Used as proxy class for articles in database
public class Article {
	
	private int art_id;
	private double price;
	private String name;
	private boolean editable;
	private int color;
	
	public Article(int id, double p, String n, boolean b, int c){
		
		art_id = id;
		price = p;
		name = n;
		editable = b;
		color = c;
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
	
	public void setColor(int c){
		
		color = c;
	}
	
	public void setEditable(boolean b){
		
		editable = b;
	}

	public int getColor() {
		
		return color;
	}
	
}
