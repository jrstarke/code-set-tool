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
import ca.ucalgary.codesets.listeners.InteractionListener;
import ca.ucalgary.codesets.listeners.SetListener;
import ca.ucalgary.codesets.sets.*;

//This view displays sets of source code entities as provided by one of a
//number of ResultSet's. 
public class CodeSetView extends ViewPart implements SetListener {

	// The set of all of the sets (used in preferences)
	ArrayList<CodeSet> sets = new ArrayList<CodeSet>();
	
	ResultSet resultSet = new ResultSet();
	
	ReferenceToSet searchSet = new ReferenceToSet();
	HistorySet historySet = new HistorySet();
	EditorChangeSet editorChangeSet = new EditorChangeSet();
	ReferenceFromSet dependancySet = new ReferenceFromSet();

	
	
	private TableViewer viewer;
	private TableViewer setSelectionPane;

	private Action historyAction;		//The history Action, when this is clicked, displays history set
	private Action editorChangeAction;		//The editor change Action, when this is clicked, displays editor change set
	private Action autoReferenceAction;
	private Action dependancyAction;
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
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(historySet);
//		viewer.setLabelProvider(new ElementLabelProvider(editorChangeSet,historySet,searchSet,dependancySet, resultSets ));  
		viewer.setLabelProvider(new ElementLabelProvider(editorChangeSet,historySet)); 
		codeSetView.setContentDescription("History");
		
		//setSelectionPane = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		//setSelectionPane.setContentProvider(resultSets);

		
		ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
		el.setCurrentSet(historySet);  

		viewer.setSorter(null);//new NameSorter());
		viewer.setInput(getViewSite());
		
		//setSelectionPane.setSorter(null);
		//setSelectionPane.setInput(getViewSite());
		
		makeActions();
		
		//Initializes the listener that keeps track of all of the editors
		InteractionListener.setView(this);
		
		historySet.activate();
		historySet.changeListener(this);
		historySet.setAction(historyAction);
		editorChangeSet.activate();
		editorChangeSet.changeListener(this);
		editorChangeSet.setAction(editorChangeAction);
		searchSet.activate();
		searchSet.changeListener(this);
		searchSet.setAction(autoReferenceAction);
		dependancySet.activate();
		dependancySet.changeListener(this);
		dependancySet.setAction(dependancyAction);

		sets.add(historySet);
		sets.add(editorChangeSet);
//		sets.add(searchSet);
//		sets.add(dependancySet);
		
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();			
		
		SideBar sideBar = new SideBar(parent, this, historySet, editorChangeSet);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CodeSetView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		for (CodeSet s:sets) {
			if (s.isActivated())
				manager.add(s.getAction());
		}
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		for (CodeSet s:sets) {
			if (s.isActivated())
				manager.add(s.getAction());
		}
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(setPreferencesAction);
	}

	private void makeActions() {

		historyAction = new Action() {
			public void run(){
				ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
				viewer.setContentProvider(historySet);
				el.setCurrentSet(historySet);
				codeSetView.setContentDescription("History");
				viewer.setSorter(null);//ordering for the set (Chronological)
				viewer.refresh();
			}
		};

		historyAction.setToolTipText("Shows a list of your history");  //change this for specified tooltip
		historyAction.setText("History Set");
		historyAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

		editorChangeAction = new Action() {
			public void run(){
				ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
				viewer.setContentProvider(editorChangeSet);
				el.setCurrentSet(editorChangeSet);
				codeSetView.setContentDescription("Changes by Editor");
				viewer.setSorter(null);//ordering for the set (Chronological)
				viewer.refresh();
			}
		};
		editorChangeAction.setToolTipText("Shows a list of your changes");
		editorChangeAction.setText("Change Set");
		editorChangeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action	

		autoReferenceAction = new Action() {
			public void run(){
				codeSetView.setContentDescription("Auto Referencing by Caret");
				ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
				viewer.setContentProvider(searchSet);
				el.setCurrentSet(searchSet);
				viewer.setSorter(new NameSorter());//ordering for the set (Alphabetical)
				viewer.refresh();
			}
		};
		autoReferenceAction.setToolTipText("Shows a list of elements that reference this element");
		autoReferenceAction.setText("Reference to Set");
		autoReferenceAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action
		
		dependancyAction = new Action() {
			public void run(){
				codeSetView.setContentDescription("Dependancy Elements");
				ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
				viewer.setContentProvider(dependancySet);
				el.setCurrentSet(dependancySet);
				viewer.setSorter(new NameSorter());//ordering for the set (Alphabetical)
				viewer.refresh();
			}
		};
		dependancyAction.setToolTipText("Shows a list of elements that this element references");
		dependancyAction.setText("Reference From Set");
		dependancyAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

		setPreferencesAction = new Action() {
			public void run(){
				CodeSetPreferences preferences = new CodeSetPreferences( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), sets);
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
		
		setSelectionAction = new Action () {
			public void run() {
				ISelection selection = setSelectionPane.getSelection();
				IStructuredSelection sel = (IStructuredSelection) selection;
				CodeSet set = (CodeSet) sel.getFirstElement();
				
				codeSetView.setContentDescription(set.getName());
				ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
				viewer.setContentProvider(set);
				el.setCurrentSet(set);
				viewer.setSorter(new NameSorter());//ordering for the set (Alphabetical)
				viewer.refresh();
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