package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	// query that retrieves all Books, for the Account's last name
	@Override
	public List<Pair<Account, GroceryList>> findGroceryListHistoryByAccountID(int id)
	{
		// create list of <Account, Item> for returning result of query
		List<Pair<Account, GroceryList>> result = new ArrayList<Pair<Account, GroceryList>>();

		// search through table of Accounts
		for (Account account : accountList) {
			if (account.getAccountID() == (id)) {
				result.addAll(account, account.getList())
			}
		}
		return result;
	}


	// query that retrieves all Books, with their Authors, from DB
	@Override
	public List<Pair<Account, Item>> findAllBooksWithAuthors() {
		List<Pair<Account, Item>> result = new ArrayList<Pair<Account,Item>>();
		for (Item item : itemList) {
			Account account = findAuthorByAuthorId(item.getAuthorId());
			result.add(new Pair<Account, Item>(account, item));
		}
		return result;
	}


	// query that retrieves all Authors from DB
	@Override
	public List<Account> findAllAuthors() {
		List<Account> result = new ArrayList<Account>();
		for (Account account : accountList) {
			result.add(account);
		}
		return result;
	}


	// query that inserts a new Item, and possibly new Account, into Books and Authors lists
	// insertion requires that we maintain Item and Account id's
	// this can be a real PITA, if we intend to use the IDs to directly access the ArrayLists, since
	// deleting a Item/Account in the list would mean updating the ID's, since other list entries are likely to move to fill the space.
	// or we could mark Item/Account entries as deleted, and leave them open for reuse, but we could not delete an Account
	//    unless they have no Books in the Books table
	@Override
	public String insertItemIntoItemsTable(String name, double price, int x, int y)
	{
		int itemId = -1;

		if (itemId < 0) {
			// set author_id to size of Authors list + 1 (before adding Account)
			itemId = itemList.size() + 1;

			// add new Account to Authors list
			Item newItem = new Item();			
			newItem.setItemID(itemId++);
			newItem.setItemPrice(price);
			newItem.setItemLocationX(x);
			newItem.setItemLocationY(y);
			accountList.addAll(newItem);

			System.out.println("New item (ID: " + Id + ") " + "added to Items table: <" + name + ">");
		}

		// set book_id to size of Books list + 1 (before adding Item)
		bookId = itemList.size() + 1;

		// add new Item to Books list
		Item newBook = new Item();
		newBook.setBookId(bookId);
		newBook.setAuthorId(authorId);
		newBook.setTitle(itemName);
		newBook.setIsbn(locationX);
		newBook.setPublished(published);
		itemList.add(newBook);

		// return new Item Id
		return bookId;
	}

	//not implemented in FakeDB
	public List<Account> removeItemByItemName(final String name) {
		List<Account> accounts = new ArrayList<Account>();

		return accounts;
	}


	// query that retrieves an Account based on author_id
	private Account findAccountByAccountId(int accountId) {
		for (Account account : accountList) {
			if (account.getAccountID() == accountId) {
				return account;
			}
		}
		return null;
	}

	@Override
	public boolean verifyAccountFromAccountsTable(String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double findItemPriceByItemName(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}
}
