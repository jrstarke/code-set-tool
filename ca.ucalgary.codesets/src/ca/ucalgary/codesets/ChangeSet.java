package ca.ucalgary.codesets;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ChangeSet implements IStructuredContentProvider {
	HashSet<ISourceReference> entities = new HashSet<ISourceReference>();
	
	// add a source code entity to the set
	public void add(ISourceReference entity) {
		entities.add(entity);
	}
	
	public void remove(ISourceReference entity) {
		entities.remove(entity);
	}

	// methods required by IStructuredContentProvider
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		return entities.toArray();
	}
}
