package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Objects;

public class GraphEdge  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4340501881512388913L;
	protected int srcId;
	protected int dstId;
	private Graph graph;
	
	public GraphEdge(GraphNode src, GraphNode dst, Graph graph) {
		if(src == null)
			src = new GraphNode();
		if(dst == null)
			dst = new GraphNode();
		this.srcId = src.id;
		this.dstId = dst.id;
		this.graph = graph;
	}

	public GraphNode getSrc() {
		return graph.getNode(srcId);
	}

	public GraphNode getDst() {
		return graph.getNode(dstId);
	}
	
	public String toString() {
		return "SRC: " +getSrc().toString() + " -> DST: " +getDst().toString();
	}
	
	public void removeCyclicReferences()
	{
		graph = null;
	}
	
	public void recoverCyclicReferences(Graph graph)
	{
		this.graph = graph;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GraphEdge graphEdge = (GraphEdge) o;
		return srcId == graphEdge.srcId &&
				dstId == graphEdge.dstId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(srcId, dstId);
	}
}
