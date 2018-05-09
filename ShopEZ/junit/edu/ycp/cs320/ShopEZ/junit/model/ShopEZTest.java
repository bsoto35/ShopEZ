
package edu.ycp.cs320.ShopEZ.junit.model;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.Location;
import edu.ycp.cs320.ShopEZ.model.Route;
import edu.ycp.cs320.ShopEZ.model.Node;
import edu.ycp.cs320.ShopEZ.model.Graph;

public class ShopEZTest {
	private Account account;
	
	private GroceryList list;
	private Item item;
	private Route route;
	private Location location;
	private Graph graph;
	private Node node;
	
	@Before
	public void setUp() throws SQLException {//set up models
		account = new Account(); 
		
		list = new GroceryList();
		item = new Item();
		location = new Location();
		route = new Route();
		node = new Node();
		graph = new Graph();
		
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
		assertEquals(1.5, item.getItemPrice(), 0.01);
		assertEquals(0, item.getItemID());
		assertEquals("Test", item.getItemName());
		assertEquals(1, item.getItemLocationX());
		assertEquals(2, item.getItemLocationY());

	}

	@Test
	public void testGroceryListmethod() {//test the grocery list model
		//create initial values
		list.insertItems(1, 1);;
		list.setAccountID(1);
		//check to see if the correct values are returned
		assertEquals(1, list.getAccountID());
	}

	@Test
	public void testAccount(){//test the account model
		//set initial values
		account.setAccountID(0);
		account.setPassword("password");
		account.setUsername("username");
		//check to make sure the correct values are returned
		assertEquals(0, account.getAccountID());
		assertEquals("password", account.getPassword());
		assertEquals("username", account.getUsername());

	}

	@Test
	public void testLocation(){//test for the location model
		//set initial variables
		
		location.setX(1);
		location.setY(2);
		//check that the correct values are returned
		assertEquals(1, location.getX());
		assertEquals(2, location.getY());
	}

	@Test
	public void testRoute(){//tests for the route model
		//set initial variables
		Location checkout = location;
		Location start = location;
		//check that starting data is returned
		route.setCheckoutLocation(checkout);
		//add multiple items into the list and set order
		route.setDistance(4);
		route.setStartLocation(start);
		//check that the information returned is correct
		assertEquals(checkout, route.getCheckoutLocation());
		assertEquals(4, route.getDistance());
		assertEquals(list, route.getGroceryList());
		assertEquals(start, route.getStartLocation());
	}

	@Test
	public void testNode(){//tests for the node model
		node.setName("test");
		assertEquals(node.getName(), "test");
		LinkedList<Node> shortestPath2 = null;
		node.setShortestPath(shortestPath2);
		node.setDistance(3);
		assertEquals(shortestPath2, node.getShortestPath());
		assertEquals(3, node.getDistance(), 0.0001);
		node.addToItemIdsList(1);
		node.addToItemIdsList(2);
		assertTrue(node.getItemIds().contains(1));
		assertTrue(node.getItemIds().contains(2));
		assertFalse(node.getItemIds().contains(5));
	}

	@Test
	public void testGraph(){
		graph.addNode(node);
		assertTrue(graph.getGraphNodes().contains(node));
	}
}