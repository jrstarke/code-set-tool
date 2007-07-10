package ca.ucalgary.codesets.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ca.ucalgary.codesets.controllers.SideBarController;

public class SelectionView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		new SideBarController(parent);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
