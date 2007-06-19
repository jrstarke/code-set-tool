package ca.ucalgary.codesets.listeners;

import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

import ca.ucalgary.codesets.ResultSet;
import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.views.CodeSetView;

public class LinkExpansionListener implements IHyperlinkListener {
	
	public void linkActivated(HyperlinkEvent e) {
		ResultSet r = ((ResultSet)e.getHref());
		r.toggleDisplayAll();
	}

	public void linkEntered(HyperlinkEvent e) {
	}

	public void linkExited(HyperlinkEvent e) {
	}

}
