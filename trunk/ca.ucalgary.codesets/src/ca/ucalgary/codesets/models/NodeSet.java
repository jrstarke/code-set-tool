package ca.ucalgary.codesets.models;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;

// a named and categorized collection of ASTNode'ss. the set is indexed by type declarations
// or by method and field declarations (depending on the value of classIndex). when a node
// is added all of its ancestors up to the index node are also added. 
//
// (actually rather than store ASTNode's directly we store NodeWrapper's, because ast nodes
// don't work properly in a hash table.)
public class NodeSet extends HashMap<NodeWrapper, HashSet<NodeWrapper>> {
	public enum State {
		IGNORED, INCLUDED, EXCLUDED, RESTRICTEDTO }

	public State state = State.IGNORED;

	public String name;
	public String category;

	// if this is true types are mapped to descendants, otherwise field and
	// method declarations are mapped to descendants
	boolean classIndex;

	public NodeSet(String name, String category) {
		this(name, category, false);
	}

	public NodeSet(String name, String category, boolean typeLevel) {
		this.name = name;
		this.category = category;
		this.classIndex = typeLevel;
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

	// returns true if the given node is the type this instance is mapping
	// to descendants (this is based on the value of the classIndex field)
	boolean isIndexType(ASTNode node) {
		int type = node.getNodeType();
		if (classIndex)
			return type == ASTNode.TYPE_DECLARATION;
		else
			return type == ASTNode.METHOD_DECLARATION
					|| type == ASTNode.FIELD_DECLARATION;
	}

	public NodeWrapper add(NodeWrapper node) {
		return add(node.getNode());
	}

	// returns the key (the type of which depends on the value of classIndex)
	public NodeWrapper add(ASTNode node) {
		HashSet<NodeWrapper> set = new HashSet<NodeWrapper>();
		NodeWrapper key = null;
		while (node != null) {
			set.add(new NodeWrapper(node));

			if (isIndexType(node)) {
				key = new NodeWrapper(node);
				if (containsKey(key))
					get(key).addAll(set);
				else
					put(key, set);
				break;
			}

			node = node.getParent();
		}

		return key;
	}

	// returns a copy of this node set (the copy doesn't need to have the same
	// value for classIndex)
	NodeSet copy(boolean classIndex) {
		NodeSet result = new NodeSet(this.name, this.category, classIndex);
		for (NodeWrapper key : keySet())
			for (NodeWrapper node : get(key))
				result.add(node);

		return result;
	}

	// returns true if the given node is in this set, returns false otherwise
	public boolean containsNode(ASTNode node) {
		int type = classIndex ? ASTNode.TYPE_DECLARATION : ASTNode.METHOD_DECLARATION;
		NodeWrapper key = new NodeWrapper(ASTHelper.getAncestorByType(node, type));
		if (containsKey(key))
			return get(key).contains(new NodeWrapper(node));
		return false;
	}

	// various set operations

	// returns a new set that is the union of this set and
	public NodeSet union(NodeSet set) {
		NodeSet result = copy(this.classIndex);
		for (NodeWrapper key : set.keySet())
			if (result.containsKey(key))
				result.get(key).addAll(set.get(key));
			else
				result.put(key, (HashSet<NodeWrapper>) set.get(key).clone());
		return result;
	}

	// returns a new set that is the set difference of this and the given set
	public NodeSet setDifference(NodeSet set) {
		NodeSet result = new NodeSet("", "");
		for (NodeWrapper key : keySet())
			if (!set.containsKey(key))
				result.put(key, get(key));
		return result;
	}

	// returns a set that represent the intersection of this and the given set
	public NodeSet intersection(NodeSet set) {
		NodeSet result = new NodeSet(this.name, this.category);
		for (NodeWrapper key : keySet())
			if (set.containsKey(key)) {
				result.put(key, get(key));
				result.get(key).addAll(set.get(key));
			}
		return result;
	}
}
