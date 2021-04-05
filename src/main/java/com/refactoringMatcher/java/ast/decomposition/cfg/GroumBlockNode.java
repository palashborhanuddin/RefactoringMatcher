package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroumBlockNode {
    private static int blockNum = 0;
    private int id;
    private PDGNode leader;

    public GroumBlockNode(PDGNode node) {
        blockNum++;
        this.id = blockNum;
        this.leader = node;
    }

    public int getId() {
        return id;
    }

    public PDGNode getLeader() {
        return leader;
    }

    public static void resetBlockNum() {
        blockNum = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroumBlockNode blockNode = (GroumBlockNode) o;
        return getId() == blockNode.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "GroumBlockNode{" +
                "id=" + id +
                ", leader=" + leader +
                '}';
    }
}
