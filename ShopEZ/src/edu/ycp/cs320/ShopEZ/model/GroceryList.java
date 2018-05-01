package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int accountID;
	private int itemID;
	private ArrayList <String> list= new ArrayList<String>();

	public GroceryList() {
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int id) {
		accountID = id;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	public int getLengthofList() {
		return list.size();
	}
	
	public void addItem(String item) {
		list.add(item);
	}
	
	public void removeItem(String item) {
		list.remove(item);
	}
	
	public String getItem(int index) {
		return list.get(index);
	}
	
	public ArrayList<String> getList(){
		return list; 
	}

}
