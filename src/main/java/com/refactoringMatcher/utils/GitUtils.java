package com.refactoringMatcher.utils;

import com.refactoringMatcher.domain.*;
import com.refactoringMatcher.java.ast.ConstructorObject;
import com.refactoringMatcher.java.ast.MethodObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.*;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Diptopol
 * @since 11/28/2020 10:32 AM
 */
public class GitUtils {

    private static Logger logger = LoggerFactory.getLogger(GitUtils.class);

    public static String getParentCommit(Repository repository, String commitId) throws Exception {
        final RevWalk walk = new RevWalk(repository);
        walk.reset();
        ObjectId id = repository.resolve(commitId);
        RevCommit commit = walk.parseCommit(id);
        return commit.getParents()[0].getName();
    }


    public static String getWholeTextFromFile(LocationInfo locationInfo, Repository repository, String commitId)
            throws IOException, Exception {
        try {
            if (!repository.getFullBranch().equals(commitId))
                new GitServiceImpl().checkout(repository, commitId);

            String wholeText = readFile(new String(repository.getDirectory().getAbsolutePath().replaceAll("\\.git", "")
                    + "/" + locationInfo.getFilePath()), StandardCharsets.UTF_8);
            return wholeText;
        } catch (Exception e) {
            revert(repository);

            logger.error("Could not read file: " + locationInfo.getFilePath());
            throw e;
        }
    }

    public static MethodObject createMethodObject(MethodDeclaration methodDeclaration) {
        final ConstructorObject constructorObject = new ConstructorObject(methodDeclaration);
        MethodObject methodObject = new MethodObject(constructorObject);

        return methodObject;
    }

    public static String extractText(LocationInfo locationInfo, String wholeText, String file) throws IOException, Exception {
        MethodDeclaration methodDeclaration = getMethodDeclaration(file, wholeText, locationInfo);
        String text = methodDeclaration.toString();
        return text;
    }

    public static String extractText(int startOffset, int length, String wholeText, String file) throws IOException, Exception {
        MethodDeclaration methodDeclaration = getMethodDeclaration(file, wholeText, startOffset, length);
        String text = methodDeclaration.toString();
        return text;
    }

    public static String getRepositoryLocalDirectory(RepositoryInfo repositoryInfo) {
        return "projectDirectory/" + repositoryInfo.getFullName().replaceAll("/", "-");
    }

    public static List<RefactoringData> generateRefactoringData(List<Pair<String, Refactoring>> refactoringsFromRM, RepositoryInfo repositoryInfo) throws Exception {
        List<RefactoringData> extractedRefactorings = new ArrayList<>();
        GitService gitService = new GitServiceImpl();

        Repository repository = gitService.cloneIfNotExists(getRepositoryLocalDirectory(repositoryInfo), repositoryInfo.getHtmlUrl());

        for (Pair<String, Refactoring> refactoringFromRM : refactoringsFromRM) {
            try {
                if (refactoringFromRM.getRight().getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                    ExtractOperationRefactoring extractOperationRefactoring = (ExtractOperationRefactoring) refactoringFromRM.getRight();
                    String commitId = refactoringFromRM.getLeft();
                    ExtractMethodRefactoringData extractMethodRefactoring = new ExtractMethodRefactoringData(extractOperationRefactoring, repositoryInfo.getHtmlUrl(),
                            commitId, repository);
                    try {
                        extractMethodRefactoring.retrieveCode(repository);
                        extractedRefactorings.add(extractMethodRefactoring);
                    } catch (Exception e) {
                        logger.error("Could not retrive refactoring details. Skipping Refactoring: " + extractMethodRefactoring.toString());
                        logger.error(e.getStackTrace().toString());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                logger.error("Could not retrive refactoring details. Skipping Refactoring: " + refactoringFromRM.getLeft());
                logger.error(e.getStackTrace().toString());
                e.printStackTrace();
            }

            break;
        }
        return extractedRefactorings;
    }

    public static MethodDeclaration getMethodDeclaration(String file, String wholeText, int startOffSet, int length)
            throws IOException, Exception {
        MethodDeclaration methodDeclaration;
        try {
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            Map options = JavaCore.getOptions();
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
            parser.setCompilerOptions(options);
            parser.setResolveBindings(false);
            parser.setEnvironment(new String[0], new String[] { file }, null, false);
            parser.setSource(wholeText.toCharArray());
            parser.setResolveBindings(true);
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            ASTNode block = NodeFinder.perform(compilationUnit, startOffSet,
                    length);

            methodDeclaration = (MethodDeclaration) block;
        } catch (Exception e) {
            logger.error("Can not extract method from file.");
            throw e;
        }
        return methodDeclaration;
    }

    public static MethodDeclaration getMethodDeclaration(String file, String wholeText, LocationInfo locationInfo)
            throws IOException, Exception {
        MethodDeclaration methodDeclaration;
        try {
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            Map options = JavaCore.getOptions();
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
            parser.setCompilerOptions(options);
            parser.setResolveBindings(false);
            parser.setEnvironment(new String[0], new String[] { file }, null, false);
            parser.setSource(wholeText.toCharArray());
            parser.setResolveBindings(true);
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            ASTNode block = NodeFinder.perform(compilationUnit, locationInfo.getStartOffset(),
                    locationInfo.getLength());
            methodDeclaration = (MethodDeclaration) block;
        } catch (Exception e) {
            logger.error("Can not extract method from file.");
            throw e;
        }
        return methodDeclaration;
    }

    public static void testFile() throws IOException {
        String path = "projectDirectory/TestClass.java";
        String wholeText = GitUtils.readFile(path, StandardCharsets.UTF_8);

        Cache.currentFile = path;
        Cache.currentFileText = wholeText;

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(wholeText.toCharArray());
        parser.setResolveBindings(true);
        CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

        compilationUnit.accept(new ASTVisitor() {

            public boolean visit(MethodDeclaration methodDeclaration) {
                MethodObject methodObject = createMethodObject(methodDeclaration);
                CFG cfg = new CFG(methodObject);
                PDG pdg = new PDG(cfg);
                Graph groum = new Groum(pdg);

                for(GraphNode graphNode : groum.getNodes()) {
                    if (graphNode instanceof GroumNode) {
                        System.out.println(((GroumNode) graphNode).ToGroumString());
                    }
                }

                for (GraphEdge graphEdge : groum.getEdges()) {
                    GroumNode srcNode = (GroumNode) graphEdge.getSrc();
                    GroumNode dstNode = (GroumNode) graphEdge.getDst();

                    System.out.println("From: " + srcNode.ToGroumString() + " To" + dstNode.ToGroumString());
                }

                return false;
            }
        });
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    private static boolean revert(Repository repository) {
        try (Git git = new Git(repository)) {
            git.revert().setStrategy(MergeStrategy.THEIRS).call();
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }
}
