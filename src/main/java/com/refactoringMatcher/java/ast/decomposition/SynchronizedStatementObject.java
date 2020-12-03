package com.refactoringMatcher.java.ast.decomposition;

import com.refactoringMatcher.java.ast.ParameterObject;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;

import java.io.Serializable;
import java.util.List;

public class SynchronizedStatementObject extends CompositeStatementObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8532291980665623839L;

	public SynchronizedStatementObject(Statement statement, List<ParameterObject> parameters, AbstractMethodFragment parent) {
		super(statement, parameters, StatementType.SYNCHRONIZED, parent);
		AbstractExpression abstractExpression = new AbstractExpression(
				((SynchronizedStatement)statement).getExpression(), parameters, this);
		this.addExpression(abstractExpression);
	}

}
