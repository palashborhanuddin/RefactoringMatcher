package com.refactoringMatcher.domain;

import com.refactoringMatcher.java.ast.decomposition.cfg.Graph;

/**
 * @author Diptopol
 * @since 11/30/2020 1:34 AM
 */
public class RefactoringExtractionInfo {

    private String uuid;

    private String filePath;
    private String sourceCode;

    private int startOffset;
    private int length;

    private String code;
    private Graph pdg;
    private Graph groum;

    public RefactoringExtractionInfo(String filePath, String sourceCode, int startOffset, int length) {
        this.filePath = filePath;
        this.sourceCode = sourceCode;
        this.startOffset = startOffset;
        this.length = length;
    }

    public RefactoringExtractionInfo(String uuid, String filePath, String sourceCode, int startOffset, int length) {
        this.uuid = uuid;
        this.filePath = filePath;
        this.sourceCode = sourceCode;
        this.startOffset = startOffset;
        this.length = length;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getLength() {
        return length;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Graph getPdg() {
        return pdg;
    }

    public void setPdg(Graph pdg) {
        this.pdg = pdg;
    }

    public Graph getGroum() {
        return groum;
    }

    public void setGroum(Graph groum) {
        this.groum = groum;
    }
}
