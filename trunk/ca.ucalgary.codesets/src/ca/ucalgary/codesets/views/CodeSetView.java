package ca.ucalgary.codesets.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.*;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
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

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class CodeSetView extends ViewPart {

	HistorySet historySet = new HistorySet();	//Set containing all elements that have been selected
	ChangeSet changeSet = new ChangeSet();		//Set containing all elements that have been modified
	
	private TableViewer viewer;
	
	private Action historyAction;		//The history Action, when this is clicked, displays history set
	private Action changeAction;		//The change Action, when this is clicked, displays change set
	
	private Action doubleClickAction;
	
	class NameSorter extends ViewerSorter {
	}

	public CodeSetView() {
	}

	/**
	 * This is a callback that allows us to create and initialize the viewer.
	 *  
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(historySet);
		viewer.setLabelProvider(new ElementLabelProvider(changeSet,historySet));  
		
		ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
		el.setCurrentSet(historySet);  
		
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();			
		
		
		// globally listen for part activation events
		final EditorFocusListener listener = new EditorFocusListener(viewer, historySet);
		final EditorModifiedListener changeListener = new EditorModifiedListener(viewer, changeSet); 
		
		IPartListener partListener = new IPartListener() {
			public void partActivated(IWorkbenchPart part) {
				if (part instanceof JavaEditor) {
					System.out.println("Editor Changed " + (JavaEditor)part);
					listener.register((JavaEditor)part);
					changeListener.register((JavaEditor)part);
				}
			}

			public void partBroughtToTop(IWorkbenchPart part) {
				
			}

			public void partClosed(IWorkbenchPart part) {
				
			}

			public void partDeactivated(IWorkbenchPart part) {
				
			}

			public void partOpened(IWorkbenchPart part) {
				
			}
		};

		getSite().getPage().addPartListener(partListener);
		
		//Get current editor
		IEditorPart editor = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
		
		//if the editor is a JavaEditor, register the editor with the listeners. 
		if (editor instanceof JavaEditor) {
			listener.register((JavaEditor)editor);
			changeListener.register((JavaEditor)editor);
		}
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
		manager.add(changeAction);
		
//		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(historyAction);
		manager.add(changeAction);		
		
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(historyAction);
		manager.add(changeAction);		
	}

	private void makeActions() {
		
		historyAction = new Action() {
			public void run(){
				ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
				viewer.setContentProvider(historySet);
				el.setCurrentSet(historySet);
				
			}
		};
		historyAction.setToolTipText("Shows a list of your history");  //change this for specified tooltip
		historyAction.setText("History Set");
		historyAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action
			
		changeAction = new Action() {
				public void run(){
					ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
					viewer.setContentProvider(changeSet);
					el.setCurrentSet(changeSet);
				}
		};
		changeAction.setToolTipText("Shows a list of your changes");
		changeAction.setText("Change Set");
		changeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action	
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
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
			"Code Sets",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
}