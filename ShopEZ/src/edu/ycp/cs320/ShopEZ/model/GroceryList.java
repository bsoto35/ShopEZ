package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int groceryListID;
	private int accountID;
	private int historyID;
	private String ListName;
	private double ListPrice;
	private currentList items = new currentList();
	
	public GroceryList() {
		
	}
	
	public void setListName(String x) {
		this.ListName = x;
	}
	
	public String getListName() {
		return this.ListName;
	}
	
	public void setListPrice(double cash) {
		this.ListPrice = cash;
	}
	
	public void updatePrice(double money) {
		this.ListPrice += money;
	}
	
	public double getTotalPrice() {
		return this.ListPrice;
	}
	
	public ArrayList<String> getCurrentList(){
		return this.items.getItemsInCurrentList();
	}

	public void addToCurrentList(String item) {
		this.items.addItemInCurrentList(item);
	}
	
	public int getHistoryID() {
		return historyID;
	}

	public void setHistoryID(int id) {
		this.historyID = id;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int id) {
		accountID = id;
	}

	public int getGroceryListID() {
		return this.groceryListID;
	}

	public void setGroceryListID(int groceryListID) {
		this.groceryListID = groceryListID;
	}
	
}
