package ca.ucalgary.codesets.listeners;

import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.views.CodeSetView;

public class LinkListener implements IHyperlinkListener {

	CodeSetView theView;
	
	public LinkListener (CodeSetView theView) {
		this.theView = theView;
	}
	
	public void linkActivated(HyperlinkEvent e) {
		theView.setCurrentSet((CodeSet)e.getHref());
	}

	public void linkEntered(HyperlinkEvent e) {
	}

	public void linkExited(HyperlinkEvent e) {
	}

}
