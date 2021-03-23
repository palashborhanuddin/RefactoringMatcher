package com.refactoringMatcher.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.options.Options;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;

public class SootPDGTest {
    private static SootPDGTestUtility testUtility;

    private static String TARGET_CLASS = "com.refactoringMatcher.targets.TargetClass";

    @BeforeClass
    public static void setUp() throws IOException {
        G.reset();
        List<String> processDir = new ArrayList<>();
        File f = new File("./target/test-classes");
        if (f.exists()) {
            processDir.add(f.getCanonicalPath());
        }
        Options.v().set_process_dir(processDir);

        Options.v().set_src_prec(Options.src_prec_only_class);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);
        Scene.v().addBasicClass(TARGET_CLASS);
        Scene.v().loadNecessaryClasses();
        Options.v().set_prepend_classpath(true);
        Scene.v().forceResolve(TARGET_CLASS, SootClass.BODIES);
        testUtility = new SootPDGTestUtility();
        PackManager.v().getPack("jtp").add(new Transform("jtp.TestSootPDGGraphUtility", testUtility));
        PackManager.v().runPacks();
    }

    @Test
    public void generatePDG() {
        EnhancedUnitGraph unitGraph = testUtility.getUnitGraph();
        System.out.print("UnitGraph: " +unitGraph.toString());
        HashMutablePDG hashPdg = new HashMutablePDG(unitGraph);
        System.out.println("PDG: " +hashPdg.toString());
    }
}