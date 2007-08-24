package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;


//Special node for the Cache. Contains the ICompiationUnit and the CompilationUnit.
//The CompilationUnit is created through the constructor.  This node is set up so that
//you can compare it to aother nodes. 
public class CacheNode implements Comparable {

	private ICompilationUnit iCompUnit;
	private CompilationUnit compUnit;

	public CacheNode(ICompilationUnit unit){
		iCompUnit = unit;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(iCompUnit);
		parser.setResolveBindings(true);
		compUnit = (CompilationUnit) parser.createAST(null);
	}

	public CompilationUnit getCompUnit() {
		return this.compUnit;
	}

	public ICompilationUnit getICompUnit() {
		return this.iCompUnit;
	}

	//Only returns 0 if the ICompilationUnits are equal to each other
	//otherwise it returns -1
	public int compareTo(Object arg0) {
		if(arg0 instanceof ICompilationUnit){
			ICompilationUnit temp = (ICompilationUnit) arg0;
			if(this.iCompUnit.equals(temp))
				return 0;
		}
		return -1;		
	}
}
