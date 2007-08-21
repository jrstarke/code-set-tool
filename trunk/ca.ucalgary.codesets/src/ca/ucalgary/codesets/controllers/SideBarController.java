package ca.ucalgary.codesets.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

import ca.ucalgary.codesets.models.*;
import ca.ucalgary.codesets.views.NodeSetLabel;
import ca.ucalgary.codesets.views.SideBarSection;

//a controller for the "side bar" view that allows users to specify the combination
//of events that they are interested in
public class SideBarController implements INodeSetListener {
	// the ui is a simple series of SideBarSections
	
	HashMap<String, SideBarSection> sections = new HashMap<String, SideBarSection>();
	Composite sideBar;
	ScrolledComposite sc;
	IToolBarManager toolBarManager;

	public SideBarController(Composite parent) {
		sideBar = view(parent);		
		addTextBox();
		for (NodeSet set : NodeSetManager.instance().sets()) 
			setAdded(set);
		NodeSetManager.instance().addListener(this);
	}

	private void addTextBox() {
		new SearchBox(sideBar);
	}

	Composite view(Composite parent) {
		// sc is a scrolled Composite, basically a container with scroll bars for the sidebar content
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		Composite sideBar = new Composite(sc, SWT.NONE);
		sc.setContent(sideBar);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.wrap = true;
		layout.spacing  = 5;
		sideBar.setLayout(layout);
		sc.setMinSize(sideBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return sideBar;
	}

	void addLink(final SideBarSection section, final NodeSet set) {
		final NodeSetLabel label = section.addSet(set);
		label.getLink().addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				NodeSetManager.instance().changeState(set);
			}
			public void linkEntered(HyperlinkEvent e) {
			}
			public void linkExited(HyperlinkEvent e) {
			}
		});
		sideBar.layout();
	}
	
	void addLinks(String category, List<NodeSet> sets) {
		SideBarSection section = sections.get(category);
		section.clear();
		for (NodeSet set : sets)
			addLink(section, set);
		sc.setMinSize(sideBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sideBar.layout();
	}

	List<NodeSetLabel> labels() {
		ArrayList<NodeSetLabel> links = new ArrayList<NodeSetLabel>();
		for (SideBarSection section : sections.values())
			links.addAll(section.labels());
		return links;
	}
	
	NodeSetLabel findLabel(NodeSet set) {
		for (NodeSetLabel label : labels()) 
			if (label.getSet() == set) return label;
		return null;
	}

	// the following methods are for listening to events generated by the NodeSetManager

	public void setAdded(final NodeSet set) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!sections.containsKey(set.category))
					sections.put(set.category, new SideBarSection(sideBar, set.category));
				addLinks(set.category, NodeSetManager.instance().sets(set.category));
			}
		});
	}
	
	public void setChanged(NodeSet set) {
		NodeSetLabel label = findLabel(set);
		if (label != null) {
			label.setSizeText();
			label.layout();
		}
	}
	
	public void focusChanged(ASTNode focus) {
		for (NodeSetLabel label : labels()) {
			if (label.getSet().containsNode(focus))
				label.emphasizeLink();
			else
				label.demphasizeLink();
		}
		sc.setMinSize(sideBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sideBar.layout();
	}
	
	public void stateChanged(NodeSet set) {
		NodeSetLabel label = findLabel(set);
		if (label != null) {
			label.setIcon(set.state);
			label.layout();
		}
	}
	
	public Composite getSideBar() {
		return this.sideBar;
	}
}

