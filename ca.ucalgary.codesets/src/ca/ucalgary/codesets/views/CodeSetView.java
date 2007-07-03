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
import ca.ucalgary.codesets.controllers.DisplayController;
import ca.ucalgary.codesets.controllers.SideBarController;

// displays sets of source code entities as provided by one of a number of CodeSets.
public class CodeSetView extends ViewPart {
//	private TableViewer setSelectionPane;
//	private Action setPreferencesAction;
//	private Action doubleClickAction;
//	private Action setSelectionAction;
	
	// a callback that allows us to create and initialize the part.
	public void createPartControl(Composite parent) {
		hookContextMenu();
		contributeToActionBars();
		new DisplayController(getViewSite(), parent);
		new SideBarController(parent);
	}

	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				CodeSetView.this.fillContextMenu(manager);
//			}
//		});
		//Menu menu = menuMgr.createContextMenu(viewer.getControl());
		//viewer.getControl().setMenu(menu);
		//getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(setPreferencesAction);
	}

//	private void makeActions() {
//		setPreferencesAction = new Action() {
//			public void run(){
//				
//			}
//		};
//		setPreferencesAction.setToolTipText("Selects the running sets");
//		setPreferencesAction.setText("Preferences");
//		setPreferencesAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action
//	}
		
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
//		viewer.getControl().setFocus();
	}
//
	public void refresh (Object set) {
//		if (set == viewer.getContentProvider())
//			viewer.refresh();
////		if (set == setSelectionPane.getContentProvider())
////			setSelectionPane.refresh();
	}
//	
//	public void setCurrentSet(CodeSet codeSet) {
//		ElementLabelProvider el = (ElementLabelProvider) viewer.getLabelProvider();
//		viewer.setContentProvider(codeSet);
//		el.setCurrentSet(codeSet);
//		codeSetView.setContentDescription(codeSet.getName());
//		viewer.setSorter(null);
//		viewer.refresh();
//	}
	
//	public CodeSet getCurrentSet() {
//		return ((ElementLabelProvider)viewer.getLabelProvider()).getCurrentSet();
//	}
	

}