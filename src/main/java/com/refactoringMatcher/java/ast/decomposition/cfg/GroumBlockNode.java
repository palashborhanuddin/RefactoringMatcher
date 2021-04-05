package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.ArrayList;
import java.util.List;

public class GroumBlockNode {
    private static int blockNum = 0;
    private int id;
    private GroumNode leader;
    private List<GroumNode> nodes;
    private GroumBlockNode innerGroumBlock;

    public GroumBlockNode() {
        blockNum++;
        this.id = blockNum;
    }

    public GroumBlockNode(GroumNode node) {
        blockNum++;
        this.id = blockNum;
        this.leader = node;
        node.setGroumBlockNode(this);
        this.nodes = new ArrayList<GroumNode>();
    }

    public int getId() {
        return id;
    }

    public GroumNode getLeader() {
        return leader;
    }

    public List<GroumNode> getNodes() {
        return nodes;
    }

    public List<GroumNode> getAllNodes() {
        List<GroumNode> allNodes = new ArrayList<GroumNode>();
        allNodes.add(leader);
        allNodes.addAll(nodes);
        return allNodes;
    }

    public void add(GroumNode node) {
        nodes.add(node);
        node.setGroumBlockNode(this);
    }

    public static void resetBlockNum() {
        blockNum = 0;
    }

    @Override
    public String toString() {
        return "GroumBlockNode{" +
                "id=" + id +
                ", leader=" + leader +
                ", nodes=" + nodes +
                ", innerGroumBlock=" + innerGroumBlock +
                '}';
    }
}
