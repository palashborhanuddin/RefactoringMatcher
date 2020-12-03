package com.refactoringMatcher.domain;

import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.Refactoring;

/**
 * @author Diptopol
 * @since 11/28/2020 10:14 AM
 */
public interface RefactoringData {

    Refactoring getRefactoring();

    String getCommitId();

    String getProjectLink();

    void prepareForSerialization();

    void recoverAfterDeserialization();

    void retrieveCode(Repository repository) throws Exception;

    String getExtractedMethodCode();

    String getSourceMethodBeforeExtractionCode();

    String getSourceMethodAfterExtractionCode();

    Graph getExtractedMethodPDG();

    Graph getSourceMethodBeforeExtractionPDG();

    Graph getSourceMethodAfterExtractionPDG();

    Graph getExtractedMethodGroum();

    Graph getSourceMethodBeforeExtrationGroum();

    Graph getSourceMethodAfterExtractionGroum();
}
