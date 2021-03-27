package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public abstract class PDGAbstractDataDependence extends PDGDependence  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 22228578950586322L;
	private AbstractVariable data;
	private CFGBranchNode loop;
	private volatile int hashCode = 0;
	
	public PDGAbstractDataDependence(PDGNode src, PDGNode dst, PDGDependenceType type,
			AbstractVariable data, CFGBranchNode loop, Graph graph) {
		super(src, dst, type, graph);
		this.data = data;
		this.loop = loop;
		src.addOutgoingEdge(this);
		dst.addIncomingEdge(this);
	}

	public AbstractVariable getData() {
		return data;
	}

	public CFGBranchNode getLoop() {
		return loop;
	}

	public boolean isLoopCarried() {
		if(loop != null)
			return true;
		else
			return false;
	}

	public boolean equals(Object o) {
		if(this == o)
    		return true;
		
		if(o instanceof PDGAbstractDataDependence) {
			PDGAbstractDataDependence dataDependence = (PDGAbstractDataDependence)o;
			boolean equalLoop = false;
			if(this.loop != null && dataDependence.loop != null)
				equalLoop = this.loop.equals(dataDependence.loop);
			if(this.loop == null && dataDependence.loop == null)
				equalLoop = true;
			return this.getSrc().equals(dataDependence.getSrc()) && this.getDst().equals(dataDependence.getDst()) &&
				this.data.equals(dataDependence.data) && equalLoop &&
				this.getType().equals(dataDependence.getType());
		}
		return false;
	}

	public int hashCode() {
		if(hashCode == 0) {
			int result = 17;
			result = 37*result + getSrc().hashCode();
			result = 37*result + getDst().hashCode();
			result = 37*result + data.hashCode();
			if(loop != null)
				result = 37*result + loop.hashCode();
			result = 37*result + getType().hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	@Override
	public String toString() {
		String loopInfo = isLoopCarried() ? " (through loop " + loop.getId() + ")": "";
		return "SRC:" + getSrc().toString() + "-->DST:" + getDst().toString()
		+ " <" + getType().toString().toLowerCase() + "> " + data.toString() 
		+	loopInfo 
		+	"\n";
	}
}
