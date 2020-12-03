package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public class PDGOutputDependence extends PDGAbstractDataDependence  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8519600187411189723L;

	public PDGOutputDependence(PDGNode src, PDGNode dst,
			AbstractVariable data, CFGBranchNode loop, Graph graph) {
		super(src, dst, PDGDependenceType.OUTPUT, data, loop, graph);
	}
	
}
