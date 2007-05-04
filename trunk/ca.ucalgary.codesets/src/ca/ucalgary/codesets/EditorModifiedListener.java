package ca.ucalgary.codesets;

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
import org.eclipse.swt.custom.StyledText;

public class EditorModifiedListener implements IDocumentListener {
	ChangeSet changeSet;
	TableViewer viewer;
	JavaEditor editor;
	DocumentEvent event;
	
	public EditorModifiedListener(TableViewer viewer, ChangeSet changeSet) {
		this.changeSet = changeSet;
		this.viewer = viewer;
	}
	
	public void documentChanged (DocumentEvent event) {
		this.event = event;
		changeSet.add(computeSelection());
		System.out.println("A Change was detected");

		viewer.refresh();
	}
	
	public void documentAboutToBeChanged (DocumentEvent event) {
		this.event = event;
		changeSet.remove(computeSelection());
		
		viewer.refresh();
	}
	
	public void register(JavaEditor editor) {
		this.editor = editor;
		editor.getViewer().getDocument().addDocumentListener(this);
	}
	
	ISourceReference computeSelection() {
		ISourceViewer sourceViewer = editor.getViewer();
		if (sourceViewer == null) return null;

		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null) return null;

		int caret = getCaretPosition(sourceViewer, styledText);
		System.out.println(caret);
		
		IWorkingCopyManager manager = JavaPlugin.getDefault()
				.getWorkingCopyManager();
		ICompilationUnit unit = manager.getWorkingCopy(editor.getEditorInput());
		if (unit == null) return null;
		
		try {
			IJavaElement element = getElementAt(unit, caret, true);
			if (!(element instanceof ISourceReference))
				return null;
			
			return (ISourceReference)element;

		} catch (JavaModelException x) {
			System.err.println("Exception raised: " + x);
			if (!x.isDoesNotExist())
				JavaPlugin.log(x.getStatus());
		}
		
		return null;
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
			//caretLine = getCurrentLine(source, caret);
		}
		
		//historySet.add(element); //, caretLine, caret);
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
