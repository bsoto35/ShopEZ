package edu.ycp.cs320.ShopEZ.model;

import edu.ycp.cs320.ShopEZ.model.Node;
import java.util.HashSet;
import java.util.Set;

public class Graph {
	 
    private Set<Node> graphNodes = new HashSet<>();
     
    public void addNode(Node nodeA) {
        graphNodes.add(nodeA);
    }

	public Set<Node> getGraphNodes() {
		return graphNodes;
	}

	public void setGraphNodes(Set<Node> graphNodes) {
		this.graphNodes = graphNodes;
	}
 
    // getters and setters 

}