package ca.ucalgary.codesets.controllers;

import java.util.HashMap;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

import ca.ucalgary.codesets.models.NodeSet;
import ca.ucalgary.codesets.models.NodeWrapper;
import ca.ucalgary.codesets.models.NodeSetManager;
import ca.ucalgary.codesets.models.INodeSetListener;
import ca.ucalgary.codesets.views.AdvancedViewSection;

// controls what is shown in the CombinedView by listening to the NodeSetManager.
public class CombinedViewController implements INodeSetListener  {
	Composite parent;
	ScrolledComposite sc;
	Color background = new Color(null,255,255,255);

	public CombinedViewController(Composite parent) {
		
		sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		this.parent = new Composite(sc, SWT.NONE);
		sc.setContent(this.parent);
		Layout layout = new RowLayout(SWT.VERTICAL);
		this.parent.setLayout(layout);
		this.parent.setBackground(background);
		this.parent.setBackgroundMode(SWT.INHERIT_DEFAULT);
		NodeSetManager.instance().addListener(this);
	}
	
	// updates the view with the currently appropriate elements. the NodeSetBuilder
	// class is used to actually generate the UI elements.
	void changeDisplaySet() {
		for (Control w : parent.getChildren())
			w.dispose();
		
		NodeSet combined = NodeSetManager.instance().combinedSet();
		for (NodeWrapper key : combined.keySet())
			NodeSetViewBuilder.build(parent, key.getNode(), combined.get(key));
		
		sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		parent.layout();
	}
	
	// methods for listening to NodeSetManager events

	public void focusChanged(ASTNode focus) {
	}
	
	public void setAdded(NodeSet set) {
		if (set.state != NodeSet.State.IGNORED)
			changeDisplaySet();
	}
	
	public void setChanged(NodeSet set) {
		changeDisplaySet();
	}

	public void stateChanged(NodeSet set) {
		changeDisplaySet();
	}
}
