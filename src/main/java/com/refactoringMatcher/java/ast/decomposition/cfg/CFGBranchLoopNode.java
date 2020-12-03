package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CFGBranchLoopNode extends CFGBranchNode  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6220555994537519347L;

	public CFGBranchLoopNode(AbstractStatement statement) {
		super(statement);
	}

	public CFGNode getJoinNode() {
		return this;
	}

	public List<BasicBlock> getNestedBasicBlocks() {
		List<BasicBlock> blocksBetween = new ArrayList<BasicBlock>();
		BasicBlock srcBlock = getBasicBlock();
		Flow falseControlFlow = getFalseControlFlow();
		if(falseControlFlow != null) {
			CFGNode dstNode = (CFGNode)falseControlFlow.getDst();
			if(dstNode.getBasicBlock().getId() < srcBlock.getId() && dstNode instanceof CFGBranchLoopNode) {
				CFGBranchLoopNode loopNode = (CFGBranchLoopNode)dstNode;
				Flow falseControlFlow2 = loopNode.getFalseControlFlow();
				if(falseControlFlow2 != null)
					dstNode = (CFGNode)falseControlFlow2.getDst();
				else
					return getNestedBasicBlocksToEnd();
			}
			BasicBlock dstBlock = dstNode.getBasicBlock();
			if(srcBlock.getId() < dstBlock.getId()) {
				BasicBlock nextBlock = srcBlock;
				while(!nextBlock.getNextBasicBlock().equals(dstBlock)) {
					nextBlock = nextBlock.getNextBasicBlock();
					blocksBetween.add(nextBlock);
				}
			}
			else if(srcBlock.getId() > dstBlock.getId()) {
				return getNestedBasicBlocksToEnd();
			}
		}
		else {
			return getNestedBasicBlocksToEnd();
		}
		return blocksBetween;
	}
}
