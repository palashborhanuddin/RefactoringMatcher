package com.refactoringMatcher.java.ast.decomposition.cfg;

import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GroumFieldAccessNode extends GroumActionNode {

    private FieldAccess fieldAccessExpression;
    private List<FieldDeclaration> fieldDeclarationList;
    private String value;

    public GroumFieldAccessNode(FieldAccess statement, PDGNode pdgNode, List<FieldDeclaration> fieldDeclarationList, GroumBlockNode groumBlockNode) {
        super(pdgNode);
        fieldAccessExpression = statement;
        this.fieldDeclarationList = fieldDeclarationList;
        setGroumBlockNode(groumBlockNode);
        determineDefinedAndUsedVariables();
        setValue(ToGroumString());
    }

    private void determineDefinedAndUsedVariables() {
        Expression typeName = fieldAccessExpression.getExpression();
        SimpleName fieldName = fieldAccessExpression.getName();

        String type = "";
        Iterator<AbstractVariable> definedVariableIterator = this.pdgNode.getDefinedVariableIterator();

        while (definedVariableIterator.hasNext()) {
            AbstractVariable abstractVariable = definedVariableIterator.next();
            if (Objects.nonNull(abstractVariable)
                    && Objects.nonNull(typeName)
                    && abstractVariable.getVariableName().equals(typeName.toString())) {
                if (abstractVariable.getVariableType() != null) {
                    type = abstractVariable.getVariableType();
                    break;
                }
            }
        }
        if (type.isEmpty()) {
            Iterator<AbstractVariable> usedVariableIterator = this.pdgNode.getUsedVariableIterator();

            while (usedVariableIterator.hasNext()) {
                AbstractVariable abstractVariable = usedVariableIterator.next();
                if (Objects.nonNull(abstractVariable)
                        && Objects.nonNull(typeName)
                        && abstractVariable.getVariableName().equals(typeName.toString())) {
                    if (abstractVariable.getVariableType() != null) {
                        type = abstractVariable.getVariableType();
                        break;
                    }
                }
            }
        }
        if (type.isEmpty()) {
            boolean isFound = false;
            if (typeName instanceof ThisExpression) {
                FieldDeclaration field = fieldDeclarationList.get(0);
                TypeDeclaration parentClass = (TypeDeclaration) field.getParent();
                type = parentClass.getName().toString();
            }
            else if (typeName instanceof SimpleName) {
                for (FieldDeclaration field : fieldDeclarationList) {
                    for (Object variable : field.fragments()) {
                        VariableDeclaration var = (VariableDeclaration) variable;
                        if (var.getName().toString().equals(typeName.toString())) {
                            type = field.getType().toString();
                            isFound = true;
                            break;
                        }
                    }
                    if (isFound)
                        break;
                }
            }
            else if (typeName instanceof FieldAccess) {
                for (FieldDeclaration field : fieldDeclarationList) {
                    for (Object variable : field.fragments()) {
                        VariableDeclaration var = (VariableDeclaration) variable;
                        if (var.getName().toString().equals(typeName.toString())) {
                            type = field.getType().toString();
                            isFound = true;
                            break;
                        }
                    }
                    // TODO GROUM check for other cases.
                    if (isFound)
                        break;
                }
            }
            else if (typeName instanceof MethodInvocation) {
                // TODO GROUM need to have Method Return type!!
            }
        }
        if (type.isEmpty()) {
            // [TODO GROUM] This is to avoid having null/empty filed node label. there is a chance that we couldn't determine the exact type
            value = fieldAccessExpression.toString();
        }
        else {
            value = type + "." + fieldName.toString();
        }
    }

    public FieldAccess GetFieldAccessStatement() {
        return fieldAccessExpression;
    }

    private String ToGroumString() {
        return value;
    }
}
