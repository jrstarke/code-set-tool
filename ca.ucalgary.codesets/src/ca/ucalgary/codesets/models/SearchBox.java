package ca.ucalgary.codesets.models;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;
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
	
	//checks whether or not the there has been something typed in the text box.
	//If something has been typed into the text box, the listener searches through all sets that are not search sets
	//and adds the ISourceReferences to a CodeSet with the name of the search that was performed. 
//	private void search() {
//		if(!text.getText().equals("Enter Search") && text.getText() != null  && !text.getText().equals("")){
//			List<CodeSet> list = CodeSetManager.instance.sets();
//			CodeSet searchSet = new CodeSet(text.getText(),"search");
//			//Checks every set, to see if contains the word that was written in the text box
//			for(CodeSet set: list){
//				if(!set.category.equals("search")) {
//					for(ISourceReference isr: set){
//						try {
//							if(isr instanceof ResolvedSourceMethod && isr.getSource().contains(text.getText()))
//								searchSet.add(isr);
//						} catch (JavaModelException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//			CodeSetManager.instance.addSet(searchSet);
//			
//		}
//	}
	
	private void searchNumeroTwo() {		
		if(!text.getText().equals("Enter Search") && text.getText() != null  && !text.getText().equals("")) {
			IJavaSearchScope searchScope = org.eclipse.jdt.core.search.SearchEngine.createWorkspaceScope();
			SearchEngine.createWorkspaceScope().setIncludesClasspaths(true);
			searchScope = SearchEngine.createWorkspaceScope();
			JavaSearchQuery query= new JavaSearchQuery(new PatternQuerySpecification(text.getText(), IJavaSearchConstants.METHOD | IJavaSearchConstants.CLASS | IJavaSearchConstants.PACKAGE | IJavaSearchConstants.FIELD | IJavaSearchConstants.ALL_OCCURRENCES,false,  IJavaSearchConstants.ALL_OCCURRENCES, searchScope ,"Search"));
			
			final NodeSet searchSet = new NodeSet(text.getText(),"Search");
			
			query.getSearchResult().addListener(new ISearchResultListener() {
				public void searchResultChanged(SearchResultEvent e) {
					if(e instanceof MatchEvent){
						Match[] matches = ((MatchEvent)e).getMatches();
						for (Match m:matches) {
							ICompilationUnit unit = (ICompilationUnit)((IJavaElement)m.getElement()).getAncestor(IJavaElement.COMPILATION_UNIT);
							if(unit != null){
								ASTNode node = ASTHelper.getNodeAtPosition(unit, m.getOffset());
								searchSet.add(node);
							}
						}	
						if(searchSet.size() > 0)	
							NodeSetManager.instance.addSet(searchSet);
					}
				}
			});
			query.run(new NullProgressMonitor());
			Logger.instance().addEvent("Searched for "+"\t"+text.getText());
		}		
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
