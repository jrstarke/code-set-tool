package ca.ucalgary.codesets.models;

import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;

import ca.ucalgary.codesets.controllers.Logger;

public class ASTHelper {
	private static HashMap<ICompilationUnit,CompilationUnit> ASTCache = new HashMap<ICompilationUnit,CompilationUnit>();

	// returns the IJavaElement corresponding to the given node
	public static IJavaElement getJavaElement(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		return binding.getJavaElement();
	}

	public static IJavaElement getJavaElement(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		return binding.getJavaElement();
	}
	
	public static IJavaElement getJavaElement(ASTNode node) {
		int type = node.getNodeType();
		if (type == ASTNode.TYPE_DECLARATION)
			return getJavaElement((TypeDeclaration)node);
		else if (type == ASTNode.METHOD_DECLARATION)
			return getJavaElement((MethodDeclaration)node);
		return null;
	}

	// returns a root AST node based on the given compilation unit
	static CompilationUnit getStartNode(ICompilationUnit unit) {
//		Logger.instance().start("ASTHelper.getStartNode(ICompilationUnit)");
		CompilationUnit compUnit = ASTCache.get(unit);
		if (compUnit == null) {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(unit);
			parser.setResolveBindings(true);
			compUnit = (CompilationUnit) parser.createAST(null);
//			ASTCache.put(unit, compUnit);
		}
//		Logger.instance().stop("ASTHelper.getStartNode(ICompilationUnit)");

		System.out.println("Size of cache: " + ASTCache.size());
		return compUnit;
	}
	
	// returns the most specific ASTNode for the given compilation unit and
	// position
	public static ASTNode getNodeAtPosition(ICompilationUnit unit, int position) {
		return getNodeAtPosition(unit, position, false);
	}
	
	public static ASTNode getNodeAtPosition(ICompilationUnit unit, int position, boolean lookForDeclaration) {
//		Logger.instance().start("ASTHelper.getNodeAtPosition(ICompilationUnit,int)");
		ASTNode node = new Visitor(lookForDeclaration).findNode(unit,position);
//		Logger.instance().stop("ASTHelper.getNodeAtPosition(ICompilationUnit,int)");
		return node;
	}	

	// returns the character position at the start of the given line number
	static int getPosition(String source, int lineNumber) {
		int lines = 0;
		for (int i = 0; i < source.length(); i++) {
			if (lines == lineNumber - 1)
				return i;
			if (source.charAt(i) == '\n')
				lines++;
		}

		return -1;
	}

	public static ASTNode getNode(IJavaElement element) {
		if (element instanceof ISourceReference) {
			ISourceReference ref = (ISourceReference)element;
			ICompilationUnit unit = (ICompilationUnit)element.getAncestor(IJavaElement.COMPILATION_UNIT);
			
			if (unit != null)
				try {
					return getNodeAtPosition(unit, ref.getSourceRange().getOffset(), true);
				} catch (JavaModelException e) {
					// we'll just return null in this situation
				}
		}

		return null;
	}
	
	// returns the most specific ASTNode for the given compilation unit and
	// line
	public static ASTNode getNodeAtLine(ICompilationUnit unit, int line) {
		try {
			int position = 0;
			String source = unit.getSource();
			for (char c : source.toCharArray()) {
				position++;
				if (c == '\n')
					line--;
				if (line==0)
					break;
			}

			return getNodeAtPosition(unit, position);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// return the method node that is an ancestor of the given node, or null if
	// not found
	public static ASTNode getMethodAncestor(ASTNode node) {
		return getAncestorByType(node, ASTNode.METHOD_DECLARATION);
	}

	// return the method node that is an ancestor of the given node, or null if
	// not found
	public static ASTNode getTypeAncestor(ASTNode node) {
		return getAncestorByType(node, ASTNode.TYPE_DECLARATION);
	}

	// returns an ancestor node of the given type, or null if not found
	public static ASTNode getAncestorByType(ASTNode node, int type) {
		if (node == null)
			return null;
		else if (node.getNodeType() == type)
			return node;
		else
			return getAncestorByType(node.getParent(), type);
	}

	// a simple visitor for finding nodes based on position
	static class Visitor extends ASTVisitor {
		ASTNode found = null;
		int position = 0;
		boolean lookForDeclaration = false;

		Visitor(boolean lookForDeclaration) {
			this.lookForDeclaration = lookForDeclaration;
		}
		
		public void preVisit(ASTNode node) {
			if (contains(node.getStartPosition(), node.getLength(), position)) {
				if (!lookForDeclaration)
					found = node;
				else if (node.getNodeType() == ASTNode.TYPE_DECLARATION ||
						node.getNodeType() == ASTNode.METHOD_DECLARATION ||
						node.getNodeType() == ASTNode.FIELD_DECLARATION)
					found = node;
			}
		}

		public void postVisit(ASTNode node) {
		}

		char precedingChar(char[] source, int position) {
			for (int i = position; i >= 0; i--)
				if (!Character.isWhitespace(source[i]))
					return source[i];
			return 0;
		}

		void doSearch(ICompilationUnit unit, int position) {
//			Logger.instance().start("ASTHelper.Visitor.doSearch(ICompilationUnit,int)");
			CompilationUnit node = getStartNode(unit);
			found = null;
			this.position = position;
			node.accept(this);
//			Logger.instance().start("ASTHelper.Visitor.doSearch(ICompilationUnit,int)");
		}

		// returns the most specific ASTNode for the given compilation unit and
		// position (exception: if a block is found we try to find a statement in
		// the block to return, if possible.)
		public ASTNode findNode(ICompilationUnit unit, int position) {
//			Logger.instance().start("ASTHelper.Visitor.findNode(ICompilationUnit,int)");
			try {
				char[] source = unit.getSource().toCharArray();
				doSearch(unit, position);
				while (found != null && found.getNodeType() == ASTNode.BLOCK && 
						precedingChar(source, position) == ';')
					doSearch(unit, --position);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
//			Logger.instance().stop("ASTHelper.Visitor.findNode(ICompilationUnit,int)");
			return found;
		}

		ASTNode previousNode() {
			return null;
		}

		// return true if the given position is within start and start+length
		boolean contains(int start, int length, int position) {
			return position >= start && position < (start + length);
		}
	}

}
