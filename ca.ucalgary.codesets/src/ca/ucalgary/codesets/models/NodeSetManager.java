package ca.ucalgary.codesets.models;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;


// keeps track of all node sets and dispatches events to listeners about 
// changes to those sets
public class NodeSetManager {
	// a raw set is a set that is supplied externally
	LinkedList<NodeSet> rawSets = new LinkedList<NodeSet>();
	
	// the element that is the current focus in the editor
	ASTNode currentFocus = null;
	
	// a collection of objects that are listening to code set events
	HashSet<INodeSetListener> listeners = new HashSet<INodeSetListener>();

	public boolean allCleared = false;
	
	// the maximum number of raw sets that will be saved
	final static int MAX_RAW_SETS = 20;
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
	
	public void setFocus(ASTNode newFocus) {
		// update navigation history set (including clearing out previous entries so
		// that we are just saving the most recently visited node for each method)
		NodeSet nav = navigationHistorySet();
		ASTNode method = ASTHelper.getAncestorByType(newFocus, ASTNode.METHOD_DECLARATION);
		if (method == null) return;
		
		NodeWrapper key = new NodeWrapper(method);
		if (nav.containsKey(key))
			nav.get(key).clear();
		navigationHistorySet().add(newFocus);
		
		// notify listeners
		currentFocus = newFocus;
		for (INodeSetListener listener : listeners) {
			listener.setChanged(navigationHistorySet());
			listener.focusChanged(newFocus);
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
		boolean removed = rawSets.remove(set); // O(n), but n <= SET_NUM_LIMIT
		rawSets.add(set);
		if (rawSets.size() > MAX_RAW_SETS)
			// remove oldest set (not counting the navigation history set)
			rawSets.remove(1);
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
		
		return combined.copy(false);
	}
	
	public boolean containsSet(NodeSet set) {
		return rawSets.contains(set);
	}
}
