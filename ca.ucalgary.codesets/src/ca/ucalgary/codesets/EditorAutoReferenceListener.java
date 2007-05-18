package ca.ucalgary.codesets;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.MatchEvent;

import ca.ucalgary.codesets.sets.CodeSet;

/**
 * Listens for any new results from a reference search for a given element.
 * @author starkej
 *
 */
public class EditorAutoReferenceListener implements ISearchResultListener {
	CodeSet searchSet;
	
	/**
	 * Creates a new Auto Reference Listener and adds any results to the provided 
	 * searchSet
	 * @param searchSet
	 */
	public EditorAutoReferenceListener (CodeSet searchSet) {
		this.searchSet = searchSet;
	}

	/**
	 * Notifies the system of a change in the results for a given search.  Any matches
	 * from the Search Result Event are added to the searchSet
	 */
	public void searchResultChanged (SearchResultEvent event) {
		if (event instanceof MatchEvent) {
			JavaSearchResult results = (JavaSearchResult)event.getSearchResult();
			Object[] elements = results.getElements();
			for (int i = 0; i < elements.length; i++) {
//				System.out.println(elements[i]);
				searchSet.add((ISourceReference)elements[i]);
			}
			System.out.println(results);
		}
	}

}
