package edu.ycp.cs320.ShopEZ.controller;

import java.sql.SQLException;

import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DatabaseProvider;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;
import edu.ycp.cs320.ShopEZ.persist.IDatabase;

public class InsertItemController {

	private IDatabase db;

	public InsertItemController() {
		
		// creating DB instance here
		DatabaseProvider.setInstance(db);
		db = DatabaseProvider.getInstance();		
	}

	public boolean insertItemIntoLibrary(Item item_name) {
		boolean result = false;
		// insert new book (and possibly new author) into DB
		String item_id = null;
		try { 
			item_id = db.insertItemIntoItemsTable(item_name.getItemName(), item_name.getItemPrice(), item_name.getItemLocationX(), item_name.getItemLocationY());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// check if the insertion succeeded
		if (item_id.equals("Complete")){
			
			System.out.println("New item (ID: " + item_name.getItemID() + ") successfully added item to Items table: <" + item_name.getItemName() + ">");
			
			return true;
		}
		else
		{
			System.out.println("Failed to insert new item (ID: " + item_name.getItemID() + ") into Items table: <" + item_name.getItemName() + ">");
			
			return result;
		}
	}
	
	public String insertItemIntoItemsTable(String name, double price, int x, int y, int quantity) throws SQLException {
		// TODO Auto-generated method stub
		String result = "incomplete";
		
		
		
		return result;
	}
}
