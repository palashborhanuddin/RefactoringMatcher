package com.refactoringMatcher.utils;

import soot.Body;
import soot.BodyTransformer;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

public class SootPDGTestUtility extends BodyTransformer {
    private EnhancedUnitGraph unitGraph = null;

    @Override
    protected void internalTransform(Body body, String phase, java.util.Map<String, String> options) {
        String methodSig = body.getMethod().getSignature();
        if (methodSig.contains("com.refactoringMatcher.targets.TargetClass")
                && body.getMethod().getName().contains("groumTestMethod")) {
            System.out.println("Inside internalTransform......com.refactoringMatcher.utils.TestClass..." + methodSig);
            unitGraph = new EnhancedUnitGraph(body);
        }
    }

    public EnhancedUnitGraph getUnitGraph() {
        return unitGraph;
    }
}

