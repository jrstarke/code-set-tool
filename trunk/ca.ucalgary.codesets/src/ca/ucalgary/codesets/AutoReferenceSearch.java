package ca.ucalgary.codesets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.internal.ui.search.SearchMessages;
import org.eclipse.jdt.internal.ui.search.SearchUtil;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.listeners.*;
/**
 * 
 * @author starkej
 *
 */
public class AutoReferenceSearch {
	CodeSet searchSet;
	
	/**
	 * Create a new auto reference search with a given search set and element what we would like
	 * to search and add results to.
	 * @param searchSet
	 * @param element
	 */
	public AutoReferenceSearch (CodeSet searchSet, IJavaElement element) {
		this.searchSet = searchSet;
		searchSet.clear();
		run(element);
	}

	/**
	 * Run a new search for a given element
	 * @param element
	 */
	public void run(IJavaElement element) {

		try {
			performNewSearch(element);
		} catch (JavaModelException ex) {
			ExceptionHandler.handle(ex, SearchMessages.Search_Error_search_notsuccessful_title, SearchMessages.Search_Error_search_notsuccessful_message); 
		}
	}

	/**
	 * Performs a new search for a given IJavaElement to discover all of the things that
	 * reference that object.
	 * @param element
	 * @throws JavaModelException
	 */
	private void performNewSearch(IJavaElement element) throws JavaModelException {
		JavaSearchQuery query= new JavaSearchQuery(createQuery(element));
		if (query.canRunInBackground()) {
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
			 */
			query.getSearchResult().addListener(new EditorAutoReferenceListener(searchSet));
			query.run(new NullProgressMonitor());
			
			//SearchUtil.runQueryInBackground(query);
			JavaSearchResult results = (JavaSearchResult)query.getSearchResult();
			Object[] elements = results.getElements();
			System.out.println(results);
			
			//query.getSearchResult().addListener(new EditorAutoReferenceListener(searchSet));
		} else {
			IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case it would be ISearchQuery).
			 */
			IStatus status= SearchUtil.runQueryInForeground(progressService, query);
//			if (status.matches(IStatus.ERROR | IStatus.INFO | IStatus.WARNING)) {
//			ErrorDialog.openError(getShell(), SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message, status); 
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
