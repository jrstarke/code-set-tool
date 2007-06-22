package ca.ucalgary.codesets.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.*;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import ca.ucalgary.codesets.*;
import ca.ucalgary.codesets.listeners.CodeSetListener;
import ca.ucalgary.codesets.listeners.EditorFocusListener;
import ca.ucalgary.codesets.listeners.EditorModifiedListener;
import ca.ucalgary.codesets.listeners.ReferenceFromListener;
import ca.ucalgary.codesets.listeners.ReferenceToListener;
import ca.ucalgary.codesets.listeners.SetListener;
import ca.ucalgary.codesets.sets.*;

//This view displays sets of source code entities as provided by one of a
//number of ResultSet's. 
public class CodeSetView extends ViewPart implements SetListener {

	// The set of all of the sets (used in preferences)
	ArrayList<CodeSetListener> listeners = new ArrayList<CodeSetListener>();
	
	ResultSet referenceToSet = new ResultSet();
	CodeSet historySet = new CodeSet(CodeSet.Type.History);
	CodeSet editorChangeSet = new CodeSet(CodeSet.Type.Change);
	ResultSet referenceFromSet = new ResultSet();
	
	private TableViewer viewer;
	private TableViewer setSelectionPane;

	private Action setPreferencesAction;

	private Action doubleClickAction;
	private Action setSelectionAction;

	private CodeSetView codeSetView = this;


	class NameSorter extends ViewerSorter {
	}

	/**
	 * This is a callback that allows us to create and initialize the viewer.
	 *  
	 */
	public void createPartControl(Composite parent) {
		listeners.add(new EditorFocusListener(historySet));
		listeners.add(new EditorModifiedListener(editorChangeSet));
		listeners.add(new ReferenceFromListener(referenceFromSet));
		listeners.add(new ReferenceToListener(referenceToSet));
		
		historySet.changeListener(this);
		editorChangeSet.changeListener(this);
		referenceFromSet.changeListener(this);
		referenceToSet.changeListener(this);
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(historySet);
//		viewer.setLabelProvider(new ElementLabelProvider(editorChangeSet,historySet,searchSet,dependancySet, resultSets ));  
		viewer.setLabelProvider(new ElementLabelProvider(editorChangeSet,historySet, referenceToSet, referenceFromSet)); 
		codeSetView.setContentDescription(listeners.get(0).getName());
		
		ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
		el.setCurrentSet(historySet);  

		viewer.setSorter(null);//new NameSorter());
		viewer.setInput(getViewSite());
		
		makeActions();

		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();			
		
		SideBar sideBar = new SideBar(parent, this, listeners);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				CodeSetView.this.fillContextMenu(manager);
//			}
//		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(setPreferencesAction);
	}

	private void makeActions() {

		setPreferencesAction = new Action() {
			public void run(){
				CodeSetPreferences preferences = new CodeSetPreferences( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), listeners);
				preferences.open();
			}
		};
		setPreferencesAction.setToolTipText("Selects the running sets");
		setPreferencesAction.setText("Preferences");
		setPreferencesAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				IStructuredSelection sel = (IStructuredSelection)selection;								
				IJavaElement elem = (IJavaElement) sel.getFirstElement();
				IEditorPart editor = setCurrentElement(elem);				
			}
		};
	}

//	Jonathan's code
//	Opens the selected source in the editor
	private IEditorPart setCurrentElement(IJavaElement element) {
		try {
			IJavaElement unit = element
			.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit == null) return null;
			IEditorPart editor;
			editor = JavaUI.openInEditor(unit);
			JavaUI.revealInEditor(editor, element);
			return editor;
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
//		setSelectionPane.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				setSelectionAction.run();
//			}
//		});
		
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"Code",
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refresh (Object set) {
		if (set == viewer.getContentProvider())
			viewer.refresh();
//		if (set == setSelectionPane.getContentProvider())
//			setSelectionPane.refresh();
	}
	
	public void setCurrentSet(CodeSet codeSet) {
		ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
		viewer.setContentProvider(codeSet);
		el.setCurrentSet(codeSet);
		codeSetView.setContentDescription(codeSet.getName());
		viewer.setSorter(null);
		viewer.refresh();
	}

}