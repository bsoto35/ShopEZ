package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;

public class GroceryList {
	private int accountID;
	//private int itemID;
	private ArrayList <Integer> groceries = new ArrayList<Integer>();
	
	public GroceryList() {
		
	}
	public GroceryList(int aID, ArrayList <Integer> iID ) {
			accountID=aID;
			groceries=iID;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int id) {
		accountID = id;
	}


	public void insertItems(int itemID, int qty) {
		System.out.println("qty: "+qty+" itemID: "+itemID);
		int i=0;
		while(i<qty) {
			groceries.add(itemID);
			i++;
		}
	}

	public void removeItems(int itemID, int qty) {
		int i=0;
		while(i<qty) {
			groceries.remove(itemID);
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
