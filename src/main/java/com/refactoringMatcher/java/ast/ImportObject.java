package com.refactoringMatcher.java.ast;

import org.eclipse.jdt.core.dom.ImportDeclaration;

/**
 * @author Diptopol
 * @since 1/2/2021 10:03 AM
 */
public class ImportObject {

    private String importName;
    private boolean onDemand;
    private boolean isStatic;

    public ImportObject(ImportDeclaration importDeclaration) {
        this.importName = importDeclaration.getName().getFullyQualifiedName();
        this.onDemand = importDeclaration.isOnDemand();
        this.isStatic = importDeclaration.isStatic();
    }

    public String getImportStatement() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("import");

        if (isStatic) {
            stringBuilder.append(" static");
        }

        stringBuilder.append(this.importName);

        if (onDemand) {
            stringBuilder.append(".*");
        }

        return stringBuilder.toString();
    }

    public String getImportName() {
        return importName;
    }

    public boolean isOnDemand() {
        return onDemand;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
