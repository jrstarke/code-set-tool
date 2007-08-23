package ca.ucalgary.codesets.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;


public class TypeMembers {
	public static class Entry {
		public IJavaElement element;
		public HashSet<ASTNodePlaceholder> placeholders;
		
		Entry(IJavaElement element, HashSet<ASTNodePlaceholder> placeholders) {
			this.element = element;
			this.placeholders = placeholders;
		}
	}
	
	public ICompilationUnit type;
	public ArrayList<Entry> entries = new ArrayList<Entry>();
	
	TypeMembers(ICompilationUnit type) {
		this.type = type;
	}

	public void addEntry(IJavaElement key, HashSet<ASTNodePlaceholder> value) {
		entries.add(new Entry(key, value));
	}
}
