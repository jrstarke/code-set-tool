package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.mylar.context.core.AbstractContextStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.monitor.core.IInteractionEventListener;
import org.eclipse.mylar.monitor.core.InteractionEvent;

public abstract class CodeSetListener implements IInteractionEventListener{

	protected String name;
	protected boolean activated;
	protected SetListener listensToUs;

	public CodeSetListener () {
		activate();
	}

	/**
	 * Is notified when an interaction occurs.  This method then determines the JavaElement which
	 * participated in the event, and notifies any interested parties
	 */
	public abstract void interactionObserved(InteractionEvent event);

	public abstract Object getSet();

	public IJavaElement resolveElement (InteractionEvent event) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(event.getStructureKind());
		Object theObject = bridge.getObjectForHandle(event.getStructureHandle());
		if (theObject instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) theObject;
			return element;
		}
		return null;
	}

	public String getName () {
		return name;
	}

	public void activate() {
		activated = true;
		changed();
	}

	public void deactivate() {
		activated = false;
		changed();
	}

	public boolean isActivated() {
		return activated;
	}

	protected void changed () {
		if (listensToUs != null)
			listensToUs.refresh(getSet());
	}

	public void changeListener (SetListener listener) {
		listensToUs = listener;
	}


	/* 
	 * The methods below are required by the IInteractionEventListener interface
	 * (non-Javadoc)
	 * @see org.eclipse.mylar.monitor.core.IInteractionEventListener#startMonitoring()
	 */
	public void startMonitoring() {
	}

	public void stopMonitoring() {
	}
}
