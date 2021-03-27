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
//	private GraphNode src;
//	private GraphNode dst;
	private Graph graph;
	
	public GraphEdge(GraphNode src, GraphNode dst, Graph graph) {
		if(src == null)
			src = new GraphNode();
		if(dst == null)
			dst = new GraphNode();
		this.srcId = src.id;
		this.dstId = dst.id;
//		this.src = src;
//		this.dst = dst;
		this.graph = graph;
//		graph.nodes.add(src);
//		graph.nodes.add(dst);
	}

	public GraphNode getSrc() {
		return graph.getNode(srcId);
//		return src;
	}

	public GraphNode getDst() {
		return graph.getNode(dstId);
//		return dst;
	}
	
	public String toString() {
		return "SRC: " +getSrc().toString() + " -> DST: " +getDst().toString();
	}
	
	public void removeCyclicReferences()
	{
//		src = null;
//		dst = null;
		graph = null;
	}
	
	public void recoverCyclicReferences(Graph graph)
	{
//		src = graph.getNode(srcId);
//		dst = graph.getNode(dstId);
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
