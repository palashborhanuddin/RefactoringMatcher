package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;

public class GroumForNode extends GroumControlNode implements Serializable {

	private ForStatement forStatement;
	private EnhancedForStatement enhancedForStatement;
	private boolean enhancedForType;

	public GroumForNode(ForStatement statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
		super(pdgNode, groumBlockNode);
		forStatement = statement;
		enhancedForType = false;
		setValue(ToGroumString());
	}

	public GroumForNode(EnhancedForStatement statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
		super(pdgNode, groumBlockNode);
		enhancedForStatement = statement;
		enhancedForType = true;
		setValue(ToGroumString());
	}

	public boolean isEnhancedForType() {
		return enhancedForType;
	}

	public ForStatement GetForStatement() {
		return forStatement;
	}

	public EnhancedForStatement GetEnhancedForStatement() {
		return enhancedForStatement;
	}

	public String ToGroumString(){
		return "FOR";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2195148809891513989L;

}
