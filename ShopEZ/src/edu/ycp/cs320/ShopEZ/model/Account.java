package edu.ycp.cs320.ShopEZ.model;

public class Account {
	private int accountID;
	private String username;
	private String password;
<<<<<<< HEAD
	private int historyListID;
	private History history = new History();
	private Boolean success;


=======
	
>>>>>>> pmnorris
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
<<<<<<< HEAD

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
	public Boolean isPasswordCorrect(String Password) {
		if(this.password.equals(password)) {
			return true;
		}
		else {
			return false;	
		}
	}
	public Boolean getSuccess() {
		return this.success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
=======
}
>>>>>>> pmnorris
