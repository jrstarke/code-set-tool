package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
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
		MethodDeclaration method = (MethodDeclaration)ASTHelper.getAncestorByType(node, ASTNode.METHOD_DECLARATION);
		if (method == null) return;

		set = new NodeSet(labelProvider.getFullText(element), "references from");
		if (NodeSetManager.instance().containsSet(set))
			return;
		method.accept(this);

		if (set.size() > 0)
			NodeSetManager.instance().addSet(set);
	}

	protected boolean visitNode(ASTNode node) {
		return true;
	}

	public boolean visit (ExpressionStatement node) {
		Expression expression = node.getExpression();
		return visitNode(node);
	}

	public boolean visit (MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		IJavaElement element = binding.getJavaElement();
		if (element != null) {
			ICompilationUnit unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit == null)
				return !visitNode(node);
			ISourceReference isr = (ISourceReference) element;
			try {
				ASTNode bindedNode = ASTHelper.getNodeAtPosition(unit, isr.getSourceRange().getOffset());
				ASTNode key = bindedNode;
				if (set.containsKey(key))
					set.get(key).clear();
				set.add(bindedNode);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return visitNode(node);
	}

	public boolean visit (ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		IJavaElement element = binding.getJavaElement();
		if (element != null) {
			ICompilationUnit unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit == null)
				return !visitNode(node);
			ISourceReference isr = (ISourceReference) element;
			try {
				ASTNode bindedNode = ASTHelper.getNodeAtPosition(unit, isr.getSourceRange().getOffset());
				ASTNode key = bindedNode;
				if (set.containsKey(key))
					set.get(key).clear();
				set.add(bindedNode);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		return visitNode(node);
	}
}