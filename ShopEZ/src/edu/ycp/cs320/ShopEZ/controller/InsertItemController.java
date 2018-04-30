package edu.ycp.cs320.ShopEZ.controller;
import java.sql.SQLException;

import edu.ycp.cs320.ShopEZ.model.Account;
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
	private boolean bool;
	
	public InsertItemController() {
		
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
		return bool;
	}
	
	public boolean addItem(int accout, Item item, int qty) throws SQLException{
		bool= db.insertItemIntoGroceryListTable(id, item, qty);
		return bool;
	}
}
