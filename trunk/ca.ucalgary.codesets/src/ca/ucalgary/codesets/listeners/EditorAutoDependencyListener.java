package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

import ca.ucalgary.codesets.AutoDependencySearch;
import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.sets.DependencySet;

public class EditorAutoDependencyListener implements CodeSetListener {
	
	public EditorAutoDependencyListener () {
	}
	
	public void eventOccured(IJavaElement element) {
		DependencySet dependencySet = new DependencySet();
		
		try {
			AutoDependencySearch.perform((IMember)element, dependencySet);
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		if (dependencySet.size() > 0) {
			JavaElementLabelProvider lp = new JavaElementLabelProvider();
			IJavaElement parent = element.getParent();
			String name = lp.getText(element);
			dependencySet.setName(parent.getElementName() + "." + name);
			InteractionListener.addReferenceTo(dependencySet);
		}
	}
}
