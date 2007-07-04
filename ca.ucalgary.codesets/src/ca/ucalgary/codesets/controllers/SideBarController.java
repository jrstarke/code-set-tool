package ca.ucalgary.codesets.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.ICodeSetListener;
import ca.ucalgary.codesets.views.SideBarSection;

//a controller for the "side bar" view that allows users to specify the combination
//of events that they are interested in
public class SideBarController implements ICodeSetListener {
	// the ui is a simple series of SideBarSections
	HashMap<String, SideBarSection> sections = new HashMap<String, SideBarSection>();
	Composite sideBar;

	public SideBarController(Composite parent) {
		sideBar = view(parent);
		createButton();
		for (CodeSet set : CodeSetManager.instance().sets()) 
			setAdded(set);
		CodeSetManager.instance().addListener(this);
	}

	Composite view(Composite parent) {
		Composite sideBar = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.spacing = 5;
		sideBar.setLayout(layout);
		return sideBar;
	}

	void addLink(final SideBarSection section, final CodeSet set) {
		final Hyperlink link = section.addLink(set.name, set);
		link.addHyperlinkListener(new IHyperlinkListener() {
			public void linkActivated(HyperlinkEvent e) {
				CodeSetManager.instance().changeState(set);
				SideBarSection.styleLink(link, set);
				section.layout();
			}
			public void linkEntered(HyperlinkEvent e) {
			}
			public void linkExited(HyperlinkEvent e) {
			}
		});
	}

	void addLinks(String category, List<CodeSet> sets) {
		SideBarSection section = sections.get(category);
		section.clearLinks();

		for (CodeSet set : sets) {
			addLink(section, set);
		}

		sideBar.layout();
	}

	public List<Hyperlink> links() {
		ArrayList<Hyperlink> links = new ArrayList<Hyperlink>();
		for (SideBarSection section : sections.values())
			links.addAll(section.links());
		return links;
	}

	// the following methods are for listening to events generated by the CodeSetManager

	public void setAdded(CodeSet set) {
		if (!sections.containsKey(set.category))
			sections.put(set.category, new SideBarSection(sideBar, set.category));
		addLinks(set.category, CodeSetManager.instance().sets(set.category));	
	}
	public void setChanged(CodeSet set) {
		for (Hyperlink link : links()) {
			CodeSet s = (CodeSet)link.getData();
			if (set == s) {
				SideBarSection.styleLink(link, set);
				sideBar.layout();
				break;
			}
		}
	}
	public void focusChanged(ISourceReference focus) {
		for (Hyperlink link : links()) {
			CodeSet set = (CodeSet)link.getData();
			if (set.contains(focus))
				SideBarSection.emphasizeLink(link);
			else
				SideBarSection.demphasizeLink(link);
		}
		sideBar.layout();
	}
	public void stateChanged(CodeSet set) {
		// ignore for now...
	}

	protected Button createButton() {
		Button button = new Button(sideBar, SWT.PUSH);
		button.setText("Name this set");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				InputDialog dialog = new InputDialog(sideBar.getShell(), 
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
		});
		return button;
	}
}