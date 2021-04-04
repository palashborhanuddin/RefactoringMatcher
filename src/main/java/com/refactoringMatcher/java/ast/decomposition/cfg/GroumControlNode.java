package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.List;

public class GroumControlNode extends GroumNode{
    public GroumControlNode(PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.CONTROL);
        determineDefinedAndUsedVariables();
    }

    private void determineDefinedAndUsedVariables() {
        definedVariables = pdgNode.definedVariables;
        usedVariables = pdgNode.usedVariables;
        BasicBlock bs = pdgNode.getBasicBlock();
        List<CFGNode> blockCfgNodes = pdgNode.getBasicBlock().getNextBasicBlock().getAllNodes();
        for (CFGNode cfgNode : blockCfgNodes) {
            definedVariables.addAll(cfgNode.getPDGNode().definedVariables);
            usedVariables.addAll(cfgNode.getPDGNode().usedVariables);
        }
    }
}
