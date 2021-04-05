package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;

public class GroumMethodInvocationNode extends GroumActionNode implements Serializable {

	private MethodInvocation methodInvocation;
	private Boolean isLocal = false;

	public GroumMethodInvocationNode(MethodInvocation statement, PDGNode pdgNode) {
		super(pdgNode);
		methodInvocation = statement;
		setValue(ToGroumString());
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

		while (usedVariableIterator.hasNext()) {
			AbstractVariable abstractVariable = usedVariableIterator.next();

			if (Objects.nonNull(abstractVariable)
					&& Objects.nonNull(methodInvocation.getExpression())
					&& abstractVariable.getVariableName().equals(methodInvocation.getExpression().toString())) {
				// Though it is usedVariable but considering this as definedVariable as it acts alike during groum edge generation
				definedVariables.add(abstractVariable);
				break;
			}
		}

		// TODO GROUM need to consider arguments.
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
