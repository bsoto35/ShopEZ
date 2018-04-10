package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class Account {
	private int accountID;
	private String username;
	private String password;
	private ArrayList<GroceryList> historyList = new ArrayList<GroceryList>();
	
	public Account() {
		
	}
	
	public void setAccountID(int x) {
		this.accountID = x;
	}
	
	public int getAccountID() {
		return this.accountID;
	}
	
	public void setUsername(String x) {
		this.username = x;
	}
	
	public void setPassword(String x) {
		this.password = x;
	}
	
	// get the account's username
	public String getUsername() {
		return this.username;
	}
	
	// get the account's password1
	public String getPassword() {
		return this.password;
	}
	
	// check to see if the repeated username and password strings match up with the account's username and passord
	public boolean confirmAccount(String name, String password) {
		boolean result = false;
		if(name.equals(this.username) == true) {
			if(password.equals(this.password) == true) {
				result = true;
			}
		}
		return result;
	}
	
	// store the String name for the GroceryList and the GroceryList itself into the HashMap 
	public void addToHistoryList(GroceryList list) {
		this.historyList.add(list);
	}
	
	public ArrayList<GroceryList> getList(){
		return this.historyList;
	}
	
}
