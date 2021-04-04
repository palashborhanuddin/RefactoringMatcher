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
            processNode(compilationUnit, pdgNode);
        }
        createGroumGraph(pdg);
    }

    private void createGroumGraph(PDG pdg) {
        extractTemporalGroum();

        Set<GroumNode> destinationEdges = new LinkedHashSet<GroumNode>();
        for (GraphNode sourceNode : pdg.getNodes()) {
            GroumNode sourceGroumNode = compoundGroumNodes.get(sourceNode);
            boolean sourceGroumNodeExists = true;
            if (Objects.isNull(sourceGroumNode)) {
                sourceGroumNodeExists = false;
                //continue;
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
                if (sourceGroumNodeExists && (sourceGroumNode.getId() == outGoingEdge.getDst().getId())) {
                    //GROUM's DAG requirement.
                    continue;
                }

                GroumNode destination = getInnerNode(compoundGroumNodes.get(outGoingEdge.getDst()));
                if (destinationEdges.contains(destination))
                    continue;
                if (sourceGroumNodeExists)
                    addEdge(new GraphEdge(sourceGroumNode, destination));
                for (GroumNode node : destinationEdges) {
                    addEdge(new GraphEdge(node, destination));
                }
                destinationEdges.add(destination);
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

    private void processNode(CompilationUnit compilationUnit, PDGNode pdgNode) {
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
