package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

public interface CodeSetListener {
	
	/**
	 * This is notified of an event when it occurs with the Java Element which caused the event
	 * @param e
	 */
	public void eventOccured (IJavaElement e);
}
