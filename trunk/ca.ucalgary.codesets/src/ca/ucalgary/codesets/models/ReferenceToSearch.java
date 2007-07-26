package ca.ucalgary.codesets.models;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;

// uses eclipse's search api to produce a set of all elements that reference
// a given IJavaElement
public class ReferenceToSearch implements ISearchResultListener {
	int REFERENCETOVALUE = 1;
	IJavaElement element;
	CodeSet set;
	
	void search(IJavaElement element, String name) {
		this.element = element;
		set = new CodeSet(name, "references to");
		
		// TODO: really we should just add to the existing set if there is one
		if (CodeSetManager.instance().containsSet(set))
			return;
		
		try {
			JavaSearchQuery query= new JavaSearchQuery(createQuery(element));
			query.getSearchResult().addListener(this);
			query.run(new NullProgressMonitor());
			//SearchUtil.runQueryInBackground(query);
		} catch (JavaModelException ex) {
			// TODO what should we do here?
		}
	}
	
	/**
	 * Notifies the system of a change in the results for a given search.  Any matches
	 * from the Search Result Event are added to the searchSet
	 */
	public void searchResultChanged (SearchResultEvent event) {
		if (event instanceof MatchEvent) {
			Match[] matches = ((MatchEvent)event).getMatches();
			boolean empty = set.size() == 0;
			JavaSearchResult results = (JavaSearchResult)event.getSearchResult();
			Object[] elements = results.getElements();
			for (int i = 0; i < matches.length; i++) {
				ISourceReference isr = (ISourceReference)matches[i].getElement();
				if (!element.equals(isr)) {
					set.add(isr);
					set.srcCache.incrementPosition(isr, matches[i].getOffset(), REFERENCETOVALUE);
				}
			}
			if (empty && set.size() > 0)
				CodeSetManager.instance().addSet(set);
		}
	}
	
	/**
	 * Create a new search query for the given element that will look for all things that 
	 * reference it in the workspace
	 * @param element
	 * @return ElementQuerySpecification
	 * @throws JavaModelException
	 */
	QuerySpecification createQuery(IJavaElement element) throws JavaModelException {
		JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
		boolean isInsideJRE= factory.isInsideJRE(element);

		IJavaSearchScope scope= factory.createWorkspaceScope(isInsideJRE);
		String description= factory.getWorkspaceScopeDescription(isInsideJRE);
		return new ElementQuerySpecification(element, 10, scope, description);
	}
}
