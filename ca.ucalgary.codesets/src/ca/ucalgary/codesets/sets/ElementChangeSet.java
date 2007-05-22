package ca.ucalgary.codesets.sets;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.viewers.TableViewer;

import ca.ucalgary.codesets.ElementLabelProvider;
import ca.ucalgary.codesets.listeners.*;

public class ElementChangeSet extends CodeSet {

	public ElementChangeSet () {
		super();
		listener = new JavaElementChangeListener(this);
	}
	
	@Override
	public void activate() {
		super.activate();
		JavaCore.addElementChangedListener((JavaElementChangeListener)listener, ElementChangedEvent.POST_RECONCILE);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		JavaCore.removeElementChangedListener((JavaElementChangeListener)listener);
	}
}
