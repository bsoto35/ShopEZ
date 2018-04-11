package edu.ycp.cs320.ShopEZ.persist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Pair;

public interface IDatabase {
	
	public String insertItemIntoItemsTable(String name, double price, int x, int y) throws SQLException;
	public boolean verifyAccountFromAccountsTableByUsernameAndPassword(String username, String password);
	public double findItemPriceByItemName(String itemName);
	public int findAccountIDbyUsernameAndPassword(String username, String password);
	public Account findAccountByAccountID(int id) throws SQLException;
	public Set<String> getGroceryListHistoryByAccountID(int id);
	public ArrayList<Account> findAllAccounts();
	public ArrayList<Item> findAllItems();
}
