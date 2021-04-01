package com.refactoringMatcher.java.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

public class LocalVariableInstructionObject {
    private String name;
    private ASTInformation simpleName;
    private volatile int hashCode = 0;

    public LocalVariableInstructionObject(SimpleName simpleName) {
        this.simpleName = ASTInformationGenerator.generateASTInformation(simpleName);
        this.name = simpleName.getIdentifier();
    }

    public String getName() {
        return name;
    }

    public SimpleName getSimpleName() {
        ASTNode node = this.simpleName.recoverASTNode();
        if(node instanceof QualifiedName) {
            return ((QualifiedName)node).getName();
        }
        else {
            return (SimpleName)node;
        }
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if (o instanceof LocalVariableInstructionObject) {
            LocalVariableInstructionObject lvio = (LocalVariableInstructionObject)o;
            return this.hashCode() == lvio.hashCode();
        }
        return false;
    }

    public boolean equals(LocalVariableDeclarationObject lvdo) {
        return this.name.equals(lvdo.getName());
    }

    public boolean equals(ParameterObject parameter) {
        return this.name.equals(parameter.getName());
    }

    public int hashCode() {
        if(hashCode == 0) {
            int result = 17;
            result = 37*result + name.hashCode();
            result = 37*result + simpleName.hashCode();
            hashCode = result;
        }
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        return sb.toString();
    }
}
