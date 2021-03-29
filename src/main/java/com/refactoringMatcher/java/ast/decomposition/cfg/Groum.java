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

public class Groum extends Graph implements Serializable {

    private HashMap<PDGNode, GroumNode> compoundGroumNodes = new HashMap<PDGNode, GroumNode>();

    public Groum(PDG pdg) {
        for (GraphNode graphNode : pdg.nodes) {
            PDGNode pdgNode = (PDGNode) graphNode;
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            String statement = "public class DummyClass{void dummy(){" + pdgNode.getCFGNode().getStatementString() + ";}}";
            parser.setSource(statement.toCharArray());
            parser.setResolveBindings(true);
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            processNode(compilationUnit, pdgNode);
        }

        //CreateGroumGraph(pdg);
        createGroumGraph(pdg);
    }

    private void createGroumGraph(PDG pdg) {
        GroumNode currentNode = null;
        GroumNode lastNode = null;

        for (GroumNode node : compoundGroumNodes.values()) {
            if (Objects.isNull(node)) {
                continue;
            }

            currentNode = constructInnerNode(node);

            if (Objects.nonNull(lastNode)) {
                addEdge(new GraphEdge(lastNode, currentNode, this));
            }

            lastNode = node;
        }

        Set<GroumNode> destinationEdges = new LinkedHashSet<GroumNode>();
        for (GraphNode sourceNode : pdg.getNodes()) {
            GroumNode sourceGroumNode = compoundGroumNodes.get(sourceNode);

            if (Objects.isNull(sourceGroumNode)) {
                continue;
            }

            destinationEdges.clear();
            for (GraphEdge outGoingEdge : sourceNode.getOutgoingEdges()) {
                if (Objects.isNull(compoundGroumNodes.get(outGoingEdge.getDst())))
                    continue;

                PDGDependence outGoingPDGEdge = (PDGDependence) outGoingEdge;
                if (PDGDependenceType.ANTI.equals(outGoingPDGEdge.getType())) {
                    // GROUM's DAG requirement.
                    continue;
                }
                if (sourceGroumNode.getId() == outGoingEdge.getDst().getId()) {
                    //GROUM's DAG requirement.
                    continue;
                }

                GroumNode destination = getInnerNode(compoundGroumNodes.get(outGoingEdge.getDst()));
                if (destinationEdges.contains(destination))
                    continue;
                addEdge(new GraphEdge(sourceGroumNode, destination, this));
                for (GroumNode node : destinationEdges) {
                    addEdge(new GraphEdge(node, destination, this));
                }
                destinationEdges.add(destination);
            }
        }
    }

    /*private void CreateGroumGraph(PDG pdg) {
        GroumNode lastNode = null;
        GroumNode currentNode = null;
        for (GroumNode node : compoundGroumNodes.values()) {
            if (node == null)
                continue;
            currentNode = CreateNodesFor(node);

            if (lastNode != null) {
                addEdge(new GraphEdge(lastNode, currentNode, this));
            }

            lastNode = node;
        }

        for (GraphNode node : pdg.getNodes()) {
            if (compoundGroumNodes.get(node) == null)
                continue;
            GroumNode source = GetTop(compoundGroumNodes.get(node));

            for (GraphEdge edge : node.outgoingEdges) {
                if (compoundGroumNodes.get(edge.getDst()) == null)
                    continue;

                GroumNode destination = GetTop(compoundGroumNodes.get(edge.getDst()));
                addEdge(new GraphEdge(source, destination, this));
            }
        }
    }*/

    private GroumNode getInnerNode(GroumNode groumNode) {
        if (!groumNode.HasInnerNode()) {
            return groumNode;
        } else {
            return getInnerNode(groumNode.GetInnerNode());
        }
    }

    /*private GroumNode GetTop(GroumNode groumNode) {
        if (!groumNode.HasInnerNode()) {
            return groumNode;
        } else {
            return CreateNodesFor(groumNode.GetInnerNode());
        }
    }*/

    /*private GroumNode CreateNodesFor(GroumNode groumNode) {
        addNode(groumNode);
        if (!groumNode.HasInnerNode()) {
            return groumNode;
        } else {
            addEdge(new GraphEdge(groumNode.GetInnerNode(), groumNode, this));
            return CreateNodesFor(groumNode.GetInnerNode());
        }
    }*/

    private GroumNode constructInnerNode(GroumNode groumNode) {
        if (!groumNode.HasInnerNode()) {
            addNode(groumNode);
            return groumNode;
        } else {
            GroumNode innerNode = constructInnerNode(groumNode.GetInnerNode());
            addNode(groumNode);
            addEdge(new GraphEdge(groumNode.GetInnerNode(), groumNode, this));
            return innerNode;
        }
    }

    private void processNode(CompilationUnit compilationUnit, PDGNode pdgNode) {
        Stack<GroumNode> groumNodes = new Stack<GroumNode>();
        compilationUnit.accept(new ASTVisitor() {
            public boolean visit(ClassInstanceCreation statement) {
                GroumClassInstantiationNode gcicn = new GroumClassInstantiationNode(statement, pdgNode);
                groumNodes.push(gcicn);
                return true;
            }

            public boolean visit(MethodInvocation statement) {
                GroumMethodNode gmn = new GroumMethodNode(statement, pdgNode);
                groumNodes.push(gmn);
                return true;
            }

            public boolean visit(IfStatement statement) {
                GroumIfNode gin = new GroumIfNode(statement, pdgNode);
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
            // [TODO Question] what if there are multiple inner node? there is link to previous popped node
            //  as the current one is going to replace that.
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
                "nodes=" + nodes +
                ",\nedges=" + edges +
                '}';
    }
}
