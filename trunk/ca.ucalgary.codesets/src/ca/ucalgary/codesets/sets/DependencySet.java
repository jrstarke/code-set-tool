package ca.ucalgary.codesets.sets;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.monitor.core.InteractionEvent;

import ca.ucalgary.codesets.ElementLabelProvider;
import ca.ucalgary.codesets.listeners.*;

/**
 * The Auto Reference Set keeps track of all of the items that reference this object
 * @author starkej
 *
 */
public class DependencySet extends CodeSet {

	private IJavaElement lastElement;
	
	/**
	 * Creates a new AutoReferenceSet with a specified Listener
	 *
	 */
	public DependencySet () {
		super();
		listener = new EditorAutoDependencyListener();
	}
	
	@Override
	public void activate() {
		super.activate();
		InteractionListener.addListener(InteractionEvent.Kind.SELECTION, listener);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		InteractionListener.removeListener(listener);
	}
	
	public void checkClean (IJavaElement element) {
		if (!element.equals(lastElement)) {
			super.clear();
			super.listensToUs.refresh(this);
			lastElement = element;
		}
	}
}
