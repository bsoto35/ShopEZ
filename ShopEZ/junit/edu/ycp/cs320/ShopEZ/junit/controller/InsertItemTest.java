 package edu.ycp.cs320.ShopEZ.junit.controller;
 

 import static org.junit.Assert.*;
 
 import org.junit.Before;
 import org.junit.Test;
 

 import edu.ycp.cs320.ShopEZ.controller.ShopezeViewController;
 import edu.ycp.cs320.ShopEZ.model.ShopezeModel;
 import edu.ycp.cs320.ShopEZ.model.Account;
 import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
 
 public class InsertItemTest {

 	private  ShopezeModel model;
 	private ShopezeViewController controller;
 	private InsertItemController iteminsert;
 	private Account account;
 	
 	@Before
 	public void setUp() {
 		model = new ShopezeModel();
 		controller = new ShopezeViewController();
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
 }