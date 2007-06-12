package ca.ucalgary.codesets;

import ca.ucalgary.codesets.sets.*;


import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

import ca.ucalgary.codesets.sets.CodeSet;

public class ElementLabelProvider extends JavaElementLabelProvider implements IColorProvider{
	private Color white = new Color(null, 255,255,255);	

//	Background colours for each set reference
	private Color refToBackground = new Color(null,255,255,0); 		// Yellow
	private Color refFromBackground = new Color(null,153,204,255);	// Blue
	private Color historyBackground = new Color(null,255,204,0);	// Orange
	private Color changeBackground = new Color(null,0,255,0);		// Green

//	Sets
	private CodeSet changeSet;
	private CodeSet historySet;
	private CodeSet currentSet;
	private ResultSets resultSets;

	public ElementLabelProvider(CodeSet editorChangeSet,CodeSet hset, ResultSets rSets)
	{
		super(JavaElementLabelProvider.SHOW_PARAMETERS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS
				| JavaElementLabelProvider.SHOW_RETURN_TYPE
				| JavaElementLabelProvider.SHOW_TYPE);

		changeSet = editorChangeSet;
		historySet = hset;
		resultSets = rSets;
	}

	public void setCurrentSet(CodeSet set) {
		this.currentSet = set;
	}

	public CodeSet getCurrentSet(){
		return currentSet;
	}

	public Color getBackground(Object element) {

		CodeSet temp1 = null;
		CodeSet temp2 = null;

		if(resultSets.size()>1) {
			temp1 = resultSets.get(0);
			temp2 = resultSets.get(1);
		}
		if(resultSets.size()==1) {
			temp1 = resultSets.get(0);
		}


		if(currentSet instanceof AutoReferenceSet) { //to

			if(changeSet.contains((ISourceReference)element)) {
				return changeBackground;
			}
			if(historySet.contains((ISourceReference)element)) {
				return historyBackground;
			}
		} else if(currentSet instanceof DependencySet) {  //from

			if(changeSet.contains((ISourceReference)element)) {
				return changeBackground;
			}
			if(historySet.contains((ISourceReference)element)) {
				return historyBackground;
			}
		} else if(currentSet instanceof EditorChangeSet || currentSet instanceof HistorySet) {

			if(temp1 != null && temp1 instanceof AutoReferenceSet) {
				if(temp1.contains((ISourceReference)element))
					return refToBackground;
			}
			if(temp1 != null && temp1 instanceof DependencySet) {
				if(temp1.contains((ISourceReference)element))
					return refFromBackground;
			}
			if(temp2 != null && temp2 instanceof AutoReferenceSet) {
				if(temp2.contains((ISourceReference)element))
					return refToBackground;
			}
			if(temp2 != null && temp2 instanceof DependencySet) {
				if(temp2.contains((ISourceReference)element))
					return refFromBackground;
			}
			if(currentSet instanceof HistorySet && changeSet.contains((ISourceReference)element)) {
				return changeBackground;
			}			
		}		
		return white;
	}

	public Color getForeground(Object element) {			
		return new Color(null, 0,0,0); 	// Always black
	}

	public void setForgroundColor(Color colour) {
//		Does nothing right now
	}

	public String getText(Object element) {
		String text = super.getText(element);
		if (element instanceof IJavaElement) {
			IJavaElement source = (IJavaElement)element;
			text = source.getParent().getElementName() + "." + text;
		} 
		return text;
	}


	/**The methods below change the colour of the designated set. 
	 * @param colour
	 */
	public void changeHistoryBackground(Color colour) {
		this.historyBackground = colour;
	}
	
	public void changeChangeBackground(Color colour) {
		this.changeBackground = colour;
	}
	
	public void changeRefToBackground(Color colour) {
		this.refToBackground = colour;
	}

	public void changeRefFromBackground(Color colour) {
		this.refFromBackground = colour;
	}
	
	
	
}
