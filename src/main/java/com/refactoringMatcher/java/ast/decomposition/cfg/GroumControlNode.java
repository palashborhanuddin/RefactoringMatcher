package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.Set;

public class GroumControlNode extends GroumNode {
    public GroumControlNode(PDGNode pdgNode, GroumBlockNode groumBlockNode) {
        super(pdgNode, GroumNodeType.CONTROL);
        setGroumBlockNode(groumBlockNode);
    }
}
