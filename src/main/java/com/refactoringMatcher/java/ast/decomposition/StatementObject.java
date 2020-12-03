package com.refactoringMatcher.java.ast.decomposition;

import com.refactoringMatcher.java.ast.ParameterObject;
import com.refactoringMatcher.java.ast.util.ExpressionExtractor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * StatementObject represents the following AST Statement subclasses:
 * 1.	ExpressionStatement
 * 2.	VariableDeclarationStatement
 * 3.	ConstructorInvocation
 * 4.	SuperConstructorInvocation
 * 5.	ReturnStatement
 * 6.	AssertStatement
 * 7.	BreakStatement
 * 8.	ContinueStatement
 * 9.	SwitchCase
 * 10.	EmptyStatement
 * 11.	ThrowStatement
 */

public class StatementObject extends AbstractStatement  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1572661510460765186L;

	public StatementObject(Statement statement, List<ParameterObject> parameters, StatementType type, AbstractMethodFragment parent) {
		super(statement, parameters, type, parent);
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
        List<Expression> assignments = expressionExtractor.getAssignments(statement);
        List<Expression> postfixExpressions = expressionExtractor.getPostfixExpressions(statement);
        List<Expression> prefixExpressions = expressionExtractor.getPrefixExpressions(statement);
        processVariablesWithoutBindingInfo(expressionExtractor.getVariableInstructions(statement), assignments, postfixExpressions, prefixExpressions);
		processArrayCreations(expressionExtractor.getArrayCreations(statement));
		processLiterals(expressionExtractor.getLiterals(statement));
		if(statement instanceof ThrowStatement) {
			processThrowStatement((ThrowStatement)statement);
		}
	}

	public String toString() {
		return getStatement().toString();
	}

	public List<String> stringRepresentation() {
		List<String> stringRepresentation = new ArrayList<String>();
		stringRepresentation.add(this.toString());
		return stringRepresentation;
	}
}
