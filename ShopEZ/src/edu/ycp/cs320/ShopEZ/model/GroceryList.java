package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int accountID;
	private int itemID;
	private ArrayList <String> groceries;

	public GroceryList() {
		groceries= new ArrayList<>();
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
		return groceries.size();
	}

	public void insertItems(String item, int qty) {
		System.out.println("passed2");
		int i=0;
		while(i<qty) {
			groceries.add(item);
			i++;
		}
	}

	public void removeItems(String item, int qty) {
		int i=0;
		while(i<qty) {
			groceries.remove(item);
			i++;
		}
	}

	public String getItem(int index) {
		return groceries.get(index);
	}

	public ArrayList<String> getList(){
		return groceries; 
	}

}
