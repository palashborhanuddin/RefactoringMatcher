package com.refactoringMatcher.java.ast;

import java.io.Serializable;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;

public class AnonymousClassDeclarationObject extends ClassDeclarationObject  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1758804424797822243L;
	private ASTInformation anonymousClassDeclaration;
	private ClassObject classObject;
	
	public void setAnonymousClassDeclaration(AnonymousClassDeclaration anonymous) {
		this.anonymousClassDeclaration = ASTInformationGenerator.generateASTInformation(anonymous);
	}
	
	public AnonymousClassDeclaration getAnonymousClassDeclaration() {
		return (AnonymousClassDeclaration)anonymousClassDeclaration.recoverASTNode();
	}

	public ClassObject getClassObject() {
		return classObject;
	}

	public void setClassObject(ClassObject classObject) {
		this.classObject = classObject;
	}

	public ITypeRoot getITypeRoot() {
		return anonymousClassDeclaration.getITypeRoot();
	}

	public IFile getIFile() {
		if(classObject != null) {
			return classObject.getIFile();
		}
		return null;
	}

	public TypeObject getSuperclass() {
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("\n\n").append("Fields:");
        for(FieldObject field : fieldList)
            sb.append("\n").append(field.toString());

        sb.append("\n\n").append("Methods:");
        for(MethodObject method : methodList)
            sb.append("\n").append(method.toString());

        return sb.toString();
	}
}
