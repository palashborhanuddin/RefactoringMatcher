package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.IfStatement;

public class GroumIfNode extends GroumNode implements Serializable {

	private IfStatement ifStatement;
	
	public IfStatement GetIfStatement(){
		return ifStatement;
	}
	
	public GroumIfNode(IfStatement statement, PDGNode pdgNode) {
		super(pdgNode);
		ifStatement = statement;
	}

	public String ToGroumString(){
		return "IF";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1494068999898494563L;

}
