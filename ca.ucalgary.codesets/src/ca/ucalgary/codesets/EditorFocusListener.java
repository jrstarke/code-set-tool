package ca.ucalgary.codesets;

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


/**
* This is used for listening to events corresponding to the cursor or selection
* changing in a given editor. This can only be listening to one JavaEditor at a
* time.
*/
public class EditorFocusListener implements ISelectionChangedListener {
	ResultSet historySet;
	ResultSet searchSet;
	JavaEditor editor;
	
	ISelectionProvider selectionProvider;
	TableViewer viewer;
	
	public EditorFocusListener(TableViewer viewer, ResultSet historySet, ResultSet searchSet) {
		this.historySet = historySet;
		this.searchSet = searchSet;
		this.viewer = viewer;
	}
	
	public void register(JavaEditor part) {
		unregister();
		editor = part;
		//JavaEditor editor = (JavaEditor)part;
		selectionProvider = editor.getSelectionProvider();
		
		if (selectionProvider != null) {
			if (selectionProvider instanceof IPostSelectionProvider) {
				IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
				provider.addPostSelectionChangedListener(this);
			} else {
				selectionProvider.addSelectionChangedListener(this);
			}
		}
	}
	
	void unregister() {
		if (selectionProvider != null) {
			selectionProvider.removeSelectionChangedListener(this);
		}
		
		selectionProvider = null;
		editor = null;
	}
	
	// Entry point for handling each change event.
	public void selectionChanged(SelectionChangedEvent event) {
		if (computeSelection()) {
			System.out.println("Selection changed");
			viewer.refresh();
		}
	}
	
	boolean computeSelection() {
		ISourceViewer sourceViewer = editor.getViewer();
		if (sourceViewer == null) return false;

		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null) return false;

		int caret = getCaretPosition(sourceViewer, styledText);

		IWorkingCopyManager manager = JavaPlugin.getDefault()
					.getWorkingCopyManager();
		ICompilationUnit unit = manager.getWorkingCopy(editor.getEditorInput());
		if (unit == null) return false;
		
		try {
			IJavaElement element = getElementAt(unit, caret, false);
			//The Following line performs a new search automatically for any references
			// to this element
			new AutoReferenceSearch(searchSet, element);
			if (!(element instanceof ISourceReference))
				return false;
			
			if (element instanceof IType)  {
				// The Following line performs a new search automatically for any references
				// to this element
				new AutoReferenceSearch(searchSet, element);
				historySet.add((ISourceReference)element); //, null, caret);
			} else 
				computeLines((ISourceReference)element, unit.getSource(), caret);
			
			return true;

		} catch (JavaModelException x) {
			System.err.println("Exception raised: " + x);
			if (!x.isDoesNotExist())
				JavaPlugin.log(x.getStatus());
		}
		
		return false;
	}
	
	void computeLines(ISourceReference element, String source, int caret) throws JavaModelException {
		int offset = element.getSourceRange().getOffset();
		int length = element.getSourceRange().getLength();
		String memberSource = source.substring(offset, offset+length);
		
		// get position of first source line (after leading comments)
		int sourceStart = offset;
		if (memberSource.startsWith("//"))
			sourceStart = source.indexOf("\n", offset) + 1;
		else if (memberSource.startsWith("/*"))
			sourceStart = source.indexOf("*/", offset) + 2;
		
		// get position of end of first source line
		int endFirstLine = source.indexOf("{", sourceStart) + 1;
		if (endFirstLine == 0) 
			endFirstLine = source.indexOf(";", sourceStart) + 1;
		if (endFirstLine == 0)
			endFirstLine = source.indexOf("\n", sourceStart) + 1;
		if (endFirstLine == 0)
			endFirstLine = offset + length;

//		String topLine = source.substring(sourceStart, endFirstLine).trim();
		String caretLine = null;
		if (caret > endFirstLine) {
			caretLine = getCurrentLine(source, caret);
		}
		
		historySet.add(element); //, caretLine, caret);
	}
	
	protected IJavaElement getElementAt(ICompilationUnit unit, int offset,
			boolean reconcile) throws JavaModelException {
		if (reconcile) {
			synchronized (unit) {
				unit.reconcile(ICompilationUnit.NO_AST, false, null, null);
			}
			return unit.getElementAt(offset);
		} else if (unit.isConsistent())
			return unit.getElementAt(offset);
		
		return null;
	}
	
	String getCurrentLine(String source, int offset) {
		int start = source.lastIndexOf("\n", offset-1);
		int end = source.indexOf("\n", offset);
		if (start == -1 || end == -1)
			return "";
		
		String result = source.substring(start, end).trim();
		if (result.equals("}") || result.equals(""))
			return getCurrentLine(source, start-1);
		return source.substring(start, end).trim();
	}
	
	int getCaretPosition(ISourceViewer sourceViewer, StyledText styledText) {
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			return extension.widgetOffset2ModelOffset(styledText
					.getCaretOffset());
		} else {
			int offset = sourceViewer.getVisibleRegion().getOffset();
			return offset + styledText.getCaretOffset();
		}
	}
}
