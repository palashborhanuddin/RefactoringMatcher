package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public class PDGDataDependence extends PDGAbstractDataDependence  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7806724937334368715L;

	public PDGDataDependence(PDGNode src, PDGNode dst,
			AbstractVariable data, CFGBranchNode loop, Graph graph) {
		super(src, dst, PDGDependenceType.DATA, data, loop, graph);
	}
	
}
