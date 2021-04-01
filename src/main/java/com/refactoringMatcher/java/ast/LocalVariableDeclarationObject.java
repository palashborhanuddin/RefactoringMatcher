package com.refactoringMatcher.java.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class LocalVariableDeclarationObject extends VariableDeclarationObject {
    private TypeObject type;
    private String name;
    private ASTInformation scope;
    private ASTInformation variableDeclaration;
    private volatile int hashCode = 0;

    public LocalVariableDeclarationObject(VariableDeclaration localVariableDeclaration) {
        ASTNode scope = null;
        if (localVariableDeclaration != null) {
            if (localVariableDeclaration instanceof VariableDeclarationFragment) {
                ASTNode variableDeclarationParent = localVariableDeclaration.getParent();
                if (variableDeclarationParent instanceof VariableDeclarationStatement) {
                    VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) variableDeclarationParent;
                    this.type = new TypeObject(variableDeclarationStatement.getType().toString());
                    scope = variableDeclarationStatement.getParent();
                } else if (variableDeclarationParent instanceof VariableDeclarationExpression) {
                    this.type = new TypeObject(((VariableDeclarationExpression) variableDeclarationParent).getType().toString());
                    if (variableDeclarationParent.getParent() instanceof ForStatement)
                        scope = variableDeclarationParent.getParent();
                    else if (variableDeclarationParent.getParent() != null) {
                        scope = variableDeclarationParent.getParent();
                    }
                }
            } else if (localVariableDeclaration instanceof SingleVariableDeclaration) {
                this.type = new TypeObject(((SingleVariableDeclaration) localVariableDeclaration).getType().toString());
                scope = localVariableDeclaration.getParent();
            }
            this.scope = ASTInformationGenerator.generateASTInformation(scope);
            this.name = localVariableDeclaration.getName().getIdentifier();
            this.variableDeclaration = ASTInformationGenerator.generateASTInformation(localVariableDeclaration);
        }
    }

    public TypeObject getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public VariableDeclaration getVariableDeclaration() {
        ASTNode node = this.variableDeclaration.recoverASTNode();
        if (node instanceof SimpleName) {
            return (VariableDeclaration) node.getParent();
        } else {
            return (VariableDeclaration) node;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof LocalVariableDeclarationObject) {
            LocalVariableDeclarationObject lvdo = (LocalVariableDeclarationObject) o;
            return this.hashCode() == lvdo.hashCode();
        }
        return false;
    }

    public boolean equals(LocalVariableInstructionObject lvio) {
        return this.name.equals(lvio.getName());
    }

    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            result = 37 * result + name.hashCode();
            result = 37 * result + type.hashCode();
            result = 37 * result + variableDeclaration.hashCode();
            result = 37 * result + scope.getStartPosition();
            hashCode = result;
        }
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ");
        sb.append(name);
        return sb.toString();
    }
}
