package com.refactoringMatcher.java.ast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ITypeRoot;

public abstract class ClassDeclarationObject  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2767006496579305752L;
	protected String name;
	protected List<MethodObject> methodList;
	protected List<FieldObject> fieldList;
	
	public ClassDeclarationObject() {
		this.methodList = new ArrayList<MethodObject>();
		this.fieldList = new ArrayList<FieldObject>();
	}

	public abstract ITypeRoot getITypeRoot();
	public abstract ClassObject getClassObject();
	public abstract IFile getIFile();
	public abstract TypeObject getSuperclass();

	public boolean addMethod(MethodObject method) {
		return methodList.add(method);
	}

	public boolean addField(FieldObject f) {
		return fieldList.add(f);
	}

	public List<MethodObject> getMethodList() {
		return methodList;
	}

	public ListIterator<MethodObject> getMethodIterator() {
		return methodList.listIterator();
	}

	public ListIterator<FieldObject> getFieldIterator() {
		return fieldList.listIterator();
	}

	public int getNumberOfMethods() {
		return methodList.size();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

    public boolean hasFieldType(String className) {
        ListIterator<FieldObject> fi = getFieldIterator();
        while(fi.hasNext()) {
            FieldObject fo = fi.next();
            if(fo.getType().getClassType().equals(className))
                return true;
        }
        return false;
    }

    public FieldObject getField(FieldInstructionObject fieldInstruction) {
		for(FieldObject field : fieldList) {
			if(field.equals(fieldInstruction)) {
				return field;
			}
		}
		return null;
	}
	
	protected FieldObject findField(FieldInstructionObject fieldInstruction) {
		FieldObject field = getField(fieldInstruction);
		if(field != null) {
			return field;
		}
		else {
			TypeObject superclassType = getSuperclass();
			if(superclassType != null) {
/*				ClassObject superclassObject = ASTReader.getSystemObject().getClassObject(superclassType.toString());
				if(superclassObject != null) {
					return superclassObject.findField(fieldInstruction);
				}*/
				throw new NullPointerException();
			}
		}
		return null;
	}
}
