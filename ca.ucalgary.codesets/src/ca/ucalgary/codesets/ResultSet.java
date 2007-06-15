package ca.ucalgary.codesets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.ucalgary.codesets.listeners.SetListener;
import ca.ucalgary.codesets.sets.CodeSet;


public class ResultSet extends ArrayList<CodeSet> implements IStructuredContentProvider {

private SetListener listensToUs;
	
	String name;

	// This code below is not optimized, and might be slowing the code down.
	public boolean add(CodeSet set) {
		if(contains(set))
			remove(set);  				//delete the object if it is in the list already
		
		super.add(0,set);						//add the object to the front of the list
		listensToUs.refresh(this);
		return true;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
	public Object[] getElements(Object parent) {
		return toArray();
	}
	
	private void changed () {
		if (listensToUs != null)
			listensToUs.refresh(this);
	}
	
	public void changeListener (SetListener listens) {
		listensToUs = listens;
	}
	
	public CodeSet get (String name) {
		for (CodeSet c: this) {
			if (c.getName() == name)
				return c;
		}
		return null;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public int hashCode () {
		return name.hashCode();
	}
}
