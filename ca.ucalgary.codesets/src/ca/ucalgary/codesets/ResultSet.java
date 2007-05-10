package ca.ucalgary.codesets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ResultSet extends ArrayList<ISourceReference> implements IStructuredContentProvider {

	private int sizeOfList = 10;
	
	// This code below is not optimized, and might be slowing the code down.
	public boolean add(ISourceReference isr) {
		if(contains(isr))
			remove(isr);  				//delete the object if it is in the list already
		
		add(0,isr);						//add the object to the front of the list
		if(size() > sizeOfList-1)		//remove the last unit if the size is greater than allowed
			remove(sizeOfList);		
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
