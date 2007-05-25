package ca.ucalgary.codesets;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

import ca.ucalgary.codesets.sets.CodeSet;

public class ElementLabelProvider extends JavaElementLabelProvider implements IColorProvider{
	private Color white = new Color(null, 255,255,255);
	private Color foreground = new Color(null, 0,0,0);
	private Color background = new Color(null, 225,225,210);
	private Color greyText = new Color(null, 100,100,100);
	private CodeSet changeSet;
	
//	private ChangeSet changeSet;
	private CodeSet historySet;
	private CodeSet searchSet;
	private CodeSet currentSet;
	
	public ElementLabelProvider(CodeSet editorChangeSet,CodeSet hset,CodeSet sSet)
	{
		super(JavaElementLabelProvider.SHOW_PARAMETERS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS
				| JavaElementLabelProvider.SHOW_RETURN_TYPE
				| JavaElementLabelProvider.SHOW_TYPE);
		changeSet = editorChangeSet;
		historySet = hset;
		searchSet = sSet;
	}

	public void setCurrentSet(CodeSet set) {
		this.currentSet = set;
	}
	
	public CodeSet getCurrentSet(){
		return currentSet;
	}
	
	public Color getBackground(Object element) {
		if (currentSet == historySet && changeSet.contains((ISourceReference)element))
			return background;
		if(currentSet == changeSet)  //everything that's in the changeSet will have a background Colour other than white. 
			return background;
		return white;
	}

		
	/* returns a grey colour if the element is an object of HistorySet and SearchSet
	 * returns black, if it is not
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		if((currentSet == historySet || currentSet == searchSet) && searchSet.contains((ISourceReference)element)
				&& historySet.contains((ISourceReference)element))
			return greyText;			
		return foreground;
	}
	
	public void setBackColor(Color colour) {
		this.background = colour;
	}
	 
	public void setForgroundColor(Color colour) {
		this.foreground = colour;
	}
}
