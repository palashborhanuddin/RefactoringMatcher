package com.refactoringMatcher.domain;

import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import com.refactoringMatcher.java.ast.decomposition.cfg.Groum;
import com.refactoringMatcher.java.ast.decomposition.cfg.PDG;
import com.refactoringMatcher.utils.Cache;
import com.refactoringMatcher.utils.GitUtils;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.Refactoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diptopol
 * @since 11/28/2020 10:17 AM
 */
public class ExtractMethodRefactoringData implements RefactoringData {

    private static Logger logger = LoggerFactory.getLogger(ExtractMethodRefactoringData.class);

    private ExtractOperationRefactoring refactoring;
    private String projectLink;
    private String previousCommitId;
    private String commitId;

    private String extractedMethodCode;
    private String sourceMethodBeforeExtractionCode;
    private String sourceMethodAfterExtractionCode;

    private Graph extractedMethodPDG;
    private Graph sourceMethodBeforeExtractionPDG;
    private Graph sourceMethodAfterExtractionPDG;

    private Graph extractedMethodGroum;
    private Graph sourceMethodBeforeExtrationGroum;
    private Graph sourceMethodAfterExtractionGroum;


    public ExtractMethodRefactoringData(ExtractOperationRefactoring refactoring, String projectLink,
                                        String commitId, Repository repository) throws Exception {

        this.refactoring = refactoring;
        this.projectLink = projectLink;
        this.commitId = commitId;
        this.previousCommitId = GitUtils.getParentCommit(repository, commitId);
    }


    @Override
    public Refactoring getRefactoring() {
        return this.refactoring;
    }

    @Override
    public String getCommitId() {
        return this.commitId;
    }

    @Override
    public String getProjectLink() {
        return this.projectLink;
    }

    @Override
    public void prepareForSerialization() {

    }

    @Override
    public void recoverAfterDeserialization() {

    }

    public void retrieveCode(Repository repository) throws Exception {
        String currentFile;
        String currentFileText;

        try {
            currentFile = refactoring.getExtractedOperation().getLocationInfo().getFilePath();
            currentFileText = GitUtils.getWholeTextFromFile(refactoring.getExtractedOperation().getLocationInfo(),
                    repository, commitId);

            Cache.currentFile = currentFile;
            Cache.currentFileText = currentFileText;

            this.extractedMethodCode = GitUtils.extractText(refactoring.getExtractedOperation().getLocationInfo(), currentFileText, currentFile);

            PDG extractedMethod = new PDG(GitUtils.createMethodObject(GitUtils.getMethodDeclaration(currentFile, currentFileText,
                    refactoring.getExtractedOperation().getLocationInfo())));

            this.extractedMethodPDG = extractedMethod;

            this.extractedMethodGroum = new Groum(extractedMethod);
        } catch (Exception e) {
            logger.error("Could not retrieve info about the extracted method! " + toString());
            e.printStackTrace();
        }
        try {
            currentFile = refactoring.getSourceOperationAfterExtraction().getLocationInfo().getFilePath();
            currentFileText = GitUtils.getWholeTextFromFile(
                    refactoring.getSourceOperationAfterExtraction().getLocationInfo(), repository, commitId);

            Cache.currentFile = currentFile;
            Cache.currentFileText = currentFileText;

            this.sourceMethodAfterExtractionCode = GitUtils.extractText(
                    refactoring.getSourceOperationAfterExtraction().getLocationInfo(), currentFileText, currentFile);

            PDG sourceMethodAfterExtraction = new PDG(GitUtils.createMethodObject(GitUtils.getMethodDeclaration(currentFile, currentFileText,
                    refactoring.getSourceOperationAfterExtraction().getLocationInfo())));

            this.sourceMethodAfterExtractionPDG = sourceMethodAfterExtraction;

            this.sourceMethodAfterExtractionGroum = new Groum(sourceMethodAfterExtraction);
        } catch (Exception e) {
            logger.error("Could not retrieve info about the source method after extraction! " + toString());
            e.printStackTrace();
        }
        try {
            currentFile = refactoring.getSourceOperationBeforeExtraction().getLocationInfo().getFilePath();
            currentFileText = GitUtils.getWholeTextFromFile(
                    refactoring.getSourceOperationBeforeExtraction().getLocationInfo(), repository, previousCommitId);

            Cache.currentFile = currentFile;
            Cache.currentFileText = currentFileText;

            this.sourceMethodBeforeExtractionCode = GitUtils.extractText(
                    refactoring.getSourceOperationBeforeExtraction().getLocationInfo(), currentFileText, currentFile);

            PDG sourceMethodBeforeExtraction = new PDG(GitUtils.createMethodObject(GitUtils.getMethodDeclaration(
                    currentFile, currentFileText, refactoring.getSourceOperationBeforeExtraction().getLocationInfo())));

            this.sourceMethodBeforeExtractionPDG = sourceMethodBeforeExtraction;

            this.sourceMethodBeforeExtrationGroum = new Groum(sourceMethodBeforeExtraction);

        } catch (Exception e) {
            logger.error("Could not retrieve info about the source method before extraction! " + toString());
            e.printStackTrace();
        }
    }

    public String getExtractedMethodCode() {
        return extractedMethodCode;
    }

    public String getSourceMethodBeforeExtractionCode() {
        return sourceMethodBeforeExtractionCode;
    }

    public String getSourceMethodAfterExtractionCode() {
        return sourceMethodAfterExtractionCode;
    }

    public Graph getExtractedMethodPDG() {
        return extractedMethodPDG;
    }

    public Graph getSourceMethodBeforeExtractionPDG() {
        return sourceMethodBeforeExtractionPDG;
    }

    public Graph getSourceMethodAfterExtractionPDG() {
        return sourceMethodAfterExtractionPDG;
    }

    @Override
    public Graph getExtractedMethodGroum() {
        return extractedMethodGroum;
    }

    @Override
    public Graph getSourceMethodBeforeExtrationGroum() {
        return sourceMethodBeforeExtrationGroum;
    }

    @Override
    public Graph getSourceMethodAfterExtractionGroum() {
        return sourceMethodAfterExtractionGroum;
    }

    public String toString() {
        return refactoring.getName() + ": " + refactoring.getExtractedOperation().getName() + " ["
                + refactoring.getExtractedOperation().statementCount() + "] ["
                + refactoring.getExtractedOperation().getClassName() + "] " + getProjectName() + " ("
                + getCommitIdShort() + ")";
    }

    public String getProjectName() {
        int splitIndex1 = projectLink.lastIndexOf('/');
        projectLink = projectLink.substring(splitIndex1 + 1);
        int splitIndex2 = projectLink.lastIndexOf('.');
        if (splitIndex2 != -1) {
            projectLink = projectLink.substring(0, splitIndex2);
        }
        return projectLink;
    }

    public String getCommitIdShort() {
        if (commitId != null && commitId.length() > 6)
            return commitId.substring(0, 5);
        else
            return commitId;
    }
}
