package ca.ucalgary.codesets;

import ca.ucalgary.codesets.sets.*;
import ca.ucalgary.codesets.listeners.InteractionListener;


import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

import ca.ucalgary.codesets.sets.CodeSet;

public class ElementLabelProvider extends JavaElementLabelProvider implements IColorProvider{
	private Color white = new Color(null, 255,255,255);	

//	Background colours for each set reference
	private Color refToBackground = new Color(null,255,255,100); 	// Yellow
	private Color refFromBackground = new Color(null,153,204,255);	// Blue
	private Color historyBackground = new Color(null,255,204,100);	// Orange
	private Color changeBackground = new Color(null,100,255,100);	// Green

//	Sets
	private CodeSet changeSet;
	private CodeSet historySet;
	private CodeSet currentSet;

	public ElementLabelProvider(CodeSet editorChangeSet,CodeSet hset)
	{
		super(JavaElementLabelProvider.SHOW_PARAMETERS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS
				| JavaElementLabelProvider.SHOW_RETURN_TYPE
				| JavaElementLabelProvider.SHOW_TYPE);

		changeSet = editorChangeSet;
		historySet = hset;
	}

	public void setCurrentSet(CodeSet set) {
		this.currentSet = set;
	}

	public CodeSet getCurrentSet(){
		return currentSet;
	}

	public Color getBackground(Object element) {

		CodeSet referenceToSet = null;
		CodeSet referenceFromSet = null;

		ResultSet referenceTo = InteractionListener.getReferenceTo();
		ResultSet referenceFrom = InteractionListener.getReferenceFrom();
	
		if(referenceTo.size() >= 1)
			referenceToSet = referenceTo.get(0);
		if(referenceTo.size() >= 1)
			referenceFromSet = referenceFrom.get(0);

		if(currentSet instanceof ReferenceToSet) { //to

			if(changeSet.contains((ISourceReference)element)) {
				return changeBackground;
			}
			if(historySet.contains((ISourceReference)element)) {
				return historyBackground;
			}
		} else if(currentSet instanceof ReferenceFromSet) {  //from

			if(changeSet.contains((ISourceReference)element)) {
				return changeBackground;
			}
			if(historySet.contains((ISourceReference)element)) {
				return historyBackground;
			}
		} else if(currentSet instanceof EditorChangeSet || currentSet instanceof HistorySet) {

			if(referenceToSet != null && referenceToSet instanceof ReferenceFromSet) {
				if(referenceToSet.contains((ISourceReference)element))
					return refToBackground;
			}
			if(referenceFromSet != null && referenceFromSet instanceof ReferenceToSet) {
				if(referenceFromSet.contains((ISourceReference)element))
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
