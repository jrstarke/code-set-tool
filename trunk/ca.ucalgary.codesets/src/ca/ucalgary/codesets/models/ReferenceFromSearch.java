package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;

import ca.ucalgary.codesets.views.ElementLabelProvider;

//uses eclipse's ast parser to compute the set of all references from a
//given element.
public class ReferenceFromSearch extends GenericVisitor {
	int REFERENCEFROMVALUE = 1;
	NodeSet set;
	ElementLabelProvider labelProvider = new ElementLabelProvider();

	public void search(IJavaElement element, ASTNode node) {
		// Parse the tree, but only from the closest previous Method Declaration
		MethodDeclaration method = (MethodDeclaration)ASTHelper.getAncestorByType(node, ASTNode.METHOD_DECLARATION);
		// If there is no previous method declaration, we must not be inside of a method, so no references will exist
		if (method == null) return;

		set = new NodeSet(labelProvider.getFullText(element), "references from");
		// If we've already computed this set, save time, don't repeat
		if (NodeSetManager.instance().containsSet(set))
			return;
		method.accept(this);

		if (set.size() > 0)
			NodeSetManager.instance().addSet(set);
	}

	// This will always return true.  From any point Method declaration down, we will parse to look for MethodInvocations
	// or ClassInstanceCreations
	protected boolean visitNode(ASTNode node) {
		return true;
	}

	public boolean visit (MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		IJavaElement element = binding.getJavaElement();
		// Elements do not necessarily have a binding
		if (element != null) {
			ICompilationUnit unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
			// or a compilation unit
			if (unit == null) 
				// If our element does not have a compilartion unit, there is no point in traversing, as our children won't
				// either
				return !visitNode(node);
			ISourceReference isr = (ISourceReference) element;
			try {
				ASTNode key = ASTHelper.getNodeAtPosition(unit, isr.getSourceRange().getOffset());
				if (set.containsKey(key))
					set.get(key).clear();
				set.add(key);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return visitNode(node);
	}

	public boolean visit (ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		IJavaElement element = binding.getJavaElement();
		// Elements do not necessarily have a binding
		if (element != null) {
			// or a compilation unit
			ICompilationUnit unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit == null)
				// if our element does not have a compilation unit, just stop traversing here
				return !visitNode(node);
			ISourceReference isr = (ISourceReference) element;
			try {
				ASTNode key = ASTHelper.getNodeAtPosition(unit, isr.getSourceRange().getOffset());
				if (set.containsKey(key))
					set.get(key).clear();
				set.add(key);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return visitNode(node);
	}
}