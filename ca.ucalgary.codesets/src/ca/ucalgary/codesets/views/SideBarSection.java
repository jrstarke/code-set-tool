package ca.ucalgary.codesets.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ca.ucalgary.codesets.models.NodeSet;


// displays a title and a list of labels for code sets which are expected to be 
// in the same category
public class SideBarSection extends Composite {
	String name;
	
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
	
	public List<NodeSetLabel> labels() {
		ArrayList<NodeSetLabel> result = new ArrayList<NodeSetLabel>();
		for (Control child : getChildren())
			if (child instanceof NodeSetLabel)
				result.add((NodeSetLabel)child);
		return result;
	}
	
	// dispose of all CodeSetLabel's being displayed
	public void clear() {
		for (NodeSetLabel label : labels())
			label.dispose();
	}
	
	public NodeSetLabel addSet(NodeSet set) {
		return new NodeSetLabel(this, set);
	}
	
	// there has to be a better way than this, what is it?
	void fontStyle(Control widget, int style) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		widget.setFont(new Font(f.getDevice(), fd));
	}
}
