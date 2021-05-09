package com.refactoringMatcher.representation;

import com.refactoringMatcher.dbConnection.DBConnection;
import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphNode;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphEdge;
import com.refactoringMatcher.java.ast.decomposition.cfg.PDG;
import com.refactoringMatcher.java.ast.decomposition.cfg.Groum;
import com.refactoringMatcher.utils.ASTUtils;
import com.refactoringMatcher.utils.Cache;
import com.refactoringMatcher.utils.GitUtils;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Diptopol
 * @since 12/5/2020 7:40 PM
 */
public class GroumTest {

    List<FieldDeclaration> fieldDeclarationList = new ArrayList<>();

    @Test
    public void testSaveGroumRepresentation() throws Exception {
        //String filePath = "testFileDirectory/GroumForTestClass.java";
        String filePath = "testFileDirectory/GroumWhileTestClass.java";
        Groum groum = getGroumRepresentation(filePath);

        DBConnection connection = new DBConnection();
        String uuid = UUID.randomUUID().toString();

        connection.insertGraphNodes(groum, uuid);
        connection.insertGraphEdge(groum, uuid);

        System.out.println(uuid);
        connection.close();
    }

    @Test
    public void testGroumRepresentation() {
        //String filePath = "testFileDirectory/GroumForTestClass.java";
        String filePath = "testFileDirectory/GroumWhileTestClass.java";
        Groum groum = getGroumRepresentation(filePath);

        System.out.println(groum);
        // Following asserts to satisfy GROUM generated from the paper source example.
        assert groum.getNodes().size() == 10;
        assert groum.getEdges().size() == 19;
        assert verifyGroumRepresentation(groum, GroumRepresentationVerificator.groumNodesOfPaperExample, GroumRepresentationVerificator.groumEdgesOfPaperExample);
    }

    private boolean verifyGroumRepresentation(Groum groum, Set<String> nodes, Set<String> edges) {
        for (GraphNode gnode : groum.getNodes()) {
            if (!nodes.contains(gnode.toString()))
                return false;
        }
        for (GraphEdge gedge : groum.getEdges()) {
            if (!edges.contains(gedge.toString()))
                return false;
        }
        return true;
    }

    private Groum getGroumRepresentation(String filePath) {
        List<Groum> groumGraphList = new ArrayList<>();

        try {
            Cache.currentFile = filePath;
            Cache.currentFileText = GitUtils.readFile(filePath);
            List<MethodDeclaration> methodDeclarationList = getMethodObjects(filePath);

            CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(Cache.currentFileText);
            List<ImportDeclaration> importDeclarationList = compilationUnit.imports();
            List<ImportObject> importObjectList = importDeclarationList.stream().map(ImportObject::new)
                    .collect(Collectors.toList());

            methodDeclarationList.forEach(methodObject -> {
                PDG extractedMethodPDG = new PDG(ASTUtils.createMethodObject(methodDeclarationList.get(0), importObjectList, fieldDeclarationList, new HashSet<>()), importObjectList, fieldDeclarationList);
                groumGraphList.add(new Groum(extractedMethodPDG));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Test class will only contain one method.
         */
        return groumGraphList.get(0);
    }

    private List<MethodDeclaration> getMethodObjects(String filePath) throws IOException {
        String sourceCode = GitUtils.readFile(filePath);

        Cache.currentFile = filePath;
        Cache.currentFileText = sourceCode;

        CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(sourceCode);

        List<ImportDeclaration> importDeclarationList = compilationUnit.imports();

        List<ImportObject> importObjectList = importDeclarationList.stream().map(ImportObject::new)
                .collect(Collectors.toList());

        List<MethodDeclaration> methodDeclarationList = new ArrayList<>();

        compilationUnit.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                methodDeclarationList.add(node);

                return false;
            }

            @Override
            public boolean visit(FieldDeclaration node) {
                fieldDeclarationList.add(node);

                return false;
            }
        });

        return methodDeclarationList;
    }
}
