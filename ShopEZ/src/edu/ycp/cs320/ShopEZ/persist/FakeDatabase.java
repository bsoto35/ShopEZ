package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Pair;

public class FakeDatabase implements IDatabase {

	private List<Account> accountList;
	private List<Item> itemList;
	private List<GroceryList> historyList;

	// Fake database constructor - initializes the DB
	// the DB only consists for a List of Authors and a List of Books
	public FakeDatabase() {
		accountList = new ArrayList<Account>();
		itemList = new ArrayList<Item>();
		historyList = new ArrayList<GroceryList>();

		// Add initial data
		readInitialData();

		//		System.out.println(accountList.size() + " authors");
		//		System.out.println(itemList.size() + " books");
	}

	// loads the initial data retrieved from the CSV files into the DB
	public void readInitialData() {
		try {
			accountList.addAll(InitialData.getAccounts());
			itemList.addAll(InitialData.getItems());
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't read initial data", e);
		}
	}

	// query that retrieves Account ID by Username and Password
	@Override
	public int findAccountIDbyUsernameAndPassword(String username, String password) {
		int result = -1;
		for (Account account : accountList) {
			if (account.getUsername().equals(username)) {
				if (account.getPassword().equals(password)) {
					result = account.getAccountID();
				}
			}
		}
		return result;
	}
	
	// query that retrieves Account by Account ID
	@Override
	public Account findAccountByAccountID(int id) throws SQLException {
		Account result = new Account();
		for (Account account : accountList) {
			if (account.getAccountID() == id) {
				result = account;
			}
		}
		return result;
	}

	// query that retrieves all Books, for the Account's last name
	@Override
	public Set<String> getGroceryListHistoryByAccountID(int id)
	{
		// search through table of Accounts
		for (Account account : accountList) {
			if (account.getAccountID() == (id)) {
				return account.getHistoryList();
			}
		}
		return null;
	}


	// query that retrieves all items from DB
	@Override
	public ArrayList<Item> findAllItems() {
		ArrayList<Item> result = new ArrayList<Item>();
		for (Item item : itemList) {
			result.add(item);
		}
		return result;
	}


	// query that retrieves all Accounts from DB
	@Override
	public ArrayList<Account> findAllAccounts() {
		ArrayList<Account> result = new ArrayList<Account>();
		for (Account account : accountList) {
			result.add(account);
		}
		return result;
	}

	@Override
	public String insertItemIntoItemsTable(String name, double price, int x, int y)
	{
		String result = "incomplete";
		int itemId = -1;
		
		for (Item item : itemList) {
			if (item.getItemName().equals(name)) {
				if (item.getItemPrice() == price) {
					if (item.getItemLocationX() == x) {
						if (item.getItemLocationY() == y) {
							System.out.println("Item already exists in items table!");
							itemId = item.getItemID();
						}
					}
				}
			}
		}

		if (itemId < 0) {
			// set item id to size of items list + 1 (before adding item)
			itemId = itemList.size() + 1;
			
			int ID = itemId++;

			// add new Account to Authors list
			Item newItem = new Item();			
			newItem.setItemID(ID);
			newItem.setItemName(name);
			newItem.setItemPrice(price);
			newItem.setItemLocationX(x);
			newItem.setItemLocationY(y);
			itemList.add(newItem);

			System.out.println("New item (ID: " + ID + ") " + "added to Items table: <" + name + ">");
			result = "complete";
		}
		return result;
	}

	//not implemented in FakeDB
	public List<Account> removeItemByItemName(final String name) {
		List<Account> accounts = new ArrayList<Account>();

		return accounts;
	}

	@Override
	public boolean verifyAccountFromAccountsTableByUsernameAndPassword(String username, String password) {
		boolean result = false;
		for (Account account : accountList) {
			if (account.getUsername().equals(username)) {
				if (account.getPassword().equals(password)) {
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public double findItemPriceByItemName(String name) {
		double result = 0.00;
		for (Item item : itemList) {
			if (item.getItemName().equals(name)) {
				result = item.getItemPrice();
			}
		}
		return result;
	}
}
