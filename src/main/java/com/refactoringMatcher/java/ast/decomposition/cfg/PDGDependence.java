package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public abstract class PDGDependence extends GraphEdge  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7429600958608590849L;
	private PDGDependenceType type;
	
	public PDGDependence(PDGNode src, PDGNode dst, PDGDependenceType type, Graph graph) {
		super(src, dst, graph);
		this.type = type;
	}

//	public GraphNode getSrc() {
//		return src;
//	}
//
//	public GraphNode getDst() {
//		return dst;
//	}

	public PDGDependenceType getType() {
		return type;
	}
}
