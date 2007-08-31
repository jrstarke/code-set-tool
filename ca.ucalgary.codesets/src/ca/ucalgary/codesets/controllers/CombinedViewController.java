package ca.ucalgary.codesets.controllers;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ca.ucalgary.codesets.models.NodeSet;
import ca.ucalgary.codesets.models.NodeSetManager;
import ca.ucalgary.codesets.models.INodeSetListener;
import ca.ucalgary.codesets.models.TypeMembers;
import ca.ucalgary.codesets.views.CombinedView;

// controls what is shown in the CombinedView by listening to the NodeSetManager.
public class CombinedViewController implements INodeSetListener  {
	Composite parent;
	ScrolledComposite sc;
	CombinedView view;
	int level = 2;
	
	Color background = new Color(null,255,255,255);

	public CombinedViewController(Composite parent, CombinedView view) {
		this.view = view;
		sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		this.parent = new Composite(sc, SWT.NONE);
		sc.setContent(this.parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
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
		this.view.setNumberElements(combined.size(), combined.elementsByType().size());
		for (TypeMembers tm : combined.elementsByType()) {
			Composite classView = CombinedView.classView(parent, tm.type, "");
			for (TypeMembers.Entry entry : tm.entries)
				NodeSetViewBuilder.build(classView, entry.element, entry.placeholders, level);
		}
		
		sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		parent.layout();
	}
	
	// methods for listening to NodeSetManager events

	public void focusChanged(IJavaElement focus) {

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
		parent.setFocus();
	}

	public void setRemoved(NodeSet set) {
	}
	
	//increases the level of comments
	public void incLevel() {
		if (level < 2){
			level++;
			Logger.instance().addEvent("Comments Changed"+'\t'+level);
			changeDisplaySet();
		}
	}
	
	//decrease the level of comments
	public void decLevel() {
		if (level > 0){
			level--;
			Logger.instance().addEvent("Comments Changed"+'\t'+level);
			changeDisplaySet();
		}
	}
	
	//returns if the level comments is at the max or not
	public boolean getInc(){
		if(level == 2)
			return   false;
		else
			return  true;
	}
	
	//returns if the level comments is at the min or not
	public boolean getDec() {
		if(level == 0)
			return   false;
		else
			return  true;
	}
	
}
