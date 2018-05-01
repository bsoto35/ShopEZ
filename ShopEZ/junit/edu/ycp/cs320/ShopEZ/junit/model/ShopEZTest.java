
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
import edu.ycp.cs320.ShopEZ.model.History;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class ShopEZTest {
	private Account account;
	private Aisle aisle;
	private GroceryList list;
	private Item item;
	private Route route;
	private Location location;
	private History hist;
	
	
	@Before
	public void setUp() throws SQLException {//set up models
		account = new Account(); 
		aisle = new Aisle();
		list = new GroceryList();
		item = new Item();
		location = new Location();
		route = new Route();
		hist = new History();
		
	}

	@Test
	public void testAisle() {//test isle returns 
		//set test values
		aisle.setX1(1);
		aisle.setX2(1);
		aisle.setY1(1);
		aisle.setY2(1);
		//test that the correct numbers are returned
		assertEquals(aisle.getX1(), 1);
		assertEquals(aisle.getX2(), 1);
		assertEquals(aisle.getY1(), 1);
		assertEquals(aisle.getY2(), 1);
		assertNotEquals(aisle.getX1(), 2);
		assertNotEquals(aisle.getX2(), 2);
		assertNotEquals(aisle.getY1(), 2);
		assertNotEquals(aisle.getY2(), 2);

	}

	@Test
	public void testItem(){//testing the item model
		//creating initial values for the test item
		item.setItemID(0);
		item.setItemLocationX(1);
		item.setItemLocationY(2);
		item.setItemName("Test");
		item.setItemPrice(1.5);
		//check to see if the correct values re returned when fetched
		assertEquals(item.getItemPrice(), 1.5, 0.01);
		assertEquals(item.getItemID(), 0);
		assertEquals(item.getItemName(), "Test");
		assertEquals(item.getItemLocationX(), 1);
		assertEquals(item.getItemLocationY(), 2);

	}

	@Test
	public void testGroceryListmethod() {//test the grocery list model
		//create initial values
		list.setItemID(0);
		list.setAccountID(1);
		//check to see if the correct values are returned
		assertEquals(list.getItemID(), 0);
		assertEquals(list.getAccountID(), 1);
	}

	@Test
	public void testAccount(){//test the account model
		//set initial values
		account.setAccountID(0);
		account.setPassword("password");
		account.setUsername("username");
		//check to make sure the correct values are returned
		assertEquals(account.getAccountID(), 0);
		assertEquals(account.getPassword(), "password");
		assertEquals(account.getUsername(), "username");

	}



	@Test
	public void testLocation(){//test for the location model
		//set initial variables
		location.setAisleInfo();
		location.setCurrentAisle(aisle);
		location.setX(1);
		location.setY(2);
		//check that the correct values are returned
		assertEquals(location.getX(), 1);
		assertEquals(location.getY(), 2);
		assertEquals(location.getCurrentAisle(), aisle);
	}
/*
	@Test
	public void testRoute(){//tests for the route model
		//set initial variables
		Location checkout = location;
		Location start = location;
		//check that starting data is returned
		route.setCheckoutLocation(checkout);
		//add multiple items into the list and set order
		route.setGroceryList(list);
		Item first = item;
		route.setRouteOrder(first, 0);
		Item second = item;
		route.setRouteOrder(second, 1);
		Item third = item;
		route.setRouteOrder(third, 2);
		route.setStartLocation(start);
		//check that the information returned is correct
		assertEquals(route.getCheckoutLocation(), checkout);
		assertEquals(route.getDistance(), 4);
		assertEquals(route.getGroceryList(), list);
		assertEquals(route.getStartLocation(), start);
	}
*/
}