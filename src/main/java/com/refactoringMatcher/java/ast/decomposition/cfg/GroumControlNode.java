package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.Set;

public class GroumControlNode extends GroumNode {
    public GroumControlNode(PDGNode pdgNode, GroumBlockNode groumBlockNode) {
        super(pdgNode, GroumNodeType.CONTROL);
        determineDefinedAndUsedVariables(groumBlockNode);
    }

    private void determineDefinedAndUsedVariables(GroumBlockNode groumBlockNode) {
        // TODO GROUM list the defined and used variables.
        // declared variables should only be aplied to nested block
    }
}
