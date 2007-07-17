package ca.ucalgary.codesets.models;

import java.util.List;

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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

//uses eclipse's ast parser to compute the set of all references from a
//given element.
public class RefOfFile extends GenericVisitor {
	IMember element;
	CodeSet set;

	public void search(IJavaElement element) {
		this.element = (IMember)element;
		ICompilationUnit unit = this.element.getCompilationUnit();
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		set = new CodeSet(new JavaElementLabelProvider().getText(unit), "elements of file");

		CompilationUnit node = (CompilationUnit) parser.createAST(null);

		if (CodeSetManager.instance().containsSet(set))
			return;
		
		node.accept(this);
		if (set.size() != 0)
			CodeSetManager.instance().addSet(set);
	}

	protected boolean visitNode(ASTNode node) {
		return true;
	}



	public boolean visit (MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		IJavaElement element = binding.getJavaElement();
		if (element != null)
			set.add((ISourceReference)element);
		return visitNode(node);
	}

	// TODO fix exception problem found when using this block of code
	public boolean visit (ClassInstanceCreation node) {
		IMethodBinding binding = node.resolveConstructorBinding();
		IJavaElement element = binding.getJavaElement();
		if (element != null)
			set.add((ISourceReference)element);
		return visitNode(node);
	}

	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		IJavaElement element = binding.getJavaElement();
		if (element != null)
			set.add((ISourceReference)element);
		return visitNode(node);
	}

	public boolean visit(FieldDeclaration node) {
		for (Object f: node.fragments()) {
			if (f instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) f;
				IVariableBinding binding = fragment.resolveBinding();
				IJavaElement element = binding.getJavaElement();
				if (element != null)
					set.add((ISourceReference)element);
				System.out.println(element.getElementName());
			}
		}
		return visitNode(node);

	}

}