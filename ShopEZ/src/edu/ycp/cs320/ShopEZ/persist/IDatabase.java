package edu.ycp.cs320.ShopEZ.persist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Item;

public interface IDatabase {
	
	public String addAccountIntoAccountsTable(final String username, final String password) throws SQLException;
	public Item findItemByItemName(final String itemName) throws SQLException;
	public String insertItemIntoItemsTable(String name, double price, int x, int y) throws SQLException;
	public boolean verifyAccountFromAccountsTableByUsernameAndPassword(String username, String password) throws SQLException;
	public double findItemPriceByItemName(String itemName) throws SQLException;
	public int findAccountIDbyUsernameAndPassword(String username, String password) throws SQLException;
	public Account findAccountByAccountID(int id) throws SQLException;
	public ArrayList<Account> findAllAccounts() throws SQLException;
	public List<Item> findAllItemsForAccount(int id) throws SQLException; 
	public Item updateItemPriceByItemNameAndPrice(final String name, final double price) throws SQLException;
	public Item updateItemLocationByItemNameAndXYCoords(final String item, final int x, final int y) throws SQLException;
	void dropTables() throws SQLException;
}
