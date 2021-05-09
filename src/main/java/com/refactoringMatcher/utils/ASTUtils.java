package com.refactoringMatcher.utils;

import com.refactoringMatcher.java.ast.ConstructorObject;
import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.MethodObject;
import io.vavr.Tuple3;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Diptopol
 * @since 12/16/2020 4:25 PM
 */
public class ASTUtils {

    private static Logger logger = LoggerFactory.getLogger(ASTUtils.class);

    public static MethodObject createMethodObject(MethodDeclaration methodDeclaration, List<ImportObject> importObjectList, List<FieldDeclaration> fieldDeclarationList, Set<Tuple3<String, String, String>> jarSet) {
        final ConstructorObject constructorObject = new ConstructorObject(methodDeclaration, importObjectList, fieldDeclarationList, jarSet);

        return new MethodObject(constructorObject);
    }

    public static String extractText(int startOffset, int length, String wholeText, String file) throws IOException, Exception {
        MethodDeclaration methodDeclaration = getMethodDeclaration(file, wholeText, startOffset, length);
        return methodDeclaration.toString();
    }

    public static CompilationUnit getCompilationUnit(String sourceCode) {
        ASTParser parser = ASTParser.newParser(AST.JLS14);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
        parser.setCompilerOptions(options);

        parser.setSource(sourceCode.toCharArray());

        return (CompilationUnit) parser.createAST(null);
    }

    /**
     *
     * TODO: Compilation Unit creation should be replaced by {@link ASTUtils#getCompilationUnit(String)}
     *
     * @param file
     * @param wholeText
     * @param startOffSet
     * @param length
     * @return
     * @throws Exception
     */
    public static MethodDeclaration getMethodDeclaration(String file, String wholeText, int startOffSet, int length)
            throws Exception {
        MethodDeclaration methodDeclaration;
        try {
            ASTParser parser = ASTParser.newParser(AST.JLS14);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            Map options = JavaCore.getOptions();
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
            parser.setCompilerOptions(options);

            /*
             * If there is no need to resolving binding, then there should not be any necessity to resolve bindings.
             */
            /*parser.setEnvironment(new String[0], new String[] { file }, null, false);*/

            parser.setSource(wholeText.toCharArray());

            /*
             * We probably will not need to resolve binding types, since we will depend on Jar Analyzer too infer type
             * and we aren't setting all the source files in the environment for
             */
            /*parser.setResolveBindings(true);*/

            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            ASTNode block = NodeFinder.perform(compilationUnit, startOffSet,
                    length);

            methodDeclaration = (MethodDeclaration) block;
        } catch (Exception e) {
            logger.error("Can not extract method from file.");
            throw e;
        }
        return methodDeclaration;
    }
}
