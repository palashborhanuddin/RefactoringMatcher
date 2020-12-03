package com.refactoringMatcher.java.ast;

import java.io.Serializable;

public abstract class CreationObject  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3682298098662989268L;
	private TypeObject type;
	protected ASTInformation creation;
	
	public CreationObject(TypeObject type) {
		this.type = type;
	}

	public TypeObject getType() {
		return type;
	}
}
