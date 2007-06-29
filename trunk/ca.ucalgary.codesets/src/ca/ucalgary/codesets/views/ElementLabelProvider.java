package ca.ucalgary.codesets.views;

import ca.ucalgary.codesets.models.CodeSetManager;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

// provides colors and text for the display of java elements (used by the table viewer)
public class ElementLabelProvider extends JavaElementLabelProvider implements IColorProvider{
	static Color white = new Color(null, 255,255,255);
	static Color black = new Color(null, 0,0,0);
	static Color[] highlightColors = new Color[] {
		new Color(null, 100,100,220), new Color(null, 125,125,225), 
		new Color(null, 150,150,230), new Color(null, 175,175,235) };

	public ElementLabelProvider() {
		super(JavaElementLabelProvider.SHOW_PARAMETERS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS
				| JavaElementLabelProvider.SHOW_RETURN_TYPE
				| JavaElementLabelProvider.SHOW_TYPE);
	}

	// the background color is white unless the specified obj can be found
	// in a previous display set, in which case it is one of the highlight
	// colors (a shade of blue)
	public Color getBackground(Object obj) {
		ISourceReference element = (ISourceReference)obj;
		int offset = CodeSetManager.instance().displaySetsAgo(element);
		if (offset != -1 && offset < highlightColors.length)
			return highlightColors[offset];
		return null;
	}

	public Color getForeground(Object element) {			
		return black;
	}

//	public String getText(Object element) {
//		String text = super.getText(element);
//		if (element instanceof IJavaElement) {
//			IJavaElement source = (IJavaElement)element;
//			text = source.getParent().getElementName() + "." + text;
//		} 
//		return text;
//	}

}
