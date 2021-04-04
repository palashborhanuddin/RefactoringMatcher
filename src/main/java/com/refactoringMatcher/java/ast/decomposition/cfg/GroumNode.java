package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class GroumNode extends GraphNode implements Serializable  {

	private static final long serialVersionUID = -7440768179525614414L;

	protected PDGNode pdgNode;
	protected Set<AbstractVariable> declaredVariables;
	protected Set<AbstractVariable> definedVariables;
	protected Set<AbstractVariable> usedVariables;
	private GroumNodeType groumNodeType;

	public GroumNode(PDGNode pdg, GroumNodeType nodeType){
		pdgNode = pdg;
		groumNodeType = nodeType;
		this.declaredVariables = new LinkedHashSet<AbstractVariable>();
		this.definedVariables = new LinkedHashSet<AbstractVariable>();
		this.usedVariables = new LinkedHashSet<AbstractVariable>();
	}
	
	private GroumNode innerNode;
	
	public void SetInnerNode(GroumNode node) {
		innerNode = node;
	}

	public GroumNode GetInnerNode()	{
		return innerNode;
	}
	
	public boolean HasInnerNode() {
		return innerNode != null;
	}
	
	public PDGNode GetPdgNode()	{
		return pdgNode;
	}
}
