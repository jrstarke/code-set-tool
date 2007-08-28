package ca.ucalgary.codesets.views;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import ca.ucalgary.codesets.controllers.Logger;
import ca.ucalgary.codesets.controllers.SideBarController;
import ca.ucalgary.codesets.models.EditorFocusListener;
import ca.ucalgary.codesets.models.NodeSetManager;

public class SelectionView extends ViewPart {
//	Action nameSetAction;   
	Action clearSetsAction;
	SideBarController barController;
	EditorFocusListener listener = new EditorFocusListener();
	
	@Override
	public void createPartControl(Composite parent) {
		barController = new SideBarController(parent);
		makeActions();
		createToolbar();
		
		IViewSite site = getViewSite();
		site.getWorkbenchWindow().addPageListener(new IPageListener() {
			void connect(IWorkbenchPage page) {
				IEditorPart part = page.getActiveEditor();
				if (part instanceof JavaEditor)
					listener.register((JavaEditor)part);
			}
			
			public void pageActivated(IWorkbenchPage page) {
				connect(page);
				page.addPartListener(listener);
			}
			public void pageClosed(IWorkbenchPage page) {
			}
			public void pageOpened(IWorkbenchPage page) {
			}
		});
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set up actions, need new pictures for them
	 */
	private void makeActions() {
		clearSetsAction = new Action() {
			public void run(){
				
				NodeSetManager.instance().allCleared = true;
				NodeSetManager.instance().clearStates();
				NodeSetManager.instance().allCleared = false;
				
			}
		};

		clearSetsAction.setToolTipText("Sets all sets to being ignored");  //change this for specified tooltip
		clearSetsAction.setText("Clear");
		clearSetsAction.setImageDescriptor(getImageDescriptor("blanks.png"));
	}
	
	// getting the right path to the icons is tricky, but this seems to do it...
	ImageDescriptor getImageDescriptor(String name) {
		Bundle bundle = Platform.getBundle("ca.ucalgary.codesets");
		Path path = new Path("icons/" + name);
		URL url = FileLocator.find(bundle, path, null);
		return ImageDescriptor.createFromURL(url);
	}
	
    /**
     * Create toolbar.
     */
    private void createToolbar() {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
            mgr.add(clearSetsAction);
    }
	

	
}
