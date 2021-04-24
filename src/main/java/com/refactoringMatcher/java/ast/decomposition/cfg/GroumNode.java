package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.FieldInstructionObject;

import java.io.Serializable;
import java.util.*;

public class GroumNode extends GraphNode implements Serializable  {

	private static final long serialVersionUID = -7440768179525614414L;

	protected PDGNode pdgNode;
	protected Set<AbstractVariable> declaredVariables;
	protected Set<AbstractVariable> definedVariables;
	protected Set<AbstractVariable> usedVariables;
	private GroumNodeType groumNodeType;
	private GroumBlockNode groumBlockNode;
	//private GroumNode innerNode;
	private List<GroumNode> innerNodes;

	public GroumNode(PDGNode pdg, GroumNodeType nodeType){
		pdgNode = pdg;
		groumNodeType = nodeType;

		this.declaredVariables = new LinkedHashSet<AbstractVariable>();
		this.definedVariables = new LinkedHashSet<AbstractVariable>();
		this.usedVariables = new LinkedHashSet<AbstractVariable>();
		this.innerNodes = new ArrayList<GroumNode>();
	}

	public GroumNodeType getGroumNodeType() {
		return groumNodeType;
	}

	public void SetInnerNode(GroumNode node) {
		//innerNode = node;
		innerNodes.add(node);
	}

	public List<GroumNode> GetInnerNodes()	{
		return innerNodes;
	}
	
	public boolean HasInnerNode() {
		return (innerNodes.size() > 0);
	}

	public GroumBlockNode getGroumBlockNode() {
		return groumBlockNode;
	}

	public void setGroumBlockNode(GroumBlockNode node) {
		groumBlockNode = node;
	}

	public boolean isBelongsToBlockScope() {
		return groumBlockNode != null;
	}

	public PDGNode GetPdgNode()	{
		return pdgNode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GroumNode groumNode = (GroumNode) o;
		return getId() == groumNode.getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
