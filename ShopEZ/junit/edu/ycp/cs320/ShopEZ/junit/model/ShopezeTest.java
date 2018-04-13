package edu.ycp.cs320.ShopEZ.junit.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.sqldemo.Shopeze.model.Guest;
import edu.ycp.cs320.sqldemo.lab02.model.Account;
import edu.ycp.cs320.sqldemo.lab02.model.Aisle;
import edu.ycp.cs320.sqldemo.lab02.model.GroceryList;
import edu.ycp.cs320.sqldemo.lab02.model.Item;
import edu.ycp.cs320.sqldemo.lab02.model.Location;
import edu.ycp.cs320.sqldemo.lab02.model.Route;

public class ShopezeTest {
	private Account account;
	private Aisle aisle;
	private GroceryList list;
	private Guest guest;
	private Item item;
	private Location locate;
	private Route route;
	
	@Before
	public void setUp() {//set up models
		account = new Account(); 
		aisle = new Aisle();
		list = new GroceryList();
		item = new Item();
		locate = new Location();
		route = new Route();
		guest = new Guest();
	}
	
	@Test
	public void testAisle() {//test isle returns 
		aisle.setX1(1);//set test cases
		aisle.setX2(1);
		aisle.setY1(1);
		aisle.setY2(1);
		assertEquals(aisle.getX1(), 1);//test that the correct numebrs are returned
		assertEquals(aisle.getX2(), 1);
		assertEquals(aisle.getY1(), 1);
		assertEquals(aisle.getY2(), 1);
		assertNotEquals(aisle.getX1(), 2);
		assertNotEquals(aisle.getX2(), 2);
		assertNotEquals(aisle.getY1(), 2);
		assertNotEquals(aisle.getY2(), 2);
		
	}
	@Test
	public void testUsernameConfirmation() {//awaiting completion
		
	}
	@Test
	public void testItem(){
		Location tmp = new Location();
		item.setItemInfo("test" , 1.5, 3,  tmp);//create temp item
		assertEquals(item.getPrice("test"), 1.5);
		assertEquals(item.getQuantity("test"), 3);
		assertTrue(item.containsItem("test"));
		item.removeItem(item);
		
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
	}
	@Test
	public void testCheckGuest(){
		assertEquals(guest.getPassword(), "Guest"); //test if password is recognized
		assertNotEquals(guest.getPassword() , "Guest");
		assertEquals(guest.getEmail(), "Guest"); //test if email is recognized.
		
	}
}


