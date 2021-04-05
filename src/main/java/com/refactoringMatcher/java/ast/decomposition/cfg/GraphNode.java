package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.LinkedHashSet;
import java.util.Set;

public class GraphNode {
	private static int nodeNum = 0;
	protected int id;
	protected String value = "";
	protected Set<GraphEdge> incomingEdges;
	protected Set<GraphEdge> outgoingEdges;

	public GraphNode(String value, int id) {
		this.value = value;
		this.id = id;
		this.incomingEdges = new LinkedHashSet<GraphEdge>();
		this.outgoingEdges = new LinkedHashSet<GraphEdge>();
	}

	public GraphNode() {
		nodeNum++;
		this.id = nodeNum;
		this.incomingEdges = new LinkedHashSet<GraphEdge>();
		this.outgoingEdges = new LinkedHashSet<GraphEdge>();
	}
	
	public int getId() {
		return id;
	}

	public void addIncomingEdge(GraphEdge edge) {
		incomingEdges.add(edge);
	}
	
	public void addOutgoingEdge(GraphEdge edge) {
		outgoingEdges.add(edge);
	}

	public Set<GraphEdge> getIncomingEdges() {
		return incomingEdges;
	}

	public Set<GraphEdge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public static void resetNodeNum() {
		nodeNum = 0;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return id + " " + value;
	}
}
