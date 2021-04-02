package com.refactoringMatcher.java.ast.decomposition.cfg;

public class PDGDefOrderDependence extends PDGAbstractDataDependence {

    public PDGDefOrderDependence(PDGNode src, PDGNode dst,
                               AbstractVariable data, CFGBranchNode loop) {
        super(src, dst, PDGDependenceType.DEF_ORDER, data, loop);
    }
}
