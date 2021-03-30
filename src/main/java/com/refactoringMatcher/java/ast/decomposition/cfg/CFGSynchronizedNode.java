package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;

public class CFGSynchronizedNode extends CFGBlockNode {
	public CFGSynchronizedNode(AbstractStatement statement) {
		super(statement);
	}
}
