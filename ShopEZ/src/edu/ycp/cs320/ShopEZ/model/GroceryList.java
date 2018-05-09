package edu.ycp.cs320.ShopEZ.model;

import java.util.ArrayList;
import java.util.List;

public class GroceryList {
	private int accountID;
	private List <Integer> groceries = new ArrayList<Integer>();
	public GroceryList() {

	}
	public GroceryList(int aID, List <Integer> iID ) {
		accountID=aID;
		int i=0;
		while(i< iID.size()) {
			groceries.add(iID.get(i));
		}
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
		int ct=0;
		while(i < groceries.size() && ct < qty) {
			if(groceries.get(i) == itemID) {
				System.out.print("itemID to be removed: "+ itemID +" ID being removed: "+groceries.get(i));
				groceries.remove(i);
				ct++;
			}
			else {
				i++;
			}
		}
	}

	public int getItemID(int index) {
		return groceries.get(index);
	}

	public List<Integer> getList(){
		return groceries; 
	}

	public void setList(List<Integer> iIDs) {
		groceries=iIDs; 
	}


}
