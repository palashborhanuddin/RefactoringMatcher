package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.ParameterizedType;

import com.refactoringMatcher.java.ast.util.ExpressionExtractor;

public class GroumClassInstantiationNode extends GroumActionNode implements Serializable  {

	private ClassInstanceCreation classInstanceCreation;
	
	public ClassInstanceCreation GetClassInstanceCreation() {
		return classInstanceCreation;
	}
	
	public GroumClassInstantiationNode(ClassInstanceCreation statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
		super(pdgNode);
		classInstanceCreation = statement;
		setValue(ToGroumString());
		setGroumBlockNode(groumBlockNode);
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
						declaredVariables.add(abstractVariable);
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
		}
		Iterator<AbstractVariable> usedVariableIterator = this.pdgNode.getUsedVariableIterator();
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		List<Expression> variableInstructions = expressionExtractor.getVariableInstructions((Expression) classInstanceCreation);

		while (usedVariableIterator.hasNext()) {
			AbstractVariable abstractVariable = usedVariableIterator.next();

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

	private static final long serialVersionUID = 3562275374298174028L;

	private String ToGroumString() {
		if (classInstanceCreation.getType() instanceof ParameterizedType) {
			return ((ParameterizedType) classInstanceCreation.getType()).getType().toString() + ".<init>";
		}
		return classInstanceCreation.getType().toString() + ".<init>";
	}

}
