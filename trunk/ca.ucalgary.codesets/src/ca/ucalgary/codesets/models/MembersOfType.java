package ca.ucalgary.codesets.models;


import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;

import ca.ucalgary.codesets.views.ElementLabelProvider;

//uses eclipse's ast parser to compute the set of all references from a
//given element.
public class MembersOfType extends GenericVisitor {
	TypeDeclaration type;
	IJavaElement key;
	NodeSet set;
	ElementLabelProvider labelProvider = new ElementLabelProvider();

	public void search(IJavaElement element, ASTNode node) {
		key = element;
		TypeDeclaration typeNode = (TypeDeclaration) ASTHelper.getAncestorByType(node, ASTNode.TYPE_DECLARATION);
		// If the node does not have a TypeDeclaration, it is not a sufficient element to add, so we can stop now
		if (typeNode == null)
			return;
		set = new NodeSet(labelProvider.getFullText(element.getParent()), "Members of Type");

		// If we already have a set for this type, don't recompute it
		if (NodeSetManager.instance().containsSet(set))
			return;
		typeNode.accept(this);

		// Only add those sets that actually have elements
		if (set.size() != 0)
			NodeSetManager.instance().addSet(set);
	}

	void add(ASTNode node) {
		IJavaElement element = ASTHelper.getJavaElement(node);
		// There may not be a JavaElement associated with this specific node.  Adding null elements will cause 
		// display issues
		if (element != null)
			set.add(element, node);
	}
	
	// The methods below are the ASTVisitor method required to traverse the tree.  Once we have reached a method or a
	// Field, we have everything that we're interested in the tree, so traverse no further
	
	protected boolean visitNode(ASTNode node) {
		return true;
	}

	public boolean visit(MethodDeclaration node) {
		add(node);
		return false;
	}

	public boolean visit(FieldDeclaration node) {
		add(node);
		return false;
	}

}