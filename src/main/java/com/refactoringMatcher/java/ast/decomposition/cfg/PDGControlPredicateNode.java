package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.VariableDeclarationObject;
import com.refactoringMatcher.java.ast.decomposition.AbstractExpression;
import com.refactoringMatcher.java.ast.decomposition.CompositeStatementObject;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class PDGControlPredicateNode extends PDGNode implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1963548772530957086L;

	public PDGControlPredicateNode(CFGNode cfgNode, Set<VariableDeclarationObject> variableDeclarationsInMethod) {
		super(cfgNode, variableDeclarationsInMethod);
		determineDefinedAndUsedVariables();
	}

	private void determineDefinedAndUsedVariables() {
		CFGNode cfgNode = getCFGNode();
		if(cfgNode.getStatement() instanceof CompositeStatementObject) {
			CompositeStatementObject composite = (CompositeStatementObject)cfgNode.getStatement();
			List<AbstractExpression> expressions = composite.getExpressions();
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
