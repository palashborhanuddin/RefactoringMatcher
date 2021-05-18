package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.WhileStatement;

public class GroumWhileNode extends GroumControlNode implements Serializable {

	private WhileStatement whileStatement;
	
	public WhileStatement GetWhileStatement(){
		return whileStatement;
	}
	
	public GroumWhileNode(WhileStatement statement, PDGNode pdgNode, GroumBlockNode groumBlock) {
		super(pdgNode, groumBlock);
		whileStatement = statement;
		setValue(ToGroumString());
	}

	private String ToGroumString(){
		return "WHILE";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8651854045659000268L;
	
}
