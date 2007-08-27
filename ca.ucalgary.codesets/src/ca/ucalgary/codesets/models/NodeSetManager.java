package ca.ucalgary.codesets.models;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;


//keeps track of all node sets and dispatches events to listeners about 
//changes to those sets
public class NodeSetManager {
	// a raw set is a set that is supplied externally
	LinkedList<NodeSet> rawSets = new LinkedList<NodeSet>();

	// the element that is the current focus in the editor
	ASTNode currentFocus = null;

	// a collection of objects that are listening to code set events
	HashSet<INodeSetListener> listeners = new HashSet<INodeSetListener>();

	public boolean allCleared = false;

	// the maximum number of raw sets that will be saved per category
	static int MAX_SETS_PER_CATEGORY = 3;
	static NodeSetManager instance = new NodeSetManager();

	// there is one global instance of this class
	public static NodeSetManager instance() {
		return instance;
	}

	NodeSetManager() {
		addSet(new NodeSet("navigation", "history"));
		navigationHistorySet().state = NodeSet.State.INCLUDED;
	}

	public void addListener(INodeSetListener listener) {
		listeners.add(listener);
	}

	// assumes the navigation history set is the first one added
	public NodeSet navigationHistorySet() {
		return rawSets.getFirst();
	}

	public void setFocus(IJavaElement key, ASTNode newFocus) {
		// update navigation history set (including clearing out previous entries so
		// that we are just saving the most recently visited node for each method)
		NodeSet navigationSet = navigationHistorySet();

//		ASTNode method = ASTHelper.getAncestorByType(newFocus, ASTNode.METHOD_DECLARATION);;
//		while (method != null && method.getParent().getNodeType() == ASTNode.ANONYMOUS_CLASS_DECLARATION)
//			method = ASTHelper.getAncestorByType(method.getParent(), ASTNode.METHOD_DECLARATION);
//
//		if (method == null) return;
		
//		IJavaElement key = ASTHelper.getJavaElement(method);
		if (navigationSet.containsKey(key))
			navigationSet.get(key).clear();
		navigationSet.add(key, newFocus);
		
		// notify listeners
		currentFocus = newFocus;
		for (INodeSetListener listener : listeners) {
			listener.setChanged(navigationHistorySet());
			listener.focusChanged(key);
		}
	}

	// set all sets to IGNORED
	public void clearStates() {
		for(NodeSet set:rawSets)
			if(set.state != NodeSet.State.IGNORED) {
				set.state = NodeSet.State.IGNORED;
				for (INodeSetListener listener : listeners)
					listener.stateChanged(set);
			}
	}

	public ASTNode getFocus() {
		return currentFocus;
	}

	public void changeState(NodeSet set) {
		set.transition();
		for (INodeSetListener listener : listeners)
			listener.stateChanged(set);
	}

	public synchronized void addSet(NodeSet set) {
		boolean removed = rawSets.remove(set);

		// count the number of sets in the added category...
		int total = 0;
		ArrayList<NodeSet> toRemove = new ArrayList<NodeSet>();
		for (NodeSet s : sets())
			if (s.category.equals(set.category)) {
				// we only want to remove ignored states
				if (s.state == NodeSet.State.IGNORED)
					toRemove.add(s);
				total++;
			}

		for (NodeSet s : toRemove) {
			if (total < MAX_SETS_PER_CATEGORY) break;
			rawSets.remove(s);
			for (INodeSetListener listener : listeners)
				listener.setRemoved(s);
			total--;
		}

		rawSets.add(set);
		if (!removed)
			for (INodeSetListener listener : listeners)
				listener.setAdded(set);
	}

	
	// returns the list of all "raw" sets
	public List<NodeSet> sets() {
		return rawSets;
	}

	// returns a list of all sets in the given category
	public List<NodeSet> sets(String category) {
		ArrayList<NodeSet> result = new ArrayList<NodeSet>();
		for (NodeSet set : rawSets)
			if (set.category == category) 
				result.add(set);
		return result;
	}

	// returns a new node set that is a combination of all of the raw sets
	// taking states into account (the resulting set is indexed by type)
	public NodeSet combinedSet() {
		NodeSet combined = new NodeSet("x", "display");

		// add in elements from all included sets
		for (NodeSet s : sets())
			if (s.state == NodeSet.State.INCLUDED)
				combined = combined.union(s);

		// remove elements from all excluded sets
		for (NodeSet s : sets())
			if (s.state == NodeSet.State.EXCLUDED)
				combined = combined.setDifference(s);

		// remove elements not in "restricted to" sets
		for (NodeSet s : sets())
			if (s.state == NodeSet.State.RESTRICTEDTO)
				combined = combined.intersection(s);

		return combined.copy();
	}

	public boolean containsSet(NodeSet set) {
		return rawSets.contains(set);
	}
}
