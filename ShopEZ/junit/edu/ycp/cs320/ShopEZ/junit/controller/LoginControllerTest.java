 package edu.ycp.cs320.ShopEZ.junit.controller;
 

 import static org.junit.Assert.*;
 
 import org.junit.Before;
 import org.junit.Test;
 

 import edu.ycp.cs320.ShopEZ.controller.ShopezeViewController;
 import edu.ycp.cs320.ShopEZ.model.ShopezeModel;
 
 public class LoginControllerTest {

 	private  ShopezeModel model;
 	private ShopezeViewController controller;
 	
 	@Before
 	public void setUp() {
 		model = new ShopezeModel();
 		controller = new ShopezeViewController();
 		
 	}
 	
 }