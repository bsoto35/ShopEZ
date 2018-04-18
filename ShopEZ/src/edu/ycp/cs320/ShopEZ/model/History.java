package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;
import java.util.Set;

public class History {
	private int historyID;
	private int accountID;
	private int groceryListID;
	private GroceryList historyList = new GroceryList();
	
	public History() {
		
	}

	public int getAccountID() {
		return this.accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public int getHistoryID() {
		return this.historyID;
	}

	public void setHistoryID(int historyID) {
		this.historyID = historyID;
	}

	public ArrayList<String> getHistoryListNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Item name : this.historyList.getTheList()) {
			result.add(name.getItemName());
		}
		return result;
	}
	
	public GroceryList getHistoryList(String name) {
		return this.historyList;
	}
	

	public int getGroceryListID() {
		return groceryListID;
	}

	public void setGroceryListID(int groceryListID) {
		this.groceryListID = groceryListID;
	}
}
