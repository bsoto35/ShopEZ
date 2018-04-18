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

	public double getupdatedPrice() {
		this.ListPrice = getTotalPrice();
		return this.ListPrice;
	}

	public double getTotalPrice() {
		double total = 0.00; 
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
		return theList;
	}

	public void addItem(Item item, int qty) {
		for (int i = 0; i < qty; i++) {
			theList.add(item);
		}
	}
	
	public ArrayList<Integer> getTheListOfItemIds(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (Item item : this.theList) {
			result.add(item.getItemID());
		}
		return result;
	}
	
	public void removeItemFromTheList(Item item, int qty) {
		int x = 0;
		while(x < qty) {
			for (int i = 0; (i < theList.size()) && (x < qty); i++) {
				if (theList.get(i).equals(item)) {
					theList.remove(i);
					i--;
					x++;
				}
			}
		}
	}


	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

}
