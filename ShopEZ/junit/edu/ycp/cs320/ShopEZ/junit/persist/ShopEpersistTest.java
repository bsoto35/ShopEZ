
package edu.ycp.cs320.ShopEZ.junit.persist;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Aisle;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Location;
import edu.ycp.cs320.ShopEZ.model.Route;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;
import edu.ycp.cs320.ShopEZ.model.History;
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
		derby.insertItemIntoItemsTable("tmp", 1.5, 0, 0);
		assertEquals(derby.findItemByItemName("tmp"), tmp);
		assertEquals(derby.findItemByItemName("tmp").getItemLocationX(), 0);
		assertEquals(derby.findItemByItemName("tmp").getItemLocationY(), 1);
		assertEquals(derby.findItemByItemName("tmp").getItemPrice(), 1.5, 0.01);
		derby.updateItemLocationByItemNameAndXYCoords("tmp", 2, 3);
		assertEquals(derby.findItemByItemName("tmp").getItemLocationX(), 2);
		assertEquals(derby.findItemByItemName("tmp").getItemLocationY(), 3);
		assertEquals(derby.findItemPriceByItemName("tmp"), 1.5, 0.1);
		derby.removeItemFromItemsTable(tmp);
	}

	@Test
	public void testAccounts() throws SQLException{
		derby.addAccountIntoAccountsTable("test", "pass");
		assertEquals(derby.findAccountByUsername("test").getPassword(), "pass");
		assertTrue(derby.verifyAccountFromAccountsTableByUsernameAndPassword("test", "pass"));
		assertEquals(derby.findAccountByAccountID(4).getUsername(), "dhenry5");
		assertEquals(derby.findAccountIDbyUsernameAndPassword("dhenry5", "Wallace"), 3);
		Account tmp = new Account();
		tmp.setUsername("test");
		tmp.setPassword("pass");
		assertTrue(derby.findAllAccounts().contains(tmp));
	}

	@Test
	public void testGroceryList() throws SQLException{
		Item item = new Item();
		item.setItemID(30);
		item.setItemLocationX(1);
		item.setItemLocationY(1);
		item.setItemName("something");
		item.setItemPrice(1.5);
		assertTrue(derby.insertItemIntoGroceryListTable(1, item, 2)) ;
		assertEquals(derby.findAllItemsForAccount(1), item);
		derby.removeItemFromGroceryListTable(1, item, 2);
		assertNotEquals(derby.findAllItemsForAccount(1), item);
		derby.insertItemIntoGroceryListTable(1, item, 2);
		assertTrue(derby.clearGroceryListForAccount(1));
		
	}
	
	@Test
	public void testLocation(){
		
	}
}

