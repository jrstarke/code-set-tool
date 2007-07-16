package ca.ucalgary.codesets.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.CodeSet;

public class AdvancedViewSection extends Composite {
	 //this will be the method name
	ISourceReference isr;
	
	//Just displaying the name of the set right now, not doing anything else
	public AdvancedViewSection(Composite parent, ISourceReference name) {
		super(parent, SWT.NO_BACKGROUND);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.fill = false;
		this.setLayout(layout);
		this.isr = name;
		setText();
	}
	
	
	private void setText() {
		if(isr != null) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new JavaElementLabelProvider().getText(isr));
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

	
}
