 package edu.ycp.cs320.ShopEZ.junit.controller;
 

 import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
 import org.junit.Test;
 

 import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
 
 public class InsertItemTest {

 	
 	private InsertItemController iteminsert;
 	private Account account;
 	
 	@Before
 	public void setUp() {
 		
 		iteminsert = new InsertItemController();
 		account = new Account();
 	}
 	
 	@Test
 	public void testAccount(){
 		account.setAccountID(0);
 		iteminsert.setAccount(account);
 		assertEquals(iteminsert.getAccount(), account);
 		assertEquals(iteminsert.getAccountId(), 0, 0.0);
 		iteminsert.setTotalPrice(5);
 		assertEquals(iteminsert.getTotalPrice(), 5, 0.1);
 		
 	}
 	
 	@Test
 	public void testGroceryList() throws SQLException{
 		Item apple = new Item();
 		apple.setItemName("apple");
 		iteminsert.addItem(4, apple, 1);
 		assertEquals(iteminsert.getItemNameatIndex(0), "apple");
 		assertEquals(iteminsert.getListlength(), 1);
 		assertTrue(iteminsert.getIdList().contains(apple));
 	}
 }