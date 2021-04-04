package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.WhileStatement;

public class GroumWhileNode extends GroumNode implements Serializable {

	private WhileStatement whileStatement;
	
	public WhileStatement GetWhileStatement(){
		return whileStatement;
	}
	
	public GroumWhileNode(WhileStatement statement, PDGNode pdgNode) {
		super(pdgNode, GroumNodeType.CONTROL);
		whileStatement = statement;
		setValue(ToGroumString());
		determineDefinedAndUsedVariables();
	}

	private void determineDefinedAndUsedVariables() {
		definedVariables.addAll(pdgNode.definedVariables);
		usedVariables.addAll(pdgNode.usedVariables);
		BasicBlock bs = pdgNode.getBasicBlock();
		List<CFGNode> blockCfgNodes = pdgNode.getBasicBlock().getNextBasicBlock().getAllNodes();
		for (CFGNode cfgNode : blockCfgNodes) {
			definedVariables.addAll(cfgNode.getPDGNode().definedVariables);
			usedVariables.addAll(cfgNode.getPDGNode().usedVariables);
		}
	}

	public String ToGroumString(){
		return "WHILE";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8651854045659000268L;
	
}
