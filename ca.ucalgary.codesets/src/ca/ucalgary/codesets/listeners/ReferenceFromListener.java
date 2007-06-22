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
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;

import ca.ucalgary.codesets.AutoDependencySearch;
import ca.ucalgary.codesets.ResultSet;
import ca.ucalgary.codesets.sets.CodeSet;

public class ReferenceFromListener extends CodeSetListener {
	ResultSet referencesFrom;
	
	public ReferenceFromListener (ResultSet referencesFrom) {
		super();
		this.referencesFrom = referencesFrom;
		this.name = "References From";
		this.referencesFrom.setName(this.name);
	}

	@Override
	public void interactionObserved(InteractionEvent event) {
		if (event.getKind() == InteractionEvent.Kind.SELECTION) {
			CodeSet referenceFromSet = new CodeSet(CodeSet.Type.ReferenceFrom);
			
			IJavaElement element = resolveElement(event);
			
			try {
				AutoDependencySearch.perform((IMember)element, referenceFromSet);
			}
			catch (JavaModelException e)
			{
				e.printStackTrace();
			}
			if (referenceFromSet.size() > 0) {
				JavaElementLabelProvider lp = new JavaElementLabelProvider();
				IJavaElement parent = element.getParent();
				String name = lp.getText(element);
				referenceFromSet.setName(parent.getElementName() + "." + name);
				referencesFrom.add(referenceFromSet);
			}
		}	
	}
	
	public void activate () {
		super.activate();
		MylarMonitorUiPlugin.getDefault().addInteractionListener(this);
	}
	
	public void deactivate () {
		super.deactivate();
		MylarMonitorUiPlugin.getDefault().removeInteractionListener(this);
	}

	@Override
	public Object getSet() {
		return referencesFrom;
	}
}
