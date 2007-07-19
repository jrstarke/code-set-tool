package ca.ucalgary.codesets.views;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.osgi.framework.Bundle;

import ca.ucalgary.codesets.models.CodeSet;

public class CodeSetLabel extends Composite {
	static Color demphasizeColor = new Color(null, 150,150,150);
	static Color emphasizeColor = new Color(null, 200, 50, 50);
	
	CodeSet set = null;
	Hyperlink link = new Hyperlink(this, SWT.NONE);
	Label sizeLabel = new Label(this, SWT.NONE);
	Label iconLabel = new Label(this, SWT.NONE);

	// images for indicating the state of the set
	Image[] images = new Image[] {getImage("blank.png"), getImage("plus.png"), getImage("x.png"), getImage("full.png")};
		
	public CodeSetLabel(Composite parent, CodeSet set) {
		super(parent, SWT.NO_BACKGROUND);
		this.set = set;
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = false;
		layout.fill = true;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		this.setLayout(layout);
		
		setIcon(set.state);
		getLink().setText(set.name);
		setSizeText();
		sizeLabel.setForeground(demphasizeColor);
		
		this.layout();
	}

	public void setSizeText() {
		sizeLabel.setText("- " + getSet().size());
	}
	public void setIcon(CodeSet.State state) {
		iconLabel.setImage(images[state.ordinal()]);
	}
	public void emphasizeLink() {
		getLink().setForeground(emphasizeColor);
	}
	public void demphasizeLink() {
		getLink().setForeground(null);
	}
	
	// getting the right path to the icons is tricky, but this seems to do it...
	Image getImage(String name) {
		Bundle bundle = Platform.getBundle("ca.ucalgary.codesets");
		Path path = new Path("icons/" + name);
		URL url = FileLocator.find(bundle, path, null);
		return ImageDescriptor.createFromURL(url).createImage();
	}

	public Hyperlink getLink() {
		return link;
	}

	public CodeSet getSet() {
		return set;
	}
}
