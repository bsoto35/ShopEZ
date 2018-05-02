package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int accountID;
	//private int itemID;
	private ArrayList <Integer> groceries = new ArrayList<>();
	
	public GroceryList() {
		
	}
	public GroceryList(int aID, int iID ) {
			accountID=aID;
			groceries.add(iID);
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int id) {
		accountID = id;
	}


	public void insertItems(Item item, int qty) {
		int i=0;
		while(i<qty) {
			groceries.add(item.getItemID());
			i++;
		}
	}

	public void removeItems(Item item, int qty) {
		int i=0;
		while(i<qty) {
			groceries.remove(item.getItemID());
			i++;
		}
	}

	public int getItemID(int index) {
		return groceries.get(index);
	}

	public ArrayList<Integer> getList(){
		return groceries; 
	}
	

}
