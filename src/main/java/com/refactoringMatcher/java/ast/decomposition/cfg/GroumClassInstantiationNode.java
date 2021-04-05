package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class GroumClassInstantiationNode extends GroumActionNode implements Serializable  {

	private ClassInstanceCreation classInstanceCreation;
	
	public ClassInstanceCreation GetClassInstanceCreation() {
		return classInstanceCreation;
	}
	
	public GroumClassInstantiationNode(ClassInstanceCreation statement, PDGNode pdgNode) {
		super(pdgNode);
		classInstanceCreation = statement;
		setValue(ToGroumString());
		determineDefinedAndUsedVariables();
	}

	private void determineDefinedAndUsedVariables() {
		if (pdgNode.definedVariables.size() > 0) {
			if ((classInstanceCreation.getParent() instanceof VariableDeclarationFragment)) {
				Iterator<AbstractVariable> definedVariableIterator = this.pdgNode.getDefinedVariableIterator();
				VariableDeclarationFragment variableFragment = (VariableDeclarationFragment) classInstanceCreation.getParent();
				String simpleName = variableFragment.getName().toString();
				while (definedVariableIterator.hasNext()) {
					AbstractVariable abstractVariable = definedVariableIterator.next();
					if (Objects.nonNull(abstractVariable)
							&& abstractVariable.getVariableName().equals(simpleName)) {
						definedVariables.add(abstractVariable);
						break;
					}
				}
			}
			else if (classInstanceCreation.getParent() instanceof Assignment) {
				Iterator<AbstractVariable> definedVariableIterator = this.pdgNode.getDefinedVariableIterator();
				Assignment assignment = (Assignment) classInstanceCreation.getParent();
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
			// TODO GROUM need to consider arguments.
		}
	}

	private static final long serialVersionUID = 3562275374298174028L;

	public String ToGroumString() {
		return classInstanceCreation.getType().toString() + ".<init>" ;
	}

}
