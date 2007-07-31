package ca.ucalgary.codesets.models;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
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
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

//uses eclipse's ast parser to compute the set of all references from a
//given element.
public class MembersOfType extends GenericVisitor {
	IType type;
	CodeSet set;

	public void search(IJavaElement element) {
		if (element instanceof IMember) {
			element = (IMember) element;
			ICompilationUnit unit = ((IMember)element).getCompilationUnit();
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(unit);
			parser.setResolveBindings(true);
			this.type = (IType)element.getAncestor(IJavaElement.TYPE);
			if (type == null)
				return;
			set = new CodeSet(new JavaElementLabelProvider().getText(type),
					"Members of Type");

			CompilationUnit node = (CompilationUnit) parser.createAST(null);

			if (CodeSetManager.instance().containsSet(set))
				return;

			node.accept(this);
			if (set.size() != 0)
				CodeSetManager.instance().addSet(set);
		}
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
	
	public boolean visit (TypeDeclaration node) {
		return checkType(node);
	}

	public boolean visit(FieldDeclaration node) {
		for (Object f: node.fragments()) {
			if (f instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) f;
				IVariableBinding binding = fragment.resolveBinding();
				IJavaElement element = binding.getJavaElement();
				if (element != null)
					set.add((ISourceReference)element);
//				System.out.println(element.getElementName());
			}
		}
		return visitNode(node);
	}
	
	// returns true if the given node is within the range of the IJavaElement we
	// are searching on
	public boolean checkType (ASTNode node) {
		try {
			if ((node.getStartPosition() == type.getSourceRange().getOffset()) && 
					(node.getLength() == type.getSourceRange().getLength()))
				return visitNode(node);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return !visitNode(node);
	}

}