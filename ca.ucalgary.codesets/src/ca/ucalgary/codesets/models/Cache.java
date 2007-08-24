package ca.ucalgary.codesets.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;



//This cache is an array of length MAX_LENGTH, and contains CacheNodes, which 
//act like a hash table in which the contain the key/value where the key is the 
//ICompilationUnit and the value is a CompilationUnit.
//The get method will always return a CompilationUnit. If the ICompilationUnit does not 
//exist in the cache already, it will create one, and add it to the cache. 
public class Cache extends ArrayList<CacheNode> {

	final private int MAX_LENGTH = 15;
	
	public Cache() {
		super();
	}
	
	//Keeps the list of size MAX_LENGTH, by deleting the one that has been in the
	//cache the longest
	public void add(ICompilationUnit unit){
		if(this.size() >= MAX_LENGTH)
			super.remove(0);
		super.add(new CacheNode(unit));
	}
	
	public boolean contains(ICompilationUnit unit) {
		return super.contains(unit);
	}
	
	
	//Either finds the ICompilationUnit and returns the CompilationUnit, or
	//creates a new CacheNode and places it in the Cache and returns the created 
	//CompilationUnit 
	public CompilationUnit get(ICompilationUnit comp) {
		CacheNode node = null;
		for(int x = 0; x < this.size();x++){
			node = this.get(x);
			if(node.compareTo(comp)==0)
				return node.getCompUnit();
		}
		
		this.add(comp);
		return this.get(comp);
	}
}
