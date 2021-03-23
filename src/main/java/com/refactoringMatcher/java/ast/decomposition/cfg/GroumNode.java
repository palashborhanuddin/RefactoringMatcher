package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public abstract class GroumNode extends GraphNode implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7440768179525614414L;

	public GroumNode(PDGNode pdg){
		pdgNode = pdg;
	}
	
	private GroumNode innerNode;
	
	public void SetInnerNode(GroumNode node)	{
		innerNode = node;
	}
	public GroumNode GetInnerNode()	{
		return innerNode;
	}
	
	public boolean HasInnerNode() {
		return innerNode != null ? true : false;
	}
	
	protected PDGNode pdgNode;
	
	public PDGNode GetPdgNode()	{
		return pdgNode;
	}

	public abstract String ToGroumString();
}
