package edu.ycp.cs320.ShopEZ.model;

public class Account {
	private int accountID;
	private String username;
	private String password;
	
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
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
}