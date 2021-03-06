package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.IfStatement;

public class GroumIfNode extends GroumControlNode implements Serializable {

	private IfStatement ifStatement;

	public GroumIfNode(IfStatement statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
		super(pdgNode, groumBlockNode);
		ifStatement = statement;
		setValue(ToGroumString());
	}

	public IfStatement GetIfStatement() {
		return ifStatement;
	}

	private String ToGroumString(){
		return "IF";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1494068999898494563L;

}
