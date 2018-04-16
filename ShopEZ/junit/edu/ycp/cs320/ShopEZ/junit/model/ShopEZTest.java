package edu.ycp.cs320.ShopEZ.junit.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Aisle;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Location;
import edu.ycp.cs320.ShopEZ.model.Route;
import edu.ycp.cs320.ShopEZ.model.Pair;
import edu.ycp.cs320.ShopEZ.model.Library;

public class ShopEZTest {
	private Account account;
	private Aisle aisle;
	private GroceryList list;
	private Item item;
	private Location locate;
	private Route route;
	private Pair pair;
	private Library lib;
	private Location location;
	
	
	@Before
	public void setUp() {//set up models
		account = new Account(); 
		aisle = new Aisle();
		list = new GroceryList();
		item = new Item();
		locate = new Location();
		route = new Route();
		
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
	
	@SuppressWarnings("deprecation")
	@Test
	public void testItem(){
		Item test = new Item();
		test.setItemID(0);
		test.setItemLocationX(1);
		test.setItemLocationY(1);
		test.setItemName("Test");
		test.setItemPrice(1.5);
		test.setItemQuantity(3);
		assertEquals(test.getItemPrice(), 1.5);
		assertEquals(test.getItemQuantity(), 3);
		assertEquals(test.getItemID(), 0);
		assertEquals(test.getItemName(), "Test");
		assertEquals(test.getItemLocationX(), 1);
		assertEquals(test.getItemLocationY(), 1);
		
	}
	@Test
	public void testGroceryList() {
		assertEquals(list.getTotalPrice(), 0, 0);//get starting value
		list.updatePrice(1.5);
		assertEquals(list.getTotalPrice(), 1.5, 0);//1 addition
		list.updatePrice(1.5);
		assertEquals(list.getTotalPrice(), 3.0, 0);//2 additions
		Item test = new Item();
		list.addItem(test);
		assertTrue(list.getList().contains(test));//check if fake item is added
		list.removeItem(test);
		assertFalse(list.getList().contains(test));//check to see if item is removed
	}
	@Test
	public void testAccount(){
		Account test = new Account();
		test.setAccountID(0);
		test.setPassword("password");
		test.setUsername("username");
		assertEquals(test.getAccountID(), 0);
		assertEquals(test.getPassword(), "password");
		assertEquals(test.getUsername(), "username");
		assertTrue(test.confirmAccount("username", "password"));
	}
	@Test
	public void testPair(){
		pair.setLeft(item);
		pair.setRight(account);
		assertEquals(pair.getLeft(), item);
		assertEquals(pair.getRight(), account);
	}
	@Test
	public void testLibrary(){
		assertTrue(lib.validateUserName("student"));
		assertFalse(lib.validateUserName("fakeName"));
		assertTrue(lib.validatePW("student","ycp"));
		assertFalse(lib.validatePW("faculty","ycp"));
		assertTrue(lib.validatePW("faculty","E&CS"));
		assertFalse(lib.validatePW("student","E&CS"));
		
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
}


