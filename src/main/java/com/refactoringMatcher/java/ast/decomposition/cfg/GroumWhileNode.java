package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.WhileStatement;

public class GroumWhileNode extends GroumControlNode implements Serializable {

	private WhileStatement whileStatement;
	
	public WhileStatement GetWhileStatement(){
		return whileStatement;
	}
	
	public GroumWhileNode(WhileStatement statement, PDGNode pdgNode) {
		super(pdgNode);
		whileStatement = statement;
		setValue(ToGroumString());
	}

	public String ToGroumString(){
		return "WHILE";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8651854045659000268L;
	
}
