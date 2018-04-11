package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int AccountID;
	private String ListName;
	private ArrayList<Item> Items = new ArrayList<Item>();
	private double ListPrice;
	
	public GroceryList() {
		
	}
	
	public void setListName(String x) {
		this.ListName = x;
	}
	
	public String getListName() {
		return this.ListName;
	}
	
	public void updatePrice(double money) {
		this.ListPrice += money;
	}
	
	public double getTotalPrice() {
		return this.ListPrice;
	}
	
	public void addItem(Item name) {
		this.Items.add(name);
	}

	public void removeItem(Item name) {
		this.Items.remove(name);
	}
	
	public ArrayList<Item> getList(){
		return this.Items;
	}

	public int getHistoryID() {
		return HistoryID;
	}

	public void setHistoryID(int historyID) {
		HistoryID = historyID;
	}

	public int getAccountID() {
		return AccountID;
	}

	public void setAccountID(int accountID) {
		AccountID = accountID;
	}
	
}
