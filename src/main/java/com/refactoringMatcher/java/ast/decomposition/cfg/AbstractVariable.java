package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.ASTInformation;
import com.refactoringMatcher.java.ast.ASTInformationGenerator;
import com.refactoringMatcher.java.ast.TypeObject;
import org.eclipse.jdt.core.dom.*;

import java.io.Serializable;

public abstract class AbstractVariable  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1331711440860601891L;
	protected String variableName;
	protected String variableType;
	protected ASTInformation variableDeclaration;
	protected ASTInformation scope;
	
	public AbstractVariable(String variableName) {
		this.variableName = variableName;
	}

	public AbstractVariable(VariableDeclaration variableDeclaration) {
		this.variableName = variableDeclaration.getName().getFullyQualifiedName();
		this.variableDeclaration = ASTInformationGenerator.generateASTInformation(variableDeclaration);
		extractScopeAndTypeInfo(variableDeclaration);
	}

	private void extractScopeAndTypeInfo(VariableDeclaration variableDeclaration){
		ASTNode scope = null;
		if (variableDeclaration instanceof VariableDeclarationFragment) {
			ASTNode variableDeclarationParent = variableDeclaration.getParent();
			if (variableDeclarationParent instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) variableDeclarationParent;
				this.variableType = new TypeObject(variableDeclarationStatement.getType().toString()).getClassType();
				scope = variableDeclarationStatement.getParent();
			} else if (variableDeclarationParent instanceof VariableDeclarationExpression) {
				this.variableType = new TypeObject(((VariableDeclarationExpression) variableDeclarationParent).getType().toString()).getClassType();
				if (variableDeclarationParent.getParent() instanceof ForStatement)
					scope = variableDeclarationParent.getParent();
				else if (variableDeclarationParent.getParent() != null){
					scope = variableDeclarationParent.getParent();
				}
			} else {
				throw new NullPointerException();
			}
		} else if (variableDeclaration instanceof SingleVariableDeclaration) {
			this.variableType = new TypeObject(((SingleVariableDeclaration) variableDeclaration).getType().toString()).getClassType();
			scope = variableDeclaration.getParent();
		} else {
			throw new NullPointerException();
		}
		this.scope = ASTInformationGenerator.generateASTInformation(scope);
	}
	
	public ASTNode getScope() {
		if(scope == null)
			return null;
		return scope.recoverASTNode();
	}
	
	public boolean scopeContains(ASTNode astNode) {
		if(scope == null)
			return true; // scope is null for fields
		else {
			if((scope.getStartPosition() + scope.getLength()) > astNode.getStartPosition() && scope.getStartPosition() < astNode.getStartPosition() )
				return true;
			else 
				return false;
		}
	}
	
	public VariableDeclaration getVariableDeclaration() {
		if(variableDeclaration == null)
			return null;
		return (VariableDeclaration) variableDeclaration.recoverASTNode();
	}

	public String getVariableName() {
		return variableName;
	}

	public String getVariableType() {
		if(variableType == null)
			return null;
		return variableType;
	}

	public abstract boolean containsPlainVariable(PlainVariable variable);

	public abstract boolean startsWithVariable(AbstractVariable variable);

	public abstract PlainVariable getInitialVariable();
}
