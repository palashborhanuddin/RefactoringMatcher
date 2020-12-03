package com.refactoringMatcher.java.ast.decomposition;

import java.io.Serializable;

public enum StatementType implements Serializable{
	ASSERT,
	BLOCK {
		public String toString() {
			return "{";
		}
	},
	BREAK,
	CONSTRUCTOR_INVOCATION,
	CONTINUE,
	DO,
	EMPTY,
	ENHANCED_FOR {
		public String toString() {
			return "for";
		}
	},
	EXPRESSION,
	FOR,
	IF,
	LABELED,
	RETURN,
	SUPER_CONSTRUCTOR_INVOCATION,
	SWITCH_CASE,
	SWITCH,
	SYNCHRONIZED,
	THROW,
	TRY,
	TYPE_DECLARATION,
	VARIABLE_DECLARATION,
	WHILE;
	
	public String toString() {
		return name().toLowerCase();
	}
}
