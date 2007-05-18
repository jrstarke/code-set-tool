package ca.ucalgary.codesets.sets;

import java.util.ArrayList;
import java.util.EventListener;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.ucalgary.codesets.ResultSet;
import ca.ucalgary.codesets.listeners.CodeSetListener;
import ca.ucalgary.codesets.listeners.SetListener;

public class CodeSet extends ArrayList<ISourceReference> implements IStructuredContentProvider {

	private int sizeOfList = 10;
	protected CodeSetListener listener;
	protected Boolean isActivated;
	protected SetListener listensToUs;

	public void activate () {}

	public void deactivate () {}
	
	public boolean isActivated () {
		return isActivated;
	}
	
	public void changeListener (SetListener listens) {
		listensToUs = listens;
	}
	
	private void changed () {
		if (listensToUs != null)
			listensToUs.refresh(this);
	}
	
	// This code below is not optimized, and might be slowing the code down.
	public boolean add(ISourceReference isr) {
		if(contains(isr))
			remove(isr);  				//delete the object if it is in the list already
		
		add(0,isr);						//add the object to the front of the list
		changed();
		if(size() > sizeOfList)		//remove the last unit if the size is greater than allowed
			remove(sizeOfList-1);		
		return true;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
	public Object[] getElements(Object parent) {
		return toArray();
	}
	
	// Can use this in the future if you want the size of the list to change
	public void setSize(int size) {
		sizeOfList = size;
	}

}
