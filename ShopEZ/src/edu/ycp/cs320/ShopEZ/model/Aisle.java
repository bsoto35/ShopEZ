package edu.ycp.cs320.ShopEZ.model;

import java.util.HashMap;

public class Aisle {
	private int X1coordinate;
	private int Y1coordinate;
	private int X2coordinate;
	private int Y2coordinate;
	private HashMap<String, Aisle> aisleInfo;
	
	public Aisle() {
		
	}
	
	public void setX1(int number) {
		this.X1coordinate = number;
	}
	
	public void setY1(int number) {
		this.Y1coordinate = number;
	}
	
	public void setX2(int number) {
		this.X2coordinate = number;
	}
	
	public void setY2(int number) {
		this.Y2coordinate = number;
	}
	
	public int getX1() {
		return this.X1coordinate;
	}
	
	public int getY1() {
		return this.Y1coordinate;
	}
	
	public int getX2() {
		return this.X2coordinate;
	}
	
	public int getY2() {
		return this.Y2coordinate;
	}
	
	public void setAisleInfo(String aisleNameAndNumber, Aisle description) {
		this.aisleInfo.put(aisleNameAndNumber, description);
	}
	
}
