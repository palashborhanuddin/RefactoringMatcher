package com.refactoringMatcher.service;

import com.refactoringMatcher.domain.RefactoringExtractionInfo;
import com.refactoringMatcher.domain.RefactoringInfo;
import com.refactoringMatcher.domain.RepositoryInfo;
import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.MethodObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import com.refactoringMatcher.java.ast.decomposition.cfg.Groum;
import com.refactoringMatcher.java.ast.decomposition.cfg.PDG;
import com.refactoringMatcher.utils.ASTUtils;
import com.refactoringMatcher.utils.Cache;
import com.refactoringMatcher.utils.GitUtils;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import io.vavr.Tuple3;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.util.GitServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Diptopol
 * @since 11/29/2020 11:49 PM
 */
public class RefactoringExtractionService {

    private static Logger logger = LoggerFactory.getLogger(RefactoringExtractionService.class);

    public RefactoringExtractionService() {
    }


    public List<RefactoringInfo> getRefactoringInfo(List<Pair<String, Refactoring>> refactoringsFromRM, RepositoryInfo repositoryInfo) throws Exception {
        List<RefactoringInfo> extractedRefactorings = new ArrayList<>();
        GitService gitService = new GitServiceImpl();

        try {
            Repository repository = gitService.cloneIfNotExists(getRepositoryLocalDirectory(repositoryInfo), repositoryInfo.getHtmlUrl());

            for (Pair<String, Refactoring> refactoringFromRM : refactoringsFromRM) {
                if (refactoringFromRM.getRight().getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                    ExtractOperationRefactoring extractOperationRefactoring = (ExtractOperationRefactoring) refactoringFromRM.getRight();
                    String commitId = refactoringFromRM.getLeft();

                    RefactoringInfo refactoringInfo = new RefactoringInfo();

                    refactoringInfo.setCommitId(commitId);
                    refactoringInfo.setProjectUrl(repositoryInfo.getHtmlUrl());

                    LocationInfo extractedCodeLocationInfo = extractOperationRefactoring.getExtractedOperation().getLocationInfo();
                    refactoringInfo.setExtracted(getRefactoringExtractionInfo(repository, commitId, extractedCodeLocationInfo));

                    extractedRefactorings.add(refactoringInfo);
                }
            }
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return extractedRefactorings;
    }

    public void populateRefactoringExtractionInfo(RefactoringExtractionInfo refactoringExtractionInfo,
                                                  String type, RepositoryInfo repositoryInfo, String commitId) {
        try {
            Cache.currentFile = refactoringExtractionInfo.getFilePath();
            Cache.currentFileText = refactoringExtractionInfo.getSourceCode();

            CompilationUnit compilationUnit = ASTUtils.getCompilationUnit(refactoringExtractionInfo.getSourceCode());
            List<ImportDeclaration> importDeclarationList = compilationUnit.imports();
            List<ImportObject> importObjectList = importDeclarationList.stream().map(ImportObject::new)
                    .collect(Collectors.toList());

            MethodDeclaration methodDeclaration = (MethodDeclaration) NodeFinder.perform(compilationUnit,
                    refactoringExtractionInfo.getStartOffset(),
                    refactoringExtractionInfo.getLength());

            String code = methodDeclaration.toString();

            GitService gitService = new GitServiceImpl();
            Repository repository = gitService.cloneIfNotExists(getRepositoryLocalDirectory(repositoryInfo), repositoryInfo.getHtmlUrl());

            Optional<String> effectivePOM = GitUtils.generateEffectivePom(commitId, repository);

            // TODO GROUM check how to get field declerations here
            List<FieldDeclaration> fieldDeclarationList = new ArrayList<>();
            Set<Tuple3<String, String, String>> jarSet = new HashSet<>();
            if (effectivePOM.isPresent()) {
                jarSet = GitUtils.listOfJavaProjectLibraryFromEffectivePom(effectivePOM.get());
            }

            PDG extractedMethodPDG = new PDG(ASTUtils.createMethodObject(methodDeclaration, importObjectList, fieldDeclarationList, jarSet), importObjectList, fieldDeclarationList);

            Groum extractedMethodGroum = new Groum(extractedMethodPDG);

            refactoringExtractionInfo.setCode(code);
            refactoringExtractionInfo.setPdg(extractedMethodPDG);
            refactoringExtractionInfo.setGroum(extractedMethodGroum);

            System.out.println("Source: " +code);
            System.out.println(extractedMethodGroum.toString());

        } catch (Exception e) {
            logger.error("Could not retrieve info about the extracted method! Type: {}", type);
        }
    }


    private String getRepositoryLocalDirectory(RepositoryInfo repositoryInfo) {
        return "projectDirectory/" + repositoryInfo.getFullName().replaceAll("/", "-");
    }

    private RefactoringExtractionInfo getRefactoringExtractionInfo(Repository repository, String commitId, LocationInfo locationInfo) {
        try {
            String filePath = locationInfo.getFilePath();
            String sourceCode = GitUtils.getWholeTextFromFile(locationInfo,
                    repository, commitId);

            return new RefactoringExtractionInfo(filePath, sourceCode,
                    locationInfo.getStartOffset(), locationInfo.getLength());
        } catch (Exception e) {
            logger.error("Failed in refactoring extraction", e);
        }

        return null;
    }

}
