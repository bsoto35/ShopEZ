package edu.ycp.cs320.ShopEZ.model;

public class Location {
	private int coordinateX;
	private int coordinateY;
	private Aisle currentAisle;
	
	public Location() {
		
	}
	
	public void setX(int number) {
		this.coordinateX = number;
	}
	
	public void setY(int number) {
		this.coordinateY = number;
	}
	
	public void setCurrentAisle(Aisle current) {
		this.currentAisle = current;
	}
	
	public Aisle getCurrentAisle() {
		return this.currentAisle;
	}
	
	public int getX() {
		return coordinateX;
	}
	
	public int getY() {
		return coordinateY;
	}
	
	public void setAisleInfo() {
		
	}
	
}
