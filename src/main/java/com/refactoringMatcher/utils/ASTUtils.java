package com.refactoringMatcher.utils;

import com.refactoringMatcher.java.ast.ConstructorObject;
import com.refactoringMatcher.java.ast.MethodObject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author Diptopol
 * @since 12/16/2020 4:25 PM
 */
public class ASTUtils {

    private static Logger logger = LoggerFactory.getLogger(ASTUtils.class);

    public static MethodObject createMethodObject(MethodDeclaration methodDeclaration) {
        final ConstructorObject constructorObject = new ConstructorObject(methodDeclaration);

        return new MethodObject(constructorObject);
    }

    public static String extractText(int startOffset, int length, String wholeText, String file) throws IOException, Exception {
        MethodDeclaration methodDeclaration = getMethodDeclaration(file, wholeText, startOffset, length);
        return methodDeclaration.toString();
    }

    public static CompilationUnit getCompilationUnit(String sourceCode) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(sourceCode.toCharArray());
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null);
    }

    public static MethodDeclaration getMethodDeclaration(String file, String wholeText, int startOffSet, int length)
            throws Exception {
        MethodDeclaration methodDeclaration;
        try {
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            Map options = JavaCore.getOptions();
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
            parser.setCompilerOptions(options);
            parser.setResolveBindings(false);
            parser.setEnvironment(new String[0], new String[] { file }, null, false);
            parser.setSource(wholeText.toCharArray());
            parser.setResolveBindings(true);
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
