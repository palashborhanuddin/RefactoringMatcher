package com.refactoringMatcher.java.ast.decomposition.cfg;

public class GroumActionNode extends GroumNode {
    private PDGNode ancestorDefinedNode = null;
    public GroumActionNode(PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.ACTION);
    }

    public void setAncestorDefinedNode(PDGNode node) {
        ancestorDefinedNode = node;
    }

    public PDGNode getAncestorDefinedNode() {
        return ancestorDefinedNode;
    }
}
