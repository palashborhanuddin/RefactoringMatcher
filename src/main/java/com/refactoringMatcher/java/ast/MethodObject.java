package com.refactoringMatcher.java.ast;

import com.refactoringMatcher.java.ast.decomposition.MethodBodyObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.PlainVariable;
import org.eclipse.jdt.core.dom.*;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MethodObject implements AbstractMethodDeclaration, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8073230851903060259L;
	private TypeObject returnType;
	private boolean _abstract;
	private boolean _static;
	private boolean _synchronized;
	private boolean _native;
	private ConstructorObject constructorObject;
	private boolean testAnnotation;
	private volatile int hashCode = 0;

	public MethodObject(ConstructorObject co) {
		this.constructorObject = co;

		this._abstract = false;
		this._static = false;
		this._synchronized = false;
		this._native = false;
		this.testAnnotation = false;

		int methodModifiers = co.getMethodDeclaration().getModifiers();
		if ((methodModifiers & Modifier.ABSTRACT) != 0)
			this._abstract = true;
		if ((methodModifiers & Modifier.STATIC) != 0)
			this._static = true;
		if ((methodModifiers & Modifier.SYNCHRONIZED) != 0)
			this._synchronized = true;
		if ((methodModifiers & Modifier.NATIVE) != 0)
			this._native = true;

		Type returnType = co.getMethodDeclaration().getReturnType2();
		TypeObject typeObject;
		if(returnType == null)
			typeObject = null;
			else
			typeObject = TypeObject.extractTypeObject(returnType.toString());
		
		this.returnType = typeObject;

		List<IExtendedModifier> extendedModifiers = co.getMethodDeclaration().modifiers();
		for (IExtendedModifier extendedModifier : extendedModifiers) {
			if (extendedModifier.isAnnotation()) {
				Annotation annotation = (Annotation) extendedModifier;
				if (annotation.getTypeName().getFullyQualifiedName().equals("Test")) {
					this.testAnnotation = true;
					break;
				}
			}
		}
	}

	public void setReturnType(TypeObject returnType) {
		this.returnType = returnType;
	}

	public TypeObject getReturnType() {
		return returnType;
	}

	public void setAbstract(boolean abstr) {
		this._abstract = abstr;
	}

	public boolean isAbstract() {
		return this._abstract;
	}

	public boolean isStatic() {
		return _static;
	}

	public void setStatic(boolean s) {
		_static = s;
	}

	public boolean isSynchronized() {
		return this._synchronized;
	}

	public void setSynchronized(boolean s) {
		this._synchronized = s;
	}

	public boolean isNative() {
		return this._native;
	}

	public void setNative(boolean n) {
		this._native = n;
	}

	public String getName() {
		return constructorObject.getName();
	}

	public boolean hasTestAnnotation() {
		return testAnnotation;
	}

	public void setTestAnnotation(boolean testAnnotation) {
		this.testAnnotation = testAnnotation;
	}

	public Set<String> getExceptionsInJavaDocThrows() {
		return constructorObject.getExceptionsInJavaDocThrows();
	}

	public Access getAccess() {
		return constructorObject.getAccess();
	}

	public MethodDeclaration getMethodDeclaration() {
		return constructorObject.getMethodDeclaration();
	}

	public MethodBodyObject getMethodBody() {
		return constructorObject.getMethodBody();
	}

	public boolean containsNullCheckForTargetObject(ClassObject targetClass) {
		List<LiteralObject> literals = getLiterals();
		for (LiteralObject literal : literals) {
			if (literal.getLiteralType().equals(LiteralType.NULL)) {
				Expression nullLiteral = literal.getLiteral();
				if (nullLiteral.getParent() instanceof InfixExpression) {
					InfixExpression infixExpression = (InfixExpression) nullLiteral.getParent();
					Expression leftOperand = infixExpression.getLeftOperand();
					ITypeBinding typeBinding = leftOperand.resolveTypeBinding();
					if (typeBinding != null && typeBinding.getQualifiedName().equals(targetClass.getName())
							&& infixExpression.getOperator().equals(InfixExpression.Operator.EQUALS)) {
						if (leftOperand instanceof SimpleName) {
							SimpleName simpleName = (SimpleName) leftOperand;
							IBinding binding = simpleName.resolveBinding();
							if (binding.getKind() == IBinding.VARIABLE) {
								IVariableBinding variableBinding = (IVariableBinding) binding;
								if (variableBinding.isParameter() || variableBinding.isField()) {
									return true;
								}
							}
						} else if (leftOperand instanceof FieldAccess) {
							FieldAccess fieldAccess = (FieldAccess) leftOperand;
							SimpleName simpleName = fieldAccess.getName();
							IBinding binding = simpleName.resolveBinding();
							if (binding.getKind() == IBinding.VARIABLE) {
								IVariableBinding variableBinding = (IVariableBinding) binding;
								if (variableBinding.isField()) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean overridesMethod() {
		IMethodBinding methodBinding = getMethodDeclaration().resolveBinding();
		ITypeBinding declaringClassTypeBinding = methodBinding.getDeclaringClass();
		Set<ITypeBinding> typeBindings = new LinkedHashSet<ITypeBinding>();
		ITypeBinding superClassTypeBinding = declaringClassTypeBinding.getSuperclass();
		if (superClassTypeBinding != null)
			typeBindings.add(superClassTypeBinding);
		ITypeBinding[] interfaceTypeBindings = declaringClassTypeBinding.getInterfaces();
		for (ITypeBinding interfaceTypeBinding : interfaceTypeBindings)
			typeBindings.add(interfaceTypeBinding);
		return overridesMethod(typeBindings);
	}

	private boolean overridesMethod(Set<ITypeBinding> typeBindings) {
		IMethodBinding methodBinding = getMethodDeclaration().resolveBinding();
		Set<ITypeBinding> superTypeBindings = new LinkedHashSet<ITypeBinding>();
		for (ITypeBinding typeBinding : typeBindings) {
			ITypeBinding superClassTypeBinding = typeBinding.getSuperclass();
			if (superClassTypeBinding != null)
				superTypeBindings.add(superClassTypeBinding);
			ITypeBinding[] interfaceTypeBindings = typeBinding.getInterfaces();
			for (ITypeBinding interfaceTypeBinding : interfaceTypeBindings)
				superTypeBindings.add(interfaceTypeBinding);
			if (typeBinding.isInterface()) {
				IMethodBinding[] interfaceMethodBindings = typeBinding.getDeclaredMethods();
				for (IMethodBinding interfaceMethodBinding : interfaceMethodBindings) {
					if (methodBinding.overrides(interfaceMethodBinding)
							|| methodBinding.toString().equals(interfaceMethodBinding.toString()))
						return true;
				}
			} else {
				IMethodBinding[] superClassMethodBindings = typeBinding.getDeclaredMethods();
				for (IMethodBinding superClassMethodBinding : superClassMethodBindings) {
					if (methodBinding.overrides(superClassMethodBinding)
							|| (methodBinding.toString().equals(superClassMethodBinding.toString())
									&& (superClassMethodBinding.getModifiers() & Modifier.PRIVATE) == 0))
						return true;
				}
			}
		}
		if (!superTypeBindings.isEmpty()) {
			return overridesMethod(superTypeBindings);
		} else
			return false;
	}

	public String getClassName() {
		return constructorObject.getClassName();
	}

	public ListIterator<ParameterObject> getParameterListIterator() {
		return constructorObject.getParameterListIterator();
	}

	public ParameterObject getParameter(int position) {
		return constructorObject.getParameter(position);
	}

	public List<LocalVariableDeclarationObject> getLocalVariableDeclarations() {
		return constructorObject.getLocalVariableDeclarations();
	}

	public List<LocalVariableInstructionObject> getLocalVariableInstructions() {
		return constructorObject.getLocalVariableInstructions();
	}

	public List<CreationObject> getCreations() {
		return constructorObject.getCreations();
	}

	public List<LiteralObject> getLiterals() {
		return constructorObject.getLiterals();
	}

	public List<AnonymousClassDeclarationObject> getAnonymousClassDeclarations() {
		return constructorObject.getAnonymousClassDeclarations();
	}

	public Set<String> getExceptionsInThrowStatements() {
		return constructorObject.getExceptionsInThrowStatements();
	}

	public Set<PlainVariable> getDeclaredLocalVariables() {
		return constructorObject.getDeclaredLocalVariables();
	}

	public Set<PlainVariable> getDefinedLocalVariables() {
		return constructorObject.getDefinedLocalVariables();
	}

	public Set<PlainVariable> getUsedLocalVariables() {
		return constructorObject.getUsedLocalVariables();
	}

	public boolean containsSuperFieldAccess() {
		return constructorObject.containsSuperFieldAccess();
	}

	public List<TypeObject> getParameterTypeList() {
		return constructorObject.getParameterTypeList();
	}

	public List<String> getParameterList() {
		return constructorObject.getParameterList();
	}

	private boolean equalParameterTypes(List<TypeObject> list1, List<TypeObject> list2) {
		if (list1.size() != list2.size())
			return false;
		for (int i = 0; i < list1.size(); i++) {
			TypeObject type1 = list1.get(i);
			TypeObject type2 = list2.get(i);
			if (!type1.equalsClassType(type2))
				return false;
			// array dimension comparison is skipped if at least one of the
			// class types is a type parameter name, such as E, K, N, T, V, S, U
			if (type1.getArrayDimension() != type2.getArrayDimension() && type1.getClassType().length() != 1
					&& type2.getClassType().length() != 1)
				return false;
		}
		return true;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof MethodObject) {
			MethodObject methodObject = (MethodObject) o;

			return this.returnType.equals(methodObject.returnType)
					&& this.constructorObject.equals(methodObject.constructorObject);
		}
		return false;
	}

	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			if (returnType != null) {
				result = 37 * result + returnType.hashCode();
			}
			result = 37 * result + constructorObject.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!constructorObject.access.equals(Access.NONE))
			sb.append(constructorObject.access.toString()).append(" ");
		if (_abstract)
			sb.append("abstract").append(" ");
		if (_static)
			sb.append("static").append(" ");
		sb.append(returnType.toString()).append(" ");
		sb.append(constructorObject.name);
		sb.append("(");
		if (!constructorObject.parameterList.isEmpty()) {
			for (int i = 0; i < constructorObject.parameterList.size() - 1; i++)
				sb.append(constructorObject.parameterList.get(i).toString()).append(", ");
			sb.append(constructorObject.parameterList.get(constructorObject.parameterList.size() - 1).toString());
		}
		sb.append(")");
		return sb.toString();
	}

	public String getSignature() {
		return constructorObject.getSignature();
	}
}