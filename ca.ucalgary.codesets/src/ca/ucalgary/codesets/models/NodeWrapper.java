package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

// a simple wrapper for ASTNode's to make them work in hash tables.
public class NodeWrapper {
	ASTNode node;
	public NodeWrapper(ASTNode node) {
		node.getParent();
		this.node = node;
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof NodeWrapper) {
			NodeWrapper other = (NodeWrapper)o;
			// compare based position and subtree match comparisons...
			if (node.getStartPosition() == other.node.getStartPosition())
				return node.subtreeMatch(new ASTMatcher(), other.node) &&
					node.getRoot().subtreeMatch(new ASTMatcher(), other.node.getRoot());
		}
		return false;
	}
	
	public String toString() {
		return node.toString();
	}

	public ASTNode getNode() {
		return node;
	}
}
