package ca.ucalgary.codesets.listeners;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.internal.ui.search.SearchMessages;
import org.eclipse.jdt.internal.ui.search.SearchUtil;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.MatchEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ca.ucalgary.codesets.ResultSet;
import ca.ucalgary.codesets.sets.CodeSet;

/**
 * Listens for any new results from a reference search for a given element.
 * @author starkej
 *
 */
public class ReferenceToListener extends CodeSetListener implements ISearchResultListener {
	ResultSet referencesTo;
	
	CodeSet referenceToSet;
	IJavaElement element;

	public ReferenceToListener (ResultSet referencesTo) {
		super();
		this.referencesTo = referencesTo;
		this.name = "References To";
		this.referencesTo.setName(this.name);
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
				if (!element.equals((IJavaElement)elements[i]))
					referenceToSet.add((ISourceReference)elements[i]);
			}
			if (referenceToSet.size() > 0) {
				referencesTo.add(referenceToSet);
			}
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
			query.getSearchResult().addListener(this);
			query.run(new NullProgressMonitor());

			//SearchUtil.runQueryInBackground(query);
			JavaSearchResult results = (JavaSearchResult)query.getSearchResult();
			Object[] elements = results.getElements();
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

	@Override
	public void interactionObserved(InteractionEvent event) {
		if (event.getKind() == InteractionEvent.Kind.SELECTION) {
			IJavaElement element = resolveElement(event);
			
			JavaElementLabelProvider lp = new JavaElementLabelProvider();
			IJavaElement parent = element.getParent();
			String name = lp.getText(element);
			String fullName = (parent.getElementName() + "." + name);
			referenceToSet = (CodeSet)referencesTo.get(fullName);
			if (referenceToSet == null)
				referenceToSet = new CodeSet(CodeSet.Type.ReferenceTo);
			referenceToSet.setName(fullName);

			try {
				this.element = element;
				performNewSearch(element);
			} catch (JavaModelException ex) {
				ExceptionHandler.handle(ex, SearchMessages.Search_Error_search_notsuccessful_title, SearchMessages.Search_Error_search_notsuccessful_message); 
			}
		}
	}
	
	public void activate () {
		super.activate();
		MylarMonitorUiPlugin.getDefault().addInteractionListener(this);
	}
	
	public void deactivate () {
		super.deactivate();
		MylarMonitorUiPlugin.getDefault().removeInteractionListener(this);
	}
	
	public Object getSet () {
		return referencesTo;
	}
}
