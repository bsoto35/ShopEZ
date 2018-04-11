package edu.ycp.cs320.ShopEZ.persist;

import java.sql.SQLException;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Pair;

public interface IDatabase {
	
	public String insertItemIntoItemsTable(String name, double price, int x, int y);
	public boolean verifyAccountFromAccountsTable(String username, String password);
	public double findItemPriceByItemName(String itemName);
	public int findAccountIDbyUsernameAndPassword(String username, String password);
	public List<Pair<Account, GroceryList>> findGroceryListHistoryByAccountID(int id);
}
