package ca.ucalgary.codesets.models;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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

import ca.ucalgary.codesets.views.ElementLabelProvider;

//uses eclipse's search api to produce a set of all elements that reference
//a given IJavaElement
public class ReferenceToSearch implements ISearchResultListener {
	int REFERENCETOVALUE = 1;
	NodeWrapper method;
	NodeSet set;
	ElementLabelProvider labelProvider = new ElementLabelProvider();

	void search(ASTNode node) {
		MethodDeclaration method = (MethodDeclaration)ASTHelper.getMethodAncestor(node);
		if (method == null)
			return;
		this.method = new NodeWrapper(method);
		IJavaElement methodElement = ASTHelper.getJavaElement(method);

		set = new NodeSet(labelProvider.getFullText(methodElement), "references to");

		// TODO: really we should just add to the existing set if there is one
		if (NodeSetManager.instance().containsSet(set))
			return;

		try {
			JavaSearchQuery query= new JavaSearchQuery(createQuery(methodElement));
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
			for (Match m:matches) {
				ICompilationUnit unit = (ICompilationUnit)((IJavaElement)m.getElement()).getAncestor(IJavaElement.COMPILATION_UNIT);
				ASTNode node = ASTHelper.getNodeAtPosition(unit, m.getOffset());
				if (!method.equals(new NodeWrapper((MethodDeclaration)ASTHelper.getAncestorByType(node, ASTNode.METHOD_DECLARATION)))) {
					set.add(node);
				}
			}
			if (empty && set.size() > 0)
				NodeSetManager.instance().addSet(set);
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
