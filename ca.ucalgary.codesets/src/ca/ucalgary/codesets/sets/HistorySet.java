package ca.ucalgary.codesets.sets;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.viewers.TableViewer;
import ca.ucalgary.codesets.ElementLabelProvider;
import ca.ucalgary.codesets.listeners.*;

public class HistorySet extends CodeSet {

	public HistorySet (CodeSet searchSet) {
		super();
		listener = new EditorFocusListener(this, searchSet);
	}
	
	@Override
	public void activate() {
		PartListener.addListener(listener);
	}

	@Override
	public void deactivate() {
		PartListener.removeListener(listener);
	}
}
