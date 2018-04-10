package edu.ycp.cs320.ShopEZ.persist;

import java.sql.SQLException;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;

public interface IDatabase {
	public String updateItemQuantity(Item item, int quantity) throws SQLException;
	public String insertItemIntoItemsTable(String name, double price, int x, int y, int quantity) throws SQLException;
	public boolean retrieveAccountFromAccountsTable(int id) throws SQLException;
	double findItemPriceByItemName(edu.ycp.cs320.ShopEZ.model.Item item);
}
