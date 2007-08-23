package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.dom.ASTNode;

public class ASTNodePlaceholder {
	int startPosition;
	int nodeType;
	
	public ASTNodePlaceholder(ASTNode node) {
		startPosition = node.getStartPosition();
		nodeType = node.getNodeType();
	}
	
	boolean match(ASTNodePlaceholder node) {
		return node.startPosition == startPosition && 
			node.nodeType == nodeType;
	}
	
	public int hashCode() {
		return startPosition + nodeType;
	}
	
	public boolean equals(Object other) {
		if (other instanceof ASTNodePlaceholder)
			return ((ASTNodePlaceholder)other).match(this);
		return false;
	}
}