package ca.ucalgary.codesets.listeners;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

public interface CodeSetListener {

	public void register (JavaEditor o);
	public void unregister (JavaEditor o);
}
