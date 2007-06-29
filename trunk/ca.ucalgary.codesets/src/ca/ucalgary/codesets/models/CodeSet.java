package ca.ucalgary.codesets.models;

import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

// a named and categorized set of ISourceReference's
public class CodeSet extends HashSet<ISourceReference> implements IStructuredContentProvider  {
	public enum State { IGNORED, INCLUDED, EXCLUDED, RESTRICTEDTO }
	public State state = State.IGNORED;
	public String name;
	public String category;
	
	public CodeSet(String name, String category) {
		this.name = name;
		this.category = category;
	}
	
	// move to the "next" state
	public State transition() {
		State[] states = State.values();
		state = states[(state.ordinal() + 1) % states.length];
		return state;
	}
	
	// for now this is just based on name and category
	public boolean equals(Object other) {
		return (other instanceof CodeSet) && ((CodeSet)other).name.equals(this.name) &&
			((CodeSet)other).category.equals(this.category);
	}
	
	public Object[] getElements(Object inputElement) {
		return toArray();
	}
	
	public void dispose() {
	}
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	public String toString() {
		return category + "/" + name + " " + super.toString();
	}
	
	// returns a set that represent the intersection of this and the given set
	public CodeSet intersection(CodeSet set) {
		CodeSet result = new CodeSet(this.name, this.category);
		for (ISourceReference element : set)
			if (contains(element)) 
				result.add(element);
		return result;
	}
}
