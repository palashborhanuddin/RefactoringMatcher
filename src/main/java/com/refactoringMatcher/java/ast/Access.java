package com.refactoringMatcher.java.ast;

import java.io.Serializable;

public enum Access implements Serializable{
    NONE, PUBLIC, PRIVATE, PROTECTED;

    public String toString() {
        switch(this) {
            case NONE: return "";
            case PUBLIC: return "public";
            case PRIVATE: return "private";
            case PROTECTED: return "protected";
            default: return "";
        }
    }
}
