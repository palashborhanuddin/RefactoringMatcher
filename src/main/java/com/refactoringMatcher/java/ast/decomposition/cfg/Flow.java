package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public class Flow extends GraphEdge  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5161486289445769959L;
	private boolean loopbackFlow = false;
	private boolean trueControlFlow = false;
	private boolean falseControlFlow = false;
	
	public Flow(CFGNode src, CFGNode dst, Graph graph) {
		super(src, dst, graph);
		src.addOutgoingEdge(this);
		dst.addIncomingEdge(this);
	}

	public boolean isLoopbackFlow() {
		return loopbackFlow;
	}

	public void setLoopbackFlow(boolean loopbackFlow) {
		this.loopbackFlow = loopbackFlow;
	}

	public boolean isTrueControlFlow() {
		return trueControlFlow;
	}

	public void setTrueControlFlow(boolean trueControlFlow) {
		this.trueControlFlow = trueControlFlow;
	}

	public boolean isFalseControlFlow() {
		return falseControlFlow;
	}

	public void setFalseControlFlow(boolean falseControlFlow) {
		this.falseControlFlow = falseControlFlow;
	}

	public String toString() {
		StringBuilder type = new StringBuilder();
		if(trueControlFlow)
			type.append("T");
		if(falseControlFlow)
			type.append("F");
		if(loopbackFlow)
			type.append("LB");
		return getSrc().toString() + "-->" + type.toString() + " " + getDst().toString() + "\n";
	}
}
