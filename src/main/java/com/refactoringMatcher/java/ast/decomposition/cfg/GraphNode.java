package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class GraphNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5761935198073644799L;
	private static int nodeNum = 0;
	protected int id;
	protected String value = "";
	protected Set<GraphEdge> incomingEdges;
	public Set<GraphEdge> outgoingEdges;
	
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

	public static void resetNodeNum() {
		nodeNum = 0;
	}

	public Set<GraphEdge> getIncomingEdges() {
		return incomingEdges;
	}

	public Set<GraphEdge> getOutgoingEdges() {
		return outgoingEdges;
	}

	public int getInDegree() {
		return incomingEdges.size();
	}

	public int getOutDegree() {
		return outgoingEdges.size();
	}
	
	public String getValue() {
		return value;
	}

	public String toString() {
		if(value.isEmpty())
			return id + "";
		return id + ":" + value;
	}
	
	public boolean isSuccessorOf(GraphNode graphNode) {
		boolean isSuccessorOf = false;
		for (GraphEdge graphEdge : incomingEdges) {
			if (graphEdge.getSrc().id == graphNode.id) {
				isSuccessorOf = true;
				break;
			}
		}
		return isSuccessorOf;
	}

	public boolean isPredecessorOf(GraphNode graphNode) {
		boolean isPredecessorOf = false;
		for (GraphEdge graphEdge : outgoingEdges) {
			if (graphEdge.getDst().id == graphNode.id) {
				isPredecessorOf = true;
				break;
			}
		}
		return isPredecessorOf;
	}
}
