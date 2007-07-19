package ca.ucalgary.codesets.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.osgi.framework.Bundle;

import ca.ucalgary.codesets.models.CodeSet;


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
	
	public List<CodeSetLabel> labels() {
		ArrayList<CodeSetLabel> result = new ArrayList<CodeSetLabel>();
		for (Control child : getChildren())
			if (child instanceof CodeSetLabel)
				result.add((CodeSetLabel)child);
		return result;
	}
	
	// dispose of all CodeSetLabel's being displayed
	public void clear() {
		for (CodeSetLabel label : labels())
			label.dispose();
	}
	
	public CodeSetLabel addSet(CodeSet set) {
		return new CodeSetLabel(this, set);
	}
	
	// there has to be a better way than this, what is it?
	void fontStyle(Control widget, int style) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		widget.setFont(new Font(f.getDevice(), fd));
	}
}
