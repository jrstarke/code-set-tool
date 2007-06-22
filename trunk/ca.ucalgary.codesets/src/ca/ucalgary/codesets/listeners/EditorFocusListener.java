package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;

import ca.ucalgary.codesets.sets.*;


/**
 * This is used for listening to events corresponding to the cursor or selection
 * changing in a given editor. This can only be listening to one JavaEditor at a
 * time.
 */
public class EditorFocusListener extends CodeSetListener {
	CodeSet historySet;

	/**
	 * Creates a new Listener which will add elements to the provided set
	 * @param historySet
	 */
	public EditorFocusListener(CodeSet historySet) { 
		super();
		this.historySet = historySet;
		this.name = "Navigation History";
		historySet.setName(this.name);
	}

	@Override
	public void interactionObserved(InteractionEvent event) {
		if (event.getKind() == InteractionEvent.Kind.SELECTION) {
			historySet.add((ISourceReference)resolveElement(event));
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
	
	public Object getSet() {
		return historySet;
	}
	

}
