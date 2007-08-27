package ca.ucalgary.codesets.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;


// a named and categorized collection of IJavaElements (method declarations) mapped to 
// ASTNode's (actually ASTNodePlacehoder's). when a node is added all of its ancestors 
// upto the method declaration level are added as well.
public class NodeSet extends HashMap<IJavaElement, HashSet<ASTNodePlaceholder>> {

	public String name;
	public String category;
	
	// state is used to create combined sets (based on set operations)
	public enum State {
		IGNORED, INCLUDED, EXCLUDED, RESTRICTEDTO }
	public State state = State.IGNORED;

	public NodeSet(String name, String category) {
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
		return (other instanceof NodeSet)
				&& ((NodeSet) other).name.equals(this.name)
				&& ((NodeSet) other).category.equals(this.category);
	}

	public String toString() {
		return category + "/" + name + " " + super.toString();
	}

	public void add(IJavaElement key, ASTNode node) {
		if (!containsKey(key))
			put(key, new HashSet<ASTNodePlaceholder>());	
		addPlaceholders(get(key), node);
	}
	
	void addPlaceholders(HashSet<ASTNodePlaceholder> set, ASTNode node) {
		while (node != null) {
			set.add(new ASTNodePlaceholder(node));
			
			int type = node.getNodeType();
			if ((type == ASTNode.METHOD_DECLARATION || 
					type == ASTNode.FIELD_DECLARATION || 
					type == ASTNode.TYPE_DECLARATION) && 
				node.getParent().getNodeType() != ASTNode.ANONYMOUS_CLASS_DECLARATION)
				break;
			
			node = node.getParent();
		}
	}
	
	public void add(ASTNode node) {
		HashSet<ASTNodePlaceholder> set = new HashSet<ASTNodePlaceholder>();
		while (node != null) {
			set.add(new ASTNodePlaceholder(node));
			IJavaElement key = ASTHelper.getJavaElement(node);
			if (key != null && key.getParent().getElementType() == IJavaElement.TYPE) {
				IType t = (IType) key.getParent();
				try {
					if (!t.isAnonymous()) {
						if (containsKey(key))
							get(key).addAll(set);
						else
							put(key, set);
						break; // reached a method or field declaration so stop
					}
				} catch (JavaModelException e) {
					// continue ...
				}
			}

			node = node.getParent();
		}
	}

	// if the key is not found, the key is added and the node is added as
	// a member of the set the key maps to. if the key is found, the node
	// is just added to the corresponding set.
	void putOrAdd(IJavaElement key, ASTNodePlaceholder node) {
		HashSet<ASTNodePlaceholder> set = null;
		if (containsKey(key)) {
			set = get(key);
		} else {
			set = new HashSet<ASTNodePlaceholder>();
			put(key, set);
		}
		set.add(node);
	}
	
	// returns a copy of this node set (the copy doesn't need to have the same
	// value for classIndex)
	NodeSet copy() {
		NodeSet result = new NodeSet(this.name, this.category);
		for (IJavaElement key : keySet())
			for (ASTNodePlaceholder node : get(key))
				result.putOrAdd(key, node);

		return result;
	}
	
	// returns a collection of TypeMember instances constructed from the elements
	// of this set
	public Collection<TypeMembers> elementsByType() {
		HashMap<IType, TypeMembers> byType = new HashMap<IType, TypeMembers>();
		for (IJavaElement key : keySet()) {
			IType unit = (IType)key.getAncestor(IJavaElement.TYPE);
			if (!byType.containsKey(unit)) {
				TypeMembers tm = new TypeMembers(unit);
				byType.put(unit, tm);
			}
			
			byType.get(unit).addEntry(key, get(key));
		}
		
		return byType.values();
	}

	// various set operations

	// returns a new set that is the union of this set and
	public NodeSet union(NodeSet set) {
		NodeSet result = copy();
		for (IJavaElement key : set.keySet())
			if (result.containsKey(key))
				result.get(key).addAll(set.get(key));
			else
				result.put(key, (HashSet<ASTNodePlaceholder>) set.get(key).clone());
		return result;
	}

	// returns a new set that is the set difference of this and the given set
	public NodeSet setDifference(NodeSet set) {
		NodeSet result = new NodeSet("", "");
		for (IJavaElement key : keySet())
			if (!set.containsKey(key))
				result.put(key, get(key));
		return result;
	}

	// returns a set that represent the intersection of this and the given set
	public NodeSet intersection(NodeSet set) {
		NodeSet result = new NodeSet(this.name, this.category);
		for (IJavaElement key : keySet())
			if (set.containsKey(key)) {
				result.put(key, get(key));
				result.get(key).addAll(set.get(key));
			}
		return result;
	}

	public String displayName() {
		return name;
	}
}
