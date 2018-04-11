package edu.ycp.cs320.ShopEZ.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.ycp.cs320.ShopEZ.model.Account;
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
				accountList.add(account);
			}
			return accountList;
		} finally {
			readAccounts.close();
		}
	}
}
