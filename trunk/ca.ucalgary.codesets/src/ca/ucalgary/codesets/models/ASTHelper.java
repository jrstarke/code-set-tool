package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
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

public class ASTHelper {

	// returns the IJavaElement corresponding to the given node
	public static IJavaElement getJavaElement(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		return binding.getJavaElement();
	}

	public static IJavaElement getJavaElement(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		return binding.getJavaElement();
	}

	// returns a root AST node based on the given compilation unit
	static CompilationUnit getStartNode(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

	// returns the most specific ASTNode for the given compilation unit and
	// position
	public static ASTNode getNodeAtPosition(ICompilationUnit unit, int position) {
		return new Visitor().findNode(unit, position);
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

		public void preVisit(ASTNode node) {
			if (contains(node.getStartPosition(), node.getLength(), position)) {
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
			CompilationUnit node = getStartNode(unit);
			found = null;
			this.position = position;
			node.accept(this);
		}
		
		// returns the most specific ASTNode for the given compilation unit and
		// position (exception: if a block is found we try to find a statement in
		// the block to return, if possible.)
		public ASTNode findNode(ICompilationUnit unit, int position) {
			try {
				char[] source = unit.getSource().toCharArray();
				doSearch(unit, position);
				while (found != null && found.getNodeType() == ASTNode.BLOCK && 
					precedingChar(source, position) == ';')
						doSearch(unit, --position);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
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
