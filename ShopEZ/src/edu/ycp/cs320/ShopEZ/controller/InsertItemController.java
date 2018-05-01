package edu.ycp.cs320.ShopEZ.controller;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;
/**
 * Controller for the guessing game.
 */
public class InsertItemController {
	private DerbyDatabase db= new DerbyDatabase();
	private int id; 
	private Item item;
	private int amount=1;
	private String name; 
	private Account login;
	private boolean bool=false;
	private GroceryList list;
	
	public InsertItemController() {
		list=new GroceryList();
	}
	
	public Item findItemByName(String name) throws SQLException{
		item=db.findItemByItemName(name);
		return item;
	}
	
	public int getAccountId() {
		id=login.getAccountID();
		return id;
	}
	public Account getAccountByUser(String name) throws SQLException {
		login= db.findAccountByUsername(name);
		return login;
	}
	
	public boolean removeItem(int id, Item item, int qty)  throws SQLException{
		bool= db.removeItemFromGroceryListTable(id, item, qty);
		list.removeItem(item.getItemName());
		return bool;
	}
	
	public boolean addItem(int accout, Item item, int qty) throws SQLException{
		bool= db.insertItemIntoGroceryListTable(id, item, qty);
		list.addItem(item.getItemName());
		return bool;
	}
	
	public String getItemNameatIndex(int index) {
		return list.getItem(index);
	}
	
	public int getListlength() {
		return list.getLengthofList();
	}
	public ArrayList<String> getArrayList(){
		return list.getList();
	}
	
}
