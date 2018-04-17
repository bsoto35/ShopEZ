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

	public Set<String> getHistoryListNames() {
		Set<String> result = null;
		for (GroceryList list : this.historyList) {
			result.add(list.getListName());
		}
		return result;
	}
	
	public GroceryList getHistoryList(String name) {
		GroceryList result = new GroceryList();
		for (GroceryList list : this.historyList) {
			if (list.getListName().equals(name)) {
				result = list;
			}
		}
		return result;
	}

	public void addToHistoryList(GroceryList list) {
		this.historyList.add(list);
	}
	
	public void removeHistoryList(GroceryList list) {
		this.historyList.remove(list);
	}

	public int getGroceryListID() {
		return groceryListID;
	}

	public void setGroceryListID(int groceryListID) {
		this.groceryListID = groceryListID;
	}
}
