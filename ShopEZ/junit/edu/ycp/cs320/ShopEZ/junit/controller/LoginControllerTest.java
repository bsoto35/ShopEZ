 package edu.ycp.cs320.ShopEZ.junit.controller;
 

 import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
 import org.junit.Test;
 

 import edu.ycp.cs320.ShopEZ.controller.ShopezeViewController;
 import edu.ycp.cs320.ShopEZ.model.ShopezeModel;
 import edu.ycp.cs320.ShopEZ.controller.LoginController;
 import edu.ycp.cs320.ShopEZ.model.Account;

 
 public class LoginControllerTest {

 	private  ShopezeModel model;
 	private ShopezeViewController controller;
 	private LoginController logCon;
 	private Account account;
 	
 	@Before
 	public void setUp() {
 		model = new ShopezeModel();
 		controller = new ShopezeViewController();
 		logCon = new LoginController();
 		account = new Account();
 	}
 	
 	@Test
 	public void logingTest() throws SQLException{
 		logCon.setAccount(account);
 		account.setAccountID(4);
 		account.setPassword("Wallace");
 		account.setUsername("dhenry5");
 		assertEquals(logCon.getAccount(), account);
 		assertTrue(logCon.verifyAccount("dhenry5", "Wallace"));
 		assertEquals(logCon.getAccountbyUser("dhenry5"), account);
 	}
 }