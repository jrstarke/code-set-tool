package ca.ucalgary.codesets.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.ViewPart;

import ca.ucalgary.codesets.controllers.CombinedViewController;

// this view is the main presentation of the currently applicable set of ASTNode's.
public class CombinedView extends ViewPart  {
	public void createPartControl(Composite parent) {
		new CombinedViewController(parent);
	}
	
	public void setFocus() {
	}
	
	// static methods and fields for creating the various widgets and composites used
	// to display node sets
	
	static Color methodColor = new Color(null, 100,100,100);
	static Color commentColor = new Color(null, 100,100,175);
	static Color methodNameColor = new Color(null, 0,0,0);
	static Color classNameColor = methodNameColor;
	
	static Label label(Composite parent, String text, int style, int height, Color color) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		fontStyle(label, style, height);
		label.setForeground(color);
		return label;
	}
	
	static void fontStyle(Control widget, int style, int height) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		fd[0].setHeight(height);
		widget.setFont(new Font(f.getDevice(), fd));
	}
	
	public static Composite classView(Composite parent, String text, String comment) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		result.setLayout(layout);
		label(result, text, SWT.BOLD, 13, classNameColor);
		return result;
	}
	public static Composite methodView(Composite parent, String text) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		result.setLayout(layout);
		label(result, text, SWT.BOLD, 11, methodNameColor);
		
		return result;
	}
	public static Widget methodBodyWidget(Composite parent, String text) {
		return label(parent, text, SWT.NORMAL, 11, methodColor);
	}
	public static Widget commentLabel(Composite parent, String text){
		return label(parent, text, SWT.NORMAL, 11, commentColor);
	}
}
