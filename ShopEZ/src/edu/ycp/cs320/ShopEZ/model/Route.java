package edu.ycp.cs320.ShopEZ.model;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.awt.Color;
import javax.swing.JFrame;

public class Route extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[][] path = new int[720][540];
	private Set<Item> items = new HashSet<>();
	private Set<Node> graphNodes = new HashSet<>();


	public Route() {
		setTitle("Route");
		setSize(720, 540);
		setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setPath(int[][] route) {
		this.path = route;
	}

	public int[][] getPath(){
		return path;
	}

	public void addNode(Node n) {
		graphNodes.add(n);
	}

	public Set<Node> getGraphNodes() {
		return graphNodes;
	}

	public void setGraphNodes(Set<Node> graphNodes) {
		this.graphNodes = graphNodes;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		ArrayList<Node> arr = new ArrayList<Node>();

		int iter = 0;
		for (Node n : graphNodes) {
			if (n.getOrder() == iter++) {
				arr.add(n);
				if (n.getItemIds() != null) {
					path[n.getCoordinates().getX()][n.getCoordinates().getY()] = 9;
				}else {
					path[n.getCoordinates().getX()][n.getCoordinates().getY()] = 1;
				}
				if (n.getOrder() == graphNodes.size()) {
					path[n.getCoordinates().getX()][n.getCoordinates().getY()] = 5;
				}
			}
		}

		for(int row = 0; row < 540; row++) {
			for (int col = 0; col < 720; col++) {
				Color color = Color.WHITE;
				if (path[col][row]==1) {
					color = Color.CYAN;
				}
				else if (path[col][row]==9) {
					color = Color.RED;
				}
				else if (path[col][row]==5){
					color = Color.YELLOW;
				}
				if (col == 660 && row == 90) {
					color = Color.GREEN;
				}
				g.setColor(color);
				if (color != Color.WHITE) {
					g.drawOval(col-10, row+10, 20, 20);
				}

				for (int i = 0; i < arr.size()-1; i++) {
					int x1 = arr.get(i).getCoordinates().getX();
					int y1 = arr.get(i).getCoordinates().getY();
					int x2 = arr.get(i+1).getCoordinates().getX();
					int y2 = arr.get(i+1).getCoordinates().getY();
					g.setColor(Color.ORANGE);
					g.drawLine(x1, y1, x2, y2);
				}
			}
		}
	}

	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
	}




}
