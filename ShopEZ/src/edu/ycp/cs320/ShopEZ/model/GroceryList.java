package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int accountID;
	private int itemID;
	private double ListPrice;
	private ArrayList<Item> theList = new ArrayList<Item>();
	
	public GroceryList() {
		
	}
	
	public void setListPrice(double cash) {
		this.ListPrice = cash;
	}
	
	public void updatePrice(double money) {
		this.ListPrice += money;
	}
	
	public double getTotalPrice() {
		double total=0; 
		for(int i=0; i< theList.size(); i++) {
			theList.get(i).getItemPrice(); 
		}
		return total; 
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int id) {
		accountID = id;
	}
    
	public ArrayList<Item> getTheList() {
		return this.theList;
	}
	
	public ArrayList<Integer> getTheListOfItemIds(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (Item item : this.theList) {
			result.add(item.getItemID());
		}
		return result;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	public void addItem(Item item) {
		theList.add(item);
	}
	
}
