package com.refactoringMatcher.java.ast.decomposition.cfg;

import org.eclipse.jdt.core.dom.FieldAccess;

public class GroumFieldAccessNode extends GroumNode{

    private FieldAccess fieldAccessExpression;

    public GroumFieldAccessNode(FieldAccess statement, PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.ACTION);
        fieldAccessExpression = statement;
        setValue(ToGroumString());
        determineDefinedAndUsedVariables();
    }

    private void determineDefinedAndUsedVariables() {
        definedVariables = pdgNode.definedVariables;
        usedVariables = pdgNode.usedVariables;
    }

    public FieldAccess GetFieldAccessStatement() {
        return fieldAccessExpression;
    }

    public String ToGroumString() {
        // TODO
        return "TODO";
    }
}
