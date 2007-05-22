package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jdt.core.JavaCore;

import ca.ucalgary.codesets.sets.CodeSet;


public class JavaElementChangeListener implements IElementChangedListener, CodeSetListener {
	private CodeSet changeSet;
	//private ChangeSet changeSet;

	public JavaElementChangeListener(CodeSet elementChangeSet) {
		this.changeSet = elementChangeSet;
	}

	public void elementChanged(ElementChangedEvent event) {
		IJavaElementDelta delta = event.getDelta();
		int kind = delta.getKind();
		IJavaElementDelta[] added = delta.getAddedChildren();
		IJavaElementDelta[] affected = delta.getAffectedChildren();
		IJavaElementDelta[] changed = delta.getChangedChildren();
		IJavaElement element = delta.getElement();
		delta.getFlags();
		resolveDelta(event.getDelta());
		//Viewer does not get refreshed by this listener		
	}

	public void resolveDelta (IJavaElementDelta delta) {
		IJavaElementDelta[] added = delta.getAddedChildren();
		IJavaElementDelta[] affected = delta.getAffectedChildren();
		IJavaElementDelta[] changed = delta.getChangedChildren();

		if (((added.length == 0) && (affected.length == 0)) && changed.length == 0) {
			switch(delta.getKind()) {
			case 1:
				changeSet.add((ISourceReference)delta.getElement());
				break;
			case 2:
				changeSet.remove((ISourceReference)delta.getElement());
				break;
			default:
				changeSet.add((ISourceReference)delta.getElement());
			}
//			viewer.refresh();  causes objects not to be removed from the set on a deleted item
		}
		else {
			for (int i = 0; i < added.length; i++) {
				resolveDelta(added[i]);
			}
			for (int i = 0; i < affected.length; i++) {
				resolveDelta(affected[i]);
			}
			for (int i = 0; i < changed.length; i++) {
				resolveDelta(changed[i]);
			}
		}
	}
	
	public void register (JavaEditor e) {
		JavaCore.addElementChangedListener(this, ElementChangedEvent.POST_RECONCILE);
	}
	
	public void unregister (JavaEditor e) {
		
	}
}
