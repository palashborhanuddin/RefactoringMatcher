package com.refactoringMatcher.java.ast.decomposition.cfg;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.SimpleName;

import java.util.Iterator;
import java.util.Objects;

public class GroumFieldAccessNode extends GroumActionNode {

    private FieldAccess fieldAccessExpression;
    private String value;

    public GroumFieldAccessNode(FieldAccess statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
        super(pdgNode);
        fieldAccessExpression = statement;
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
                    && Objects.nonNull(fieldName)
                    && abstractVariable.getVariableName().equals(fieldName.toString())) {
                definedVariables.add(abstractVariable);
                if (abstractVariable.getVariableType() != null) {
                    type = abstractVariable.getVariableType();
                }
            }
            if (Objects.nonNull(abstractVariable)
                    && Objects.nonNull(typeName)
                    && abstractVariable.getVariableName().equals(typeName.toString())) {
                if (abstractVariable.getVariableType() != null) {
                    type = abstractVariable.getVariableType();
                }
            }
        }
        if (type.isEmpty()) {
            Iterator<AbstractVariable> usedVariableIterator = this.pdgNode.getUsedVariableIterator();

            while (usedVariableIterator.hasNext()) {
                AbstractVariable abstractVariable = usedVariableIterator.next();
                if (Objects.nonNull(abstractVariable)
                        && Objects.nonNull(fieldName)
                        && abstractVariable.getVariableName().equals(fieldName.toString())) {
                    usedVariables.add(abstractVariable);
                    if (abstractVariable.getVariableType() != null) {
                        type = abstractVariable.getVariableType();
                    }
                }
                if (Objects.nonNull(abstractVariable)
                        && Objects.nonNull(typeName)
                        && abstractVariable.getVariableName().equals(typeName.toString())) {
                    if (abstractVariable.getVariableType() != null) {
                        type = abstractVariable.getVariableType();
                    }
                }
            }
        }
        if (type.isEmpty()) {
            // [TODO GROUM] This is to avoid having null/empty filed node label. there is a chance that we couldn't determine the exact type
            value = fieldAccessExpression.toString();
        } else {
            value = type + "." + fieldName.toString();
        }
    }

    public FieldAccess GetFieldAccessStatement() {
        return fieldAccessExpression;
    }

    public String ToGroumString() {
        return value;
    }
}
