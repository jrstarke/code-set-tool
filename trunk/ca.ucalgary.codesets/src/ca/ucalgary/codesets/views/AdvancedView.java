package ca.ucalgary.codesets.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.ucalgary.codesets.controllers.AdvancedViewController;
import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;

public class AdvancedView extends ViewPart  {

	Action nameSetAction;   
	AdvancedViewController adController;
	
	@Override
	public void createPartControl(Composite parent) {
		adController = new AdvancedViewController(parent);
		makeActions();
		createToolbar();
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(nameSetAction);
		
	}

	private void makeActions() {
		nameSetAction = new Action() {
		public void run(){
			InputDialog dialog = new InputDialog(adController.getAdvancedView().getShell(), 
					"Set Name",
					"Please enter a name for the new set:", "", null);
			dialog.open();
			String name = dialog.getValue();
			if (name != null) {
				CodeSet currentSet = CodeSetManager.instance().displaySet();
				currentSet.name = name;
				currentSet.category = "named";
				CodeSetManager.instance().addSet(currentSet);
			}
		}
	};
	
	nameSetAction.setToolTipText("Renames this set");  //change this for specified tooltip
	nameSetAction.setText("Name this Set");
	nameSetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
