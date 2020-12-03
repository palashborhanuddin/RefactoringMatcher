package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public class PDGAntiDependence extends PDGAbstractDataDependence  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324037958907216192L;

	public PDGAntiDependence(PDGNode src, PDGNode dst,
			AbstractVariable data, CFGBranchNode loop, Graph graph) {
		super(src, dst, PDGDependenceType.ANTI, data, loop, graph);
	}
	
}
