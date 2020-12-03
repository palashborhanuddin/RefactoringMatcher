package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.VariableDeclarationObject;
import com.refactoringMatcher.java.ast.decomposition.AbstractExpression;
import com.refactoringMatcher.java.ast.decomposition.CompositeStatementObject;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class PDGBlockNode extends PDGNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3536763719125848838L;
	protected CFGNode controlParent;

	public PDGBlockNode(CFGNode cfgNode, Set<VariableDeclarationObject> variableDeclarationsInMethod/*,
			Set<FieldObject> fieldsAccessedInMethod*/) {
		super(cfgNode, variableDeclarationsInMethod/*, fieldsAccessedInMethod*/);
	}

	public PDGNode getControlDependenceParent() {
		if(controlParent != null) {
			if(controlParent.getPDGNode() != null)
				return controlParent.getPDGNode();
		}
		return super.getControlDependenceParent();
	}

	protected void determineDefinedAndUsedVariables() {
		CFGNode cfgNode = getCFGNode();
		if(cfgNode.getStatement() instanceof CompositeStatementObject) {
			CompositeStatementObject compositeStatement = (CompositeStatementObject)cfgNode.getStatement();
			List<AbstractExpression> expressions = compositeStatement.getExpressions();
			for(AbstractExpression expression : expressions) {
				for(PlainVariable variable : expression.getDeclaredLocalVariables()) {
					declaredVariables.add(variable);
					definedVariables.add(variable);
				}
				for(PlainVariable variable : expression.getDefinedLocalVariables()) {
					definedVariables.add(variable);
				}
				for(PlainVariable variable : expression.getUsedLocalVariables()) {
					usedVariables.add(variable);
				}
			}
		}
	}
}