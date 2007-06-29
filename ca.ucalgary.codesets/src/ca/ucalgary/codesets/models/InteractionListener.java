package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.mylar.context.core.AbstractContextStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.monitor.core.IInteractionEventListener;
import org.eclipse.mylar.monitor.core.InteractionEvent;

// this class listens to mylar monitor events and in response to events this 
// currently creates three kinds of code sets: (1) the navigation history set,
// (2) the references to set, and (3) the references from set
public class InteractionListener implements IInteractionEventListener {
	public void interactionObserved(InteractionEvent event) {
		if (event.getKind() == InteractionEvent.Kind.SELECTION)
			handleInteraction(resolveElement(event));
	}
	public void startMonitoring() {
	}
	public void stopMonitoring() {
	}
	
	public void handleInteraction(IJavaElement element) {
		if (element != null) {
			CodeSetManager.instance().setFocus((ISourceReference)element);
			new ReferenceToSearch().search(element, name(element));
			new ReferenceFromSearch().search(element, name(element));
		}
	}

	// retruns a suitable name for the given element
	String name(IJavaElement element) {
		return new JavaElementLabelProvider().getText(element);
	}

	// returns the IJavaElement referenced by the given event
	public static IJavaElement resolveElement (InteractionEvent event) {
		return resolveElement(event.getStructureHandle(), event.getStructureKind());
	}
	
	public static IJavaElement resolveElement (String handle, String kind) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(kind);
		Object theObject = bridge.getObjectForHandle(handle);
		if (theObject instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) theObject;
			return element;
		}
		return null;
	}

}
