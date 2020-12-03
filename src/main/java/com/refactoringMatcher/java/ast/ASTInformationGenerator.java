package com.refactoringMatcher.java.ast;

import java.io.Serializable;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;

public class ASTInformationGenerator  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6945510446183453245L;
	private static ITypeRoot iTypeRoot;
	
	public static void setCurrentITypeRoot(ITypeRoot typeRoot) {
		iTypeRoot = typeRoot;
	}

	public static ASTInformation generateASTInformation(ASTNode astNode) {
		return new ASTInformation(iTypeRoot, astNode);
	}
}
