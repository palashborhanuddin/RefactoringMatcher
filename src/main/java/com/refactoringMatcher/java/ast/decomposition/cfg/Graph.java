package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Graph  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9094505154895070776L;
	protected Set<GraphNode> nodes;
	protected Set<GraphEdge> edges;
	
	public Graph() {
		this.nodes = new LinkedHashSet<GraphNode>();
		this.edges = new LinkedHashSet<GraphEdge>();
	}
	
	public Set<GraphNode> getNodes() {
		return nodes;
	}

	public Set<GraphEdge> getEdges() {
		return edges;
	}

	public void addNode(GraphNode node) {
		if(node != null)
		nodes.add(node);
	}
	
	public void addEdge(GraphEdge edge) {
		if(edge != null)
		edges.add(edge);
	}
	
	public int size()	{
		return nodes.size();
	}

	public GraphNode getNode(int nodeId) {
		for (GraphNode graphNode : nodes) {
			if(graphNode.id == nodeId)
				return graphNode;
		}
		return null;
	}
	
	public void removeCyclicReferences()
	{
		for (GraphEdge edge : edges) {
			edge.removeCyclicReferences();
		}
	}
	
	public void recoverCyclicReferences(Graph graph)
	{
		for (GraphEdge edge : edges) {
			edge.recoverCyclicReferences(graph);
		}
	}

	@Override
	public String toString() {
		return "Graph{" +
				"\nnodes=" + nodes +
				"\nedges=" + edges +
				'}';
	}
}
