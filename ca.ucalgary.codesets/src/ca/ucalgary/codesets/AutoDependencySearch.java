package ca.ucalgary.codesets;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;

import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.sets.DependencySet;

public class AutoDependencySearch extends GenericVisitor {

	private IMember fElement;
	private int fStart;
	private int fLength;
	private int fEnd;
	private ASTNode fResult;
	private DependencySet dependencySet;

	private AutoDependencySearch(IMember element, CodeSet dependencySet) throws JavaModelException {
		super(true);
		Assert.isNotNull(element);
		this.dependencySet = (DependencySet)dependencySet;
		fElement= element;
		ISourceRange sourceRange= fElement.getNameRange();
		fStart= sourceRange.getOffset();
		fLength= sourceRange.getLength();
		fEnd= fStart + fLength;
	}

	public static ASTNode perform(IMember member, CodeSet dependencySet) throws JavaModelException {
		AutoDependencySearch selector= new AutoDependencySearch(member, dependencySet);
		ICompilationUnit unit= member.getCompilationUnit();

		ASTParser parser= ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		parser.setResolveBindings(true);

		CompilationUnit node= (CompilationUnit) parser.createAST(null);
		node.accept(selector);
		ASTNode result= selector.fResult;
		return result;
	}

	protected boolean visitNode(ASTNode node) {
		return true;
	}

	protected void endVisitNode(ASTNode node) {
		// Do Nothing
	}

	public boolean visit (MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		IJavaElement element = binding.getJavaElement();
		dependencySet.add((ISourceReference)element);
		return visitNode(node);
	}

	public boolean visit(MethodDeclaration node) {
		return checkElement(node);
	}
	
	public boolean visit (FieldDeclaration node) {
		return checkElement(node);
	}
	
	public boolean checkElement (ASTNode node) {
		try {
			if ((node.getStartPosition() == fElement.getSourceRange().getOffset()) && (node.getLength() == fElement.getSourceRange().getLength())) {
				return true;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}
}