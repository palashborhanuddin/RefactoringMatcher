package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.Set;

public class GroumControlNode extends GroumNode {
    public GroumControlNode(PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.CONTROL);
        determineDefinedAndUsedVariables();
    }

    public GroumControlNode(PDG pdg, PDGNode pdgNode) {
        super(pdgNode, GroumNodeType.CONTROL);
        determineGroumBlock(pdg, pdgNode);
        determineDefinedAndUsedVariables();
    }

    private void determineGroumBlock(PDG pdg, PDGNode pdgNode) {
        BasicBlock bs = pdgNode.getBasicBlock();
        Set<BasicBlock> as = pdg.forwardReachableBlocks(bs);
      //  System.out.println(as);
    }

    private void determineDefinedAndUsedVariables() {
        definedVariables = pdgNode.definedVariables;
        usedVariables = pdgNode.usedVariables;
       // List<CFGNode> blockCfgNodes = pdgNode.getBasicBlock().getNextBasicBlock().getAllNodes();
       // for (CFGNode cfgNode : blockCfgNodes) {
       //     definedVariables.addAll(cfgNode.getPDGNode().definedVariables);
       //     usedVariables.addAll(cfgNode.getPDGNode().usedVariables);
       // }
    }
}
