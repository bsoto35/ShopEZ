package edu.ycp.cs320.ShopEZ.junit.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.ShopEZ.controller.ShopezeController;
import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
import edu.ycp.cs320.ShopEZ.controller.LoginController;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.model.ShopezeModel;

public class ShopezeControllerTest {
	private  ShopezeModel model;
	private ShopezeController controller;
	private LoginController login;
	private InsertItemController insert;
	@Before
	public void setUp() {
		model = new ShopezeModel();
		controller = new ShopezeController();
		login = new LoginController(null);
		insert = new InsertItemController();
		controller.setModel(model);
	}
	
	@Test
	public void testInsertItemIntoLibrary(){
		//Item test = new Item();
		//assertTrue(insert.insertItemIntoLibrary(test));
		//assertEquals(insert.insertItemIntoItemsTable("test", 1.5, 1, 2, 3), "Item Inserted");
		}
	@Test
	public void TestLoginController(){
		assertTrue(login.checkUserName("student"));
		assertFalse(login.checkUserName("incorrect"));
		assertTrue(login.validateCredentials("student", "ycp"));
		assertFalse(login.validateCredentials("fakeName", "fakePassword"));
	}
}
