package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;
import com.refactoringMatcher.java.ast.decomposition.CatchClauseObject;
import com.refactoringMatcher.java.ast.decomposition.TryStatementObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CFGTryNode extends CFGBlockNode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8872167447275377513L;
	private List<String> handledExceptions;
	private boolean hasResources;
	public CFGTryNode(AbstractStatement statement) {
		super(statement);
		this.handledExceptions = new ArrayList<String>();
		TryStatementObject tryStatement = (TryStatementObject) statement;
		this.hasResources = tryStatement.hasResources();
		for(CatchClauseObject catchClause : tryStatement.getCatchClauses()) {
			handledExceptions.addAll(catchClause.getExceptionTypes());
		}
	}
	
	public boolean hasResources() {
		return hasResources;
	}

	public List<String> getHandledExceptions() {
		return handledExceptions;
	}
	
	public boolean hasFinallyClauseClosingVariable(AbstractVariable variable) {
		return ((TryStatementObject)getStatement()).hasFinallyClauseClosingVariable(variable);
	}

	public boolean hasCatchClause() {
		return ((TryStatementObject)getStatement()).hasCatchClause();
	}
}
