package ca.ucalgary.codesets.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.LineValue;

public class AdvancedViewGroup extends Composite {
	//this will be the method name
	ISourceReference isr;
	
	private Color backgroundColor = new Color(null,220,220,220);

	//Just displaying the name of the set right now, not doing anything else
	public AdvancedViewGroup(Composite parent, ISourceReference name) {
		super(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.type = SWT.HORIZONTAL;
		layout.marginHeight = 3;
		this.setLayout(layout);
		
		this.setBackground(backgroundColor);
		this.isr = name;
		setText();
		this.layout();
	}

	private void setText() {
		if(isr != null) {
			Label label = new Label(this, SWT.NONE);
			label.setText(new JavaElementLabelProvider().getText(isr));
//			label.setForeground(new Color(null,0,0,0));
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
		f.size = 12;					//sets the size of the font
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		widget.setFont(new Font(f.getDevice(), fd));
	}

	public ISourceReference getISR() {
		return isr;
	}
}
