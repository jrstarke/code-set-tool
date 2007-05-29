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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;

import ca.ucalgary.codesets.sets.*;


/**
 * This is used for listening to events corresponding to the cursor or selection
 * changing in a given editor. This can only be listening to one JavaEditor at a
 * time.
 */
public class EditorFocusListener implements CodeSetListener {
	CodeSet historySet;
	CodeSet searchSet;

	ISelectionProvider selectionProvider;
	TableViewer viewer;

	/**
	 * Creates a new Listener which will add elements to the provided set
	 * @param historySet
	 */
	public EditorFocusListener(CodeSet historySet) { 
		this.historySet = historySet;
	}

	/**
	 * Is called to notify this listener that an event of interest to it has occured
	 */
	public void eventOccured (IJavaElement element) {
		if (element instanceof ISourceReference)
			historySet.add((ISourceReference)element);
	}
}
