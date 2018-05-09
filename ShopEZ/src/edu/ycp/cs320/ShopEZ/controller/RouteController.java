package edu.ycp.cs320.ShopEZ.controller;

import java.sql.SQLException;
import edu.ycp.cs320.ShopEZ.model.Graph;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Route;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class RouteController {
	private DerbyDatabase db= new DerbyDatabase();
	private Route route = new Route(); 
	private Graph graph = new Graph();
	private GroceryList groceries=new GroceryList(); 
	
}
