package ca.ucalgary.codesets;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;

import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.InteractionListener;
import junit.framework.TestCase;

public class TestModels extends TestCase {
	CodeSetManager manager;
	ISourceReference ref1 = ref("=DataStructures/src<datastructures{List.java[List~append~I");
	ISourceReference ref2 = ref("=DataStructures/src<datastructures{List.java[List~tail");
	ISourceReference ref3 = ref("=DataStructures/src<datastructures{List.java[List~main~\\[QString;");
	ISourceReference ref4 = ref("=DataStructures/src<datastructures{ListNode.java[ListNode~ListNode~I~QListNode;");
	ArrayList<ISourceReference> all = new ArrayList<ISourceReference>();
	
	public void setUp() {
		manager = CodeSetManager.instance();
		if (all.size() == 0) {
			all.add(ref1);
			all.add(ref2);
			all.add(ref3);
			all.add(ref4);
		}
	}
	
	public void tearDown() {
		CodeSetManager.reset();
	}
	
	public void testCodeSetEquals() {
		CodeSet c1 = new CodeSet("a", "b");
		CodeSet c2 = new CodeSet("a", "b");
		CodeSet c3 = new CodeSet("a", "c");
		assertEquals(c1, c2);
		assertFalse(c1.equals(c3));
	}
	
	public void testAddingToManager() {
		assertEquals(1, manager.sets().size());
		CodeSet c1 = new CodeSet("a", "b");
		CodeSet c2 = new CodeSet("a", "b");
		CodeSet c3 = new CodeSet("b", "b");
		manager.addSet(c1);
		manager.addSet(c2);
		manager.addSet(c3);
		assertEquals(3, manager.sets().size());
		
		assertEquals(2, manager.sets("b").size());
	}
	
	public void testSetContents() {
		assertEquals(1, manager.sets().size());
		InteractionListener i = new InteractionListener();
		i.handleInteraction((IJavaElement)ref1);
		
		assertEquals(3, manager.sets().size());
		CodeSet nav = manager.navigationHistorySet();
		CodeSet refTo = manager.sets("references to").get(0);
		CodeSet refFrom = manager.sets("references from").get(0);
		
		assertEquals(1, nav.size());
		assertEquals(1, refTo.size());
		assertTrue(refTo.contains(ref3));
		
		assertTrue(refFrom.contains(ref2));
		// there is a problem here: initializer expressions are ignored
		//assertTrue(refFrom.contains(ref4));
		//assertEquals(2, refFrom.size());
	}
	
	public void testCombiningSets() {
		assertEquals(1, manager.sets().size());
		
		CodeSet c1 = new CodeSet("a", "b");
		CodeSet c2 = new CodeSet("c", "d");
		CodeSet c3 = new CodeSet("d", "e");
		manager.addSet(c1);
		manager.addSet(c2);
		manager.addSet(c3);
		
		c1.add(ref1);
		c1.add(ref2);
		c2.add(ref1);
		c2.add(ref3);
		c3.add(ref4);
		
		c1.state = CodeSet.State.INCLUDED;
		c2.state = CodeSet.State.INCLUDED;
		c3.state = CodeSet.State.INCLUDED;
		CodeSet set = manager.displaySet();
		assertEquals(4, set.size());
		assertTrue(set.containsAll(all));
		
		c3.state = CodeSet.State.IGNORED;
		assertEquals(3, manager.displaySet().size());
		
		c2.state = CodeSet.State.EXCLUDED;
		set = manager.displaySet();
		assertEquals(1, set.size());
		assertTrue(set.contains(ref2));
		
		c2.state = CodeSet.State.RESTRICTEDTO;
		set = manager.displaySet();
		assertEquals(1, set.size());
		assertTrue(set.contains(ref1));
		
	}
	
	public void testDisplayHistory() {
		InteractionListener i = new InteractionListener();
		i.handleInteraction((IJavaElement)ref1);
		manager.displaySet();
		
		manager.navigationHistorySet().state = CodeSet.State.IGNORED;
		manager.sets("references to").get(0).state = CodeSet.State.INCLUDED;
		manager.displaySet();
		
		assertEquals(-1, manager.displaySetsAgo(ref3));
		assertEquals(1, manager.displaySetsAgo(ref1));
		assertEquals(-1, manager.displaySetsAgo(ref2));
	}
	
	// helper method to get source reference elements
	public ISourceReference ref(String handle) {
		return (ISourceReference)InteractionListener.resolveElement(handle, "java");
	}
}
