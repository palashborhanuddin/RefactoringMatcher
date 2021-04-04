package com.refactoringMatcher.java.ast.decomposition.cfg;

public class GroumActionNode extends GroumNode {
    public GroumActionNode(PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.ACTION);
        determineDefinedAndUsedVariables();
    }

    private void determineDefinedAndUsedVariables() {
        definedVariables.addAll(pdgNode.definedVariables);
        usedVariables.addAll(pdgNode.usedVariables);
    }
}
