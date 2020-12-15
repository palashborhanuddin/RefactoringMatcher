package com.refactoringMatcher.domain;

/**
 * @author Diptopol
 * @since 11/29/2020 11:39 PM
 */

public class RefactoringInfo {

    private String uuid;
    private String commitId;
    private String previousCommitId;
    private String projectUrl;

    private RefactoringExtractionInfo extracted;

    public RefactoringInfo() {
    }

    public RefactoringInfo(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getPreviousCommitId() {
        return previousCommitId;
    }

    public void setPreviousCommitId(String previousCommitId) {
        this.previousCommitId = previousCommitId;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public RefactoringExtractionInfo getExtracted() {
        return extracted;
    }

    public void setExtracted(RefactoringExtractionInfo extracted) {
        this.extracted = extracted;
    }
}
