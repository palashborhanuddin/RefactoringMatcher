package com.refactoringMatcher.java.ast.decomposition.cfg;

import java.io.Serializable;

public class PDGControlDependence extends PDGDependence  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7782234636584145199L;
	private boolean trueControlDependence;
	private volatile int hashCode = 0;
	
	public PDGControlDependence(PDGNode src, PDGNode dst, boolean trueControlDependence, Graph graph) {
		super(src, dst, PDGDependenceType.CONTROL, graph);
		this.trueControlDependence = trueControlDependence;
		src.addOutgoingEdge(this);
		dst.addIncomingEdge(this);
	}

	public boolean isTrueControlDependence() {
		if(trueControlDependence == true)
			return true;
		else
			return false;
	}

	public boolean isFalseControlDependence() {
		if(trueControlDependence == true)
			return false;
		else
			return true;
	}

	public boolean sameLabel(PDGControlDependence other) {
		return this.trueControlDependence == other.trueControlDependence;
	}

	public boolean equals(Object o) {
		if(this == o)
    		return true;
		
		if(o instanceof PDGControlDependence) {
			PDGControlDependence controlDependence = (PDGControlDependence)o;
			return this.getSrc().equals(controlDependence.getSrc()) && this.getDst().equals(controlDependence.getDst()) &&
				this.trueControlDependence == controlDependence.trueControlDependence;
		}
		return false;
	}

	public int hashCode() {
		if(hashCode == 0) {
			int result = 17;
			result = 37*result + srcId;
			result = 37*result + dstId;
			result = 37*result + Boolean.valueOf(trueControlDependence).hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	public String toString() {
		StringBuilder type = new StringBuilder();
		if(trueControlDependence == true)
			type.append("T");
		else
			type.append("F");
		return getSrc().toString() + "-->" + getDst().toString() + " " + type.toString() + "" + "\n";
	}
}
