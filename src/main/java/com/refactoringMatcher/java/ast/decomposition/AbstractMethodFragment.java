package com.refactoringMatcher.java.ast.decomposition;

import ca.concordia.jaranalyzer.Models.MethodInfo;
import ca.concordia.jaranalyzer.TypeInferenceFluentAPI;
import com.refactoringMatcher.java.ast.*;
import com.refactoringMatcher.java.ast.decomposition.cfg.PlainVariable;
import com.refactoringMatcher.java.ast.util.MethodDeclarationUtility;
import io.vavr.Tuple3;
import org.eclipse.jdt.core.dom.*;


import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractMethodFragment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8919299678903843123L;
	private AbstractMethodFragment parent;
	private List<ParameterObject> parameters;

	private List<LocalVariableDeclarationObject> localVariableDeclarationList;
	private List<LocalVariableInstructionObject> localVariableInstructionList;
	private List<CreationObject> creationList;
	private List<LiteralObject> literalList;
	private List<AnonymousClassDeclarationObject> anonymousClassDeclarationList;
	private Set<String> exceptionsInThrowStatements;

	private Set<PlainVariable> declaredLocalVariables;
	private Set<PlainVariable> definedLocalVariables;
	private Set<PlainVariable> usedLocalVariables;

	private List<MethodInvocationObject> methodInvocationList;
	private List<SuperMethodInvocationObject> superMethodInvocationList;
	
	protected AbstractMethodFragment(AbstractMethodFragment parent, List<ParameterObject> parameters) {
		this.parent = parent;
		this.parameters = parameters;
		this.localVariableDeclarationList = new ArrayList<LocalVariableDeclarationObject>();
		this.localVariableInstructionList = new ArrayList<LocalVariableInstructionObject>();
		this.creationList = new ArrayList<CreationObject>();
		this.literalList = new ArrayList<LiteralObject>();
		this.anonymousClassDeclarationList = new ArrayList<AnonymousClassDeclarationObject>();
		this.exceptionsInThrowStatements = new LinkedHashSet<String>();

		this.declaredLocalVariables = new LinkedHashSet<PlainVariable>();
		this.definedLocalVariables = new LinkedHashSet<PlainVariable>();
		this.usedLocalVariables = new LinkedHashSet<PlainVariable>();
		
		this.methodInvocationList = new LinkedList<MethodInvocationObject>();
	}

	public AbstractMethodFragment getParent() {
		return this.parent;
	}

	/*
	 * protected void processVariables(List<Expression> variableInstructions,
	 * List<Expression> assignments, List<Expression> postfixExpressions,
	 * List<Expression> prefixExpressions) { for (Expression variableInstruction
	 * : variableInstructions) { SimpleName simpleName = (SimpleName)
	 * variableInstruction; IBinding binding = simpleName.resolveBinding(); if
	 * (binding != null && binding.getKind() == IBinding.VARIABLE) {
	 * IVariableBinding variableBinding = (IVariableBinding) binding; if
	 * (variableBinding.isField()) { // field if
	 * (variableBinding.getDeclaringClass() != null) { String originClassName =
	 * variableBinding.getDeclaringClass().getQualifiedName(); String
	 * qualifiedName = variableBinding.getType().getQualifiedName(); TypeObject
	 * fieldType = TypeObject.extractTypeObject(qualifiedName); String fieldName
	 * = variableBinding.getName(); if (!originClassName.equals("")) { if
	 * (simpleName.getParent() instanceof SuperFieldAccess) { // from super
	 * class SuperFieldInstructionObject superFieldInstruction = new
	 * SuperFieldInstructionObject( originClassName, fieldType, fieldName);
	 * superFieldInstruction.setSimpleName(simpleName); if
	 * ((variableBinding.getModifiers() & Modifier.STATIC) != 0)
	 * superFieldInstruction.setStatic(true);
	 * addSuperFieldInstruction(superFieldInstruction); } else { // from same
	 * class FieldInstructionObject fieldInstruction = new
	 * FieldInstructionObject(originClassName, fieldType, fieldName);
	 * fieldInstruction.setSimpleName(simpleName); if
	 * ((variableBinding.getModifiers() & Modifier.STATIC) != 0)
	 * fieldInstruction.setStatic(true); addFieldInstruction(fieldInstruction);
	 * Set<Assignment> fieldAssignments = getMatchingAssignments(simpleName,
	 * assignments); Set<PostfixExpression> fieldPostfixAssignments =
	 * getMatchingPostfixAssignments( simpleName, postfixExpressions);
	 * Set<PrefixExpression> fieldPrefixAssignments =
	 * getMatchingPrefixAssignments(simpleName, prefixExpressions);
	 * AbstractVariable variable =
	 * MethodDeclarationUtility.createVariable(simpleName, null); if
	 * (!fieldAssignments.isEmpty()) { handleDefinedField(variable); for
	 * (Assignment assignment : fieldAssignments) { Assignment.Operator operator
	 * = assignment.getOperator(); if
	 * (!operator.equals(Assignment.Operator.ASSIGN)) handleUsedField(variable);
	 * } } if (!fieldPostfixAssignments.isEmpty()) {
	 * handleDefinedField(variable); handleUsedField(variable); } if
	 * (!fieldPrefixAssignments.isEmpty()) { handleDefinedField(variable);
	 * handleUsedField(variable); } if (fieldAssignments.isEmpty() &&
	 * fieldPostfixAssignments.isEmpty() && fieldPrefixAssignments.isEmpty()) {
	 * handleUsedField(variable); } } } } } else { // local variable if
	 * (variableBinding.getDeclaringClass() == null) { String variableName =
	 * variableBinding.getName(); String variableType =
	 * variableBinding.getType().getQualifiedName(); TypeObject
	 * localVariableType = TypeObject.extractTypeObject(variableType);
	 * PlainVariable variable = new PlainVariable(variableBinding); if
	 * (simpleName.isDeclaration()) { // declaration
	 * LocalVariableDeclarationObject localVariable = new
	 * LocalVariableDeclarationObject( localVariableType, variableName);
	 * VariableDeclaration variableDeclaration = (VariableDeclaration)
	 * simpleName.getParent();
	 * localVariable.setVariableDeclaration(variableDeclaration);
	 * addLocalVariableDeclaration(localVariable);
	 * addDeclaredLocalVariable(variable); } else { // instruction
	 * LocalVariableInstructionObject localVariable = new
	 * LocalVariableInstructionObject( localVariableType, variableName);
	 * localVariable.setSimpleName(simpleName);
	 * addLocalVariableInstruction(localVariable); Set<Assignment>
	 * localVariableAssignments = getMatchingAssignments(simpleName,
	 * assignments); Set<PostfixExpression> localVariablePostfixAssignments =
	 * getMatchingPostfixAssignments( simpleName, postfixExpressions);
	 * Set<PrefixExpression> localVariablePrefixAssignments =
	 * getMatchingPrefixAssignments( simpleName, prefixExpressions); if
	 * (!localVariableAssignments.isEmpty()) {
	 * addDefinedLocalVariable(variable); for (Assignment assignment :
	 * localVariableAssignments) { Assignment.Operator operator =
	 * assignment.getOperator(); if
	 * (!operator.equals(Assignment.Operator.ASSIGN))
	 * addUsedLocalVariable(variable); } } if
	 * (!localVariablePostfixAssignments.isEmpty()) {
	 * addDefinedLocalVariable(variable); addUsedLocalVariable(variable); } if
	 * (!localVariablePrefixAssignments.isEmpty()) {
	 * addDefinedLocalVariable(variable); addUsedLocalVariable(variable); } if
	 * (localVariableAssignments.isEmpty() &&
	 * localVariablePostfixAssignments.isEmpty() &&
	 * localVariablePrefixAssignments.isEmpty()) {
	 * addUsedLocalVariable(variable); } } } } } } }
	 */

	protected void processVariablesWithoutBindingInfo(List<Expression> variableInstructions, List<Expression> assignments, List<Expression> postfixExpressions, List<Expression> prefixExpressions) {
		for (Expression variableInstruction : variableInstructions) {
			SimpleName simpleName = (SimpleName) variableInstruction;
/*			if(simpleName.getIdentifier() == "one")
			{
				System.out.println(simpleName.getParent());
			}*/
			if (simpleName.isDeclaration()) {
				// declaration
				VariableDeclaration variableDeclaration = (VariableDeclaration) simpleName.getParent();
				LocalVariableDeclarationObject localVariable = new LocalVariableDeclarationObject(variableDeclaration);
				addLocalVariableDeclaration(localVariable);
				PlainVariable variable = new PlainVariable(variableDeclaration);
				addDeclaredLocalVariable(variable);
			} else {
				// instruction
				LocalVariableInstructionObject localVariable = new LocalVariableInstructionObject(simpleName);
				addLocalVariableInstruction(localVariable);
				
				PlainVariable variable;
				if(simpleName.getParent() instanceof FieldAccess){
					variable = new PlainVariable(localVariable.getName());
				} else if(simpleName.getParent() instanceof QualifiedName && simpleName.getParent().toString().endsWith(simpleName.getIdentifier()) ){
					variable = new PlainVariable(simpleName.getParent().toString());
				}
				else{
					VariableDeclaration variableDeclaration = getVaribleDeclaration(localVariable);
					if(variableDeclaration == null)
						variable = new PlainVariable(localVariable.getName());
					else
						variable = new PlainVariable(variableDeclaration);
				}
				Set<Assignment> localVariableAssignments = getMatchingAssignments(simpleName, assignments);
				Set<PostfixExpression> localVariablePostfixAssignments = getMatchingPostfixAssignments(simpleName, postfixExpressions);
				Set<PrefixExpression> localVariablePrefixAssignments = getMatchingPrefixAssignments(simpleName, prefixExpressions);

				if (!localVariableAssignments.isEmpty()) {
					addDefinedLocalVariable(variable);
					for (Assignment assignment : localVariableAssignments) {
						Assignment.Operator operator = assignment.getOperator();
						if (!operator.equals(Assignment.Operator.ASSIGN))
							addUsedLocalVariable(variable);
					}
				}
				if (!localVariablePostfixAssignments.isEmpty()) {
					addDefinedLocalVariable(variable);
					addUsedLocalVariable(variable);
				}
				if (!localVariablePrefixAssignments.isEmpty()) {
					addDefinedLocalVariable(variable);
					addUsedLocalVariable(variable);
				}
				if (localVariableAssignments.isEmpty() && localVariablePostfixAssignments.isEmpty() && localVariablePrefixAssignments.isEmpty()) {
					addUsedLocalVariable(variable);
				}

			}

		}
	}

	private VariableDeclaration getVaribleDeclaration(LocalVariableInstructionObject localVariable) {
		if (parent == null)
		{
			for (ParameterObject parameter : parameters) {
				if(parameter.getName().equals(localVariable.getName()))
					return parameter.getVariableDeclaration();
			}
			return null;			
		}

		Set<PlainVariable> declarationsInParent = parent.getDeclaredLocalVariables();
		PlainVariable declaration = null;
		for (PlainVariable plainVariable : declarationsInParent) {
			if (plainVariable.getVariableName().equals(localVariable.getName())) {
				if(plainVariable.scopeContains(localVariable.getSimpleName()))
					declaration = plainVariable;
			}
		}

		if (declaration != null) {
			return declaration.getVariableDeclaration();
		} else {
			return parent.getVaribleDeclaration(localVariable);
		}
	}

	private void addLocalVariableDeclaration(LocalVariableDeclarationObject localVariable) {
		localVariableDeclarationList.add(localVariable);
		if (parent != null) {
			parent.addLocalVariableDeclaration(localVariable);
		}
	}

	private void addLocalVariableInstruction(LocalVariableInstructionObject localVariable) {
		localVariableInstructionList.add(localVariable);
		if (parent != null) {
			parent.addLocalVariableInstruction(localVariable);
		}
	}

	private void addDeclaredLocalVariable(PlainVariable variable) {
		declaredLocalVariables.add(variable);
		if (parent != null) {
			parent.addDeclaredLocalVariable(variable);
		}
	}

	private void addDefinedLocalVariable(PlainVariable variable) {
		definedLocalVariables.add(variable);
		if (parent != null) {
			parent.addDefinedLocalVariable(variable);
		}
	}

	private void addUsedLocalVariable(PlainVariable variable) {
		usedLocalVariables.add(variable);
		if (parent != null) {
			parent.addUsedLocalVariable(variable);
		}
	}


	protected void processMethodInvocations(List<Expression> methodInvocations, List<ImportObject> importObjectList) {
		List<String> importStatementList = importObjectList.stream()
				.map(ImportObject::getImportStatement)
				.collect(Collectors.toList());

		try {
			for(Expression expression : methodInvocations) {
				if(expression instanceof MethodInvocation) {
					MethodInvocation methodInvocation = (MethodInvocation) expression;
					methodInvocation.arguments();

					String methodName = methodInvocation.getName().getFullyQualifiedName();

					// No need to pass the explicit Jar set here as JarAnalyzer handles it.
					Set<Tuple3<String, String, String>> jarSet = new HashSet<>();
					String javaVersion = "11.0.10";
					System.out.println("MethodName: " +methodName+ ", methodInvocation: " +methodInvocation.arguments().toString());
					List<MethodInfo> methodInfoList = TypeInferenceFluentAPI.getInstance().
							new Criteria(jarSet,
							javaVersion, importStatementList, methodName, methodInvocation.arguments().size()).getMethodList();
					if (methodInfoList.size() == 0) {
						System.out.println("No information found for: " +methodName);
						continue;
					}
					MethodInfo methodInfo = methodInfoList.get(0);
					System.out.println("MethodInfo: " + methodInfo.toString());

					String originClassName = methodInfo.getClassInfo().getQualifiedName();
					TypeObject originClassTypeObject = TypeObject.extractTypeObject(originClassName);

					String methodInvocationName = methodInvocation.getName().getIdentifier();
					String qualifiedName = methodInfo.getReturnTypeAsType().getClassName();
					TypeObject returnType = TypeObject.extractTypeObject(qualifiedName);

					MethodInvocationObject methodInvocationObject = new MethodInvocationObject(originClassTypeObject,
							methodInvocationName, returnType);
					methodInvocationObject.setMethodInvocation(methodInvocation);

					List<String> qualifiedParameterTypeNameList = Arrays.stream(methodInfo.getArgumentTypes())
							.map(org.objectweb.asm.Type::getClassName)
							.collect(Collectors.toList());

					for(String parameterTypeName : qualifiedParameterTypeNameList) {
						TypeObject typeObject = TypeObject.extractTypeObject(parameterTypeName);
						methodInvocationObject.addParameter(typeObject);
					}

					for(String thrownExceptionClassName : methodInfo.getThrownInternalClassNames()) {
						methodInvocationObject.addThrownException(thrownExceptionClassName);
					}
					if(methodInfo.isStatic()) {
						methodInvocationObject.setStatic(true);
					}

					addMethodInvocation(methodInvocationObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addSuperMethodInvocation(SuperMethodInvocationObject superMethodInvocationObject) {
		superMethodInvocationList.add(superMethodInvocationObject);
		if(parent != null) {
			parent.addSuperMethodInvocation(superMethodInvocationObject);
		}
	}
	
	private void addMethodInvocation(MethodInvocationObject methodInvocation) {
		methodInvocationList.add(methodInvocation);
		if(parent != null) {
			parent.addMethodInvocation(methodInvocation);
		}
	}
	
	protected void processArrayCreations(List<Expression> arrayCreations) {
		for (Expression arrayCreationExpression : arrayCreations) {
			ArrayCreation arrayCreation = (ArrayCreation) arrayCreationExpression;
			Type type = arrayCreation.getType();
			// ITypeBinding typeBinding = type.resolveBinding();
			// String qualifiedTypeName = typeBinding.getQualifiedName();
			TypeObject typeObject = TypeObject.extractTypeObject(type.toString());
			ArrayCreationObject creationObject = new ArrayCreationObject(typeObject);
			creationObject.setArrayCreation(arrayCreation);
			addCreation(creationObject);
		}
	}

	private void addCreation(CreationObject creationObject) {
		creationList.add(creationObject);
		if (parent != null) {
			parent.addCreation(creationObject);
		}
	}

	protected void processLiterals(List<Expression> literals) {
		for (Expression literal : literals) {
			LiteralObject literalObject = new LiteralObject(literal);
			addLiteral(literalObject);
		}
	}

	private void addLiteral(LiteralObject literalObject) {
		literalList.add(literalObject);
		if (parent != null) {
			parent.addLiteral(literalObject);
		}
	}

	protected void processThrowStatement(ThrowStatement throwStatement) {
		Expression expression = throwStatement.getExpression();
		if (expression instanceof ClassInstanceCreation) {
			/*
			 * ClassInstanceCreation creation =
			 * (ClassInstanceCreation)expression; ITypeBinding typeBinding =
			 * creation.getType().resolveBinding();
			 * addExceptionInThrowStatement(typeBinding.getQualifiedName());
			 */
			addExceptionInThrowStatement(throwStatement.toString());
		}
	}

	private void addExceptionInThrowStatement(String exception) {
		exceptionsInThrowStatements.add(exception);
		if (parent != null) {
			parent.addExceptionInThrowStatement(exception);
		}
	}

	private Set<Assignment> getMatchingAssignments(SimpleName simpleName, List<Expression> assignments) {
		Set<Assignment> matchingAssignments = new LinkedHashSet<Assignment>();
		for (Expression expression : assignments) {
			Assignment assignment = (Assignment) expression;
			Expression leftHandSide = assignment.getLeftHandSide();
			SimpleName leftHandSideName = MethodDeclarationUtility.getRightMostSimpleName(leftHandSide);
			if (leftHandSideName != null && leftHandSideName.equals(simpleName)) {
				matchingAssignments.add(assignment);
			}
		}
		return matchingAssignments;
	}

	private Set<PostfixExpression> getMatchingPostfixAssignments(SimpleName simpleName,
			List<Expression> postfixExpressions) {
		Set<PostfixExpression> matchingPostfixAssignments = new LinkedHashSet<PostfixExpression>();
		for (Expression expression : postfixExpressions) {
			PostfixExpression postfixExpression = (PostfixExpression) expression;
			Expression operand = postfixExpression.getOperand();
			SimpleName operandName = MethodDeclarationUtility.getRightMostSimpleName(operand);
			if (operandName != null && operandName.equals(simpleName)) {
				matchingPostfixAssignments.add(postfixExpression);
			}
		}
		return matchingPostfixAssignments;
	}

	private Set<PrefixExpression> getMatchingPrefixAssignments(SimpleName simpleName,
			List<Expression> prefixExpressions) {
		Set<PrefixExpression> matchingPrefixAssignments = new LinkedHashSet<PrefixExpression>();
		for (Expression expression : prefixExpressions) {
			PrefixExpression prefixExpression = (PrefixExpression) expression;
			Expression operand = prefixExpression.getOperand();
			PrefixExpression.Operator operator = prefixExpression.getOperator();
			SimpleName operandName = MethodDeclarationUtility.getRightMostSimpleName(operand);
			if (operandName != null && operandName.equals(simpleName)
					&& (operator.equals(PrefixExpression.Operator.INCREMENT)
							|| operator.equals(PrefixExpression.Operator.DECREMENT))) {
				matchingPrefixAssignments.add(prefixExpression);
			}
		}
		return matchingPrefixAssignments;
	}

	public List<LocalVariableDeclarationObject> getLocalVariableDeclarations() {
		return localVariableDeclarationList;
	}

	public List<LocalVariableInstructionObject> getLocalVariableInstructions() {
		return localVariableInstructionList;
	}

	public List<CreationObject> getCreations() {
		return creationList;
	}

	public List<ClassInstanceCreationObject> getClassInstanceCreations() {
		List<ClassInstanceCreationObject> classInstanceCreations = new ArrayList<ClassInstanceCreationObject>();
		for (CreationObject creation : creationList) {
			if (creation instanceof ClassInstanceCreationObject) {
				classInstanceCreations.add((ClassInstanceCreationObject) creation);
			}
		}
		return classInstanceCreations;
	}

	public List<ArrayCreationObject> getArrayCreations() {
		List<ArrayCreationObject> arrayCreations = new ArrayList<ArrayCreationObject>();
		for (CreationObject creation : creationList) {
			if (creation instanceof ArrayCreationObject) {
				arrayCreations.add((ArrayCreationObject) creation);
			}
		}
		return arrayCreations;
	}

	public List<LiteralObject> getLiterals() {
		return literalList;
	}

	public List<AnonymousClassDeclarationObject> getAnonymousClassDeclarations() {
		return anonymousClassDeclarationList;
	}

	public Set<String> getExceptionsInThrowStatements() {
		return exceptionsInThrowStatements;
	}

	public Set<PlainVariable> getDeclaredLocalVariables() {
		return declaredLocalVariables;
	}

	public Set<PlainVariable> getDefinedLocalVariables() {
		return definedLocalVariables;
	}

	public Set<PlainVariable> getUsedLocalVariables() {
		return usedLocalVariables;
	}

	public List<MethodInvocationObject> getMethodInvocationList() {
		return methodInvocationList;
	}
	
	
}
