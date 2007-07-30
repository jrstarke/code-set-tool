package ca.ucalgary.codesets.models;

import java.util.List;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SearchBox extends Composite{
	private final Text text = new Text(this,SWT.SINGLE | SWT.BORDER);
	private Button button = new Button(this,SWT.PUSH);
	
	public SearchBox(Composite parent) {
		
		super(parent,SWT.NO_BACKGROUND);
//		this.setSize(300, 18);
//		text.setSize(150, 18);
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = false;
		layout.fill = true;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		this.setLayout(layout);
		
//		this.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
//                | GridData.HORIZONTAL_ALIGN_FILL));
		
		addTextBox();
		addSearchButton();
	}

	//Adds a button called search to the Composite
	//When the button is pressed the listener calls search().
	private void addSearchButton() {
		button.setText("Search");
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				search();
			}
		});		
	}
	
	//checks whether or not the there has been something typed in the text box.
	//If something has been typed into the text box, the listener searches through all sets that are not search sets
	//and adds the ISourceReferences to a CodeSet with the name of the search that was performed. 
	private void search() {
		if(!text.getText().equals("Enter Search Here") && text.getText() != null  && !text.getText().equals("")){
			List<CodeSet> list = CodeSetManager.instance.sets();
			CodeSet searchSet = new CodeSet(text.getText(),"search");
			//Checks every set, to see if contains the word that was written in the text box
			for(CodeSet set: list){
				if(!set.category.equals("search")) {
					for(ISourceReference isr: set){
						try {
							if(isr instanceof ResolvedSourceMethod && isr.getSource().contains(text.getText()))
								searchSet.add(isr);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
			CodeSetManager.instance.addSet(searchSet);
		}
	}

	//Adds a text box to the composite. 
	//When the text box is clicked, if it still has the original text in it 
	//It deletes it and makes it an empty text box
	private void addTextBox() {
		text.setText("Enter Search Here");
		text.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(text.getText().equals("Enter Search Here"))
					text.setText("");
			}
		});
	}	
}
