package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;
import java.util.*;

import org.eclipse.jdt.core.dom.AST;
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

    private HashMap<PDGNode, GroumNode> compoundGroumNodes;
    private HashMap<GroumBlockNode, Set<CFGNode>> groumBlocks;

    public Groum(PDG pdg) {
        compoundGroumNodes = new HashMap<PDGNode, GroumNode>();
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
        groumBlocks = new LinkedHashMap<GroumBlockNode, Set<CFGNode>>();
        Map<CFGBranchNode, Set<CFGNode>> pdgNestingMap = pdg.getPDGNestingMap();
        for(CFGBranchNode key : pdgNestingMap.keySet()) {
            Set<CFGNode> nestedNodes = pdgNestingMap.get(key);
            GroumBlockNode block = new GroumBlockNode();
            groumBlocks.put(block, nestedNodes);
        }
        createGroumGraph(pdg);
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
                if (Objects.nonNull(sourceGroumNode)) {
                    GroumNode src = sourceGroumNode;
                    GroumNode dst = compoundGroumNodes.get(outGoingEdge.getDst());
                    if (GroumNodeType.CONTROL.equals(src.getGroumNodeType())) {
                        src = src.GetInnerNode();
                    }
                    if (GroumNodeType.CONTROL.equals(dst.getGroumNodeType())) {
                        dst = dst.GetInnerNode();
                    }
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
                if (src.definedVariables.size() > 0) {
                    boolean related = false;
                    for (AbstractVariable variable : src.definedVariables) {
                        if (dst.definedVariables.contains(variable) || dst.usedVariables.contains(variable))
                            related = true;
                    }
                    if (related)
                        addEdge(new GraphEdge(src, dst));
                }
                findEdgesActionNode(src.GetInnerNode(), dst);
                findEdgesActionNode(src, dst.GetInnerNode());
            }
        }
    }

    private void extractTemporalGroum() {
        GroumNode currentNode = null;
        GroumNode lastNode = null;

        // [TODO GROUM] Parallel merging won't work here, need to address

        for (GroumNode node : compoundGroumNodes.values()) {
            if (Objects.isNull(node)) {
                continue;
            }

            currentNode = constructInnerNode(node);

            if (Objects.nonNull(lastNode)) {
                addEdge(new GraphEdge(lastNode, currentNode));
            }

            lastNode = node;
        }
    }

    private GroumNode getInnerNode(GroumNode groumNode) {
        if (!groumNode.HasInnerNode()) {
            return groumNode;
        } else {
            return getInnerNode(groumNode.GetInnerNode());
        }
    }

    private GroumNode constructInnerNode(GroumNode groumNode) {
        // [TODO GROUM] may have issue with parallel node
        if (!groumNode.HasInnerNode()) {
            addNode(groumNode);
            return groumNode;
        } else {
            GroumNode innerNode = constructInnerNode(groumNode.GetInnerNode());
            addNode(groumNode);
            addEdge(new GraphEdge(groumNode.GetInnerNode(), groumNode));
            return innerNode;
        }
    }

    private GroumBlockNode getGroumBlock(PDGNode pdgNode) {
        for(GroumBlockNode key : groumBlocks.keySet()) {
            Set<CFGNode> nestedNodes = groumBlocks.get(key);
            if(nestedNodes.contains(pdgNode.getCFGNode()))
                return key;
        }
        return null;
    }

    private void processNode(PDG pdg, CompilationUnit compilationUnit, PDGNode pdgNode) {
        Stack<GroumNode> groumNodes = new Stack<GroumNode>();
        compilationUnit.accept(new ASTVisitor() {
            public boolean visit(ClassInstanceCreation statement) {
                GroumClassInstantiationNode gcicn = new GroumClassInstantiationNode(statement, pdgNode);
                groumNodes.push(gcicn);
                return true;
            }

            public boolean visit(MethodInvocation statement) {
                GroumMethodInvocationNode gmn = new GroumMethodInvocationNode(statement, pdgNode);
                if (!gmn.IsLocal())
                    groumNodes.push(gmn);
                return true;
            }

            public boolean visit(FieldAccess statement) {
                GroumFieldAccessNode fieldAccessNode = new GroumFieldAccessNode(statement, pdgNode);
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
            GroumNode previousNode = groumNodes.peek();
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
