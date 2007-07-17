package ca.ucalgary.codesets.views;

import java.util.ArrayList;
import java.util.List;

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

// displays a list of hyperlinks to code sets which are expected to be in the 
// same category
public class SideBarSection extends Composite {
	String name;
	
	// hyperlink background color is determined by the state of the corresponding
	// code set
	static Color[] backgroundColors = new Color[] {
		null, new Color(null,255,255,255), null, new Color(null,255,255,0)};
	static Color emphasizeColor = new Color(null, 200, 50, 50);
	static Color hideColor = new Color(null, 200, 200, 200);

	public SideBarSection(Composite parent, String name) {
		super(parent, SWT.NO_BACKGROUND);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.fill = false;
		this.setLayout(layout);
		this.name = name;
		setLabel();
	}
	
	// the first element in the section is the category label...
	void setLabel() {
		if (name != null) { 
			Label label = new Label(this, SWT.NONE);
			fontStyle(label, SWT.BOLD);
			label.setText(name.toUpperCase());
		}
	}
	
	// returns a list of all hyperlinks this section is displaying
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
		link.setText(text +" ("+set.state.toString().toLowerCase()+")");
		link.setData(set);
		styleLink(link, set);
		this.layout();
		return link;
	}

	// there has to be a better way than this, what is it?
	void fontStyle(Control widget, int style) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		widget.setFont(new Font(f.getDevice(), fd));
	}

	public static void emphasizeLink(Hyperlink link) {
		link.setForeground(emphasizeColor);
	}
	public static void demphasizeLink(Hyperlink link) {
		link.setForeground(null);
	}
	
	// set the hyperlink's style based on the state of the corresponding code set
	public static void styleLink(Hyperlink link, CodeSet set) {		
		Color color = backgroundColors[set.state.ordinal()];
//		link.setBackground(color);  //if you want the background colours, uncomment this line
		
		if (set.state == CodeSet.State.EXCLUDED)
			link.setForeground(hideColor);
		else 
			link.setForeground(null);
		link.setText(set.name +" ("+set.state.toString().toLowerCase()+")");
		// etc
	}
}
