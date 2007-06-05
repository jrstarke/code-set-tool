package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.ucalgary.codesets.AutoDependencySearch;
import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.sets.DependancySet;

public class EditorAutoDependancyListener implements CodeSetListener {

	CodeSet neighborSet;
	
	public EditorAutoDependancyListener (CodeSet set) {
		neighborSet = set;
	}
	
	public void eventOccured(IJavaElement element) {
		((DependancySet)neighborSet).checkClean(element);
		try {
			AutoDependencySearch.perform((IMember)element, neighborSet);
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		
	}
}
