package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.refactoringMatcher.java.ast.MethodInvocationObject;
import com.refactoringMatcher.java.ast.util.ExpressionExtractor;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ASTNode;

public class GroumMethodInvocationNode extends GroumActionNode implements Serializable {

	private MethodInvocation methodInvocation;
	private List<FieldDeclaration> fieldDeclarationList;
	private boolean isLocal = true;

	public GroumMethodInvocationNode(MethodInvocation statement, PDGNode pdgNode, List<MethodInvocationObject> methodInvocationObjectList, List<FieldDeclaration> fieldDeclarationList, GroumBlockNode groumBlockNode) {
		super(pdgNode);
		methodInvocation = statement;
		this.fieldDeclarationList = fieldDeclarationList;
		setValue(ToGroumString(methodInvocationObjectList));
		getDefinedAncestorNode();
		setGroumBlockNode(groumBlockNode);
		determineDefinedAndUsedVariables();
	}

	private void getDefinedAncestorNode() {
		boolean isFound = false;
		for (GraphEdge incomingEdge : pdgNode.getIncomingEdges()) {
			if (incomingEdge instanceof PDGDataDependence) {
				PDGNode node = (PDGNode) incomingEdge.getSrc();
				Iterator<AbstractVariable> definedVariableIterator = node.getDefinedVariableIterator();
				while (definedVariableIterator.hasNext()) {
					AbstractVariable abstractVariable = definedVariableIterator.next();

					if (Objects.nonNull(abstractVariable)
							&& (Objects.nonNull(methodInvocation.getExpression()))
							&& abstractVariable.getVariableName().equals(methodInvocation.getExpression().toString())) {
						setAncestorDefinedNode(node);
						isFound = true;
						break;
					}
				}
			}
			if (isFound)
				break;
		}
	}

	private void determineDefinedAndUsedVariables() {

		if (pdgNode.definedVariables.size() > 0) {
			// TODO GROUM what if parent is another expression and then assign/declare? check for.
			if ((methodInvocation.getParent() instanceof VariableDeclarationFragment)) {
				Iterator<AbstractVariable> definedVariableIterator = this.pdgNode.getDefinedVariableIterator();
				VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) methodInvocation.getParent();
				while (definedVariableIterator.hasNext()) {
					AbstractVariable abstractVariable = definedVariableIterator.next();
					if (Objects.nonNull(abstractVariable)
							&& Objects.nonNull(variableDeclarationFragment)
							&& abstractVariable.getVariableName().equals(variableDeclarationFragment.getName().toString())) {
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

	public boolean IsLocal() {
		return isLocal;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7222706825193092243L;

	private String ToGroumString(List<MethodInvocationObject> methodInvocationObjectList) {
		String variableType = null;
		for (MethodInvocationObject methodInvocationObject : methodInvocationObjectList) {
			ASTNode astNode = methodInvocationObject.getStatementInformation().recoverASTNode();
			if (astNode instanceof Statement) {
				if (methodInvocation.toString().equals(methodInvocationObject.getMethodInvocation().toString())
						&& ((Statement) astNode).toString().equals(pdgNode.getASTStatement().toString())
						&& methodInvocationObject.isInvokerTypeDetermined()) {
					variableType = methodInvocationObject.getOriginClassType().getNonQualifiedClassName();
					isLocal = false;
					break;
				}
			}
			else if(astNode instanceof Expression) {
				if (methodInvocation.toString().equals(methodInvocationObject.getMethodInvocation().toString())
						&& ((Expression) astNode).toString().equals(pdgNode.getASTStatement().toString())
						&& methodInvocationObject.isInvokerTypeDetermined()) {
					variableType = methodInvocationObject.getOriginClassType().getNonQualifiedClassName();
					isLocal = false;
					break;
				}
			}
		}

		return Objects.nonNull(variableType)
				? variableType + "." + methodInvocation.getName().toString()
				: methodInvocation.getName().toString();
	}
}
