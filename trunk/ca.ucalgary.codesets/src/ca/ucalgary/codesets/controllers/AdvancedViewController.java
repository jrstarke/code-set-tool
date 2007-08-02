package ca.ucalgary.codesets.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.ICodeSetListener;
import ca.ucalgary.codesets.views.AdvancedViewGroup;
import ca.ucalgary.codesets.views.AdvancedViewSection;

public class AdvancedViewController implements ICodeSetListener  {

	HashMap<ISourceReference, Composite> sections = new HashMap<ISourceReference, Composite>();
	Composite mainSection;
	HashMap<ISourceReference,ArrayList<ISourceReference>> groups;
	ScrolledComposite sc;
	int summarySize = 5;
	int MAXSUMMARYSIZE = 10;
	Color background = new Color(null,255,255,255);


	public AdvancedViewController(Composite parent) {
		mainSection = view(parent);
		CodeSetManager.instance().addListener(this);
	}


	Composite view(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		createScale(container,this);
		sc = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		sc.setLayoutData(gridData);
		Composite mainSection = new Composite(sc, SWT.NONE);
		sc.setContent(mainSection);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 1;
		mainSection.setLayout(layout);
		mainSection.setBackground(background);
		mainSection.setBackgroundMode(SWT.DEFAULT);
		sc.setMinSize(mainSection.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return mainSection;
	}

	Scale createScale (Composite parent, final AdvancedViewController view) {
		Scale scale = new Scale(parent, SWT.HORIZONTAL);
		scale.setMaximum(MAXSUMMARYSIZE);
		scale.setMinimum(0);
		scale.setSelection(view.summarySize());
		scale.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				Scale scale = (Scale)e.getSource();
				int temp = summarySize;
				view.setSummarySize(scale.getSelection());
				if(temp != summarySize)
					Logger.instance().addEvent("Scale Changed" + "\t" + temp +"\t to \t" + summarySize);
			}
		});
		return scale;
	}

	void setSummarySize(int size) {
		CodeSet set = CodeSetManager.instance().displaySet();
		if (size != summarySize) {
			summarySize = size;
			for(Control child:mainSection.getChildren()) {
				if (child instanceof AdvancedViewSection) {
					AdvancedViewSection section = (AdvancedViewSection) child;
					section.setSummary(set.srcCache.source(section.getISR(), summarySize));
				}
			}
			sc.setMinSize(mainSection.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			mainSection.layout();
		}
	}

	int summarySize() {
		return summarySize;
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

	public Composite getAdvancedView() {
		return this.mainSection;
	}
	//updates the view with the elements that are in each set.
	private void changeDisplaySet(CodeSet set) {
		set = CodeSetManager.instance().displaySet();
		clear();
		try{
			Object[] elements = set.getElements(null);
			groups = new HashMap<ISourceReference,ArrayList<ISourceReference>>();
			for(Object isr: elements){
				if(isr instanceof ISourceReference/* && !sections.containsKey((ISourceReference)isr)*/){
					ISourceReference type = (ISourceReference)((IJavaElement) isr).getAncestor(IJavaElement.TYPE);
					addSetToGroup(type,(ISourceReference)isr);
				}
			}
			for(ISourceReference type:groups.keySet()) {
				AdvancedViewGroup grouptitle = new AdvancedViewGroup(mainSection,type);
				GridData gridData = new GridData();
				gridData.horizontalAlignment = GridData.FILL;
				gridData.verticalIndent = 10;
				grouptitle.setLayoutData(gridData);
				sections.put(type, grouptitle);
				for (ISourceReference element:groups.get(type)) {
					AdvancedViewSection section = new AdvancedViewSection(mainSection, element, set.srcCache.source(element, summarySize));
					sections.put(element, section);


				}
			}
			sc.setMinSize(mainSection.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			mainSection.layout();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void clear() {
		for(Control sect:mainSection.getChildren())
			sect.dispose(); 
//		sections = new HashMap<ISourceReference, Composite>();
	}

	public void setAdded(CodeSet set) {


	}
	
	public void addSetToGroup(ISourceReference groupname, ISourceReference element) {
		ArrayList<ISourceReference> group = groups.get(groupname);
		if (group == null) {
			group = new ArrayList<ISourceReference>();
			groups.put(groupname, group);
		}
		group.add(element);
	}
}
