package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class currentList {
	private int currentListID;
	private int groceryListID;
	private int accountID;
	private int historyID;
	private ArrayList<String> items = new ArrayList<String>();
	
	public int getCurrentListID() {
		return this.currentListID;
	}
	
	public void setCurrentListID(int currentListID) {
		this.currentListID = currentListID;
	}
	
	public int getGroceryListID() {
		return this.groceryListID;
	}
	
	public void setGroceryListID(int groceryListID) {
		this.groceryListID = groceryListID;
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
	
	public ArrayList<String> getItemsInCurrentList(){
		return this.items;
	}
	
	public void addItemInCurrentList(String name) {
		this.items.add(name);
	}
}
