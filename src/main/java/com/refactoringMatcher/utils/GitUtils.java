package com.refactoringMatcher.utils;

import com.refactoringMatcher.domain.RepositoryInfo;
import com.refactoringMatcher.java.ast.ConstructorObject;
import com.refactoringMatcher.java.ast.MethodObject;
import gr.uom.java.xmi.LocationInfo;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Diptopol
 * @since 11/28/2020 10:32 AM
 */
public class GitUtils {

    private static Logger logger = LoggerFactory.getLogger(GitUtils.class);

    public static String getWholeTextFromFile(LocationInfo locationInfo, Repository repository, String commitIdStr)
            throws Exception {
        ObjectId commitId = ObjectId.fromString(commitIdStr);

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitId);

            List<String> filePaths = new ArrayList<>();
            filePaths.add(locationInfo.getFilePath());

            Map<String, String> fileContentsCurrent = new LinkedHashMap<>();
            Set<String> repositoryDirectoriesCurrent = new LinkedHashSet<>();

            populateFileContents(repository, commit, filePaths, fileContentsCurrent, repositoryDirectoriesCurrent);

            return fileContentsCurrent.get(locationInfo.getFilePath());

        } catch (Exception e) {
            logger.error("Could not read file: " + locationInfo.getFilePath());
            throw e;
        }
    }

    public static MethodObject createMethodObject(MethodDeclaration methodDeclaration) {
        final ConstructorObject constructorObject = new ConstructorObject(methodDeclaration);
        return new MethodObject(constructorObject);
    }

    public static String extractText(int startOffset, int length, String wholeText, String file) throws IOException, Exception {
        MethodDeclaration methodDeclaration = getMethodDeclaration(file, wholeText, startOffset, length);
        return methodDeclaration.toString();
    }

    public static String getRepositoryLocalDirectory(RepositoryInfo repositoryInfo) {
        return "projectDirectory/" + repositoryInfo.getFullName().replaceAll("/", "-");
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

    private static void populateFileContents(Repository repository, RevCommit commit,
                                             List<String> filePaths, Map<String, String> fileContents,
                                             Set<String> repositoryDirectories) throws Exception {
        RevTree parentTree = commit.getTree();
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(parentTree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                String pathString = treeWalk.getPathString();
                if(filePaths.contains(pathString)) {
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(loader.openStream(), writer);
                    fileContents.put(pathString, writer.toString());
                }
                if(pathString.endsWith(".java") && pathString.contains("/")) {
                    String directory = pathString.substring(0, pathString.lastIndexOf("/"));
                    repositoryDirectories.add(directory);
                    //include sub-directories
                    String subDirectory = new String(directory);
                    while(subDirectory.contains("/")) {
                        subDirectory = subDirectory.substring(0, subDirectory.lastIndexOf("/"));
                        repositoryDirectories.add(subDirectory);
                    }
                }
            }
        }
    }
}
