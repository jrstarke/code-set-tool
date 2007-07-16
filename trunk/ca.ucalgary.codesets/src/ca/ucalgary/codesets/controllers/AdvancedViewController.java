package ca.ucalgary.codesets.controllers;

import java.util.HashMap;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.ICodeSetListener;
import ca.ucalgary.codesets.views.AdvancedViewSection;
import ca.ucalgary.codesets.views.SideBarSection;

public class AdvancedViewController implements ICodeSetListener  {
	
	HashMap<ISourceReference, AdvancedViewSection> sections = new HashMap<ISourceReference, AdvancedViewSection>();
	Composite mainSection;
	
	public AdvancedViewController(Composite parent) {
		mainSection = view(parent);
		CodeSetManager.instance().addListener(this);
	}
		
	Composite view(Composite parent) {
		Composite mainSection = new Composite(parent, SWT.NONE | SWT.V_SCROLL);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.spacing = 1;
		mainSection.setLayout(layout);
		return mainSection;
	}
			
	void addLink(final AdvancedViewSection section, final CodeSet set) {
	
		final Hyperlink link = section.addLink(set.name, set);
		link.addHyperlinkListener(new IHyperlinkListener() {
			
			public void linkActivated(HyperlinkEvent e) {
				section.layout();
			}
			public void linkEntered(HyperlinkEvent e) {
			}
			public void linkExited(HyperlinkEvent e) {
			}
		});
	}	
	
	public void focusChanged(ISourceReference focus) {
		
	}

	public void setChanged(CodeSet set) {
		changeDisplaySet(set);
	}

	public void stateChanged(CodeSet set) {
		changeDisplaySet(set);
	}

	
	//updates the view with the elements that are in each set.
	private void changeDisplaySet(CodeSet set) {
		set = CodeSetManager.instance().displaySet();
		clear();
		try{
				Object[] elements = set.getElements(null);
				for(Object isr: elements){
					if(isr instanceof ISourceReference/* && !sections.containsKey((ISourceReference)isr)*/){
						sections.put(((ISourceReference)isr), new AdvancedViewSection(mainSection, (ISourceReference)isr));
					}
				}
				mainSection.layout();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void clear() {
		for(AdvancedViewSection sect:sections.values())
			sect.dispose();
		sections = new HashMap<ISourceReference, AdvancedViewSection>();
	}
	
	public void setAdded(CodeSet set) {
		
		
	}
}
