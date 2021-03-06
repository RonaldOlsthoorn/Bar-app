package nl.groover.bar.frame;

//Proxy class for customer in the database. Can be used to represent groups or members.
public class Customer {
	
	private int id;
	private int account;
	private String name;
	
	//pre: i is the identifier for the group/member (same as in db).
	//account is the account number in db, t is the type of customer (group/member).
	//in case of a member, the name is both first and lastname attached. 
	public Customer(int a, String n){
		
		account = a;
		name = n;
	}

	public int getAccount(){
		
		return account;
	}
	
	public String getName(){
		
		return name;
	}
}
