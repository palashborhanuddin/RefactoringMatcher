package com.refactoringMatcher.java.ast.decomposition.cfg;

public class GroumActionNode extends GroumNode {
    public GroumActionNode(PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.ACTION);
    }
}
