package com.refactoringMatcher.java.ast.decomposition;

import java.util.List;

import com.refactoringMatcher.java.ast.ASTInformation;
import com.refactoringMatcher.java.ast.ASTInformationGenerator;
import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.ParameterObject;
import com.refactoringMatcher.java.ast.util.ExpressionExtractor;

import org.eclipse.jdt.core.dom.Expression;

public class AbstractExpression extends AbstractMethodFragment {

	private ASTInformation expression;

	public AbstractExpression(Expression expression, List<ParameterObject> parameters, List<ImportObject> importObjectList, AbstractMethodFragment parent) {
		super(parent, parameters, importObjectList);
		this.expression = ASTInformationGenerator.generateASTInformation(expression);
		processExpression(expression);
	}

	private void processExpression(Expression expression) {
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
        List<Expression> assignments = expressionExtractor.getAssignments(expression);
        List<Expression> postfixExpressions = expressionExtractor.getPostfixExpressions(expression);
        List<Expression> prefixExpressions = expressionExtractor.getPrefixExpressions(expression);
        processVariables(expressionExtractor.getVariableInstructions(expression), assignments, postfixExpressions, prefixExpressions);
		processMethodInvocations(expressionExtractor.getMethodInvocations(expression));
		processClassInstanceCreations(expressionExtractor.getClassInstanceCreations(expression));
		processArrayCreations(expressionExtractor.getArrayCreations(expression));
		processLiterals(expressionExtractor.getLiterals(expression));
	}

	public Expression getExpression() {
		return (Expression)this.expression.recoverASTNode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractExpression other = (AbstractExpression) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	public String toString() {
		return getExpression().toString();
	}
}
