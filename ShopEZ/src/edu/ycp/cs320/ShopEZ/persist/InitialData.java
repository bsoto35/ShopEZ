package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.History;
import edu.ycp.cs320.ShopEZ.model.Item;

public class InitialData {
	public static List<Item> getItems() throws IOException {
		List<Item> itemList = new ArrayList<Item>();
		ReadCSV readItems = new ReadCSV("items.csv");
		try {
			// auto-generated primary key for authors table
			Integer itemId = 1;
			while (true) {
				List<String> tuple = readItems.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				Item item = new Item();
				item.setItemID(itemId++);				
				item.setItemName(i.next());
				item.setItemPrice(Double.parseDouble(i.next()));
				item.setItemLocationX(Integer.parseInt(i.next()));
				item.setItemLocationY(Integer.parseInt(i.next()));
				itemList.add(item);
			}
			return itemList;
		} finally {
			readItems.close();
		}
	}

	public static List<Account> getAccounts() throws IOException {
		List<Account> accountList = new ArrayList<Account>();
		ReadCSV readAccounts = new ReadCSV("accounts.csv");
		try {
			// auto-generated primary key for books table
			Integer accountId = 1;
			while (true) {
				List<String> tuple = readAccounts.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				Account account = new Account();
				account.setAccountID(accountId++);
				account.setUsername(i.next());
				account.setPassword(i.next());
				account.setHistoryListID(Integer.parseInt(i.next()));
				accountList.add(account);
			}
			return accountList;
		} finally {
			readAccounts.close();
		}
	}

	public static List<History> getHistory() throws IOException{
		List<History> historyList = new ArrayList<History>();
		ReadCSV readHistory = new ReadCSV("history.csv");
		try {
			// auto-generated primary key for books table
			Integer historyId = 1;
			while (true) {
				List<String> tuple = readHistory.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				History history = new History();
				history.setHistoryID(historyId++);
				history.setAccountID(Integer.parseInt(i.next()));
				history.setGroceryListID(Integer.parseInt(i.next()));
				historyList.add(history);
			}
			return historyList;
		} finally {
			readHistory.close();
		}
	}

	public static List<GroceryList> getGroceryLists() throws IOException{
		List<GroceryList> list = new ArrayList<GroceryList>();
		ReadCSV readGroceryLists = new ReadCSV("groceryLists.csv");
		try {
			// auto-generated primary key for books table
			Integer groceryListId = 1;
			while (true) {
				List<String> tuple = readGroceryLists.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				GroceryList groceryList = new GroceryList();
				groceryList.setGroceryListID(groceryListId++);
				groceryList.setAccountID(Integer.parseInt(i.next()));
				groceryList.setHistoryID(Integer.parseInt(i.next()));
				groceryList.setGroceryListID(Integer.parseInt(i.next()));
				groceryList.setListName(i.next());
				while (i.hasNext()) {
					groceryList.addToCurrentList(i.next());
				}
				list.add(groceryList);
			}
			return list;
		} finally {
			readGroceryLists.close();
		}
	}
}
