package ca.ucalgary.codesets.views;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

// provides colors and text for the display of java elements (used by the table viewer)
public class ElementLabelProvider extends JavaElementLabelProvider implements IColorProvider{
	static Color white = new Color(null, 255,255,255);
	static Color black = new Color(null, 0,0,0);
	static Color[] highlightColors = new Color[] {
		null, new Color(null, 230,230,250), new Color(null, 234,234,251), 
		new Color(null, 238,238,252), new Color(null, 242,242,253) };

	public ElementLabelProvider() {
		super(JavaElementLabelProvider.SHOW_PARAMETERS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS
				| JavaElementLabelProvider.SHOW_RETURN_TYPE
				| JavaElementLabelProvider.SHOW_TYPE);
	}

//	// the background color is white unless the specified obj can be found
//	// in a previous display set, in which case it is one of the highlight
//	// colors (a shade of blue)
	public Color getBackground(Object obj) {
//		ISourceReference element = (ISourceReference)obj;
//		int offset = CodeSetManager.instance().displaySetsAgo(element);
//		if (offset != -1 && offset < highlightColors.length)
//			return highlightColors[offset];
		return null;
	}

	public Color getForeground(Object element) {			
		return black;
	}

	public String getFullText(Object element) {
		if (element instanceof IMethod) {
			IMethod method = (IMethod) element;
			IType type = (IType) method.getAncestor(IJavaElement.TYPE);
			return (super.getText(type) + "." + super.getText(method));
		}
		else 
			return super.getText(element);
		
	}
	
	public String getText(IType element) {
		return super.getText(element);
	}
	
	public String getText(Object element) {
		if (element instanceof IType) {
			IType type = (IType) element;
			IPackageFragment currentPackage = type.getPackageFragment();
			return (super.getText(currentPackage) + "." + super.getText(type));
		}
		else
			return super.getText(element);
	}

}
