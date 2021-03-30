package com.refactoringMatcher.java.ast.decomposition;

import java.util.List;

import com.refactoringMatcher.java.ast.ASTInformation;
import com.refactoringMatcher.java.ast.ASTInformationGenerator;

import com.refactoringMatcher.java.ast.ImportObject;
import com.refactoringMatcher.java.ast.ParameterObject;
import org.eclipse.jdt.core.dom.Statement;

public abstract class AbstractStatement extends AbstractMethodFragment {

	private ASTInformation statement;
	private StatementType type;

	public AbstractStatement(Statement statement, List<ParameterObject> parameters, List<ImportObject> importObjectList, StatementType type, AbstractMethodFragment parent) {
		super(parent, parameters, importObjectList);
		this.type = type;
		this.statement = ASTInformationGenerator.generateASTInformation(statement);
	}

    public Statement getStatement() {
    	return (Statement)this.statement.recoverASTNode();
    }

	public StatementType getType() {
		return type;
	}

	public int getNestingDepth() {
		AbstractStatement parent = (AbstractStatement) this.getParent();
		int depth = 0;
		while (parent != null) {
			if (!parent.getType().equals(StatementType.BLOCK)) {
				depth++;
			}
			parent = (AbstractStatement) parent.getParent();
		}
		return depth;
	}

	public abstract List<String> stringRepresentation();
}
