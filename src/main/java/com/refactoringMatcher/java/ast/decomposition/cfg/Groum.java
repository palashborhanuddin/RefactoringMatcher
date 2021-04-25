package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.time.Instant;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.FieldAccess;

public class Groum extends Graph implements Serializable {

    // TODO PROJECTTEST lastNodeNum is temporary change to generate project data. remove afterwards
    // Set the number to start with the Groum node#
    private static int lastNodeNum = 0;
    private HashMap<PDGNode, List<GroumNode>> compoundGroumNodes;
    private HashMap<GroumBlockNode, Set<CFGNode>> groumBlocks;

    public Groum(PDG pdg) {
        compoundGroumNodes = new HashMap<PDGNode, List<GroumNode>>();
        groumBlocks = new LinkedHashMap<GroumBlockNode, Set<CFGNode>>();
        Map<CFGBranchNode, Set<CFGNode>> pdgNestingMap = pdg.getPDGNestingMap();
        //GroumNode.setNodeNum(lastNodeNum);
        long prev = Instant.now().toEpochMilli();
        for(CFGBranchNode key : pdgNestingMap.keySet()) {
            Set<CFGNode> nestedNodes = pdgNestingMap.get(key);
            GroumBlockNode block = new GroumBlockNode(key.getPDGNode());
            groumBlocks.put(block, nestedNodes);
        }
        for (GraphNode graphNode : pdg.nodes) {
            PDGNode pdgNode = (PDGNode) graphNode;
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            String statement = "public class DummyClass{void dummy(){" + pdgNode.getCFGNode().getStatementString() + ";}}";
            parser.setSource(statement.toCharArray());
            parser.setResolveBindings(true);
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            processNode(pdg, compilationUnit, pdgNode);
        }
        createGroumGraph(pdg);
        GraphNode.resetNodeNum();
        GroumBlockNode.resetBlockNum();
        long now = Instant.now().toEpochMilli();
        System.out.println("Time taken: " + (now - prev) + " ms, started: " + prev + ", ended: " + now);
        //lastNodeNum = GraphNode.getNodeNum();
    }

    private void createGroumGraph(PDG pdg) {
        extractTemporalGroum();
        extractEdgesForActionNodes(pdg);
        // extractEdgesBetweenControlNodes(pdg);
    }

    private void extractEdgesForActionNodes(PDG pdg) {
        GraphNode previous = null;
        for (GraphNode sourcePdgNode : pdg.getNodes()) {
            List<GroumNode> sourceGroumNodes = compoundGroumNodes.get(sourcePdgNode);
            if (Objects.nonNull(sourceGroumNodes)) {
                for (GroumNode sourceGroumNode : sourceGroumNodes) {
                    groumEdgesCorrespondPdgOutgoingEdges(sourceGroumNode, sourcePdgNode);
                }
            }
        }
    }

    private void groumEdgesCorrespondPdgOutgoingEdges(GroumNode sourceGroumNode, GraphNode sourcePdgNode) {
        Set<List<GroumNode>> listOfOutgoingEdgesDestination = new HashSet<List<GroumNode>>();
        for (GraphEdge outGoingEdge : sourcePdgNode.getOutgoingEdges()) {
            if (Objects.isNull(compoundGroumNodes.get(outGoingEdge.getDst())))
                continue;
            PDGDependence outGoingPDGEdge = (PDGDependence) outGoingEdge;

            if (PDGDependenceType.CONTROL.equals(outGoingPDGEdge.getType())
                    || PDGDependenceType.DATA.equals(outGoingPDGEdge.getType())
                    || PDGDependenceType.DEF_ORDER.equals(outGoingPDGEdge.getType())) {
                if (PDGDependenceType.DATA.equals(outGoingPDGEdge.getType())) {
                    PDGAbstractDataDependence outGoingPDGAbstractEdge = (PDGAbstractDataDependence) outGoingEdge;
                    if (outGoingPDGAbstractEdge.isLoopCarried())
                        continue;
                }
                if (Objects.nonNull(sourceGroumNode)) {
                    GroumNode src = sourceGroumNode;
                    List<GroumNode> dsts = compoundGroumNodes.get(outGoingEdge.getDst());

                    for (GroumNode dst : dsts) {
                        findEdgesActionNode(src, dst);
                        for (List<GroumNode> nodeList : listOfOutgoingEdgesDestination) {
                            for (GroumNode node : nodeList)
                                findEdgesActionNode(node, dst);
                        }
                    }
                    listOfOutgoingEdgesDestination.add(dsts);
                }
            }
        }
    }

    private void findEdgesActionNode(GroumNode src, GroumNode dst) {
        if (Objects.nonNull(src) && Objects.nonNull(dst)) {
            if (src.getId() != dst.getId()) {
                if (!GroumNodeType.CONTROL.equals(src.getGroumNodeType()) && !GroumNodeType.CONTROL.equals(dst.getGroumNodeType())) {
                    if (src.definedVariables.size() > 0) {
                        boolean related = false;
                        for (AbstractVariable variable : src.definedVariables) {
                            if (dst.definedVariables.contains(variable) || dst.usedVariables.contains(variable))
                                related = true;
                        }
                        if (related) {
                            addEdge(new GraphEdge(src, dst));
                            // Find if belongs to groum control structure
                            GroumBlockNode srcBlock = getNestedGroumBlock(src.GetPdgNode());
                            GroumBlockNode dstBlock = getNestedGroumBlock(dst.GetPdgNode());
                            if (Objects.nonNull(srcBlock) && Objects.nonNull(dstBlock) && !srcBlock.equals(dstBlock)) {
                                List<GroumNode> srcControlNodes = compoundGroumNodes.get(srcBlock.getLeader());
                                List<GroumNode> dstControlNodes = compoundGroumNodes.get(dstBlock.getLeader());
                                for (GroumNode srcControlNode : srcControlNodes) {
                                    for (GroumNode dstControlNode : dstControlNodes) {
                                        addEdge(new GraphEdge(srcControlNode, dstControlNode));
                                    }
                                }
                            }
                        }
                    }
                }
                for (GroumNode dnode : dst.GetInnerNodes()) {
                    findEdgesActionNode(src, dnode);
                }
                for (GroumNode snode : src.GetInnerNodes()) {
                    findEdgesActionNode(snode, dst);
                }
            }
        }
    }

    private void extractTemporalGroum() {
        List<GroumNode> lastNodes = null;

        for (List<GroumNode> nodeList : compoundGroumNodes.values()) {
            if (Objects.isNull(nodeList)) {
                continue;
            }

            for (GroumNode node : nodeList) {
                List<GroumNode> currentNodes = new ArrayList<GroumNode>();
                constructInnerNode(node, currentNodes);
                if (Objects.nonNull(lastNodes)) {
                    for (GroumNode lastNode : lastNodes) {
                        for (GroumNode currentNode : currentNodes) {
                            addEdge(new GraphEdge(lastNode, currentNode));
                        }
                    }
                }
            }

            lastNodes = nodeList;
        }
    }

    private void constructInnerNode(GroumNode groumNode, List<GroumNode> innerNodes) {
        if (!groumNode.HasInnerNode()) {
            addNode(groumNode);
            innerNodes.add(groumNode);
        } else {
            List<GroumNode> nodeList = groumNode.GetInnerNodes();
            for (GroumNode node : nodeList) {
                constructInnerNode(node, innerNodes);
                addNode(groumNode);
                addEdge(new GraphEdge(node, groumNode));
            }
        }
    }

    private GroumBlockNode getGroumBlock(PDGNode pdgNode) {
        for(GroumBlockNode key : groumBlocks.keySet()) {
            if(key.getLeader().equals(pdgNode))
                return key;
        }
        return null;
    }

    private GroumBlockNode getNestedGroumBlock(PDGNode pdgNode) {
        for(GroumBlockNode key : groumBlocks.keySet()) {
            Set<CFGNode> nestedNodes = groumBlocks.get(key);
            if(nestedNodes.contains(pdgNode.getCFGNode()))
                return key;
        }
        return null;
    }

    private GroumNode findPreviousNode(Deque<GroumNode> groumNodes, GroumNode poppedNode) {
        Iterator nodeIterator = groumNodes.iterator();

        GroumNode gnode = null;

        while (nodeIterator.hasNext()) {
            GroumNode node = (GroumNode)nodeIterator.next();
            if (poppedNode instanceof GroumMethodInvocationNode) {
                GroumMethodInvocationNode gmi = (GroumMethodInvocationNode)poppedNode;
                ASTNode astNode = gmi.GetMethodInvocation();
                if (matchForParent(node, astNode)) return node;
            } else if (poppedNode instanceof GroumClassInstantiationNode) {
                GroumClassInstantiationNode gci = (GroumClassInstantiationNode) poppedNode;
                ASTNode astNode = gci.GetClassInstanceCreation();
                if (matchForParent(node, astNode)) return node;
            } else if (poppedNode instanceof GroumFieldAccessNode) {
                GroumFieldAccessNode gfa = (GroumFieldAccessNode) poppedNode;
                ASTNode astNode = gfa.GetFieldAccessStatement();
                if (matchForParent(node, astNode)) return node;
            } else if (poppedNode instanceof GroumWhileNode) {
                GroumWhileNode gwn = (GroumWhileNode) poppedNode;
                ASTNode astNode = gwn.GetWhileStatement();
                if (matchForParent(node, astNode)) return node;
            } else if (poppedNode instanceof GroumIfNode) {
                GroumIfNode gin = (GroumIfNode) poppedNode;
                ASTNode astNode = gin.GetIfStatement();
                if (matchForParent(node, astNode)) return node;
            }
        }
        return gnode; // not found
    }

    private boolean matchForParent(GroumNode node, ASTNode astNode) {
        if (astNode == null)
            return false;
        if (node instanceof GroumMethodInvocationNode) {
            GroumMethodInvocationNode gnode = (GroumMethodInvocationNode)node;
            if (astNode.getParent() == gnode.GetMethodInvocation()) {
                return true;
            }
        }
        else if (node instanceof GroumClassInstantiationNode) {
            GroumClassInstantiationNode gnode = (GroumClassInstantiationNode) node;
            if (astNode.getParent() == gnode.GetClassInstanceCreation()) {
                return true;
            }
        }
        else if (node instanceof GroumWhileNode) {
            GroumWhileNode gnode = (GroumWhileNode) node;
            if (astNode.getParent() == gnode.GetWhileStatement()) {
                return true;
            }
        }
        else if (node instanceof GroumForNode) {
            GroumForNode gnode = (GroumForNode) node;
            if (astNode.getParent() == gnode.GetForStatement()) {
                return true;
            }
        }
        else if (node instanceof GroumIfNode) {
            GroumIfNode gnode = (GroumIfNode) node;
            if (astNode.getParent() == gnode.GetIfStatement()) {
                return true;
            }
        }
        return matchForParent(node, astNode.getParent());
    }

    private void processNode(PDG pdg, CompilationUnit compilationUnit, PDGNode pdgNode) {
        Deque<GroumNode> groumNodes = new ArrayDeque<GroumNode>();
        compilationUnit.accept(new ASTVisitor() {
            public boolean visit(ClassInstanceCreation statement) {
                GroumClassInstantiationNode gcicn = new GroumClassInstantiationNode(statement, pdgNode, getNestedGroumBlock(pdgNode));
                groumNodes.push(gcicn);
                return true;
            }

            public boolean visit(MethodInvocation statement) {
                GroumMethodInvocationNode gmn = new GroumMethodInvocationNode(statement, pdgNode, getNestedGroumBlock(pdgNode));
                if (!gmn.IsLocal())
                    groumNodes.push(gmn);
                return true;
            }

            public boolean visit(FieldAccess statement) {
                GroumFieldAccessNode fieldAccessNode = new GroumFieldAccessNode(statement, pdgNode, getNestedGroumBlock(pdgNode));
                groumNodes.push(fieldAccessNode);
                return true;
            }

            public boolean visit(IfStatement statement) {
                GroumIfNode gin = new GroumIfNode(statement, pdgNode, getGroumBlock(pdgNode));
                groumNodes.push(gin);
                return true;
            }

            public boolean visit(WhileStatement statement) {
                GroumWhileNode gwn = new GroumWhileNode(statement, pdgNode, getGroumBlock(pdgNode));
                groumNodes.push(gwn);
                return true;
            }

            public boolean visit(ForStatement statement) {
                GroumForNode gfn = new GroumForNode(statement, pdgNode, getGroumBlock(pdgNode));
                groumNodes.push(gfn);
                return true;
            }
        });

        List<GroumNode> initParallelNodes = new ArrayList<GroumNode>();
        while (groumNodes.size() > 1) {
            GroumNode poppedNode = groumNodes.pop();
            GroumNode previousNode = findPreviousNode(groumNodes, poppedNode);
            if (Objects.nonNull(previousNode)) {
                previousNode.SetInnerNode(poppedNode);
            } else {
                initParallelNodes.add(poppedNode);
            }
        }

        if (!groumNodes.isEmpty()) {
            GroumNode node = groumNodes.pop();
            initParallelNodes.add(node);
            compoundGroumNodes.put(pdgNode, initParallelNodes);
        } else {
            compoundGroumNodes.put(pdgNode, null);
        }
    }

    @Override
    public String toString() {
        return "Groum{" +
                "\nnodes=" + nodes +
                ",\nedges=" + edges +
                "\n}";
    }
}
