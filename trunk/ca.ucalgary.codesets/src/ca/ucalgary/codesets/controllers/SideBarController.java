package ca.ucalgary.codesets.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
	Color background = new Color(null,255,255,255);

	public SideBarController(Composite parent) {
		sideBar = view(parent);
		sideBar.setBackground(null);
		sideBar.setBackgroundMode(SWT.BACKGROUND);
		
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
		// the sc requires that the min size be set whenever the content size is changed
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		Composite sideBar = new Composite(sc, SWT.NONE);
		sc.setContent(sideBar);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.wrap = true;
		layout.spacing  = 5;
		sideBar.setLayout(layout);
		sideBar.setBackground(new Color(null, 255,255,255));
		sc.setMinSize(sideBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sideBar.setBackgroundMode(SWT.INHERIT_FORCE);
		return sideBar;
	}

	void addLink(final SideBarSection section, final NodeSet set) {
		final NodeSetLabel label = section.addSet(set);
		Label icon = label.getIcon();
		Label sizeLabel = label.getLabel();

		icon.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				mouseClick(e, set);
			}
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});

		sizeLabel.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				mouseClick(e, set);
			}
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});

		label.getLink().addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				mouseClick(e, set);
			}
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
//		label.getLink().addHyperlinkListener(new IHyperlinkListener() {
//			public void linkActivated(HyperlinkEvent e) {
//				// the state we transition to depends on which keys were held down and
//				// also on the current state. 
//				int mask = e.getStateMask();
//				NodeSet.State newState = NodeSet.State.IGNORED;
//				if ((mask & SWT.COMMAND) != 0) {
//					if (set.state != NodeSet.State.INCLUDED)
//						newState = NodeSet.State.INCLUDED;
//				} else if ((mask & SWT.ALT) != 0) {
//					if (set.state != NodeSet.State.EXCLUDED)
//						newState = NodeSet.State.EXCLUDED;
//				} else {
//					if (set.state != NodeSet.State.RESTRICTEDTO)
//						newState = NodeSet.State.RESTRICTEDTO;
//				}
//				NodeSetManager.instance().changeState(set, newState);
//			}
//			public void linkEntered(HyperlinkEvent e) {
//			}
//			public void linkExited(HyperlinkEvent e) {
//			}
//		});
		sideBar.layout();
	}

	//Changes the state of the NodeSetLabel to the appropriate state, according to if 
	//alt/command/ or nothing was held when the mouseEvent occurred
	private void mouseClick(MouseEvent e, NodeSet set){
		int mask = e.stateMask; 
		
		NodeSet.State newState = NodeSet.State.IGNORED;

		if ((mask & SWT.COMMAND) != 0) {
			if (set.state != NodeSet.State.INCLUDED)
				newState = NodeSet.State.INCLUDED;
		} else if ((mask & SWT.ALT) != 0) {
			if (set.state != NodeSet.State.EXCLUDED)
				newState = NodeSet.State.EXCLUDED;
		} else {
			if (set.state != NodeSet.State.RESTRICTEDTO)
				newState = NodeSet.State.RESTRICTEDTO;
		}

		NodeSetManager.instance().changeState(set, newState);
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
	
	public void focusChanged(IJavaElement element) {
		for (NodeSetLabel label : labels()) {
			NodeSet set = label.getSet();
			if (set == NodeSetManager.instance().navigationHistorySet()) {
				if (NodeSetManager.instance().containedLast)
					label.emphasizeLink();
				else
					label.demphasizeLink();
			} else {
				if (set.containsKey(element))
					label.emphasizeLink();
				else
					label.demphasizeLink();
			}
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
	
	public void setRemoved(NodeSet set){
		SideBarSection sect;
		if(set != null){
			sect = sections.get(set.category);
			for (NodeSetLabel label : sect.labels())
				if (label.getSet() == set)
					label.dispose();
			
			if (sect.labels().size()==0) {
				sections.remove(set.category);
				sect.dispose();
			}

		}
		sideBar.layout();
	}
}

