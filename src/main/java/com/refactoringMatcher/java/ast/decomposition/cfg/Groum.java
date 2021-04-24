package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.*;

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
    private HashMap<PDGNode, GroumNode> compoundGroumNodes;
    private HashMap<GroumBlockNode, Set<CFGNode>> groumBlocks;

    public Groum(PDG pdg) {
        compoundGroumNodes = new HashMap<PDGNode, GroumNode>();
        groumBlocks = new LinkedHashMap<GroumBlockNode, Set<CFGNode>>();
        Map<CFGBranchNode, Set<CFGNode>> pdgNestingMap = pdg.getPDGNestingMap();
        //GroumNode.setNodeNum(lastNodeNum);
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
            GroumNode sourceGroumNode = compoundGroumNodes.get(sourcePdgNode);
            groumEdgesCorrespondPdgOutgoingEdges(sourceGroumNode, sourcePdgNode);
        }
    }

    private void groumEdgesCorrespondPdgOutgoingEdges(GroumNode sourceGroumNode, GraphNode sourcePdgNode) {
        Set<GroumNode> listOfOutgoingEdgesDestination = new HashSet<GroumNode>();
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
                    GroumNode dst = compoundGroumNodes.get(outGoingEdge.getDst());

                    findEdgesActionNode(src, dst);
                    for (GroumNode node : listOfOutgoingEdgesDestination) {
                        findEdgesActionNode(node, dst);
                    }
                    listOfOutgoingEdgesDestination.add(dst);
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
                                GroumNode srcControlNode = compoundGroumNodes.get(srcBlock.getLeader());
                                GroumNode dstControlNode = compoundGroumNodes.get(dstBlock.getLeader());
                                addEdge(new GraphEdge(srcControlNode, dstControlNode));
                            }
                        }
                    }
                }
                for (GroumNode snode : src.GetInnerNodes()) {
                    findEdgesActionNode(snode, dst);
                    for (GroumNode dnode : dst.GetInnerNodes()) {
                        findEdgesActionNode(snode, dnode);
                    }
                }
            }
        }
    }

    private void extractTemporalGroum() {
        GroumNode lastNode = null;

        for (GroumNode node : compoundGroumNodes.values()) {
            if (Objects.isNull(node)) {
                continue;
            }

            List<GroumNode> currentNodes = new ArrayList<GroumNode>();
            constructInnerNode(node, currentNodes);
            if (Objects.nonNull(lastNode)) {
                for (GroumNode currentNode : currentNodes)
                    addEdge(new GraphEdge(lastNode, currentNode));
            }

            lastNode = node;
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
            }
        }
        return groumNodes.peek();
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

        while (groumNodes.size() > 1) {
            GroumNode poppedNode = groumNodes.pop();
            GroumNode previousNode = findPreviousNode(groumNodes, poppedNode);
            previousNode.SetInnerNode(poppedNode);
        }

        if (!groumNodes.isEmpty()) {
            GroumNode node = groumNodes.pop();
            compoundGroumNodes.put(pdgNode, node);
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
