package com.refactoringMatcher.java.ast.decomposition;

import com.refactoringMatcher.java.ast.*;
import com.refactoringMatcher.java.ast.decomposition.cfg.PlainVariable;
import com.refactoringMatcher.java.ast.util.ExpressionExtractor;
import org.eclipse.jdt.core.dom.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class MethodBodyObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3183146798042270617L;
	private CompositeStatementObject compositeStatement;
	private List<ParameterObject> parameters;
	
	public MethodBodyObject(Block methodBody, List<ParameterObject> parameterList) {
		this.parameters = parameterList;
		this.compositeStatement = new CompositeStatementObject(methodBody, parameters, StatementType.BLOCK, null);
        List<Statement> statements = methodBody.statements();
		for(Statement statement : statements) {
			processStatement(compositeStatement, statement);
		}
	}

	public CompositeStatementObject getCompositeStatement() {
		return compositeStatement;
	}

	public List<LocalVariableDeclarationObject> getLocalVariableDeclarations() {
		return compositeStatement.getLocalVariableDeclarations();
	}

	public List<LocalVariableInstructionObject> getLocalVariableInstructions() {
		return compositeStatement.getLocalVariableInstructions();
	}

	public List<CreationObject> getCreations() {
		return compositeStatement.getCreations();
	}

	public List<LiteralObject> getLiterals() {
		return compositeStatement.getLiterals();
	}

	public List<AnonymousClassDeclarationObject> getAnonymousClassDeclarations() {
		return compositeStatement.getAnonymousClassDeclarations();
	}

	public Set<String> getExceptionsInThrowStatements() {
		return compositeStatement.getExceptionsInThrowStatements();
	}

	public Set<PlainVariable> getDeclaredLocalVariables() {
		return compositeStatement.getDeclaredLocalVariables();
	}

	public Set<PlainVariable> getDefinedLocalVariables() {
		return compositeStatement.getDefinedLocalVariables();
	}

	public Set<PlainVariable> getUsedLocalVariables() {
		return compositeStatement.getUsedLocalVariables();
	}

	public boolean containsSuperMethodInvocation() {
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		List<Expression> superMethodInvocations = expressionExtractor.getSuperMethodInvocations(compositeStatement.getStatement());
		if(!superMethodInvocations.isEmpty())
			return true;
		else
			return false;
	}

	public boolean containsSuperFieldAccess() {
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		List<Expression> superFieldAccesses = expressionExtractor.getSuperFieldAccesses(compositeStatement.getStatement());
		if(!superFieldAccesses.isEmpty())
			return true;
		else
			return false;
	}

	private void processStatement(CompositeStatementObject parent, Statement statement) {
		if(statement instanceof Block) {
			Block block = (Block)statement;
			List<Statement> blockStatements = block.statements();
			CompositeStatementObject child = new CompositeStatementObject(block, parameters, StatementType.BLOCK, parent);
			parent.addStatement(child);
			for(Statement blockStatement : blockStatements) {
				processStatement(child, blockStatement);
			}
		}
		else if(statement instanceof IfStatement) {
			IfStatement ifStatement = (IfStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(ifStatement, parameters, StatementType.IF, parent);
			AbstractExpression abstractExpression = new AbstractExpression(ifStatement.getExpression(), parameters, child);
			child.addExpression(abstractExpression);
			parent.addStatement(child);
			processStatement(child, ifStatement.getThenStatement());
			if(ifStatement.getElseStatement() != null) {
				processStatement(child, ifStatement.getElseStatement());
			}
		}
		else if(statement instanceof ForStatement) {
			ForStatement forStatement = (ForStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(forStatement, parameters, StatementType.FOR, parent);
			List<Expression> initializers = forStatement.initializers();
			for(Expression initializer : initializers) {
				AbstractExpression abstractExpression = new AbstractExpression(initializer, parameters, child);
				child.addExpression(abstractExpression);
			}
			Expression expression = forStatement.getExpression();
			if(expression != null) {
				AbstractExpression abstractExpression = new AbstractExpression(expression, parameters, child);
				child.addExpression(abstractExpression);
			}
			List<Expression> updaters = forStatement.updaters();
			for(Expression updater : updaters) {
				AbstractExpression abstractExpression = new AbstractExpression(updater, parameters, child);
				child.addExpression(abstractExpression);
			}
			parent.addStatement(child);
			processStatement(child, forStatement.getBody());
		}
		else if(statement instanceof EnhancedForStatement) {
			EnhancedForStatement enhancedForStatement = (EnhancedForStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(enhancedForStatement, parameters, StatementType.ENHANCED_FOR, parent);
			SingleVariableDeclaration variableDeclaration = enhancedForStatement.getParameter();
			AbstractExpression variableDeclarationName = new AbstractExpression(variableDeclaration.getName(), parameters, child);
			child.addExpression(variableDeclarationName);
			if(variableDeclaration.getInitializer() != null) {
				AbstractExpression variableDeclarationInitializer = new AbstractExpression(variableDeclaration.getInitializer(), parameters, child);
				child.addExpression(variableDeclarationInitializer);
			}
			AbstractExpression abstractExpression = new AbstractExpression(enhancedForStatement.getExpression(), parameters, child);
			child.addExpression(abstractExpression);
			parent.addStatement(child);
			processStatement(child, enhancedForStatement.getBody());
		}
		else if(statement instanceof WhileStatement) {
			WhileStatement whileStatement = (WhileStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(whileStatement, parameters, StatementType.WHILE, parent);
			AbstractExpression abstractExpression = new AbstractExpression(whileStatement.getExpression(), parameters, child);
			child.addExpression(abstractExpression);
			parent.addStatement(child);
			processStatement(child, whileStatement.getBody());
		}
		else if(statement instanceof DoStatement) {
			DoStatement doStatement = (DoStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(doStatement, parameters, StatementType.DO, parent);
			AbstractExpression abstractExpression = new AbstractExpression(doStatement.getExpression(), parameters, child);
			child.addExpression(abstractExpression);
			parent.addStatement(child);
			processStatement(child, doStatement.getBody());
		}
		else if(statement instanceof ExpressionStatement) {
			ExpressionStatement expressionStatement = (ExpressionStatement)statement;
			StatementObject child = new StatementObject(expressionStatement, parameters, StatementType.EXPRESSION, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof SwitchStatement) {
			SwitchStatement switchStatement = (SwitchStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(switchStatement, parameters, StatementType.SWITCH, parent);
			AbstractExpression abstractExpression = new AbstractExpression(switchStatement.getExpression(), parameters, child);
			child.addExpression(abstractExpression);
			parent.addStatement(child);
			List<Statement> switchStatements = switchStatement.statements();
			for(Statement switchStatement2 : switchStatements)
				processStatement(child, switchStatement2);
		}
		else if(statement instanceof SwitchCase) {
			SwitchCase switchCase = (SwitchCase)statement;
			StatementObject child = new StatementObject(switchCase, parameters, StatementType.SWITCH_CASE, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof AssertStatement) {
			AssertStatement assertStatement = (AssertStatement)statement;
			StatementObject child = new StatementObject(assertStatement, parameters, StatementType.ASSERT, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof LabeledStatement) {
			LabeledStatement labeledStatement = (LabeledStatement)statement;
			CompositeStatementObject child = new CompositeStatementObject(labeledStatement, parameters, StatementType.LABELED, parent);
			parent.addStatement(child);
			processStatement(child, labeledStatement.getBody());
		}
		else if(statement instanceof ReturnStatement) {
			ReturnStatement returnStatement = (ReturnStatement)statement;
			StatementObject child = new StatementObject(returnStatement, parameters, StatementType.RETURN, parent);
			parent.addStatement(child);	
		}
		else if(statement instanceof SynchronizedStatement) {
			SynchronizedStatement synchronizedStatement = (SynchronizedStatement)statement;
			SynchronizedStatementObject child = new SynchronizedStatementObject(synchronizedStatement, parameters, parent);
			parent.addStatement(child);
			processStatement(child, synchronizedStatement.getBody());
		}
		else if(statement instanceof ThrowStatement) {
			ThrowStatement throwStatement = (ThrowStatement)statement;
			StatementObject child = new StatementObject(throwStatement, parameters, StatementType.THROW, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof TryStatement) {
			TryStatement tryStatement = (TryStatement)statement;
			TryStatementObject child = new TryStatementObject(tryStatement, parameters, parent);
			List<VariableDeclarationExpression> resources = tryStatement.resources();
			for(VariableDeclarationExpression expression : resources) {
				AbstractExpression variableDeclarationExpression = new AbstractExpression(expression, parameters, child);
				child.addExpression(variableDeclarationExpression);
			}
			parent.addStatement(child);
			processStatement(child, tryStatement.getBody());
			List<CatchClause> catchClauses = tryStatement.catchClauses();
			for(CatchClause catchClause : catchClauses) {
				CatchClauseObject catchClauseObject = new CatchClauseObject();
				Block catchClauseBody = catchClause.getBody();
				CompositeStatementObject catchClauseStatementObject = new CompositeStatementObject(catchClauseBody, parameters, StatementType.BLOCK, null);
				SingleVariableDeclaration variableDeclaration = catchClause.getException();
				Type variableDeclarationType = variableDeclaration.getType();
				if(variableDeclarationType instanceof UnionType) {
					UnionType unionType = (UnionType)variableDeclarationType;
					List<Type> types = unionType.types();
					for(Type type : types) {
						catchClauseObject.addExceptionType(type.toString());
					}
				}
				else {
					catchClauseObject.addExceptionType(variableDeclarationType.toString());
				}
				AbstractExpression variableDeclarationName = new AbstractExpression(variableDeclaration.getName(), parameters, child);
				catchClauseObject.addExpression(variableDeclarationName);
				if(variableDeclaration.getInitializer() != null) {
					AbstractExpression variableDeclarationInitializer = new AbstractExpression(variableDeclaration.getInitializer(), parameters, child);
					catchClauseObject.addExpression(variableDeclarationInitializer);
				}
				List<Statement> blockStatements = catchClauseBody.statements();
				for(Statement blockStatement : blockStatements) {
					processStatement(catchClauseStatementObject, blockStatement);
				}
				catchClauseObject.setBody(catchClauseStatementObject);
				child.addCatchClause(catchClauseObject);
			}
			Block finallyBlock = tryStatement.getFinally();
			if(finallyBlock != null) {
				CompositeStatementObject finallyClauseStatementObject = new CompositeStatementObject(finallyBlock, parameters, StatementType.BLOCK, null);
				List<Statement> blockStatements = finallyBlock.statements();
				for(Statement blockStatement : blockStatements) {
					processStatement(finallyClauseStatementObject, blockStatement);
				}
				child.setFinallyClause(finallyClauseStatementObject);
			}
		}
		else if(statement instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement)statement;
			StatementObject child = new StatementObject(variableDeclarationStatement, parameters, StatementType.VARIABLE_DECLARATION, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof ConstructorInvocation) {
			ConstructorInvocation constructorInvocation = (ConstructorInvocation)statement;
			StatementObject child = new StatementObject(constructorInvocation, parameters, StatementType.CONSTRUCTOR_INVOCATION, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof SuperConstructorInvocation) {
			SuperConstructorInvocation superConstructorInvocation = (SuperConstructorInvocation)statement;
			StatementObject child = new StatementObject(superConstructorInvocation, parameters, StatementType.SUPER_CONSTRUCTOR_INVOCATION, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof BreakStatement) {
			BreakStatement breakStatement = (BreakStatement)statement;
			StatementObject child = new StatementObject(breakStatement, parameters, StatementType.BREAK, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof ContinueStatement) {
			ContinueStatement continueStatement = (ContinueStatement)statement;
			StatementObject child = new StatementObject(continueStatement, parameters, StatementType.CONTINUE, parent);
			parent.addStatement(child);
		}
		else if(statement instanceof EmptyStatement) {
			EmptyStatement emptyStatement = (EmptyStatement)statement;
			StatementObject child = new StatementObject(emptyStatement, parameters, StatementType.EMPTY, parent);
			parent.addStatement(child);
		}
	}

	public List<TryStatementObject> getTryStatements() {
		return compositeStatement.getTryStatements();
	}

	public List<String> stringRepresentation() {
		return compositeStatement.stringRepresentation();
	}
}
