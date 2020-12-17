package com.refactoringMatcher.utils;

import com.refactoringMatcher.domain.RepositoryInfo;
import gr.uom.java.xmi.LocationInfo;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static String getRepositoryLocalDirectory(RepositoryInfo repositoryInfo) {
        return "projectDirectory/" + repositoryInfo.getFullName().replaceAll("/", "-");
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
