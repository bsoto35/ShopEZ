package edu.ycp.cs320.ShopEZ.model;

import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Account {
	private int accountID;
	private String username;
	private String password;
	private int historyListID;
	private History history = new History();
	private Map<String, String> credentials;
	private ArrayList<String> usernames;
	private ArrayList<String> passwords;
	
	public Account() {
		setUsernames(new ArrayList<String>());
		passwords = new ArrayList<String>();
		credentials = new TreeMap<String, String>();
		
		usernames.add("admin");
		usernames.add("guest");

		passwords.add("pasword");
		passwords.add("guest");
		
		for (int i = 0; i < usernames.size(); i++) {
			credentials.put(usernames.get(i), passwords.get(i));
		}
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
		this.history.addToHistoryList(list);
	}
	
	public Set<String> getHistoryList(){
		return this.history.getHistoryListNames();
	}
	
	public GroceryList getGroceryList(String listName){
		return this.history.getHistoryList(listName);
	}

	public int getHistoryListID() {
		return this.historyListID;
	}

	public void setHistoryListID(int historyListID) {
		this.historyListID = historyListID;
	}
	
	public boolean validatePW(String name, String pw) {
		if (credentials.containsKey(name)) {
			if  (credentials.get(name).equals(pw)) {
				return true;
			}
		}			
		return false;
	}

	public ArrayList<String> getUsernames() {
		return usernames;
	}
	
	public void addUsername(String name) {
		this.usernames.add(name);
	}
	
	public void setUsernames(ArrayList<String> usernames) {
		this.usernames = usernames;
	}
}
