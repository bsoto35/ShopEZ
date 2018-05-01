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
	private double amount=1.0;
	private String name; 
	private Account login=new Account();
	private boolean bool=false;
	private GroceryList list=new GroceryList();
	
	public InsertItemController() {
	}
	
	public InsertItemController(GroceryList list, Account login) {
		this.list=list;
		this.login=login;
	}
	
	public Item findItemByName(String name) throws SQLException{
		item=db.findItemByItemName(name);
		return item;
	}
	
	public Account getAccount() {
		return login;
	}
	
	public void setAccount(Account account) {
		login=account;
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
		list.removeItems(item.getItemName(), qty);
		return bool;
	}
	
	public boolean addItem(int id, Item item, int qty) throws SQLException{
		name=item.getItemName();
		bool= db.insertItemIntoGroceryListTable(id, item, qty);
		System.out.println("passed");
		list.insertItems(name, qty);
		System.out.println("passed3");
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
	
	public GroceryList getGroceryList() {
		
		return list;
	}
	
	public void setTotalPrice(double sum) {
		amount=sum;
	}
	
	public double getTotalPrice() {
		return amount;
	}
}
