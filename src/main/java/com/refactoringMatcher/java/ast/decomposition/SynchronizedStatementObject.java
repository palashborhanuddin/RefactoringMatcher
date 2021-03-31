package com.refactoringMatcher.java.ast.decomposition;

import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.ParameterObject;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;

import java.util.List;

public class SynchronizedStatementObject extends CompositeStatementObject {

	public SynchronizedStatementObject(Statement statement, List<ParameterObject> parameters, List<ImportObject> importObjectList, AbstractMethodFragment parent) {
		super(statement, parameters, importObjectList, StatementType.SYNCHRONIZED, parent);
		AbstractExpression abstractExpression = new AbstractExpression(
				((SynchronizedStatement)statement).getExpression(), parameters, importObjectList, this);
		this.addExpression(abstractExpression);
	}

}
