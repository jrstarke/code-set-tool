package ca.ucalgary.codesets.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.controllers.AdvancedViewController;
import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.LineValue;

public class AdvancedViewSection extends Composite {
	//this will be the method name
	ISourceReference isr;
	LineValue[] summary;

	//Just displaying the name of the set right now, not doing anything else
	public AdvancedViewSection(Composite parent, ISourceReference name, LineValue[] summary) {
		super(parent, SWT.NO_BACKGROUND);
		this.summary = summary;
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.fill = false;
		this.setLayout(layout);
		this.isr = name;
		setText();
		createSummary();
	}

	public void setSummary (LineValue[] summary) {
		this.summary = summary;
		clear();
		createSummary();
		layout();
	}

	private void setText() {
		if(isr != null) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new JavaElementLabelProvider().getText(isr));
			fontStyle(label, SWT.BOLD);

//			Double Click Listener, to open the element in the Java Editor
			label.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) {
					openElement((IJavaElement)isr);
//					label.setBackground(new Color(null,255,255,255));
				}
			});
		}
	}

//	When double clicking on an element, this method opens the element in the Java Editor
	private void openElement(IJavaElement element) {
		try {
			IJavaElement unit = element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit != null)
				JavaUI.revealInEditor(JavaUI.openInEditor(unit), element);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}



	void fontStyle(Control widget, int style) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		widget.setFont(new Font(f.getDevice(), fd));
	}

	public List<Hyperlink> links() {
		ArrayList<Hyperlink> result = new ArrayList<Hyperlink>();
		for (Control child : getChildren())
			if (child instanceof Hyperlink)
				result.add((Hyperlink)child);
		return result;
	}

	// dispose of all hyperlinks being displayed
	public void clearLinks() {
		for (Hyperlink link : links())
			link.dispose();
	}

	// append a hyperlink to this section 
	public Hyperlink addLink(String text, CodeSet set) {
		Hyperlink link = new Hyperlink(this, SWT.NONE);
		link.setText(text);
		link.setData(set);
//		styleLink(link, set);
		return link;
	}

	private void createSummary() {
		int lastLine = -1;
		if (summary != null) {
			for (LineValue line:summary) {
				if (((line.number() != 0) || (line.number() != 1)) && (lastLine != (line.number() -1))) {
					Hyperlink elipsis = new Hyperlink(this,SWT.NONE);
					elipsis.setText("...");
				}	
				Hyperlink link = new Hyperlink(this, SWT.NONE);
				link.setText(line.source());
				lastLine = line.number();
			}
			if (lastLine >= 0 && (lastLine + 1) != summary[0].lastNumber()) {   //Something is going on here, bug? ArrayIndexOutOFBoundsException for 0
				Hyperlink elipsis = new Hyperlink(this,SWT.NONE);
				elipsis.setText("...");
			}
		}	
	}

	private void clear() {
		for (Control line: lines()) {
			line.dispose();
		}
	}

	private List<Hyperlink> lines() {
		ArrayList<Hyperlink> result = new ArrayList<Hyperlink>();
		for (Control child : getChildren())
			if (child instanceof Hyperlink)
				result.add((Hyperlink)child);
		return result;	
	}

	public ISourceReference getISR() {
		return isr;
	}
}
