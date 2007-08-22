package ca.ucalgary.codesets.models;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.dom.ASTNode;

// classes that want to respond to code set events should implement this
// interface and should register themselves like so:
// NodeSetManager.instance().addListener(this);
public interface INodeSetListener {
	// called when the contents of the given set change
	public void setChanged(NodeSet set);
	
	// called when a new set is added to the manager
	public void setAdded(NodeSet set);
	
	// called when a new entity becomes the focus
	public void focusChanged(ASTNode focus);
	
	// called when the given set changes state
	public void stateChanged(NodeSet set);
	
	// called when a set is removed
	public void setRemoved(NodeSet set);

}
