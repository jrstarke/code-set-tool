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

//uses eclipse's ast parser to compute the set of all references from a
//given element.
public class ReferenceFromSearch extends GenericVisitor {
	int REFERENCEFROMVALUE = 1;
	IMember element;
	CodeSet set;

	public void search(IJavaElement element, String name) {
		set = new CodeSet(name, "references from");
		if (CodeSetManager.instance().containsSet(set))
			return;

		if (element instanceof IMember) {
			this.element = (IMember) element;
			ICompilationUnit unit = this.element.getCompilationUnit();
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(unit);
			parser.setResolveBindings(true);

			CompilationUnit node = (CompilationUnit) parser.createAST(null);
			node.accept(this);
			if (set.size() != 0)
				CodeSetManager.instance().addSet(set);
		}
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
			set.add((ISourceReference)element);
			set.srcCache.incrementPosition((ISourceReference)this.element, node.getStartPosition(), REFERENCEFROMVALUE);
		}
		return visitNode(node);
	}

	public boolean visit (ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		IJavaElement element = binding.getJavaElement();
		if (element != null) {
			set.add((ISourceReference)element);
			set.srcCache.incrementPosition((ISourceReference)this.element, node.getStartPosition(), REFERENCEFROMVALUE);
		}
		return visitNode(node);
	}

	public boolean visit(MethodDeclaration node) {
		return checkElement(node);
	}

	public boolean visit (FieldDeclaration node) {
		return checkElement(node);
	}

	// returns true if the given node is within the range of the IJavaElement we
	// are searching on
	public boolean checkElement (ASTNode node) {
		try {
			if ((node.getStartPosition() == element.getSourceRange().getOffset()) && 
					(node.getLength() == element.getSourceRange().getLength()))
				return visitNode(node);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return !visitNode(node);
	}
}