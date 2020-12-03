package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class GroumMethodNode extends GroumNode implements Serializable {

	private MethodInvocation methodInvocation;
	
	public MethodInvocation GetMethodInvocation() {
		return methodInvocation;
	}
	
	public GroumMethodNode(MethodInvocation statement, PDGNode pdgNode) {
		super(pdgNode);
		methodInvocation = statement;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7222706825193092243L;

	@Override
	public String ToGroumString() {
		return methodInvocation.getName().toString();
	}

}
