package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.ISourceReference;

// classes that want to respond to code set events should implement this
// interface and should register themselves like so:
// CodeSetManager.instance().addListener(this);
public interface ICodeSetListener {
	// called when the contents of the given set change
	public void setChanged(CodeSet set);
	
	// called when a new set is added to the manager
	public void setAdded(CodeSet set);
	
	// called when a new entity becomes the focus
	public void focusChanged(ISourceReference focus);
	
	// called when the given set changes state
	public void stateChanged(CodeSet set);

}
