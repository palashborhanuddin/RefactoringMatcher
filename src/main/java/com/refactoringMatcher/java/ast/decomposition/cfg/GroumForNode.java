package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.ForStatement;

public class GroumForNode extends GroumNode implements Serializable {

	private ForStatement forStatement;
	
	public ForStatement GetForStatement() {
		return forStatement;
	}
	
	public GroumForNode(ForStatement statement, PDGNode pdgNode) {
		super(pdgNode, GroumNodeType.CONTROL);
		forStatement = statement;
		setValue(ToGroumString());
		determineDefinedAndUsedVariables();
	}

	private void determineDefinedAndUsedVariables() {
		definedVariables = pdgNode.definedVariables;
		usedVariables = pdgNode.usedVariables;
	}

	public String ToGroumString(){
		return "FOR";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2195148809891513989L;

}
