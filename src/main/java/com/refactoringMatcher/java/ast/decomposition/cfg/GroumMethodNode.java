package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class GroumMethodNode extends GroumNode implements Serializable {

	private MethodInvocation methodInvocation;
	
	public MethodInvocation GetMethodInvocation() {
		return methodInvocation;
	}
	
	public GroumMethodNode(MethodInvocation statement, PDGNode pdgNode) {
		super(pdgNode);
		methodInvocation = statement;
		setValue(ToGroumString());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7222706825193092243L;

	@Override
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

		return Objects.nonNull(variableType)
				? variableType + "." + methodInvocation.getName().toString()
				: methodInvocation.getName().toString();
	}

}
