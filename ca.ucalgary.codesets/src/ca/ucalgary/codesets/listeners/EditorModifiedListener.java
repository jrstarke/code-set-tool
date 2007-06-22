package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;
import org.eclipse.swt.custom.StyledText;

import ca.ucalgary.codesets.sets.CodeSet;

public class EditorModifiedListener extends CodeSetListener {
	CodeSet changeSet;
	
	/**
	 * Creates a new EditorModifiedListener with the specified set to add any Modified elements to
	 * @param editorChangeSet
	 */
	public EditorModifiedListener(CodeSet editorChangeSet) {
		super();
		this.changeSet = editorChangeSet;
		this.name = "Modification History";
		this.changeSet.setName(this.name);
	}
	
	/**
	 * Is notified that an event of interest to this listener has occured, and was associated with 
	 * a specific element
	 */
	public void eventOccured (IJavaElement element) {
		if (element instanceof ISourceReference)
			changeSet.add((ISourceReference) element);
	}

	@Override
	public void interactionObserved(InteractionEvent event) {
		if (event.getKind() == InteractionEvent.Kind.EDIT)
			changeSet.add((ISourceReference) resolveElement(event));
		// TODO Auto-generated method stub
		
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
		return changeSet;
	}
}
