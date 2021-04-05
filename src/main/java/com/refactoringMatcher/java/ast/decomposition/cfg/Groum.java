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
    private List<GroumBlockNode> groumBlocks;

    public Groum(PDG pdg) {
        compoundGroumNodes = new HashMap<PDGNode, GroumNode>();
        groumBlocks = new ArrayList<GroumBlockNode>();
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
                    findEdges(src, dst);
                    for (GroumNode node : listOfOutgoingEdgesDestination) {
                        findEdges(node, dst);
                    }
                    listOfOutgoingEdgesDestination.add(dst);
                }
            }
        }
    }

    private void findEdges(GroumNode src, GroumNode dst) {
        if (Objects.nonNull(src) && Objects.nonNull(dst)) {
            if (src.getId() != dst.getId()) {
                addEdge(new GraphEdge(src, dst));
                findEdges(src.GetInnerNode(), dst);
                findEdges(src, dst.GetInnerNode());
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
                GroumIfNode gin = new GroumIfNode(pdg, statement, pdgNode);
                groumNodes.push(gin);
                return true;
            }

            public boolean visit(WhileStatement statement) {
                GroumWhileNode gwn = new GroumWhileNode(statement, pdgNode);
                groumNodes.push(gwn);
                return true;
            }

            public boolean visit(ForStatement statement) {
                GroumForNode gfn = new GroumForNode(statement, pdgNode);
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
