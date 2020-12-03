package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.VariableDeclarationObject;

import java.io.Serializable;
import java.util.Set;

public class PDGSynchronizedNode extends PDGBlockNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -838382573303920508L;

	public PDGSynchronizedNode(CFGSynchronizedNode cfgSynchronizedNode, Set<VariableDeclarationObject> variableDeclarationsInMethod/*,
			Set<FieldObject> fieldsAccessedInMethod*/) {
		super(cfgSynchronizedNode, variableDeclarationsInMethod/*, fieldsAccessedInMethod*/);
		this.controlParent = cfgSynchronizedNode.getControlParent();
		determineDefinedAndUsedVariables();
	}
}
