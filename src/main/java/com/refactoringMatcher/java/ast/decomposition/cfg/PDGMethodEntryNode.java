package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.AbstractMethodDeclaration;
import com.refactoringMatcher.java.ast.ParameterObject;

import java.io.Serializable;
import java.util.ListIterator;

public class PDGMethodEntryNode extends PDGNode  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2050485918750951938L;
	private AbstractMethodDeclaration method;
	
	public PDGMethodEntryNode(AbstractMethodDeclaration method) {
		super();
		this.method = method;
		this.id = 0;
		ListIterator<ParameterObject> parameterIterator = method.getParameterListIterator();
		while(parameterIterator.hasNext()) {
			ParameterObject parameter = parameterIterator.next();
			PlainVariable parameterVariable = new PlainVariable(parameter.getSingleVariableDeclaration());
			declaredVariables.add(parameterVariable);
			definedVariables.add(parameterVariable);
		}
	}

	public AbstractMethodDeclaration getMethod() {
		return method;
	}

	public BasicBlock getBasicBlock() {
		return null;
	}

	public boolean equals(Object o) {
		if(this == o)
    		return true;
    	
    	if(o instanceof PDGMethodEntryNode) {
    		PDGMethodEntryNode pdgNode = (PDGMethodEntryNode)o;
    		return this.method.equals(pdgNode.method);
    	}
    	return false;
	}

	public int hashCode() {
		return method.hashCode();
	}

	public String toString() {
		return id + "";
	}
}
