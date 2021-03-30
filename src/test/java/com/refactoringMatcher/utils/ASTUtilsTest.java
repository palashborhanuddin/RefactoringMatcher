package com.refactoringMatcher.utils;

import com.refactoringMatcher.dbConnection.DBConnection;
import com.refactoringMatcher.domain.RefactoringExtractionInfo;
import com.refactoringMatcher.domain.RefactoringInfo;
import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.MethodObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import com.refactoringMatcher.java.ast.decomposition.cfg.Groum;
import com.refactoringMatcher.java.ast.decomposition.cfg.PDG;
import org.eclipse.jdt.core.dom.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Diptopol
 * @since 12/18/2020 9:05 PM
 */
public class ASTUtilsTest {

    @Test
    public void methodCodeExtractionTest() {
        DBConnection connection = new DBConnection();

        List<RefactoringInfo> refactoringInfoList = connection.getRefactoringInfoList();

        RefactoringExtractionInfo firstRefactoringExtractionInfo = refactoringInfoList.get(0).getExtracted();

        try {
            String code = ASTUtils.extractText(firstRefactoringExtractionInfo.getStartOffset(),
                    firstRefactoringExtractionInfo.getLength(),
                    firstRefactoringExtractionInfo.getSourceCode(), firstRefactoringExtractionInfo.getFilePath());

            System.out.println(code);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

    }

    @Test
    public void pdgExtractionTest() {
        try {
            String filePath = "testFileDirectory/GroumWhileTestClass.java";
            Cache.currentFile = filePath;
            Cache.currentFileText = GitUtils.readFile(filePath);
            MethodDeclaration methodDeclaration = getMethodObjects(filePath).get(0).getMethodDeclaration();

            CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(Cache.currentFileText);
            List<ImportDeclaration> importDeclarationList = compilationUnit.imports();
            List<ImportObject> importObjectList = importDeclarationList.stream().map(ImportObject::new)
                    .collect(Collectors.toList());

            methodDeclaration = (MethodDeclaration) NodeFinder.perform(compilationUnit,
                    methodDeclaration.getStartPosition(), methodDeclaration.getLength());

            PDG extractedMethodPDG = new PDG(ASTUtils.createMethodObject(methodDeclaration, importObjectList), importObjectList);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void GroumTest() {
        try {
            String filePath = "testFileDirectory/GroumWhileTestClass.java";
            Cache.currentFile = filePath;
            Cache.currentFileText = GitUtils.readFile(filePath);
            MethodDeclaration methodDeclaration = getMethodObjects(filePath).get(0).getMethodDeclaration();

            CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(Cache.currentFileText);
            List<ImportDeclaration> importDeclarationList = compilationUnit.imports();
            List<ImportObject> importObjectList = importDeclarationList.stream().map(ImportObject::new)
                    .collect(Collectors.toList());

            methodDeclaration = (MethodDeclaration) NodeFinder.perform(compilationUnit,
                    methodDeclaration.getStartPosition(), methodDeclaration.getLength());

            PDG extractedMethodPDG = new PDG(ASTUtils.createMethodObject(methodDeclaration, importObjectList), importObjectList);
            System.out.println("PDG: \n" +extractedMethodPDG.toString());

            Groum extractedMethodGroum = new Groum(extractedMethodPDG);
            System.out.println("GROUM: \n" +extractedMethodGroum.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<MethodObject> getMethodObjects(String filePath) throws IOException {
        String sourceCode = GitUtils.readFile(filePath);

        Cache.currentFile = filePath;
        Cache.currentFileText = sourceCode;

        CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(sourceCode);

        List<ImportDeclaration> importDeclarationList = compilationUnit.imports();

        List<ImportObject> importObjectList = importDeclarationList.stream().map(ImportObject::new)
                .collect(Collectors.toList());

        List<MethodObject> methodObjectList = new ArrayList<>();

        compilationUnit.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                methodObjectList.add(ASTUtils.createMethodObject(node, importObjectList));


                return false;
            }
        });

        return methodObjectList;
    }

}
