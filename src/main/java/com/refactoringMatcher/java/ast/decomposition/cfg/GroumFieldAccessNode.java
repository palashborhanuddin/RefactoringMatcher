package com.refactoringMatcher.java.ast.decomposition.cfg;

import org.eclipse.jdt.core.dom.FieldAccess;

public class GroumFieldAccessNode extends GroumActionNode {

    private FieldAccess fieldAccessExpression;

    public GroumFieldAccessNode(FieldAccess statement, PDGNode pdgNode, GroumBlockNode groumBlockNode) {
        super(pdgNode);
        fieldAccessExpression = statement;
        setValue(ToGroumString());
        setGroumBlockNode(groumBlockNode);
    }

    public FieldAccess GetFieldAccessStatement() {
        return fieldAccessExpression;
    }

    public String ToGroumString() {
        // TODO
        return "TODO";
    }
}
