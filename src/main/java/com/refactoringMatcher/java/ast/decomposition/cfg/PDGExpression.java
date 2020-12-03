package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.VariableDeclarationObject;
import com.refactoringMatcher.java.ast.decomposition.AbstractExpression;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class PDGExpression  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6824012855154218708L;
	private Set<AbstractVariable> declaredVariables;
	private Set<AbstractVariable> definedVariables;
	private Set<AbstractVariable> usedVariables;
	private Set<String> thrownExceptionTypes;
	
	public PDGExpression(AbstractExpression expression, Set<VariableDeclarationObject> variableDeclarationsInMethod) {
		this.declaredVariables = new LinkedHashSet<AbstractVariable>();
		this.definedVariables = new LinkedHashSet<AbstractVariable>();
		this.usedVariables = new LinkedHashSet<AbstractVariable>();
		this.thrownExceptionTypes = new LinkedHashSet<String>();
		determineDefinedAndUsedVariables(expression);
	}

	public Iterator<AbstractVariable> getDeclaredVariableIterator() {
		return declaredVariables.iterator();
	}

	public Iterator<AbstractVariable> getDefinedVariableIterator() {
		return definedVariables.iterator();
	}

	public Iterator<AbstractVariable> getUsedVariableIterator() {
		return usedVariables.iterator();
	}

	public boolean definesLocalVariable(AbstractVariable variable) {
		return definedVariables.contains(variable);
	}

	public boolean usesLocalVariable(AbstractVariable variable) {
		return usedVariables.contains(variable);
	}

	public boolean throwsException() {
		if(!thrownExceptionTypes.isEmpty())
			return true;
		return false;
	}

	private void determineDefinedAndUsedVariables(AbstractExpression expression) {
		for(PlainVariable variable : expression.getDeclaredLocalVariables()) {
			declaredVariables.add(variable);
			definedVariables.add(variable);
		}
		for(PlainVariable variable : expression.getDefinedLocalVariables()) {
			definedVariables.add(variable);
		}
		for(PlainVariable variable : expression.getUsedLocalVariables()) {
			usedVariables.add(variable);
		}
	}
}
