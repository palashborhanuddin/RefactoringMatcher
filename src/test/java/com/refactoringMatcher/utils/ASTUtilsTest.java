package com.refactoringMatcher.utils;

import com.refactoringMatcher.dbConnection.DBConnection;
import com.refactoringMatcher.domain.RefactoringExtractionInfo;
import com.refactoringMatcher.domain.RefactoringInfo;
import com.refactoringMatcher.domain.RepositoryInfo;
import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.MethodObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphNode;
import com.refactoringMatcher.java.ast.decomposition.cfg.GraphEdge;
import com.refactoringMatcher.java.ast.decomposition.cfg.Groum;
import com.refactoringMatcher.java.ast.decomposition.cfg.PDG;
import com.refactoringMatcher.service.RefactoringDetectionService;
import com.refactoringMatcher.service.RefactoringExtractionService;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.*;
import org.junit.Test;
import org.refactoringminer.api.Refactoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.Scanner;
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
            //System.out.println("PDG: \n" +extractedMethodPDG.toString());

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

    @Test
    public void GroumForProjectsTest() {
        try {
            RefactoringExtractionService extractionService = new RefactoringExtractionService();

            RefactoringDetectionService refactoringDetectionService = new RefactoringDetectionService();

            String path = "/Users/palashborhan/Personal/ms/Concordia/Thesis/RefactoringMatcher/commitsmined/";
            File file = new File(path);

            String[] fileList = file.list();

            FileWriter fileWriter = new FileWriter("/Users/palashborhan/Personal/ms/Concordia/Thesis/RefactoringMatcher/groum.txt");
            FileWriter vertexFileWriter = new FileWriter("/Users/palashborhan/Personal/ms/Concordia/Thesis/RefactoringMatcher/vertex.txt");
            FileWriter edgeFileWriter = new FileWriter("/Users/palashborhan/Personal/ms/Concordia/Thesis/RefactoringMatcher/edge.txt");
            FileWriter queryFileWriter = new FileWriter("/Users/palashborhan/Personal/ms/Concordia/Thesis/RefactoringMatcher/query.txt");

            int query = 1;

            for(String fileName : fileList){
                List<String> commits = new ArrayList<>();
                String projectName = "";
                String projectUrl = "";
                try (Scanner scanner = new Scanner(new File(path + fileName));) {
                    if (scanner.hasNextLine())
                        projectName = scanner.nextLine();
                    if (scanner.hasNextLine())
                        projectUrl = scanner.nextLine();
                    while (scanner.hasNextLine()) {
                        commits.add(scanner.nextLine());
                    }

                    RepositoryInfo repositoryInfo = new RepositoryInfo(projectName, projectUrl);
                    for (String commitId : commits) {
                        List<Pair<String, Refactoring>> refactoringPairList =
                                refactoringDetectionService.detectRefactoringData(repositoryInfo, commitId);

                        List<RefactoringInfo> refactoringList = extractionService.getRefactoringInfo(refactoringPairList, repositoryInfo);

                        for (RefactoringInfo refactoringInfo : refactoringList) {
                            extractionService.populateRefactoringExtractionInfo(refactoringInfo.getExtracted(), "extracted", repositoryInfo, commitId);
                            RefactoringExtractionInfo refactoringExtractionInfo = refactoringInfo.getExtracted();
                            if (Objects.nonNull(refactoringExtractionInfo)) {
                                String ss = refactoringExtractionInfo.getCode();
                                if (Objects.isNull(ss))
                                    continue;
                                fileWriter.write(ss);
                                fileWriter.write(refactoringExtractionInfo.getGroum().toString());
                                Graph groum = refactoringExtractionInfo.getGroum();
                                Set<GraphNode> nodes = groum.getNodes();
                                Set<GraphEdge> edges = groum.getEdges();
                                for (GraphNode node : nodes) {
                                    String nodeName = node.getValue();
                                    nodeName = nodeName.replaceAll(",", "_COMMA_");
                                    vertexFileWriter.write(Integer.toString(node.getId()) + "," + nodeName + "\n");
                                }
                                for (GraphEdge edge : edges) {
                                    edgeFileWriter.write(Integer.toString(edge.getSrc().getId()) + "," + Integer.toString(edge.getDst().getId()) + "\n");
                                }
                                for (GraphEdge edge : edges) {
                                    String srcNodeName = edge.getSrc().getValue();
                                    srcNodeName = srcNodeName.replaceAll(",", "_COMMA_");
                                    String dstNodeName = edge.getDst().getValue();
                                    dstNodeName = dstNodeName.replaceAll(",", "_COMMA_");
                                    queryFileWriter.write(Integer.toString(query) + "," + Integer.toString(edge.getSrc().getId()) + "," + Integer.toString(edge.getDst().getId()) + "," + srcNodeName + "," + dstNodeName + "\n");
                                }
                                if (edges.size() > 0)
                                    query++;
                            }
                        }
                        edgeFileWriter.flush();
                        vertexFileWriter.flush();
                        queryFileWriter.flush();
                        fileWriter.flush();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            edgeFileWriter.close();
            vertexFileWriter.close();
            fileWriter.close();
            queryFileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
