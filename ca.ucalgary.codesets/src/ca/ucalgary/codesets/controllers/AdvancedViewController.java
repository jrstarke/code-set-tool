package ca.ucalgary.codesets.controllers;

import java.util.HashMap;

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
import ca.ucalgary.codesets.views.AdvancedViewSection;

public class AdvancedViewController implements ICodeSetListener  {

	HashMap<ISourceReference, AdvancedViewSection> sections = new HashMap<ISourceReference, AdvancedViewSection>();
	Composite mainSection;
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
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.spacing = 1;
		mainSection.setLayout(layout);
		mainSection.setBackground(background);
		mainSection.setBackgroundMode(SWT.INHERIT_FORCE);
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
				view.setSummarySize(scale.getSelection());
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
			for(Object isr: elements){
				if(isr instanceof ISourceReference/* && !sections.containsKey((ISourceReference)isr)*/){
					AdvancedViewSection section = new AdvancedViewSection(mainSection, (ISourceReference)isr, set.srcCache.source((ISourceReference)isr, summarySize));
					sections.put(((ISourceReference)isr), section);


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
		for(AdvancedViewSection sect:sections.values())
			sect.dispose();
		sections = new HashMap<ISourceReference, AdvancedViewSection>();
	}

	public void setAdded(CodeSet set) {


	}
}
