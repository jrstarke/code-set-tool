package ca.ucalgary.codesets.models;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;
import org.eclipse.search.ui.text.TextSearchQueryProvider;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import ca.ucalgary.codesets.controllers.Logger;

public class SearchBox extends Composite{
	private final Text text = new Text(this, SWT.SINGLE | SWT.BORDER);
	private Button button = new Button(this,SWT.PUSH);

	public SearchBox(Composite parent) {

		super(parent,SWT.NO_BACKGROUND);

		GridLayout grid = new GridLayout();
		grid.numColumns = 2;
		this.setLayout( grid);

		GridData data = new GridData();
		data.widthHint = 100;
		text.setLayoutData(data);
		addTextBox();
		addSearchButton();
	}

	//Adds a button called search to the Composite
	//When the button is pressed the listener calls search().
	private void addSearchButton() {
		button.setText("Search");
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				searchNumeroTwo();
			}
		});		
	}

	

	public FileTextSearchScope createTextSearchScope() { 
		String[] string = {"*.java"};
		return FileTextSearchScope.newWorkspaceScope(string, false);
	}


	private static class TextSearchPageInput extends TextSearchInput {
		private final String fSearchText;
		private final boolean fIsCaseSensitive;
		private final boolean fIsRegEx;
		private final FileTextSearchScope fScope;

		public TextSearchPageInput(String searchText, boolean isCaseSensitive, boolean isRegEx, FileTextSearchScope scope) {
			fSearchText= searchText;
			fIsCaseSensitive= isCaseSensitive;
			fIsRegEx= isRegEx;
			fScope= scope;
		}

		public String getSearchText() {
			return fSearchText;
		}

		public boolean isCaseSensitiveSearch() {
			return fIsCaseSensitive;
		}

		public boolean isRegExSearch() {
			return fIsRegEx;
		}

		public FileTextSearchScope getScope() {
			return fScope;
		}
	}

	private ISearchQuery newQuery() throws CoreException { 
		TextSearchPageInput input= new TextSearchPageInput(text.getText(), true, false, createTextSearchScope());
		return TextSearchQueryProvider.getPreferred().createQuery(input);
	}

//	public ISearchResultViewPart getActiveSearchView() {
//	IWorkbenchPage activePage= SearchPlugin.getActivePage();
//	if (activePage != null) {
//	return findLRUSearchResultView(activePage, false);
//	}
//	return null;
//	}

//	private IWorkbenchSiteProgressService getProgressService() {
//	ISearchResultViewPart view= getActiveSearchView();
//	if (view != null) {
//	IWorkbenchPartSite site= view.getSite();
//	if (site != null)
//	return (IWorkbenchSiteProgressService)view.getSite().getAdapter(IWorkbenchSiteProgressService.class);
//	}
//	return null;
//	}

//	private class SearchJobRecord {
//	public ISearchQuery query;
//	public Job job;
//	public boolean background;
//	public boolean isRunning;

//	public SearchJobRecord(ISearchQuery job, boolean bg) {
//	this.query= job;
//	this.background= bg;
//	this.isRunning= false;
//	this.job= null;
//	}
//	}





	private void searchNumeroTwo() {		
		if(!text.getText().equals("Enter Search") && text.getText() != null  && !text.getText().equals("")) {
			IJavaSearchScope searchScope = org.eclipse.jdt.core.search.SearchEngine.createWorkspaceScope();
			SearchEngine.createWorkspaceScope().setIncludesClasspaths(true);
			searchScope = SearchEngine.createWorkspaceScope();
			final NodeSet searchSet = new NodeSet(text.getText(),"search");
			try {
				ISearchQuery query = newQuery();
				query.getSearchResult().addListener(new ISearchResultListener() {
					public void searchResultChanged(SearchResultEvent e) {
						if(e instanceof MatchEvent){
							Match[] matches = ((MatchEvent)e).getMatches();
							for (Match m:matches) {
								FileMatch fm = (FileMatch)m;
								ICompilationUnit unit = JavaCore.createCompilationUnitFrom(fm.getFile());
								ASTNode node = ASTHelper.getNodeAtPosition(unit, m.getOffset());
								//if (node != null)// && !searchSet.containsNode(node))
									searchSet.add(node);
							}	
							NodeSetManager.instance.addSet(searchSet);
						}
					}
				});
				query.run(new NullProgressMonitor());

			} catch (IllegalArgumentException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (CoreException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		}







//		SearchPattern sp = SearchPattern.createPattern(text.getText(), IJavaSearchConstants.CLASS
//		| IJavaSearchConstants.INTERFACE
//		| IJavaSearchConstants.ENUM
//		| IJavaSearchConstants.ANNOTATION_TYPE
//		| IJavaSearchConstants.CLASS_AND_ENUM
//		| IJavaSearchConstants.CLASS_AND_INTERFACE
//		| IJavaSearchConstants.TYPEton
//		| IJavaSearchConstants.FIELD
//		| IJavaSearchConstants.METHOD
//		| IJavaSearchConstants.CONSTRUCTOR
//		| IJavaSearchConstants.PACKAGE,IJavaSearchConstants.ALL_OCCURRENCES,SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE);

//		JavaSearchQuery query= new JavaSearchQuery(new PatternQuerySpecification(text.getText(), IJavaSearchConstants.METHOD | IJavaSearchConstants.CLASS | IJavaSearchConstants.DECLARATIONS | IJavaSearchConstants.CONSTRUCTOR | IJavaSearchConstants.IMPLEMENTORS | IJavaSearchConstants.PREFIX_MATCH | IJavaSearchConstants.PACKAGE | IJavaSearchConstants.FIELD | IJavaSearchConstants.TYPE,true,  IJavaSearchConstants.ALL_OCCURRENCES, searchScope ,"Search"));
////		JavaSearchQuery query= new JavaSearchQuery(new PatternQuerySpecification(text.getText(), IJavaSearchConstants.ALL_OCCURRENCES,true,  IJavaSearchConstants.ALL_OCCURRENCES, searchScope ,"Search"));
////		JavaSearchQuery query= new JavaSearchQuery(new PatternQuerySpecification(sp, searchScope ,"Search"));

//		SearchParticipant[] sp2 = null;

//		SearchRequestor SearchRequestor = null;
//		IProgressMonitor IProgressMonitor = null;
//		SearchEngine searchEngine = new SearchEngine();

//		try {
//		searchEngine.search(sp, sp2, searchScope, SearchRequestor, IProgressMonitor);
//		} catch (CoreException e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//		}

//		final NodeSet searchSet = new NodeSet(text.getText(),"Search");


//		query.getSearchResult().addListener(new ISearchResultListener() {
//		public void searchResultChanged(SearchResultEvent e) {
//		if(e instanceof MatchEvent){
//		Match[] matches = ((MatchEvent)e).getMatches();
//		for (Match m:matches) {
//		ICompilationUnit unit = (ICompilationUnit)((IJavaElement)m.getElement()).getAncestor(IJavaElement.COMPILATION_UNIT);
//		if(unit != null){
//		ASTNode node = ASTHelper.getNodeAtPosition(unit, m.getOffset());
//		searchSet.add(node);
//		}
//		}	
////		if(searchSet.size() > 0)	
//		NodeSetManager.instance.addSet(searchSet);
//		}
//		}
//		});
//		query.run(new NullProgressMonitor());
//		Logger.instance().addEvent("Searched for "+"\t"+text.getText());
//		}		
	}

	//Adds a text box to the composite. 
	//When the text box is clicked, if it still has the original text in it 
	//It deletes it and makes it an empty text box
	private void addTextBox() {
		text.setText("Enter Search");
		text.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(text.getText().equals("Enter Search"))
					text.setText("");
			}
		});
	}	
}
