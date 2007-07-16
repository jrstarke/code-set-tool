package ca.ucalgary.codesets.models;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


import org.eclipse.jdt.core.ISourceReference;

import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;

// keeps track of all code sets and dispatches events to listeners about 
// changes to those sets
public class CodeSetManager {
	// a raw set is a set that is supplied externally
	LinkedList<CodeSet> rawSets = new LinkedList<CodeSet>();
	
	// display sets are composed of combinations of raw sets
	ArrayList<CodeSet> displaySets = new ArrayList<CodeSet>();
	
	// the element that is the current focus in the editor
	ISourceReference currentFocus = null;
	
	// a collection of objects that are listening to code set events
	HashSet<ICodeSetListener> listeners = new HashSet<ICodeSetListener>();
	
	// the maximum number of raw sets that will be saved
	final static int MAX_RAW_SETS = 20;
	static CodeSetManager instance = new CodeSetManager();
	
	// there is one global instance of this class
	public static CodeSetManager instance() {
		return instance;
	}
	
	CodeSetManager() {
		addSet(new CodeSet("navigation", "history"));
		navigationHistorySet().state = CodeSet.State.INCLUDED;
		MylarMonitorUiPlugin plugin = MylarMonitorUiPlugin.getDefault();
		if (plugin != null)
			plugin.addInteractionListener(new InteractionListener());
	}
	
	// likely only useful for testing purposes 
	public static void reset() {
		instance = new CodeSetManager();
	}
	
	public void addListener(ICodeSetListener listener) {
		listeners.add(listener);
	}
	
	// assumes the navigation history set is the first one added
	public CodeSet navigationHistorySet() {
		return rawSets.getFirst();
	}
	
	public void setFocus(ISourceReference newFocus) {
		currentFocus = newFocus;
		navigationHistorySet().add(currentFocus);
		for (ICodeSetListener listener : listeners) {
			listener.setChanged(navigationHistorySet());
			listener.focusChanged(newFocus);
		}
	}
	
	public ISourceReference getFocus() {
		return currentFocus;
	}
	
	public void clearStates() {
		// for each set ...
	}
	
	public void changeState(CodeSet set) {
		set.transition();
		for (ICodeSetListener listener : listeners)
			listener.stateChanged(set);
	}
	
	public void addSet(CodeSet set) {
		boolean removed = rawSets.remove(set); // O(n), but n <= SET_NUM_LIMIT
		rawSets.add(set);
		
		if (rawSets.size() > MAX_RAW_SETS)
			// remove oldest set (not counting the navigation history set)
			rawSets.remove(1);
		
		if (!removed)
			for (ICodeSetListener listener : listeners)
				listener.setAdded(set);
	}
	
	// returns list of all "raw" sets
	public List<CodeSet> sets() {
		return rawSets;
	}
	
	// returns a list of all sets in the given category
	public List<CodeSet> sets(String category) {
		ArrayList<CodeSet> result = new ArrayList<CodeSet>();
		for (CodeSet set : rawSets)
			if (set.category == category) 
				result.add(set);
		return result;
	}
	
	
	// the display set is the current elements that should be
	// displayed (made from combinations of raw sets)
	public CodeSet displaySet() {
		return displaySet(true);
	}
	
	public CodeSet displaySet(boolean newSet) {
		CodeSet set = new CodeSet("x", "display");
		
		// add in elements from all included sets
		for (CodeSet s : sets())
			if (s.state == CodeSet.State.INCLUDED)
				set.addAll(s);
		
		// remove elements from all excluded sets
		for (CodeSet s : sets())
			if (s.state == CodeSet.State.EXCLUDED)
				set.removeAll(s);
		
		// remove elements not in "restricted to" sets
		for (CodeSet s : sets())
			if (s.state == CodeSet.State.RESTRICTEDTO)
				set = set.intersection(s);
		
		if (!newSet)
			displaySets.remove(0);
		
		displaySets.add(0, set);
		return set;
	}

	public int displaySetsAgo(ISourceReference element) {
		int counter = 0;
		for (CodeSet set : displaySets) {
			if (counter != 0 && set.contains(element))
				return counter;
			counter++;
		}
		return -1;
	}
	
}
