package ca.ucalgary.codesets.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.CodeSet;

public class AdvancedViewSection extends Composite {
	//this will be the method name
	ISourceReference isr;
	int summaryLength;

	//Just displaying the name of the set right now, not doing anything else
	public AdvancedViewSection(Composite parent, ISourceReference name, int summaryLength) {
		super(parent, SWT.NO_BACKGROUND);
		this.summaryLength = summaryLength;
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.fill = false;
		this.setLayout(layout);
		this.isr = name;
		setText();
		createSummary();
		restrictSummary();
	}

	public void setSummary (int size) {
		summaryLength = size;
		clear();
		createSummary();
		restrictSummary();
	}

	private void setText() {
		if(isr != null) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new JavaElementLabelProvider().getText(isr));
			fontStyle(label, SWT.BOLD);
		
//		Double Click Listener, to open the element in the Java Editor
			label.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event event) {
					openElement((IJavaElement)isr);
//						label.setBackground(new Color(null,255,255,255));
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

	public void restrictSummary () {
		int left = summaryLength;

		for (Control line : lines())
			if (left > 0) 
				left--;
			else	
				line.dispose();
		this.layout();
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
		String[] summary = null;
		try {
			String source = isr.getSource();
			if (source != null) {
				int startBody = source.indexOf("{");
				int endBody = source.lastIndexOf("}");
				if ((startBody >0) && (endBody >1)) {
					String bodySource = source.substring(startBody, endBody);
					summary = bodySource.replace("{", "").replace("}", "").replace("\t\t","\t").replace("\t", "    ").split("\n");
					for (String line:summary) {
						if (line.trim().length() > 0) {
							Hyperlink link = new Hyperlink(this, SWT.NONE);
							link.setText(line);
						}
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
