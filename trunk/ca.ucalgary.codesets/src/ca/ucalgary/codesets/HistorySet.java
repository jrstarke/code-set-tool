package ca.ucalgary.codesets;

import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class HistorySet extends HashSet<ISourceReference> implements IStructuredContentProvider {

	// methods required by IStructuredContentProvider
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
		
	}
	public Object[] getElements(Object parent) {
		return toArray();
	}
}




//public class HistorySet implements IStructuredContentProvider {
//	HashSet<ISourceReference> entities = new HashSet<ISourceReference>();
//	
//	// add a source code entity to the set
//	public void add(ISourceReference entity) {
//		entities.add(entity);
//	}
//	// methods required by IStructuredContentProvider
//	
//	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
//		
//	}
//	public void dispose() {
//	}
//	public Object[] getElements(Object parent) {
//		return entities.toArray();
//	}
//}
