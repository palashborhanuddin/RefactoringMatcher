package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;

public class GroumClassInstantiationNode extends GroumActionNode implements Serializable  {

	private ClassInstanceCreation classInstanceCreation;
	
	public ClassInstanceCreation GetClassInstanceCreation() {
		return classInstanceCreation;
	}
	
	public GroumClassInstantiationNode(ClassInstanceCreation statement, PDGNode pdgNode) {
		super(pdgNode);
		classInstanceCreation = statement;
		setValue(ToGroumString());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3562275374298174028L;

	public String ToGroumString() {
		return classInstanceCreation.getType().toString() + ".<init>" ;
	}

}
