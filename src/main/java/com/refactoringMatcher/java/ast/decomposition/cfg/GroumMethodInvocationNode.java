package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class GroumMethodInvocationNode extends GroumNode implements Serializable {

	private MethodInvocation methodInvocation;
	private Boolean isLocal = false;

	public GroumMethodInvocationNode(MethodInvocation statement, PDGNode pdgNode) {
		super(pdgNode, GroumNodeType.ACTION);
		methodInvocation = statement;
		setValue(ToGroumString());
		determineDefinedAndUsedVariables();
	}

	private void determineDefinedAndUsedVariables() {
		definedVariables = pdgNode.definedVariables;
		usedVariables = pdgNode.usedVariables;
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
