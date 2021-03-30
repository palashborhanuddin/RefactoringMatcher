package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.refactoringMatcher.java.ast.FieldObject;
import com.refactoringMatcher.java.ast.VariableDeclarationObject;
import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;
import com.refactoringMatcher.java.ast.decomposition.CatchClauseObject;
import com.refactoringMatcher.java.ast.decomposition.TryStatementObject;

public class PDGTryNode extends PDGBlockNode {
	public PDGTryNode(CFGTryNode cfgTryNode, Set<VariableDeclarationObject> variableDeclarationsInMethod,
			Set<FieldObject> fieldsAccessedInMethod) {
		super(cfgTryNode, variableDeclarationsInMethod, fieldsAccessedInMethod);
		this.controlParent = cfgTryNode.getControlParent();
		determineDefinedAndUsedVariables();
	}
	
	public boolean hasFinallyClauseClosingVariable(AbstractVariable variable) {
		return ((CFGTryNode)getCFGNode()).hasFinallyClauseClosingVariable(variable);
	}
	
	public boolean hasCatchClause() {
		return ((CFGTryNode)getCFGNode()).hasCatchClause();
	}

	protected void determineDefinedAndUsedVariables() {
		super.determineDefinedAndUsedVariables();
		CFGNode cfgNode = getCFGNode();
		if(cfgNode.getStatement() instanceof TryStatementObject) {
			TryStatementObject tryStatement = (TryStatementObject)cfgNode.getStatement();
			List<AbstractStatement> statementsInCatchClausesAndFinallyBlock = new ArrayList<AbstractStatement>();
			for(CatchClauseObject catchClause : tryStatement.getCatchClauses()) {
				statementsInCatchClausesAndFinallyBlock.add(catchClause.getBody());
			}
			if(tryStatement.getFinallyClause() != null) {
				statementsInCatchClausesAndFinallyBlock.add(tryStatement.getFinallyClause());
			}
			for(AbstractStatement statement : statementsInCatchClausesAndFinallyBlock) {
				for(PlainVariable variable : statement.getDefinedLocalVariables()) {
					definedVariables.add(variable);
				}
				for(PlainVariable variable : statement.getUsedLocalVariables()) {
					usedVariables.add(variable);
				}
			}
		}
	}
}
