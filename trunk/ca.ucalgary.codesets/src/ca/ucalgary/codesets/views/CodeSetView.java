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
import ca.ucalgary.codesets.listeners.PartListener;
import ca.ucalgary.codesets.listeners.SetListener;
import ca.ucalgary.codesets.sets.*;

//This view displays sets of source code entities as provided by one of a
//number of ResultSet's. 
public class CodeSetView extends ViewPart implements SetListener {

	ArrayList sets = new ArrayList();
	// Set containing all elements that have been selected
	CodeSet searchSet = new CodeSet();
	HistorySet historySet = new HistorySet(searchSet);
	// Two sets containing all elements that have been modified
	EditorChangeSet editorChangeSet = new EditorChangeSet();
	CodeSet elementChangeSet = new CodeSet();


	private TableViewer viewer;

	private Action historyAction;		//The history Action, when this is clicked, displays history set
	private Action editorChangeAction;		//The editor change Action, when this is clicked, displays editor change set
	private Action elementChangeAction; 	//The element change Action, when this is clicked, displays element change set
	private Action autoReferenceAction;
	private Action setPreferencesAction;

	private Action doubleClickAction;
	
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
		viewer.setLabelProvider(new ElementLabelProvider(editorChangeSet,historySet,searchSet));  
		codeSetView.setContentDescription("History");

		ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
		el.setCurrentSet(historySet);  
		
		viewer.setSorter(null);//new NameSorter());

		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();			


		// globally listen for part activation events
//		final EditorFocusListener listener = new EditorFocusListener(viewer, historySet.getResultSet(), searchSet);
//		final EditorModifiedListener changeListener = new EditorModifiedListener(viewer, editorChangeSet); 
		//Registers the ElementChangedListener to the JavaCore to listen for changes
		JavaCore.addElementChangedListener(new JavaElementChangeListener(viewer, elementChangeSet), ElementChangedEvent.POST_RECONCILE);

		//Initializes the listener that keeps track of all of the editors
		PartListener partListener = new PartListener();
		historySet.activate();
		historySet.changeListener(this);
		editorChangeSet.activate();
		editorChangeSet.changeListener(this);
		searchSet.changeListener(this);
//		PartListener.addListener(changeListener);
		

		getSite().getPage().addPartListener(partListener);

		//The following lines get the Current Editor. 
		//There has been a bug, where the editor *might* not be loaded which is giving a 
		//nullpointerexception. If this is where the exception is happening, then the != nulls will 
		//stop the exception, and will print a statement to the console displaying which part of these 
		//statements is null
		//We will remove the if statements of the parts that we know aren't ever null
		//If you see something in the console at startup, then we know there was a null
		IWorkbenchWindow workbench = getSite().getWorkbenchWindow();
		if (workbench != null) {
			IWorkbenchPage page = workbench.getActivePage();
			if (page != null) {
				IEditorPart editor = page.getActiveEditor();

				if (editor != null && editor instanceof JavaEditor) { //if JavaEditor, register the editor with the listeners.
					for (CodeSetListener l:partListener.getListeners()) {
						l.register((JavaEditor)editor);
					}
//					 changeListener.register(editor);
				}
				else if(editor == null)
					System.out.println("Editor is Null");
			}
			else
				System.out.println("Page is Null");  //I've received this being null
		}
		else
			System.out.println("Workbench is Null");
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
		manager.add(historyAction);
		manager.add(editorChangeAction);
		manager.add(elementChangeAction);
		manager.add(autoReferenceAction);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(historyAction);
		manager.add(editorChangeAction);	
		manager.add(elementChangeAction);
		manager.add(autoReferenceAction);
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

		elementChangeAction = new Action() {
			public void run(){
				viewer.setContentProvider(elementChangeSet);
				codeSetView.setContentDescription("Changes by Element");
				viewer.setSorter(null); //ordering for the set (Chronological)
				viewer.refresh();
			}
		};
		elementChangeAction.setToolTipText("Shows a list of your changes");
		elementChangeAction.setText("Element Change Set");
		elementChangeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
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
		autoReferenceAction.setText("Auto Reference Set");
		autoReferenceAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

		setPreferencesAction = new Action() {
			public void run(){
				CodeSetPreferences preferences = new CodeSetPreferences( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
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
	
	public void refresh (CodeSet set) {
		if (set == viewer.getContentProvider())
			viewer.refresh();
	}

}