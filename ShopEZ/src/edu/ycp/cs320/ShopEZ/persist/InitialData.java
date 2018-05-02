package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;

public class InitialData {
	public static List<Item> getItems() throws IOException {
		List<Item> itemList = new ArrayList<Item>();
		ReadCSV readItems = new ReadCSV("items.csv");
		try {
			// auto-generated primary key for authors table
			int itemId = 1;
			while (true) {
				List<String> tuple = readItems.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				Item item = new Item();
				item.setItemID(itemId++);
				System.out.println(item.getItemID());
				item.setItemName(i.next());
				System.out.println(item.getItemName());
				item.setItemPrice(Double.parseDouble(i.next()));
				System.out.println(item.getItemPrice());
				item.setItemLocationX(Integer.parseInt(i.next()));
				System.out.println(item.getItemLocationX());
				item.setItemLocationY(Integer.parseInt(i.next()));
				System.out.println(item.getItemLocationY());
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
				System.out.println(account.getAccountID());
				account.setUsername(i.next());
				System.out.println(account.getUsername());
				account.setPassword(i.next());
				System.out.println(account.getPassword());
				accountList.add(account);
			}
			return accountList;
		} finally {
			readAccounts.close();
		}
	}

	public static List<GroceryList> getGroceryLists() throws IOException{
		List<GroceryList> list = new ArrayList<GroceryList>();
		ReadCSV readGroceryLists = new ReadCSV("groceryLists.csv");
		try {
			while (true) {
				List<String> tuple = readGroceryLists.next();
				if (tuple == null) {
					break;
				}
				Iterator<String> i = tuple.iterator();
				GroceryList groceryList = new GroceryList();
				groceryList.setAccountID(Integer.parseInt(i.next()));
				System.out.println(groceryList.getAccountID());
				int id = Integer.parseInt(i.next());
				groceryList.getList().add(id);
				System.out.println(groceryList.getList().get(id));
				list.add(groceryList);
			}
			return list;
		} finally {
			readGroceryLists.close();
		}
	}
}
