package com.refactoringMatcher.java.ast.decomposition.cfg;

import com.refactoringMatcher.java.ast.CreationObject;
import com.refactoringMatcher.java.ast.VariableDeclarationObject;
import com.refactoringMatcher.java.ast.decomposition.AbstractStatement;
import com.refactoringMatcher.java.ast.util.ExpressionExtractor;
import org.eclipse.jdt.core.dom.*;

import java.io.Serializable;
import java.util.*;

public class PDGNode extends GraphNode implements Comparable<PDGNode>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5345571752541404493L;
	private CFGNode cfgNode;
	protected Set<AbstractVariable> declaredVariables;
	protected Set<AbstractVariable> definedVariables;
	protected Set<AbstractVariable> usedVariables;
	protected Set<CreationObject> createdTypes;
	protected Set<String> thrownExceptionTypes;
	protected Set<VariableDeclarationObject> variableDeclarationsInMethod;
	private Set<AbstractVariable> originalDefinedVariables;
	private Set<AbstractVariable> originalUsedVariables;

	public PDGNode() {
		super();
		initializeAll();
	}

	public void initializeAll() {
		cfgNode = null;
		this.declaredVariables = new LinkedHashSet<AbstractVariable>();
		this.definedVariables = new LinkedHashSet<AbstractVariable>();
		this.usedVariables = new LinkedHashSet<AbstractVariable>();
		this.createdTypes = new LinkedHashSet<CreationObject>();
		this.thrownExceptionTypes = new LinkedHashSet<String>();
		this.variableDeclarationsInMethod = new LinkedHashSet<VariableDeclarationObject>();
		this.originalDefinedVariables = new LinkedHashSet<AbstractVariable>();
		this.originalUsedVariables = new LinkedHashSet<AbstractVariable>();
	}

	public PDGNode(CFGNode cfgNode, Set<VariableDeclarationObject> variableDeclarationsInMethod) {
		super();
		initializeAll();
		if (cfgNode != null) {
			this.id = cfgNode.id;
			cfgNode.setPDGNode(this);
			this.cfgNode = cfgNode;
		}
		this.variableDeclarationsInMethod = variableDeclarationsInMethod;
	}

	public Iterator<AbstractVariable> getDeclaredVariableIterator() {
		return declaredVariables.iterator();
	}

	public Iterator<AbstractVariable> getDefinedVariableIterator() {
		return definedVariables.iterator();
	}

	public Iterator<AbstractVariable> getUsedVariableIterator() {
		return usedVariables.iterator();
	}

	public CFGNode getCFGNode() {
		return cfgNode;
	}

	public Set<String> getThrownExceptionTypes() {
		return thrownExceptionTypes;
	}

	public Iterator<GraphEdge> getDependenceIterator() {
		Set<GraphEdge> allEdges = new LinkedHashSet<GraphEdge>();
		allEdges.addAll(incomingEdges);
		allEdges.addAll(outgoingEdges);
		return allEdges.iterator();
	}

	public Iterator<GraphEdge> getOutgoingDependenceIterator() {
		return outgoingEdges.iterator();
	}

	public Iterator<GraphEdge> getIncomingDependenceIterator() {
		return incomingEdges.iterator();
	}

	public Set<PDGNode> getControlDependentNodes() {
		Set<PDGNode> nodes = new LinkedHashSet<PDGNode>();
		for (GraphEdge edge : outgoingEdges) {
			PDGDependence dependence = (PDGDependence) edge;
			if (dependence instanceof PDGControlDependence) {
				PDGControlDependence controlDependence = (PDGControlDependence) dependence;
				PDGNode dstNode = (PDGNode) controlDependence.getDst();
				nodes.add(dstNode);
			}
		}
		return nodes;
	}

	public Set<PDGNode> getTrueControlDependentNodes() {
		Set<PDGNode> nodes = new LinkedHashSet<PDGNode>();
		for (GraphEdge edge : outgoingEdges) {
			PDGDependence dependence = (PDGDependence) edge;
			if (dependence instanceof PDGControlDependence) {
				PDGControlDependence controlDependence = (PDGControlDependence) dependence;
				if (controlDependence.isTrueControlDependence()) {
					PDGNode dstNode = (PDGNode) controlDependence.getDst();
					nodes.add(dstNode);
				}
			}
		}
		return nodes;
	}

	public PDGNode getControlDependenceParent() {
		for (GraphEdge edge : incomingEdges) {
			PDGDependence dependence = (PDGDependence) edge;
			if (dependence instanceof PDGControlDependence) {
				PDGNode srcNode = (PDGNode) dependence.getSrc();
				return srcNode;
			}
		}
		return null;
	}

	public boolean isControlDependentOnNode(PDGNode node) {
		PDGNode parent = this.getControlDependenceParent();
		while (parent != null) {
			if (parent.equals(node)) {
				return true;
			}
			parent = parent.getControlDependenceParent();
		}
		return false;
	}

	public boolean isControlDependentOnOneOfTheNodes(Set<PDGNode> nodes) {
		for (PDGNode node : nodes) {
			if (this.isControlDependentOnNode(node)) {
				return true;
			}
		}
		return false;
	}

	public PDGControlDependence getIncomingControlDependence() {
		for (GraphEdge edge : incomingEdges) {
			PDGDependence dependence = (PDGDependence) edge;
			if (dependence instanceof PDGControlDependence) {
				return (PDGControlDependence) dependence;
			}
		}
		return null;
	}

	public boolean hasIncomingControlDependenceFromMethodEntryNode() {
		for (GraphEdge edge : incomingEdges) {
			PDGDependence dependence = (PDGDependence) edge;
			if (dependence instanceof PDGControlDependence) {
				PDGNode srcNode = (PDGNode) dependence.getSrc();
				if (srcNode instanceof PDGMethodEntryNode)
					return true;
			}
		}
		return false;
	}

	public Set<AbstractVariable> incomingDataDependencesFromNodesDeclaringOrDefiningVariables() {
		Set<AbstractVariable> dataDependences = new LinkedHashSet<AbstractVariable>();
		for (GraphEdge edge : incomingEdges) {
			PDGDependence dependence = (PDGDependence) edge;
			if (dependence instanceof PDGDataDependence) {
				PDGDataDependence dataDependence = (PDGDataDependence) dependence;
				PDGNode srcNode = (PDGNode) dependence.getSrc();
				if (srcNode.declaresLocalVariable(dataDependence.getData())
						|| srcNode.definesLocalVariable(dataDependence.getData())) {
					dataDependences.add(dataDependence.getData());
				}
			} else if (dependence instanceof PDGOutputDependence) {
				PDGOutputDependence outputDependence = (PDGOutputDependence) dependence;
				PDGNode srcNode = (PDGNode) dependence.getSrc();
				if (srcNode.declaresLocalVariable(outputDependence.getData())
						|| srcNode.definesLocalVariable(outputDependence.getData())) {
					dataDependences.add(outputDependence.getData());
				}
			}
		}
		return dataDependences;
	}

	public boolean declaresLocalVariable(AbstractVariable variable) {
		/*
		 * for (AbstractVariable var : declaredVariables) {
		 * if(var.variableName.equals(variable.variableName)) { return true; } }
		 * return false;
		 */
		return declaredVariables.contains(variable);
	}

	public boolean definesLocalVariable(AbstractVariable variable) {
		/*
		 * for (AbstractVariable var : definedVariables) {
		 * if(var.variableName.equals(variable.variableName)) { return true; } }
		 * return false;
		 */
		return definedVariables.contains(variable);
	}

	public boolean usesLocalVariable(AbstractVariable variable) {
		/*
		 * for (AbstractVariable var : usedVariables) {
		 * if(var.variableName.equals(variable.variableName)) { return true; } }
		 * 
		 * return false;
		 */
		return usedVariables.contains(variable);
	}

	public boolean containsClassInstanceCreation() {
		if (!createdTypes.isEmpty())
			return true;
		return false;
	}

	public boolean throwsException() {
		if (!thrownExceptionTypes.isEmpty())
			return true;
		return false;
	}

	public BasicBlock getBasicBlock() {
		return cfgNode.getBasicBlock();
	}

	public AbstractStatement getStatement() {
		return cfgNode.getStatement();
	}

	public Statement getASTStatement() {
		return cfgNode.getASTStatement();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o instanceof PDGNode) {
			PDGNode pdgNode = (PDGNode) o;
			return this.cfgNode.equals(pdgNode.cfgNode);
		}
		return false;
	}

	public int hashCode() {
		return cfgNode.hashCode();
	}

	public String toString() {
		return cfgNode.toString();
	}

	public int compareTo(PDGNode node) {
		if (this.getId() > node.getId())
			return 1;
		else if (this.getId() < node.getId())
			return -1;
		else
			return 0;
	}

	public String getAnnotation() {
		return "Def = " + definedVariables + " , Use = " + usedVariables;
	}

	public void updateReachingAliasSet(ReachingAliasSet reachingAliasSet) {
		Set<VariableDeclarationObject> variableDeclarations = new LinkedHashSet<VariableDeclarationObject>();
		variableDeclarations.addAll(variableDeclarationsInMethod);
		Statement statement = getASTStatement();
		if (statement instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement vDStatement = (VariableDeclarationStatement) statement;
			if (!vDStatement.getType().resolveBinding().isPrimitive()) {
				List<VariableDeclarationFragment> fragments = vDStatement.fragments();
				for (VariableDeclarationFragment fragment : fragments) {
					Expression initializer = fragment.getInitializer();
					SimpleName initializerSimpleName = null;
					if (initializer != null) {
						if (initializer instanceof SimpleName) {
							initializerSimpleName = (SimpleName) initializer;
						} else if (initializer instanceof FieldAccess) {
							FieldAccess fieldAccess = (FieldAccess) initializer;
							initializerSimpleName = fieldAccess.getName();
						}
					}
					if (initializerSimpleName != null) {
						VariableDeclaration initializerVariableDeclaration = null;
						for (VariableDeclarationObject declarationObject : variableDeclarations) {
							VariableDeclaration declaration = declarationObject.getVariableDeclaration();
							if (declaration.resolveBinding().isEqualTo(initializerSimpleName.resolveBinding())) {
								initializerVariableDeclaration = declaration;
								break;
							}
						}
						if (initializerVariableDeclaration != null) {
							reachingAliasSet.insertAlias(fragment, initializerVariableDeclaration);
						}
					}
				}
			}
		} else if (statement instanceof ExpressionStatement) {
			ExpressionStatement expressionStatement = (ExpressionStatement) statement;
			Expression expression = expressionStatement.getExpression();
			if (expression instanceof Assignment) {
				Assignment assignment = (Assignment) expression;
				processAssignment(reachingAliasSet, variableDeclarations, assignment);
			}
		}
	}

	private void processAssignment(ReachingAliasSet reachingAliasSet,
			Set<VariableDeclarationObject> variableDeclarations, Assignment assignment) {
		Expression leftHandSideExpression = assignment.getLeftHandSide();
		Expression rightHandSideExpression = assignment.getRightHandSide();
		SimpleName leftHandSideSimpleName = null;
		if (leftHandSideExpression instanceof SimpleName) {
			leftHandSideSimpleName = (SimpleName) leftHandSideExpression;
		} else if (leftHandSideExpression instanceof FieldAccess) {
			FieldAccess fieldAccess = (FieldAccess) leftHandSideExpression;
			leftHandSideSimpleName = fieldAccess.getName();
		}
		if (leftHandSideSimpleName != null && !leftHandSideSimpleName.resolveTypeBinding().isPrimitive()) {
			VariableDeclaration leftHandSideVariableDeclaration = null;
			for (VariableDeclarationObject declarationObject : variableDeclarations) {
				VariableDeclaration declaration = declarationObject.getVariableDeclaration();
				if (declaration.resolveBinding().isEqualTo(leftHandSideSimpleName.resolveBinding())) {
					leftHandSideVariableDeclaration = declaration;
					break;
				}
			}
			SimpleName rightHandSideSimpleName = null;
			if (rightHandSideExpression instanceof SimpleName) {
				rightHandSideSimpleName = (SimpleName) rightHandSideExpression;
			} else if (rightHandSideExpression instanceof FieldAccess) {
				FieldAccess fieldAccess = (FieldAccess) rightHandSideExpression;
				rightHandSideSimpleName = fieldAccess.getName();
			} else if (rightHandSideExpression instanceof Assignment) {
				Assignment rightHandSideAssignment = (Assignment) rightHandSideExpression;
				processAssignment(reachingAliasSet, variableDeclarations, rightHandSideAssignment);
				Expression leftHandSideExpressionOfRightHandSideAssignment = rightHandSideAssignment.getLeftHandSide();
				SimpleName leftHandSideSimpleNameOfRightHandSideAssignment = null;
				if (leftHandSideExpressionOfRightHandSideAssignment instanceof SimpleName) {
					leftHandSideSimpleNameOfRightHandSideAssignment = (SimpleName) leftHandSideExpressionOfRightHandSideAssignment;
				} else if (leftHandSideExpressionOfRightHandSideAssignment instanceof FieldAccess) {
					FieldAccess fieldAccess = (FieldAccess) leftHandSideExpressionOfRightHandSideAssignment;
					leftHandSideSimpleNameOfRightHandSideAssignment = fieldAccess.getName();
				}
				if (leftHandSideSimpleNameOfRightHandSideAssignment != null) {
					rightHandSideSimpleName = leftHandSideSimpleNameOfRightHandSideAssignment;
				}
			}
			if (rightHandSideSimpleName != null) {
				VariableDeclaration rightHandSideVariableDeclaration = null;
				for (VariableDeclarationObject declarationObject : variableDeclarations) {
					VariableDeclaration declaration = declarationObject.getVariableDeclaration();
					if (declaration.resolveBinding().isEqualTo(rightHandSideSimpleName.resolveBinding())) {
						rightHandSideVariableDeclaration = declaration;
						break;
					}
				}
				if (leftHandSideVariableDeclaration != null && rightHandSideVariableDeclaration != null) {
					reachingAliasSet.insertAlias(leftHandSideVariableDeclaration, rightHandSideVariableDeclaration);
				}
			} else {
				if (leftHandSideVariableDeclaration != null) {
					reachingAliasSet.removeAlias(leftHandSideVariableDeclaration);
				}
			}
		}
	}

	public void applyReachingAliasSet(ReachingAliasSet reachingAliasSet) {
		if (originalDefinedVariables == null)
			originalDefinedVariables = new LinkedHashSet<AbstractVariable>(definedVariables);
		Set<AbstractVariable> defVariablesToBeAdded = new LinkedHashSet<AbstractVariable>();
		for (AbstractVariable abstractVariable : originalDefinedVariables) {
			if (abstractVariable instanceof CompositeVariable) {
				CompositeVariable compositeVariable = (CompositeVariable) abstractVariable;
				if (reachingAliasSet.containsAlias(compositeVariable)) {
					Set<VariableDeclaration> aliases = reachingAliasSet.getAliases(compositeVariable);
					for (VariableDeclaration alias : aliases) {
						CompositeVariable aliasCompositeVariable = new CompositeVariable(alias,
								compositeVariable.getRightPart());
						defVariablesToBeAdded.add(aliasCompositeVariable);
					}
				}
			}
		}
		definedVariables.addAll(defVariablesToBeAdded);
		if (originalUsedVariables == null)
			originalUsedVariables = new LinkedHashSet<AbstractVariable>(usedVariables);
		Set<AbstractVariable> useVariablesToBeAdded = new LinkedHashSet<AbstractVariable>();
		for (AbstractVariable abstractVariable : originalUsedVariables) {
			if (abstractVariable instanceof CompositeVariable) {
				CompositeVariable compositeVariable = (CompositeVariable) abstractVariable;
				if (reachingAliasSet.containsAlias(compositeVariable)) {
					Set<VariableDeclaration> aliases = reachingAliasSet.getAliases(compositeVariable);
					for (VariableDeclaration alias : aliases) {
						CompositeVariable aliasCompositeVariable = new CompositeVariable(alias,
								compositeVariable.getRightPart());
						useVariablesToBeAdded.add(aliasCompositeVariable);
					}
				}
			}
		}
		usedVariables.addAll(useVariablesToBeAdded);
	}

	public Map<VariableDeclaration, ClassInstanceCreation> getClassInstantiations() {
		Map<VariableDeclaration, ClassInstanceCreation> classInstantiationMap = new LinkedHashMap<VariableDeclaration, ClassInstanceCreation>();
		Set<VariableDeclarationObject> variableDeclarations = new LinkedHashSet<VariableDeclarationObject>();
		variableDeclarations.addAll(variableDeclarationsInMethod);
		// variableDeclarations.addAll(fieldsAccessedInMethod);
		Statement statement = getASTStatement();
		if (statement instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement vDStatement = (VariableDeclarationStatement) statement;
			List<VariableDeclarationFragment> fragments = vDStatement.fragments();
			for (VariableDeclarationFragment fragment : fragments) {
				Expression initializer = fragment.getInitializer();
				if (initializer instanceof ClassInstanceCreation) {
					ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) initializer;
					classInstantiationMap.put(fragment, classInstanceCreation);
				}
			}
		} else if (statement instanceof ExpressionStatement) {
			ExpressionStatement expressionStatement = (ExpressionStatement) statement;
			Expression expression = expressionStatement.getExpression();
			ExpressionExtractor expressionExtractor = new ExpressionExtractor();
			List<Expression> assignments = expressionExtractor.getAssignments(expression);
			for (Expression assignmentExpression : assignments) {
				Assignment assignment = (Assignment) assignmentExpression;
				Expression leftHandSideExpression = assignment.getLeftHandSide();
				Expression rightHandSideExpression = assignment.getRightHandSide();
				if (rightHandSideExpression instanceof ClassInstanceCreation) {
					ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) rightHandSideExpression;
					SimpleName leftHandSideSimpleName = null;
					if (leftHandSideExpression instanceof SimpleName) {
						leftHandSideSimpleName = (SimpleName) leftHandSideExpression;
					} else if (leftHandSideExpression instanceof FieldAccess) {
						FieldAccess fieldAccess = (FieldAccess) leftHandSideExpression;
						leftHandSideSimpleName = fieldAccess.getName();
					}
					if (leftHandSideSimpleName != null) {
						VariableDeclaration leftHandSideVariableDeclaration = null;
						for (VariableDeclarationObject declarationObject : variableDeclarations) {
							VariableDeclaration declaration = declarationObject.getVariableDeclaration();
							if (declaration.resolveBinding().isEqualTo(leftHandSideSimpleName.resolveBinding())) {
								leftHandSideVariableDeclaration = declaration;
								break;
							}
						}
						if (leftHandSideVariableDeclaration != null) {
							classInstantiationMap.put(leftHandSideVariableDeclaration, classInstanceCreation);
						}
					}
				}
			}
		}
		return classInstantiationMap;
	}

	public boolean changesStateOfVariable(PlainVariable plainVariable) {
		for (AbstractVariable abstractVariable : definedVariables) {
			if (abstractVariable instanceof CompositeVariable) {
				CompositeVariable compositeVariable = (CompositeVariable) abstractVariable;
				if (compositeVariable.getInitialVariable().equals(plainVariable)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean changesStateOfReference(VariableDeclaration variableDeclaration) {
		for (AbstractVariable abstractVariable : definedVariables) {
			if (abstractVariable instanceof CompositeVariable) {
				CompositeVariable compositeVariable = (CompositeVariable) abstractVariable;
				if (variableDeclaration.equals(compositeVariable))
					return true;
			}
		}
		return false;
	}

	public boolean accessesReference(VariableDeclaration variableDeclaration) {
		for (AbstractVariable abstractVariable : usedVariables) {
			if (abstractVariable instanceof PlainVariable) {
				PlainVariable plainVariable = (PlainVariable) abstractVariable;
				if (variableDeclaration.equals(plainVariable))
					return true;
			}
		}
		return false;
	}

	public boolean assignsReference(VariableDeclaration variableDeclaration) {
		Statement statement = getASTStatement();
		if (statement instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement vDStatement = (VariableDeclarationStatement) statement;
			List<VariableDeclarationFragment> fragments = vDStatement.fragments();
			for (VariableDeclarationFragment fragment : fragments) {
				Expression initializer = fragment.getInitializer();
				SimpleName initializerSimpleName = null;
				if (initializer != null) {
					if (initializer instanceof SimpleName) {
						initializerSimpleName = (SimpleName) initializer;
					} else if (initializer instanceof FieldAccess) {
						FieldAccess fieldAccess = (FieldAccess) initializer;
						initializerSimpleName = fieldAccess.getName();
					}
				}
				if (initializerSimpleName != null) {
					if (variableDeclaration.resolveBinding().isEqualTo(initializerSimpleName.resolveBinding())) {
						return true;
					}
				}
			}
		} else if (statement instanceof ExpressionStatement) {
			ExpressionStatement expressionStatement = (ExpressionStatement) statement;
			Expression expression = expressionStatement.getExpression();
			ExpressionExtractor expressionExtractor = new ExpressionExtractor();
			List<Expression> assignments = expressionExtractor.getAssignments(expression);
			for (Expression assignmentExpression : assignments) {
				Assignment assignment = (Assignment) assignmentExpression;
				Expression rightHandSideExpression = assignment.getRightHandSide();
				SimpleName rightHandSideSimpleName = null;
				if (rightHandSideExpression instanceof SimpleName) {
					rightHandSideSimpleName = (SimpleName) rightHandSideExpression;
				} else if (rightHandSideExpression instanceof FieldAccess) {
					FieldAccess fieldAccess = (FieldAccess) rightHandSideExpression;
					rightHandSideSimpleName = fieldAccess.getName();
				}
				if (rightHandSideSimpleName != null) {
					if (variableDeclaration.resolveBinding().isEqualTo(rightHandSideSimpleName.resolveBinding())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
