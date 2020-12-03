package com.refactoringMatcher.java.ast;

import com.refactoringMatcher.java.ast.decomposition.MethodBodyObject;
import com.refactoringMatcher.java.ast.decomposition.cfg.PlainVariable;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.io.Serializable;
import java.util.*;

public class ConstructorObject implements AbstractMethodDeclaration, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5081012349402108295L;
	protected String name;
	protected List<ParameterObject> parameterList;
    protected Access access;
    protected String className;
    protected MethodBodyObject methodBody;
    protected Set<String> exceptionsInJavaDocThrows;
    protected ASTInformation methodDeclaration;
    private volatile int hashCode = 0;

    public ConstructorObject() {
		this.parameterList = new ArrayList<ParameterObject>();
		this.exceptionsInJavaDocThrows = new LinkedHashSet<String>();
        this.access = Access.NONE;
    }

    public ConstructorObject(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = ASTInformationGenerator.generateASTInformation(methodDeclaration);
		this.name = methodDeclaration.getName().getIdentifier();
    	this.parameterList = new ArrayList<ParameterObject>();
		this.exceptionsInJavaDocThrows = new LinkedHashSet<String>();
		
		int methodModifiers = methodDeclaration.getModifiers();
		if ((methodModifiers & Modifier.PUBLIC) != 0)
			this.access = Access.PUBLIC;
		else if ((methodModifiers & Modifier.PROTECTED) != 0)
			this.access = Access.PROTECTED;
		else if ((methodModifiers & Modifier.PRIVATE) != 0)
			this.access = Access.PRIVATE;
		else
			this.access = Access.NONE;
		
		List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
		for (SingleVariableDeclaration parameter : parameters) {
			ParameterObject parameterObject = new ParameterObject(parameter);
			parameterList.add(parameterObject);
		}
		
		Block methodBody = methodDeclaration.getBody();
		if (methodBody != null) {
			this.methodBody = new MethodBodyObject(methodBody, parameterList);
		}
		

	}

	public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
    	this.methodDeclaration = ASTInformationGenerator.generateASTInformation(methodDeclaration);
    }

    public MethodDeclaration getMethodDeclaration() {
    	return (MethodDeclaration)this.methodDeclaration.recoverASTNode();
    }

    public void setMethodBody(MethodBodyObject methodBody) {
    	this.methodBody = methodBody;
    }

    public MethodBodyObject getMethodBody() {
    	return this.methodBody;
    }

    public void addExceptionInJavaDocThrows(String exception) {
    	this.exceptionsInJavaDocThrows.add(exception);
    }

    public Set<String> getExceptionsInJavaDocThrows() {
		return exceptionsInJavaDocThrows;
	}

    public void setAccess(Access access) {
        this.access = access;
    }

    public Access getAccess() {
        return access;
    }

    public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

	public boolean addParameter(ParameterObject parameter) {
		return parameterList.add(parameter);
	}

    public ListIterator<ParameterObject> getParameterListIterator() {
		return parameterList.listIterator();
	}

    public ParameterObject getParameter(int position) {
    	if(position >= 0 && position < parameterList.size())
    		return parameterList.get(position);
    	else if(position >= parameterList.size()) {
    		ParameterObject param = parameterList.get(parameterList.size()-1);
    		if(param.isVarargs())
    			return param;
    		else
    			return null;
    	}
    	else
    		return null;
    }

    public List<LocalVariableDeclarationObject> getLocalVariableDeclarations() {
    	if(methodBody != null)
    		return methodBody.getLocalVariableDeclarations();
    	else
    		return new ArrayList<LocalVariableDeclarationObject>();
    }

    public List<LocalVariableInstructionObject> getLocalVariableInstructions() {
    	if(methodBody != null)
    		return methodBody.getLocalVariableInstructions();
    	else
    		return new ArrayList<LocalVariableInstructionObject>();
    }

	public List<CreationObject> getCreations() {
		if(methodBody != null)
			return methodBody.getCreations();
		else
			return new ArrayList<CreationObject>();
	}

	public List<LiteralObject> getLiterals() {
		if(methodBody != null)
			return methodBody.getLiterals();
		else
			return new ArrayList<LiteralObject>();
	}

	public List<AnonymousClassDeclarationObject> getAnonymousClassDeclarations() {
		if(methodBody != null)
			return methodBody.getAnonymousClassDeclarations();
		else
			return new ArrayList<AnonymousClassDeclarationObject>();
	}

    public Set<String> getExceptionsInThrowStatements() {
    	if(methodBody != null)
			return methodBody.getExceptionsInThrowStatements();
		else
			return new LinkedHashSet<String>();
    }

	public Set<PlainVariable> getDeclaredLocalVariables() {
		if(methodBody != null)
			return methodBody.getDeclaredLocalVariables();
		else
			return new LinkedHashSet<PlainVariable>();
	}

	public Set<PlainVariable> getDefinedLocalVariables() {
		if(methodBody != null)
			return methodBody.getDefinedLocalVariables();
		else
			return new LinkedHashSet<PlainVariable>();
	}

	public Set<PlainVariable> getUsedLocalVariables() {
		if(methodBody != null)
			return methodBody.getUsedLocalVariables();
		else
			return new LinkedHashSet<PlainVariable>();
	}

    public boolean containsSuperFieldAccess() {
    	if(methodBody != null)
    		return methodBody.containsSuperFieldAccess();
    	else
    		return false;
    }

    public List<TypeObject> getParameterTypeList() {
    	List<TypeObject> list = new ArrayList<TypeObject>();
    	for(ParameterObject parameterObject : parameterList)
    		list.add(parameterObject.getType());
    	return list;
    }

    public List<String> getParameterList() {
    	List<String> list = new ArrayList<String>();
    	for(ParameterObject parameterObject : parameterList)
    		list.add(parameterObject.getType().toString());
    	return list;
    }

    public boolean equals(ClassInstanceCreationObject creationObject) {
    	return this.className.equals(creationObject.getType().getClassType()) &&
    			equalParameterTypes(this.getParameterTypeList(), creationObject.getParameterTypeList());
    }

    private boolean equalParameterTypes(List<TypeObject> list1, List<TypeObject> list2) {
    	if(list1.size() != list2.size())
    		return false;
    	for(int i=0; i<list1.size(); i++) {
    		TypeObject type1 = list1.get(i);
    		TypeObject type2 = list2.get(i);
    		if(!type1.equalsClassType(type2))
    			return false;
    		if(type1.getArrayDimension() != type2.getArrayDimension())
    			return false;
    	}
    	return true;
    }

    public boolean equals(Object o) {
        if(this == o) {
			return true;
		}

		if (o instanceof ConstructorObject) {
			ConstructorObject constructorObject = (ConstructorObject)o;

			return this.className.equals(constructorObject.className) && this.name.equals(constructorObject.name) &&
				this.parameterList.equals(constructorObject.parameterList);
		}
		return false;
    }

    public int hashCode() {
    	if(hashCode == 0) {
    		int result = 17;
    		if(className!=null){
    			result = 37*result + className.hashCode();
    		}
    		result = 37*result + name.hashCode();
    		result = 37*result + parameterList.hashCode();
    		hashCode = result;
    	}
    	return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(!access.equals(Access.NONE))
            sb.append(access.toString()).append(" ");
        sb.append(name);
        sb.append("(");
        if(!parameterList.isEmpty()) {
            for(int i=0; i<parameterList.size()-1; i++)
                sb.append(parameterList.get(i).toString()).append(", ");
            sb.append(parameterList.get(parameterList.size()-1).toString());
        }
        sb.append(")");
        return sb.toString();
    }

	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		sb.append("(");
		if (!this.parameterList.isEmpty()) {
			for (int i = 0; i < this.parameterList.size() - 1; i++)
				sb.append(this.parameterList.get(i).getType()).append(", ");
			sb.append(this.parameterList.get(this.parameterList.size() - 1).getType());
		}
		sb.append(")");
		return sb.toString();
	}

	public boolean isAbstract() {
		return false;
	}
}