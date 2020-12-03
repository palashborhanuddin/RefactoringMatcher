package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;

import java.io.Serializable;

public class CFGSynchronizedNode extends CFGBlockNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CFGSynchronizedNode(AbstractStatement statement) {
		super(statement);
	}
}
