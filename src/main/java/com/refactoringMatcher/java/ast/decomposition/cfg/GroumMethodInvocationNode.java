package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.refactoringMatcher.java.ast.util.ExpressionExtractor;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class GroumMethodInvocationNode extends GroumActionNode implements Serializable {

	private MethodInvocation methodInvocation;
	private Boolean isLocal = false;

	public GroumMethodInvocationNode(MethodInvocation statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
		super(pdgNode);
		methodInvocation = statement;
		setValue(ToGroumString());
		setGroumBlockNode(groumBlockNode);
		determineDefinedAndUsedVariables();
	}

	private void determineDefinedAndUsedVariables() {

		if (pdgNode.definedVariables.size() > 0) {
			if ((methodInvocation.getParent() instanceof VariableDeclarationFragment)) {
				Iterator<AbstractVariable> definedVariableIterator = this.pdgNode.getDefinedVariableIterator();

				while (definedVariableIterator.hasNext()) {
					AbstractVariable abstractVariable = definedVariableIterator.next();
					if (Objects.nonNull(abstractVariable)
							&& Objects.nonNull(methodInvocation.getExpression())
							&& abstractVariable.getVariableName().equals(methodInvocation.getExpression().toString())) {
						declaredVariables.add(abstractVariable);
						definedVariables.add(abstractVariable);
						break;
					}
				}
			}
			else if (methodInvocation.getParent() instanceof Assignment) {
				Iterator<AbstractVariable> definedVariableIterator = this.pdgNode.getDefinedVariableIterator();
				Assignment assignment = (Assignment) methodInvocation.getParent();
				Expression varName = assignment.getLeftHandSide();

				while (definedVariableIterator.hasNext()) {
					AbstractVariable abstractVariable = definedVariableIterator.next();

					if (Objects.nonNull(abstractVariable)
							&& abstractVariable.getVariableName().equals(varName.toString())) {
						definedVariables.add(abstractVariable);
						break;
					}
				}
			}
		}
		Iterator<AbstractVariable> usedVariableIterator = this.pdgNode.getUsedVariableIterator();
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		List<Expression> variableInstructions = expressionExtractor.getVariableInstructions((Expression) methodInvocation);

		while (usedVariableIterator.hasNext()) {
			AbstractVariable abstractVariable = usedVariableIterator.next();

			if (Objects.nonNull(abstractVariable)
					&& Objects.nonNull(methodInvocation.getExpression())
					&& abstractVariable.getVariableName().equals(methodInvocation.getExpression().toString())) {
				// Though it is usedVariable but considering this as definedVariable as it acts alike during groum edge generation
				definedVariables.add(abstractVariable);
			}
			for (Expression expression : variableInstructions) {
				if (expression instanceof SimpleName) {
					SimpleName simpleName = (SimpleName) expression;
					if (Objects.nonNull(abstractVariable)
							&& abstractVariable.getVariableName().equals(simpleName.toString())) {
						usedVariables.add(abstractVariable);
					}
				}
			}
		}
	}

	public MethodInvocation GetMethodInvocation() {
		return methodInvocation;
	}

	public Boolean IsLocal() {
		return isLocal;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7222706825193092243L;

	public String ToGroumString() {
		Iterator<AbstractVariable> usedVariableIterator = this.pdgNode.getUsedVariableIterator();
		String variableType = null;

		while (usedVariableIterator.hasNext()) {
			AbstractVariable abstractVariable = usedVariableIterator.next();

			if (Objects.nonNull(abstractVariable)
					&& Objects.nonNull(methodInvocation.getExpression())
					&& abstractVariable.getVariableName().equals(methodInvocation.getExpression().toString())) {
				variableType = abstractVariable.getVariableType();
			}
		}

		if (Objects.isNull(variableType))
			isLocal = true;
		return Objects.nonNull(variableType)
				? variableType + "." + methodInvocation.getName().toString()
				: methodInvocation.getName().toString();
	}
}
