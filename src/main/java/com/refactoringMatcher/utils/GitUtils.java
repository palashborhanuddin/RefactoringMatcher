package com.refactoringMatcher.utils;

import gr.uom.java.xmi.LocationInfo;
import io.vavr.Tuple3;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public static String getRepositoryLocalDirectory(String repositoryFullName) {
        return "projectDirectory/" + repositoryFullName.replaceAll("/", "-");
    }

    public static Optional<String> generateEffectivePom(String commitID, Repository repository) {
        Map<Path, String> poms = populateFileContents(repository, commitID, fileName -> fileName.endsWith("pom.xml"));
        Path pomFilesDirectoryPath = Paths.get(PropertyReader.getProperty("pom.files.directory"));
        String mavenHome = PropertyReader.getProperty("maven.home");

        Path p = pomFilesDirectoryPath.resolve("tmp").resolve(commitID);

        FileUtils.materializeAtBase(p, poms);

        Path effectivePomPath = p.resolve("effectivePom.xml");

        if (!effectivePomPath.toFile().exists()) {
            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(p.resolve("pom.xml").toAbsolutePath().toString()));
            request.setGoals(Arrays.asList("help:effective-pom", "-Doutput=" + effectivePomPath.toAbsolutePath().toString()));
            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(new File(mavenHome));
            try {
                InvocationResult result = invoker.execute(request);
                if (result.getExitCode() != 0) {
                    logger.info("Build Failed");
                    logger.info("Could not generate effective pom");

                    return Optional.empty();
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        String effectivePomPathContent = FileUtils.readFile(effectivePomPath);
        FileUtils.deleteDirectory(p);

        return Optional.ofNullable(effectivePomPathContent);
    }

    private static Map<Path, String> populateFileContents(Repository repository, String commitId,
                                                          Predicate<String> predicate) {
        Map<Path, String> fileContents = new HashMap<>();
        Optional<RevCommit> commit = findCommit(commitId, repository);

        if (commit.isPresent()) {
            return populateFileContent(repository, commit.get(), predicate);
        }

        return fileContents;
    }

    private static Optional<RevCommit> findCommit(String commitSHAId, Repository repository) {
        ObjectId commitId = ObjectId.fromString(commitSHAId);
        RevCommit commit = null;

        try (RevWalk revWalk = new RevWalk(repository)) {
            commit = revWalk.parseCommit(commitId);

        } catch (IOException ex) {
            logger.error("CommitID couldn't be parsed", ex);
        }

        return Optional.ofNullable(commit);
    }

    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    private static Map<Path, String> populateFileContent(Repository repository, RevCommit revCommit,
                                                         Predicate<String> predicate) {
        Map<Path, String> fileContents = new HashMap<>();
        RevTree parentTree = revCommit.getTree();

        if (parentTree != null) {
            try (TreeWalk treeWalk = new TreeWalk(repository)) {

                treeWalk.addTree(parentTree);
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    String pathString = treeWalk.getPathString();

                    if (predicate.test(pathString)) {
                        ObjectId objectId = treeWalk.getObjectId(0);
                        ObjectLoader loader = repository.open(objectId);
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(loader.openStream(), writer);
                        fileContents.put(Paths.get(pathString), writer.toString());
                    }
                }
            } catch (Exception ex) {
                logger.error("Couldn't read file", ex);
            }
        }

        return fileContents;
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

                if (filePaths.contains(pathString)) {
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(loader.openStream(), writer);
                    fileContents.put(pathString, writer.toString());
                }

                if (pathString.endsWith(".java") && pathString.contains("/")) {
                    String directory = pathString.substring(0, pathString.lastIndexOf("/"));
                    repositoryDirectories.add(directory);
                    //include sub-directories
                    String subDirectory = directory;
                    while (subDirectory.contains("/")) {
                        subDirectory = subDirectory.substring(0, subDirectory.lastIndexOf("/"));
                        repositoryDirectories.add(subDirectory);
                    }
                }
            }
        }
    }

    public static Set<Tuple3<String, String, String>> listOfJavaProjectLibraryFromEffectivePom(String pomContent) {
        Set<Tuple3<String, String, String>> jarSet = new HashSet<>();

        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new ByteArrayInputStream(pomContent.getBytes(StandardCharsets.UTF_8)));
            Element root = document.getRootElement();

            HashMap propertiesList = new HashMap();

            try {
                Element properties = getchild(root, "properties");
                List<Element> propertiesListNode = properties.getChildren();

                for(int temp = 0; temp < propertiesListNode.size(); ++temp) {
                    Element property = (Element)propertiesListNode.get(temp);
                    propertiesList.put("${" + property.getName() + "}", property.getValue());
                }
            } catch (Exception var25) {
            }

            Element dependencies = getchild(root, "dependencies");

            List<Element> dependencytList = dependencies.getChildren();

            for(int temp = 0; temp < dependencytList.size(); ++temp) {
                Element dependency = (Element)dependencytList.get(temp);
                List<Element> librariesList = dependency.getChildren();
                String groupId = "";
                String artifactId = "";
                String version = "";

                for(int temp1 = 0; temp1 < librariesList.size(); ++temp1) {
                    Element libraryInfo = (Element)librariesList.get(temp1);
                    if (libraryInfo.getName().equals("groupId")) {
                        groupId = libraryInfo.getValue();
                    }

                    if (libraryInfo.getName().equals("artifactId")) {
                        artifactId = libraryInfo.getValue();
                    }

                    if (libraryInfo.getName().equals("version")) {
                        version = libraryInfo.getValue();
                        if (version.startsWith("${")) {
                            version = (String)propertiesList.get(version);
                        }
                    }
                }

                Tuple3<String, String, String> tuple = new Tuple3<>(groupId, artifactId, version);
                jarSet.add(tuple);
            }
        } catch (Exception var26) {
            System.out.println(var26.toString());
            System.out.println(var26.getStackTrace().toString());
            System.out.println(var26.getMessage());
        }

        return jarSet;
    }

    private static Element getchild(Element classElement, String name) {
        try {
            List<Element> childrens = classElement.getChildren();

            for(int temp = 0; temp < childrens.size(); ++temp) {
                Element element = (Element)childrens.get(temp);
                if (element.getName().equals(name)) {
                    return element;
                }
            }
        } catch (Exception var6) {
            System.out.println("No child found under:" + name);
        }

        return null;
    }
}
