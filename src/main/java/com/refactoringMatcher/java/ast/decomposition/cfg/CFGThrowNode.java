package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;

import java.io.Serializable;

public class CFGThrowNode extends CFGNode  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3849137072173463024L;

	public CFGThrowNode(AbstractStatement statement) {
		super(statement);
	}

}
