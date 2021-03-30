package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.Objects;

public class GraphEdge {
	protected GraphNode src;
	protected GraphNode dst;
	
	public GraphEdge(GraphNode src, GraphNode dst) {
		this.src = src;
		this.dst = dst;
	}

	public GraphNode getSrc() {
		return src;
	}

	public GraphNode getDst() {
		return dst;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GraphEdge graphEdge = (GraphEdge) o;
		return src.getId() == graphEdge.getSrc().getId() &&
				dst.getId() == graphEdge.getDst().getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(src.getId(), dst.getId());
	}
}