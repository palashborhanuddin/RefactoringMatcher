package com.refactoringMatcher.java.ast.decomposition;

import com.refactoringMatcher.java.ast.ASTInformation;
import com.refactoringMatcher.java.ast.ASTInformationGenerator;
import com.refactoringMatcher.java.ast.ParameterObject;
import org.eclipse.jdt.core.dom.Statement;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractStatement extends AbstractMethodFragment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5213743545756726382L;
	private ASTInformation statement;
	private StatementType type;
	
    public AbstractStatement(Statement statement, List<ParameterObject> parameters, StatementType type, AbstractMethodFragment parent) {
    	super(parent, parameters);
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
