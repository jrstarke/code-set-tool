package ca.ucalgary.codesets.controllers;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.ICodeSetListener;
import ca.ucalgary.codesets.views.ElementLabelProvider;

// a controller for a simple display of code sets
public class DisplayController implements ICodeSetListener {
	TableViewer viewer;

	public DisplayController(IViewSite site, Composite parent) {
		initViewer(site, parent);
		CodeSetManager.instance().addListener(this);
	}

	void initViewer(IViewSite site, Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setLabelProvider(new ElementLabelProvider());
		viewer.setSorter(null);//new NameSorter());
		viewer.setContentProvider(CodeSetManager.instance().displaySet());
		viewer.setInput(site);
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = viewer.getSelection();
				IStructuredSelection sel = (IStructuredSelection)selection;								
				IJavaElement elem = (IJavaElement) sel.getFirstElement();
				openElement(elem);				

			}
		});
	}
	
	// opens the given element in the editor
	private void openElement(IJavaElement element) {
		try {
			IJavaElement unit = element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit != null)
				JavaUI.revealInEditor(JavaUI.openInEditor(unit), element);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void focusChanged(ISourceReference focus) {
	}
	public void setAdded(CodeSet set) {
	}
	public void setChanged(CodeSet set) {
		setDisplaySet();
	}
	public void stateChanged(CodeSet set) {
		setDisplaySet();
		viewer.getControl().setFocus();
	}
	
	// update the display ...
	public void setDisplaySet() {
		viewer.setContentProvider(CodeSetManager.instance().displaySet());
	}
}
