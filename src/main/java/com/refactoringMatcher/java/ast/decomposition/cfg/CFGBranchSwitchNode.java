package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;

import java.io.Serializable;

public class CFGBranchSwitchNode extends CFGBranchConditionalNode  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6216893628423008740L;

	public CFGBranchSwitchNode(AbstractStatement statement) {
		super(statement);
	}
}
