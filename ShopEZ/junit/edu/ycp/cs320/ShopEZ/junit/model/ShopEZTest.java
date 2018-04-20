
package edu.ycp.cs320.ShopEZ.junit.model;

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
import edu.ycp.cs320.ShopEZ.model.Pair;
import edu.ycp.cs320.ShopEZ.model.History;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class ShopEZTest {
	private Account account;
	private Aisle aisle;
	private GroceryList list;
	private Item item;
 	private Route route;
 	private Pair pair;
 	private Location location;
 	private History hist;
 	private DerbyDatabase derby;
 	@Before
 	public void setUp() {//set up models
 		account = new Account(); 
 		aisle = new Aisle();
 		list = new GroceryList();
 		item = new Item();
 		location = new Location();
 		route = new Route();
 		hist = new History();
 		derby = new DerbyDatabase();
 	}
 	
 	@Test
 	public void testAisle() {//test isle returns 
 		aisle.setX1(1);//set test cases
 		aisle.setX2(1);
 		aisle.setY1(1);
 		aisle.setY2(1);
 		assertEquals(aisle.getX1(), 1);//test that the correct numbers are returned
 		assertEquals(aisle.getX2(), 1);
 		assertEquals(aisle.getY1(), 1);
 		assertEquals(aisle.getY2(), 1);
 		assertNotEquals(aisle.getX1(), 2);
 		assertNotEquals(aisle.getX2(), 2);
 		assertNotEquals(aisle.getY1(), 2);
 		assertNotEquals(aisle.getY2(), 2);
 		
 	}
 	
 	@Test
 	public void testItem(){
 		item.setItemID(0);
 		item.setItemLocationX(1);
 		item.setItemLocationY(2);
 		item.setItemName("Test");
 		item.setItemPrice(1.5);
 		assertEquals(item.getItemPrice(), 1.5, 0.01);
 		assertEquals(item.getItemID(), 0);
 		assertEquals(item.getItemName(), "Test");
 		assertEquals(item.getItemLocationX(), 1);
 		assertEquals(item.getItemLocationY(), 2);
 		
 	}
 	
 	@Test
 	public void testGroceryListmethod() {
 		list.setItemID(0);
 		list.setAccountID(1);
 		assertEquals(list.getItemID(), 0);
 		assertEquals(list.getAccountID(), 1);
 	}
 	
 	@Test
 	public void testAccount(){
 		
 		account.setAccountID(0);
 		account.setPassword("password");
 		account.setUsername("username");
 		assertEquals(account.getAccountID(), 0);
 		assertEquals(account.getPassword(), "password");
 		assertEquals(account.getUsername(), "username");
 		
 	}
 	
 	@SuppressWarnings("unchecked")
	@Test
 	
	public void testPair(){
 		Object left = new Object();
		pair.setLeft(left);
 		Object right = new Object();
		pair.setRight(right);
 		assertEquals(pair.getLeft(), left);
 		assertEquals(pair.getRight(), right);
 	}
 	
 	@Test
 	public void testLocation(){
 		location.setAisleInfo();
 		location.setCurrentAisle(aisle);
 		location.setX(1);
 		location.setY(2);
 		assertEquals(location.getX(), 1);
 		assertEquals(location.getY(), 2);
 		assertEquals(location.getCurrentAisle(), aisle);
 	}
 	
 	@Test
 	public void testHistory(){
 		hist.setAccountID(0);
 		hist.setGroceryListID(1);
 		hist.setHistoryID(2);
 		assertTrue(false);
 	}
 
 	@Test
 	public void testRoute(){
 		Location checkout = location;
 		Location start = location;
 		route.setCheckoutLocation(checkout);
 		route.setGroceryList(list);
 		Item first = item;
		route.setRouteOrder(first, 0);
		Item second = item;
		route.setRouteOrder(second, 1);
		Item third = item;
		route.setRouteOrder(third, 2);
 		route.setStartLocation(start);
 		assertEquals(route.getCheckoutLocation(), checkout);
 		assertEquals(route.getDistance(), 4);
 		assertEquals(route.getGroceryList(), list);
 		assertEquals(route.getStartLocation(), start);
 	}
 	
 	@Test
	public void testItemList() throws SQLException{
	Item tmp = new Item();
	tmp.setItemLocationX(0);
	tmp.setItemLocationY(0);
	tmp.setItemName("tmp");
	tmp.setItemPrice(1.5);
	derby.insertItemIntoItemsTable("tmp", 1.5, 0, 0);
	assertEquals(derby.findItemByItemName("tmp"), tmp);
	assertEquals(derby.findItemByItemName("tmp").getItemLocationX(), 0);
	assertEquals(derby.findItemByItemName("tmp").getItemLocationY(), 0);
	assertEquals(derby.findItemByItemName("tmp").getItemPrice(), 1.5, 0.01);
	derby.removeItemFromItemsTable(tmp);
	}
	
	@Test
	public void testAccounts() throws SQLException{
		derby.addAccountIntoAccountsTable("test", "pass");
		assertEquals(derby.findAccountByUsername("test").getPassword(), "pass");
		assertTrue(derby.verifyAccountFromAccountsTableByUsernameAndPassword("test", "pass"));
		
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
}
 
 