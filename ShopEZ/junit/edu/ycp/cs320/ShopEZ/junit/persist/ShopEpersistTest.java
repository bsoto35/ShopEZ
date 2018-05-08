
package edu.ycp.cs320.ShopEZ.junit.persist;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Route;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class ShopEpersistTest {
	
	private DerbyDatabase derby;
	
	@Before
	public void setUp() throws SQLException {//set up models
	
		derby = new DerbyDatabase();
		derby.dropTables();
		derby.createTables();
		derby.loadInitialData();
	
	}
@Test
	public void testItemList() throws SQLException{//tests for the Item tables inputs and outputs
		//setting up the test item
		Item tmp = new Item();
		tmp.setItemLocationX(0);
		tmp.setItemLocationY(1);
		tmp.setItemName("tmp");
		tmp.setItemPrice(1.5);
		assertTrue(derby.insertItemIntoItemsTable("tmp", 1.5, 0, 1));
		Item tmp2 = derby.findItemByItemName("tmp");
		assertEquals(tmp2.getItemLocationX(), 0);
		assertEquals(tmp2.getItemLocationY(), 1);
		assertEquals(tmp2.getItemPrice(), 1.5, 0.01);
		derby.updateItemLocationByItemNameAndXYCoords("tmp", 2, 3);
		tmp = derby.findItemByItemName("tmp");
		assertEquals(tmp.getItemLocationX(), 2);
		assertEquals(tmp.getItemLocationY(), 3);
		assertEquals(derby.findItemPriceByItemName("tmp"), 1.5, 0.1);
		assertTrue(derby.removeItemFromItemsTable(tmp));
	}

	@Test
	public void testAccounts() throws SQLException{
		derby.addAccountIntoAccountsTable("test", "pass");
		assertEquals(derby.findAccountByUsername("test").getPassword(), "pass");
		assertTrue(derby.verifyAccountFromAccountsTableByUsernameAndPassword("test", "pass"));
		assertEquals(derby.findAccountByAccountID(4).getUsername(), "dhenry");
		assertEquals(derby.findAccountIDbyUsernameAndPassword("dhenry", "Wallace"), 4);
		Account tmp = new Account();
		tmp.setUsername("test");
		tmp.setPassword("pass");
		boolean passFail = false;
		for (Account acct : derby.findAllAccounts()){
			if (acct.getUsername().equals(tmp.getUsername())){
				passFail = true;
			}
		}
		assertTrue(passFail);
	}

	@Test
	public void testGroceryList() throws SQLException{
		Item item = new Item();
		item.setItemID(1);
		item.setItemLocationX(22);
		item.setItemLocationY(17);
		item.setItemName("apple");
		item.setItemPrice(1.5);
		assertTrue(derby.insertItemIntoGroceryListTable(3, item, 2));
		assertTrue(derby.findAllItemsForAccount(3).contains(item));
		derby.removeItemFromGroceryListTable(3, item, 2);
		assertNotEquals(derby.findAllItemsForAccount(1), item);
		derby.insertItemIntoGroceryListTable(1, item, 2);
		assertTrue(derby.clearGroceryListForAccount(1));
		
	}
	
	@Test
	public void testrouteCalc() throws SQLException{
		Item tmp = derby.findItemByItemName("apple");
		derby.insertItemIntoGroceryListTable(4, tmp, 1);
		tmp = derby.findItemByItemName("grapes");
		derby.insertItemIntoGroceryListTable(4, tmp, 1);
		List<Item> test = derby.loadGraphNodesFromGroceryListItems(4);
		assertEquals(test.get(0).getItemName(), "apples");
		assertEquals(test.get(1).getItemName(), "grapes");
	}
}

