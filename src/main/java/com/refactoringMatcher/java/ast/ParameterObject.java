package com.refactoringMatcher.java.ast;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class ParameterObject extends VariableDeclarationObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5268148924924318457L;
	private TypeObject type;
	private String name;
	private boolean varargs;
	private ASTInformation singleVariableDeclaration;
	private volatile int hashCode = 0;

	public ParameterObject(SingleVariableDeclaration parameter) {
		Type parameterType = parameter.getType();
		this.type = TypeObject.extractTypeObject(parameterType.toString());
		type.setArrayDimension(type.getArrayDimension() + parameter.getExtraDimensions());
		if (parameter.isVarargs()) {
			type.setArrayDimension(1);
		}
		this.name = parameter.getName().getIdentifier();
		this.varargs = parameter.isVarargs();
		this.singleVariableDeclaration = ASTInformationGenerator.generateASTInformation(parameter);
	}

	public TypeObject getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public boolean isVarargs() {
		return varargs;
	}

	public SingleVariableDeclaration getSingleVariableDeclaration() {
		return (SingleVariableDeclaration)this.singleVariableDeclaration.recoverASTNode();
	}

	public boolean equals(Object o) {
		if(this == o) {
            return true;
        }

        if (o instanceof ParameterObject) {
            ParameterObject parameterObject = (ParameterObject)o;
            return this.hashCode() == parameterObject.hashCode();
        }
        
        return false;
	}

	public int hashCode() {
		if(hashCode == 0) {
			int result = 17;
			result = 37*result + name.hashCode();
			result = 37*result + type.hashCode();
			result = 37*result + (varargs?result:0);
			result = 37*result + singleVariableDeclaration.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.toString()).append(" ");
		sb.append(name);
		return sb.toString();
	}

	public VariableDeclaration getVariableDeclaration() {
		return getSingleVariableDeclaration();
	}
}
