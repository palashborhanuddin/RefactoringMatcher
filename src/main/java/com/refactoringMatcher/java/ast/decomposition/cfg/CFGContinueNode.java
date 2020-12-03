package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;
import org.eclipse.jdt.core.dom.ContinueStatement;

import java.io.Serializable;

public class CFGContinueNode extends CFGNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9214233871066669750L;
	private String label;
	private CFGNode innerMostLoopNode;
	
	public CFGContinueNode(AbstractStatement statement) {
		super(statement);
		ContinueStatement continueStatement = (ContinueStatement)statement.getStatement();
		if(continueStatement.getLabel() != null)
			label = continueStatement.getLabel().getIdentifier();
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
