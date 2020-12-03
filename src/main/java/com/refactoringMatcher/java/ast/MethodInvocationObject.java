package com.refactoringMatcher.java.ast;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationObject extends AbstractMethodInvocationObject  implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3309327924574581472L;

	public MethodInvocationObject(TypeObject originClassType, String methodName, TypeObject returnType) {
        super(originClassType, methodName, returnType);
    }

    public MethodInvocationObject(TypeObject originClassType, String methodName, TypeObject returnType, List<TypeObject> parameterList) {
        super(originClassType, methodName, returnType, parameterList);
    }

    public MethodInvocationObject(String methodInvocationName, TypeObject returnType) {
    	super(methodInvocationName, returnType);
	}

	public void setMethodInvocation(MethodInvocation methodInvocation) {
    	//this.methodInvocation = methodInvocation;
    	this.methodInvocation = ASTInformationGenerator.generateASTInformation(methodInvocation);
    }

    public MethodInvocation getMethodInvocation() {
    	//return this.methodInvocation;
    	return (MethodInvocation)this.methodInvocation.recoverASTNode();
    }
}