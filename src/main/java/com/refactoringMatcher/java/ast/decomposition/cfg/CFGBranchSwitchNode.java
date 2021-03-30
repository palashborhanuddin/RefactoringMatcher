package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;

public class CFGBranchSwitchNode extends CFGBranchConditionalNode {

	public CFGBranchSwitchNode(AbstractStatement statement) {
		super(statement);
	}
}
