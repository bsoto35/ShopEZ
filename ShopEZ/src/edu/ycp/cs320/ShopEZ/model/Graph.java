package edu.ycp.cs320.ShopEZ.model;

import edu.ycp.cs320.ShopEZ.model.Node;
import edu.ycp.cs320.ShopEZ.model.Route;
import java.util.HashSet;
import java.util.Set;

public class Graph {
	 
    private Set<Node> graphNodes = new HashSet<>();
    private Route theRoute = new Route();
     
    public void addNode(Node nodeA) {
        graphNodes.add(nodeA);
    }

	public Set<Node> getGraphNodes() {
		return graphNodes;
	}

	public void setGraphNodes(Set<Node> graphNodes) {
		this.graphNodes = graphNodes;
	}

	public Route getTheRoute() {
		return theRoute;
	}

	public void addNodeToRoute(Node n) {
		this.theRoute.addNode(n);
	}
 
    // getters and setters 

}