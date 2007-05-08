package ca.ucalgary.codesets;

import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

public class ElementLabelProvider extends JavaElementLabelProvider implements IColorProvider{
	private Color white = new Color(null, 255,255,255);
	private Color foreground = new Color(null, 0,0,0);
	private Color background = new Color(null, 225,225,210);
	private ChangeSet changeSet;
	private HistorySet historySet;
	private HashSet<ISourceReference> currentSet;
	
	public ElementLabelProvider(ChangeSet cset,HistorySet hset)
	{
		super(JavaElementLabelProvider.SHOW_PARAMETERS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS
				| JavaElementLabelProvider.SHOW_RETURN_TYPE
				| JavaElementLabelProvider.SHOW_TYPE);
		changeSet = cset;
		historySet = hset;
	}

	public void setCurrentSet(HashSet<ISourceReference> set) {
		this.currentSet = set;
	}
	
	public Color getBackground(Object element) {
		if (currentSet == historySet && changeSet.contains((ISourceReference)element))
			return background;
		return white;
	}

	public Color getForeground(Object element) {
		return foreground;
	}
	
	public void setBackColor(Color colour) {
		this.background = colour;
	}
	 
	public void setForgroundColor(Color colour) {
		this.foreground = colour;
	}
}
