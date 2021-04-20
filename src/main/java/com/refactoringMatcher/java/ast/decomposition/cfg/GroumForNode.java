package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.ForStatement;

public class GroumForNode extends GroumControlNode implements Serializable {

	private ForStatement forStatement;

	public GroumForNode(ForStatement statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
		super(pdgNode, groumBlockNode);
		forStatement = statement;
		setValue(ToGroumString());
	}

	public ForStatement GetForStatement() {
		return forStatement;
	}

	public String ToGroumString(){
		return "FOR";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2195148809891513989L;

}
