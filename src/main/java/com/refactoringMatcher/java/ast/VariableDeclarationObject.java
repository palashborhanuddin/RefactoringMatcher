package com.refactoringMatcher.java.ast;

import org.eclipse.jdt.core.dom.VariableDeclaration;

public abstract class VariableDeclarationObject {
	public abstract VariableDeclaration getVariableDeclaration();
	public abstract String getName();
}
