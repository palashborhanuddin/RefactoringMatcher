package com.refactoringMatcher.java.ast;

import com.refactoringMatcher.utils.Cache;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.Serializable;
import java.util.Map;

public class ASTInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1592516912019481596L;
	private ITypeRoot iTypeRoot;
	private int startPosition;
	private int length;
	private int nodeType;
	private volatile int hashCode = 0;

	public ASTInformation(ITypeRoot iTypeRoot, ASTNode astNode) {
		this.iTypeRoot = iTypeRoot;
		this.startPosition = astNode.getStartPosition();
		this.length = astNode.getLength();
		this.nodeType = astNode.getNodeType();
		hashCode();
	}

	public ASTNode recoverASTNode() {
		try {
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			Map options = JavaCore.getOptions();
			JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
			parser.setCompilerOptions(options);
			parser.setResolveBindings(false);
			parser.setEnvironment(new String[0], new String[] { Cache.currentFile }, null, false);
			parser.setSource(Cache.currentFileText.toCharArray());
			CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
			ASTNode block = NodeFinder.perform(compilationUnit, startPosition, length);

			if (block.getNodeType() != nodeType) {
				if (block.getParent().getNodeType() == nodeType)
					return block.getParent();
				else {
					return null;
				}
			}
			return block;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ITypeRoot getITypeRoot() {
		return iTypeRoot;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getLength() {
		return length;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof ASTInformation) {
			ASTInformation astInformation = (ASTInformation) o;
			return this.iTypeRoot.equals(astInformation.iTypeRoot) && this.startPosition == astInformation.startPosition
					&& this.length == astInformation.length && this.nodeType == astInformation.nodeType;
		}
		return false;
	}

	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + startPosition;
			result = 37 * result + length;
			result = 37 * result + nodeType;
			result = 37 * result + recoverASTNode().toString().hashCode();
			hashCode = result;
		}
		return hashCode;
	}
}
