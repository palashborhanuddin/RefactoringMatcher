package com.refactoringMatcher.representation;

import com.refactoringMatcher.dbConnection.DBConnection;
import com.refactoringMatcher.java.ast.MethodObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.Groum;
import com.refactoringMatcher.java.ast.decomposition.cfg.PDG;
import com.refactoringMatcher.utils.ASTUtils;
import com.refactoringMatcher.utils.Cache;
import com.refactoringMatcher.utils.GitUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Diptopol
 * @since 12/5/2020 7:40 PM
 */
public class GroumTest {

    @Test
    public void testSaveGroumRepresentation() throws Exception {
        Groum groum = getGroumRepresentationWhileTestClass();

        DBConnection connection = new DBConnection();
        String uuid = UUID.randomUUID().toString();

        connection.insertGraphNodes(groum, uuid);
        connection.insertGraphEdge(groum, uuid);

        System.out.println(uuid);
        connection.close();
    }

    @Test
    public void testGroumRepresentationWhileStatement() {
        Groum groum = getGroumRepresentationWhileTestClass();

        assert groum.getNodes().size() == 11;
        assert groum.getEdges().size() == 15;
    }

    @Test
    public void testGroumRepresentationForStatement() {
        List<Groum> groumGraphList = new ArrayList<>();

        try {
            String filePath = "testFileDirectory/GroumForTestClass.java";
            List<MethodObject> methodObjectList = getMethodObjects(filePath);

            methodObjectList.forEach(methodObject -> {
                PDG extractedMethodPDG = new PDG(methodObject);
                groumGraphList.add(new Groum(extractedMethodPDG));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Groum getGroumRepresentationWhileTestClass() {
        List<Groum> groumGraphList = new ArrayList<>();

        try {
            String filePath = "testFileDirectory/GroumWhileTestClass.java";
            List<MethodObject> methodObjectList = getMethodObjects(filePath);

            methodObjectList.forEach(methodObject -> {
                PDG extractedMethodPDG = new PDG(methodObject);
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

    private List<MethodObject> getMethodObjects(String filePath) throws IOException {
        String sourceCode = GitUtils.readFile(filePath, StandardCharsets.UTF_8);

        Cache.currentFile = filePath;
        Cache.currentFileText = sourceCode;

        CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(sourceCode);

        List<MethodObject> methodObjectList = new ArrayList<>();

        compilationUnit.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                methodObjectList.add(ASTUtils.createMethodObject(node));

                return false;
            }
        });

        return methodObjectList;
    }
}
