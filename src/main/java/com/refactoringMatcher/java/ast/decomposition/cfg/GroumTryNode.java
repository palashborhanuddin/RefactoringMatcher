package com.refactoringMatcher.java.ast.decomposition.cfg;

import org.eclipse.jdt.core.dom.TryStatement;

public class GroumTryNode extends GroumControlNode {
    private TryStatement tryStatement;

    public GroumTryNode(TryStatement statement, PDGNode pdgNode, GroumBlockNode groumBlock) {
        super(pdgNode, groumBlock);
        this.tryStatement = statement;
        setValue(ToGroumString());
    }

    private String ToGroumString() {
        return "TRY";
    }

    public TryStatement getTryStatement() {
        return tryStatement;
    }
}
