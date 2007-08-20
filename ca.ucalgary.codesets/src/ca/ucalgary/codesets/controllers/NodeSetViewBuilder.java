package ca.ucalgary.codesets.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ca.ucalgary.codesets.models.ASTHelper;
import ca.ucalgary.codesets.models.NodeWrapper;
import ca.ucalgary.codesets.views.CombinedView;
import ca.ucalgary.codesets.views.ElementLabelProvider;


//this uses the visitor pattern to create the appropriate UI elements for the given 
//NodeSet's. the basic idea is to create a composite for each type declaration and 
//nested composites for each of the field and method declarations. declaration names,
//comment lines and method bodies are represented using labels. in method bodies, 
//code not in the given set are replaced with "...":
//show the code contained in the sets, 

//...getName() ... means this is not the first line in the block
//getName()...   skipped line(s) in this block
//getName() ...} skipped to end of block
//getName() ...} ...} skipped to end of multiple blocks
//getName() } ... skipped line(s) after this block

public class NodeSetViewBuilder extends ASTVisitor {
	Composite parent;
	Composite classView;
	Composite methodView;
	Listener lastListener;

	HashMap<NodeWrapper,Composite> compositeTracker = new HashMap<NodeWrapper,Composite>();

	HashSet<NodeWrapper> includeSet;

	// used to temporarily store lines from method bodies until they are
	// added to the UI as labels
	Stack<String> lines = new Stack<String>();
	ElementLabelProvider labelProvider = new ElementLabelProvider();

	// indent is used to keep track of the white space for lines of code 
	int indent = 0;

	NodeSetViewBuilder(Composite parent, HashSet<NodeWrapper> includeSet) {
		this.parent = parent;
		this.includeSet = includeSet;
	}

	// this is the main entry point
	public static void build(Composite parent, ASTNode node, HashSet<NodeWrapper> includeSet) {
		NodeSetViewBuilder builder = new NodeSetViewBuilder(parent, includeSet);
		node.accept(builder);
	}

	// returns true if the given node should be included in this view
	boolean shouldVisit(ASTNode node) {
		return includeSet.contains(new NodeWrapper(node));
	}

	// methods for building up a set of lines

	boolean isLeadingLine(String line) {
		Pattern p1 = Pattern.compile(" *\\{");
		Pattern p2 = Pattern.compile(" *\\{\\.\\.\\.");
		boolean result = p1.matcher(line).matches() || p2.matcher(line).matches();
		return result;
	}

	void addLine(String line) {
		line = whiteSpace() + line.trim();
		if (!lines.empty()) {
			String last = lines.peek();
//			if (last.equals("...") || last.equals("{...") || last.equals("{")) {
			if (isLeadingLine(last)) {
				line = lines.pop() + line.substring(last.length());
				lines.push(line);
			}
			else {
				lines.push(line);
			}
		} else {
			lines.push(line);
		}
	}

	void appendToLine(String content) {
		if (!lines.empty()) 
			lines.push(lines.pop() + content);
		else
			lines.push(content);
	}

	void elide() {
		if (lines.empty())
			addLine("...");
		else if (!lines.peek().endsWith("..."))
			appendToLine("...");
	}

	boolean appendIf(ASTNode node, String content) {
		if (shouldVisit(node)) {
			appendToLine(content);
			return true;
		}
		return false;
	}

	boolean printIf(ASTNode node, String content) {
		if (shouldVisit(node)) {
			addLine(content);
			return true;
		} else {
			elide();
			return false;
		}
	}

	boolean printIf(ASTNode node) {
		return printIf(node, node.toString());
	}

	String whiteSpace() {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<indent; i++)
			buffer.append("    ");
		return buffer.toString();
	}

	// generalized visitor methods (not visited automatically)

	boolean visit(ASTNode node) {
		return shouldVisit(node);
	}

	boolean visit(Expression node) {
		return shouldVisit(node);
	}

	boolean visit(Statement node) {
		return printIf(node);
	}

	boolean visit(BodyDeclaration node) {
		return shouldVisit(node);
	}

	boolean visit(Type node) {
		return shouldVisit(node);
	}

	boolean blockStatement(Statement node, String line) {
		if (shouldVisit(node)) {
			addLine(line);
			indent++;
			return true;
		} else {
			elide();
			return false;
		}
	}

	void endBlockStatement(Statement node) {
		if (shouldVisit(node))
			indent--;
	}

	// standard visitor methods... (most of these are not used, but they are left in,
	// for the time being, in case they become useful)

	public void preVisit(ASTNode node) {
		ASTNode parent = node.getParent();
		if (shouldVisit(node) && parent instanceof IfStatement) {
			Statement els = ((IfStatement)parent).getElseStatement();
			if (els != null && els.equals(node)) {
				indent--;
				addLine("else");
				indent++;
			}
		}
		else if (shouldVisit(node) && parent instanceof TryStatement) {
			List catches = ((TryStatement)parent).catchClauses();
			if (catches.size() > 0 && catches.contains(node)) {
				indent--;
				addLine("catch");
				indent++;
			}
		}
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		return visit((BodyDeclaration)node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return visit((BodyDeclaration)node);
	}

	public boolean visit(AnonymousClassDeclaration node) {
		return visit((ASTNode)node);
	}

	public boolean visit(ArrayAccess node) {
		return visit((Expression)node);
	}

	public boolean visit(ArrayCreation node) {
		return visit((Expression)node);
	}

	public boolean visit(ArrayInitializer node) {
		return visit((Expression)node);
	}

	public boolean visit(ArrayType node) {
		return visit((Type)node);
	}

	public boolean visit(AssertStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(Assignment node) {
		return visit((Expression)node);
	}

	public boolean visit(Block node) {
		indent--; // leading "{" should line up with parent
		boolean result = printIf(node, "{");
		indent++;
		return result;
	}

	public boolean visit(BlockComment node) {
		return visit((ASTNode)node);
	}

	public boolean visit(BooleanLiteral node) {
		return visit((Expression)node);
	}

	public boolean visit(BreakStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(CastExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(CatchClause node) {
		return visit((ASTNode)node);
	}

	public boolean visit(CharacterLiteral node) {
		return visit((Expression)node);
	}

	public boolean visit(ClassInstanceCreation node) {
		return visit((Expression)node);
	}

	public boolean visit(CompilationUnit node) {
		return visit((ASTNode)node);
	}

	public boolean visit(ConditionalExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(ConstructorInvocation node) {
		return visit((Statement)node);
	}

	public boolean visit(ContinueStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(DoStatement node) {
		return blockStatement(node, "do");
	}

	public boolean visit(EmptyStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(EnhancedForStatement node) {
		return blockStatement(node, "for (" + node.getExpression() + ")");
	}

	public boolean visit(EnumConstantDeclaration node) {
		return visit((BodyDeclaration)node);
	}

	public boolean visit(EnumDeclaration node) {
		return visit((BodyDeclaration)node);
	}

	public boolean visit(ExpressionStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(FieldAccess node) {
		return visit((Expression)node);
	}

	public boolean visit(FieldDeclaration node) {
		return visit((BodyDeclaration)node);
	}

	public boolean visit(ForStatement node) {
		return blockStatement(node, "for (" + node.getExpression() + ")");
	}

	public boolean visit(IfStatement node) {
		return blockStatement(node, "if (" + node.getExpression() + ")");
	}

	public boolean visit(ImportDeclaration node) {
		return visit((ASTNode)node);
	}

	public boolean visit(InfixExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(InstanceofExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(Initializer node) {
		return visit((BodyDeclaration)node);
	}

	// returns the first line of a comment block
	String getFirstLine(String comment) {
		for (String line : comment.split("\n")) {
			line = line.trim();
			if (! line.equals("/**")) {
				if (!line.startsWith("/"))
					return "/" + line + "...*/";
				else
					return line + "...*/";
			}
		}
		return null;
	}

	public boolean visit(Javadoc node) {
		String commentLine = getFirstLine(node.toString());
		Composite parent = methodView != null ? methodView : classView;
		CombinedView.commentLabel(parent, commentLine, this.lastListener);
		return false;
	}

	public boolean visit(LabeledStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(LineComment node) {
		return visit((ASTNode)node);
	}

	public boolean visit(MarkerAnnotation node) {
		return visit((Expression)node);
	}

	public boolean visit(MemberRef node) {
		return visit((ASTNode)node);
	}

	public boolean visit(MemberValuePair node) {
		return visit((ASTNode)node);
	}

	public boolean visit(MethodRef node) {
		return visit((ASTNode)node);
	}

	public boolean visit(MethodRefParameter node) {
		return visit((ASTNode)node);
	}

	public boolean visit(MethodInvocation node) {
		return visit((Expression)node);
	}

	public boolean visit(Modifier node) {
		return visit((ASTNode)node);
	}

	public boolean visit(NormalAnnotation node) {
		return visit((Expression)node);
	}

	public boolean visit(NullLiteral node) {
		return visit((Expression)node);
	}

	public boolean visit(NumberLiteral node) {
		return visit((Expression)node);
	}

	public boolean visit(PackageDeclaration node) {
		return visit((ASTNode)node);
	}

	public boolean visit(ParameterizedType node) {
		return visit((Type)node);
	}

	public boolean visit(ParenthesizedExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(PostfixExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(PrefixExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(PrimitiveType node) {
		return visit((Type)node);
	}

	public boolean visit(QualifiedName node) {
		return visit((Expression)node);
	}

	public boolean visit(QualifiedType node) {
		return visit((Type)node);
	}

	public boolean visit(ReturnStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(SimpleName node) {
		return visit((Expression)node);
	}

	public boolean visit(SimpleType node) {
		return visit((Type)node);
	}

	public boolean visit(SingleMemberAnnotation node) {
		return visit((Expression)node);
	}

	public boolean visit(SingleVariableDeclaration node) {
		return visit((ASTNode)node);
	}

	public boolean visit(StringLiteral node) {
		return visit((Expression)node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		return visit((Statement)node);
	}

	public boolean visit(SuperFieldAccess node) {
		return visit((Expression)node);
	}

	public boolean visit(SuperMethodInvocation node) {
		return visit((Expression)node);
	}

	public boolean visit(SwitchCase node) {
		return visit((Statement)node);
	}

	public boolean visit(SwitchStatement node) {
		return blockStatement(node, "switch (" + node.getExpression() + ")");
	}

	public boolean visit(SynchronizedStatement node) {
		return printIf(node, "synchronized (" + node.getExpression() + ")");
	}

	public boolean visit(TagElement node) {
		return visit((ASTNode)node);
	}

	public boolean visit(TextElement node) {
		return visit((ASTNode)node);
	}

	public boolean visit(ThisExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(ThrowStatement node) {
		return printIf(node, "throw ");
	}

	public boolean visit(TryStatement node) {
		return blockStatement(node, "try");
	}

	// start a new composite corresponding to this type declaration
	public boolean visit(TypeDeclaration node) {
		if (shouldVisit(node)) {
			if(classView == null){
				IJavaElement element = ASTHelper.getJavaElement(node);
				String line = labelProvider.getText(element);
				classView = CombinedView.classView(parent, line, "", makeListener(element,line));
			}
			return true;
		}
		return false;
		
	}

	// start a new composite corresponding to this method declaration
	public boolean visit(MethodDeclaration node) {		
		if (shouldVisit(node)) {
			if (methodView == null) {
				IJavaElement element = ASTHelper.getJavaElement(node);
				String line = labelProvider.getText(element);
				this.lastListener = makeListener(element,line);
				methodView = CombinedView.methodView(classView, line, this.lastListener);
				compositeTracker.put(new NodeWrapper(node), methodView);
				indent++;
			}
			return true;
		}
		return false;
	}

	public boolean visit(TypeDeclarationStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(TypeLiteral node) {
		return visit((Expression)node);
	}

	public boolean visit(TypeParameter node) {
		return visit((ASTNode)node);
	}

	public boolean visit(VariableDeclarationExpression node) {
		return visit((Expression)node);
	}

	public boolean visit(VariableDeclarationStatement node) {
		return visit((Statement)node);
	}

	public boolean visit(VariableDeclarationFragment node) {
		return visit((ASTNode)node);
	}

	public boolean visit(WhileStatement node) {
		return blockStatement(node, "while (" + node.getExpression().toString() + ")");
	}

	public boolean visit(WildcardType node) {
		return visit((Type)node);
	}

	public void endVisit(AnnotationTypeDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(AnnotationTypeMemberDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(AnonymousClassDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(ArrayAccess node) {
		// default implementation: do nothing
	}

	public void endVisit(ArrayCreation node) {
		// default implementation: do nothing
	}

	public void endVisit(ArrayInitializer node) {
		// default implementation: do nothing
	}

	public void endVisit(ArrayType node) {
		// default implementation: do nothing
	}

	public void endVisit(AssertStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(Assignment node) {
		// default implementation: do nothing
	}

	public void endVisit(Block node) {
		appendIf(node, " }");
	}

	public void endVisit(BlockComment node) {
		// default implementation: do nothing
	}

	public void endVisit(BooleanLiteral node) {
		// default implementation: do nothing
	}

	public void endVisit(BreakStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(CastExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(CatchClause node) {
		// default implementation: do nothing
	}

	public void endVisit(CharacterLiteral node) {
		// default implementation: do nothing
	}

	public void endVisit(ClassInstanceCreation node) {
		// default implementation: do nothing
	}

	public void endVisit(CompilationUnit node) {
		// default implementation: do nothing
	}

	public void endVisit(ConditionalExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(ConstructorInvocation node) {
		// default implementation: do nothing
	}

	public void endVisit(ContinueStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(DoStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(EmptyStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(EnhancedForStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(EnumConstantDeclaration node) {
		// default implementation: do nothing
	}	

	public void endVisit(EnumDeclaration node) {
		// default implementation: do nothing
	}	

	public void endVisit(ExpressionStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(FieldAccess node) {
		// default implementation: do nothing
	}

	public void endVisit(FieldDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(ForStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(IfStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(ImportDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(InfixExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(InstanceofExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(Initializer node) {
		// default implementation: do nothing
	}

	public void endVisit(Javadoc node) {
		// default implementation: do nothing
	}

	public void endVisit(LabeledStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(LineComment node) {
		// default implementation: do nothing
	}

	public void endVisit(MarkerAnnotation node) {
		// default implementation: do nothing
	}

	public void endVisit(MemberRef node) {
		// default implementation: do nothing
	}

	public void endVisit(MemberValuePair node) {
		// default implementation: do nothing
	}

	public void endVisit(MethodRef node) {
		// default implementation: do nothing
	}

	public void endVisit(MethodRefParameter node) {
		// default implementation: do nothing
	}

	// finishes the method declaration block by adding all of the lines of code
	// to the method composite
	public void endVisit(MethodDeclaration node) {
		if (shouldVisit(node)) {
			if (compositeTracker.get(new NodeWrapper(node)) != null) {
				StringBuffer buf = new StringBuffer();
				for (String line : lines)
					buf.append(line + "\n");
				lines.clear();
				indent--;
				CombinedView.methodBodyWidget(methodView, buf.toString(),this.lastListener);
				methodView = null;
				lastListener = null;
				compositeTracker.remove(node);
			}
		}
	}

	public void endVisit(MethodInvocation node) {
	}

	public void endVisit(Modifier node) {
	}

	public void endVisit(NormalAnnotation node) {
		// default implementation: do nothing
	}

	public void endVisit(NullLiteral node) {
		// default implementation: do nothing
	}

	public void endVisit(NumberLiteral node) {
		// default implementation: do nothing
	}

	public void endVisit(PackageDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(ParameterizedType node) {
		// default implementation: do nothing
	}	

	public void endVisit(ParenthesizedExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(PostfixExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(PrefixExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(PrimitiveType node) {
		// default implementation: do nothing
	}

	public void endVisit(QualifiedName node) {
		// default implementation: do nothing
	}

	public void endVisit(QualifiedType node) {
		// default implementation: do nothing
	}	

	public void endVisit(ReturnStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(SimpleName node) {
		// default implementation: do nothing
	}

	public void endVisit(SimpleType node) {
		// default implementation: do nothing
	}

	public void endVisit(SingleMemberAnnotation node) {
		// default implementation: do nothing
	}

	public void endVisit(SingleVariableDeclaration node) {
		// default implementation: do nothing
	}

	public void endVisit(StringLiteral node) {
		// default implementation: do nothing
	}

	public void endVisit(SuperConstructorInvocation node) {
		// default implementation: do nothing
	}

	public void endVisit(SuperFieldAccess node) {
		// default implementation: do nothing
	}

	public void endVisit(SuperMethodInvocation node) {
	}

	public void endVisit(SwitchCase node) {
	}

	public void endVisit(SwitchStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(SynchronizedStatement node) {
	}

	public void endVisit(TagElement node) {
	}

	public void endVisit(TextElement node) {
	}

	public void endVisit(ThisExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(ThrowStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(TryStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(TypeDeclaration node) {
		if (compositeTracker.get(new NodeWrapper(node)) != null)
			classView = null;
	}

	public void endVisit(TypeDeclarationStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(TypeLiteral node) {
		// default implementation: do nothing
	}

	public void endVisit(TypeParameter node) {
		// default implementation: do nothing
	}

	public void endVisit(VariableDeclarationExpression node) {
		// default implementation: do nothing
	}

	public void endVisit(VariableDeclarationStatement node) {
		// default implementation: do nothing
	}

	public void endVisit(VariableDeclarationFragment node) {
		// default implementation: do nothing
	}

	public void endVisit(WhileStatement node) {
		endBlockStatement(node);
	}

	public void endVisit(WildcardType node) {
		// default implementation: do nothing
	}

	public Listener makeListener (final IJavaElement element, final String text) {
		Listener result = new Listener() {
			public void handleEvent(Event event) {
				Logger.instance().addEvent("Double Clicked" +"\t" + text);
				openElement((IJavaElement)element);
//				label.setBackground(new Color(null,255,255,255));
			}
		};
		return result;
	}

	private void openElement(IJavaElement element) {
		try {
			IJavaElement unit = element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit != null)
				JavaUI.revealInEditor(JavaUI.openInEditor(unit), element);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
