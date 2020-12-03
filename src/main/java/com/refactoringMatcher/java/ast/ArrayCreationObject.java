package com.refactoringMatcher.java.ast;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.ArrayCreation;

public class ArrayCreationObject extends CreationObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6622264328196038417L;

	public ArrayCreationObject(TypeObject type) {
		super(type);
	}

	public ArrayCreation getArrayCreation() {
		return (ArrayCreation)this.creation.recoverASTNode();
	}

	public void setArrayCreation(ArrayCreation creation) {
		this.creation = ASTInformationGenerator.generateASTInformation(creation);
	}
}
