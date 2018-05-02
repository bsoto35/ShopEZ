package edu.ycp.cs320.ShopEZ.model;

import java.util.HashMap;

public class Route {
	private int distance;
	private GroceryList currentList;
	private Location startLocation;
	private Location checkoutLocation;
	private HashMap<Item, Integer> routeOrder;
	
	public Route() {
		
	}
	
	public void setGroceryList(GroceryList items) {
		this.currentList = items;
	}
	
	public GroceryList getGroceryList() {
		return this.currentList;
	}
	public void setDistance(int dist){
		this.distance = dist;
	}
	public int getDistance() {
		return this.distance;
	}
	
	public void setStartLocation(Location start) {
		this.startLocation = start;
	}
	
	public Location getStartLocation() {
		return this.startLocation;
	}
	
	public void setCheckoutLocation(Location checkout) {
		this.checkoutLocation = checkout;
	}
	
	public Location getCheckoutLocation() {
		return this.checkoutLocation;
	}
	
	public void setRouteOrder(Item name, int order) {
		this.routeOrder.put(name, order);
	}
	
	public void computeDistance(Location start, Location end) {
		
	}
	
	
	
}
