package edu.ycp.cs320.ShopEZ.model;

import edu.ycp.cs320.ShopEZ.model.Location;

public class Item {
	private int item_id;
	private String item_name;
	private double Price;
	private Location itemLocation = new Location();
	private int quantity;
	
	public Item() {
		quantity=1;
	}
	
	public void setItemName(String i) {
		this.item_name = i;
	}
	
	public String getItemName() {
		System.out.println(""+item_name);
		return item_name;
	}
	
	public void setItemID(int id) {
		this.item_id = id;
	}
	
	public int getItemID() {
		return this.item_id;
	}
	
	public void setItemPrice(double p) {
		this.Price = p;
	}
	
	public double getItemPrice() {
		return this.Price;
	}
	
	public void setItemLocationX(int here) {
		this.itemLocation.setX(here);
	}
	
	public void setItemLocationY(int here) {
		this.itemLocation.setY(here);
	}
	
	public int getItemLocationX() {
		return this.itemLocation.getX();
	} 
	
	public int getItemLocationY() {
		return this.itemLocation.getY();
	} 
	
	public void setQuantity(int quantity) {
		this.quantity=quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
}
