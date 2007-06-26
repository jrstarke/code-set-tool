package ca.ucalgary.codesets.listeners;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.SideBar;
import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.views.CodeSetView;

public class LinkListener implements IHyperlinkListener {
//	Color textColor = new Color(null,0,0,255);
//	Color backColor = new Color(null,255,255,255);
	private CodeSetView theView;
	private SideBar	theSideBar;
	private Hyperlink lastSelected;
	
	public LinkListener (CodeSetView theView, SideBar theSideBar) {
		this.theView = theView;
		this.theSideBar = theSideBar;
	}
	
	public void linkActivated(HyperlinkEvent e) {	
//		Hyperlink link = (Hyperlink)e.getSource();
//		link.setForeground(textColor);
//		link.setBackground(backColor);
//		
//		link.redraw();
//		if (lastSelected !=null && !link.equals(lastSelected)) {
//			if (!lastSelected.isDisposed()) {
//				lastSelected.setBackground(null); 
//				lastSelected.redraw();
//			} 
//		}
//		lastSelected = link;
		theView.setCurrentSet((CodeSet)e.getHref());
		theSideBar.refresh(null);
	}

	public void linkEntered(HyperlinkEvent e) {
	}

	public void linkExited(HyperlinkEvent e) {
	}

}
