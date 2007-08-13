package ca.ucalgary.codesets.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.ucalgary.codesets.controllers.Logger;
import ca.ucalgary.codesets.controllers.SideBarController;
import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.NodeSetManager;

public class SelectionView extends ViewPart {
//	Action nameSetAction;   
	Action clearSetsAction;
	SideBarController barController;
	
	@Override
	public void createPartControl(Composite parent) {
		barController = new SideBarController(parent);
		makeActions();
		createToolbar();
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
//				CodeSetManager.instance().allCleared = true;
//				CodeSetManager.instance().clearStates();
//				CodeSetManager.instance().allCleared = false;
				
				NodeSetManager.instance().allCleared = true;
				NodeSetManager.instance().clearStates();
				NodeSetManager.instance().allCleared = false;
				
			}
		};

		clearSetsAction.setToolTipText("Sets all sets to being ignored");  //change this for specified tooltip
		clearSetsAction.setText("Clear");		
		clearSetsAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

//		nameSetAction = new Action() {
//			public void run(){
//				InputDialog dialog = new InputDialog(barController.getSideBar().getShell(), 
//						"Set Name",
//						"Please enter a name for the new set:", "", null);
//				dialog.open();
//				String name = dialog.getValue();
//				if (name != null) {
//					CodeSet currentSet = CodeSetManager.instance().displaySet();
//					currentSet.name = name;
//					currentSet.category = "named";
//					CodeSetManager.instance().addSet(currentSet);
//				}
//			}
//		};
		
//		nameSetAction.setToolTipText("Renames this set");  //change this for specified tooltip
//		nameSetAction.setText("Name this Set");
//		nameSetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action
	}
	
    /**
     * Create toolbar.
     */
    private void createToolbar() {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
//            mgr.add(nameSetAction);
            mgr.add(clearSetsAction);
    }
	

	
}
