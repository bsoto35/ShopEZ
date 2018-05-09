package edu.ycp.cs320.ShopEZ.controller;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;
import java.util.List;
/**
 * Controller for the insertItemServlet.
 */
public class InsertItemController {
	private DerbyDatabase db= new DerbyDatabase();
	private int id; 
	private int quantity;
	private Item item = new Item();
	private double total = 0.0; 
	private Account login = new Account();
	private boolean bool = false;
	private List<Integer> itemIDs = new ArrayList<Integer>();
	private GroceryList list = new GroceryList(id, itemIDs);

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

	public boolean isGuest(Account login) {
		if(login.getUsername().equals("guest"))
			return true;
		return false;
	}

	public List<Item> findItemsByID(List<Integer> itemList) throws SQLException{
		List<Item> items= new ArrayList<Item>();
		for(int i=0; i < itemList.size(); i++) {
			item=db.findItemByItemID(itemList.get(i));
			items.add(item);
		}
		return items;
	}
	
	public List<Item> getAllItems() throws SQLException{
		return db.findAllItems();
	}

	public Item findItemByID(int num) throws SQLException{
		item=db.findItemByItemID(num);
		return item;
	}

	public List<Item> findAllItems() throws SQLException{
		return db.findAllItemsForAccount(login.getAccountID());
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
		list.removeItems(item.getItemID(), qty);
		return bool;
	}

	public boolean addItem(int id, Item item, int qty) throws SQLException{
		bool= db.insertItemIntoGroceryListTable(id, item, qty);
		System.out.println(item.getItemID()+" "+item.getItemName()+" qty:"+qty);
		/*for(int i=0; i< qty; i++) {
			list.getList().add(item.getItemID());
		}*/
		list.insertItems(item.getItemID(), qty);
		return bool;
	}

	public String getItemNameatIndex(int index) throws SQLException{
		item=db.findItemByItemID(list.getItemID(index));
		return item.getItemName();
	}

	public int getListlength() {
		return list.getList().size();
	}

	public List<Integer> getIdList(){
		System.out.println(""+list.getList().size());
		itemIDs=list.getList();
		return itemIDs;
	}

	public void setIdList(List<Integer> iIDs){		
		list.setList(iIDs);
	}

	public GroceryList getGroceryList() {

		return list;
	}
	public void setGroceryList(GroceryList list) {

		this.list=list;
	}

	public void setTotalPrice(double sum) {
		total=sum;
	}

	public double getTotalPrice() {
		list.getList();
		return total;
	}
	
	public void setQuantity(int quantity) {
		this.quantity=quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}
}
