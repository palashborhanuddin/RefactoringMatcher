package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;
import org.eclipse.jdt.core.dom.BreakStatement;

import java.io.Serializable;

public class CFGBreakNode extends CFGNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4532660655029903617L;
	private String label;
	private CFGNode innerMostLoopNode;
	
	public CFGBreakNode(AbstractStatement statement) {
		super(statement);
		BreakStatement breakStatement = (BreakStatement)statement.getStatement();
		if(breakStatement.getLabel() != null)
			label = breakStatement.getLabel().getIdentifier();
	}

	public String getLabel() {
		return label;
	}

	public boolean isLabeled() {
		return label != null;
	}

	public CFGNode getInnerMostLoopNode() {
		return innerMostLoopNode;
	}

	public void setInnerMostLoopNode(CFGNode innerMostLoopNode) {
		this.innerMostLoopNode = innerMostLoopNode;
	}
}
