package edu.ycp.cs320.ShopEZ.model;

// model class for GuessingGame
// only the controller should be allowed to call the set methods
// the JSP will call the "get" and "is" methods implicitly
// when the JSP specifies game.min, that gets converted to
//    a call to model.getMin()
// when the JSP specifies if(game.done), that gets converted to
//    a call to model.isDone()
public class ShopezeModel {
	private GroceryList shopList;
	private int mapWidth;
	private int mapHeight;
	
	public ShopezeModel() {
		
	}
	
	public void setGroceryList(GroceryList list) {
		this.shopList = list;
	}
	
	public void setMapWidth(int width) {
		this.mapWidth = width;
	}
	
	public void setMapHeight(int length) {
		this.mapHeight = length;
	}
	
	
	
	public GroceryList getCurrentGroceryList() {
		return this.shopList;
	}
	
	public int getMapWidth() {
		return this.mapWidth;
	}
	
	public int getMapHeight() {
		return this.mapHeight;
	}
	
	
	
	public void populateList() {
		
	}
}
