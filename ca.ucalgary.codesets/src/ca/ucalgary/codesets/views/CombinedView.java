package ca.ucalgary.codesets.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.ucalgary.codesets.controllers.CombinedViewController;
import ca.ucalgary.codesets.controllers.Logger;
import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.NodeSet;
import ca.ucalgary.codesets.models.NodeSetManager;

// this view is the main presentation of the currently applicable set of ASTNode's.
public class CombinedView extends ViewPart  {
	Action nameSetAction;
	CombinedViewController cvc;
	
	public void createPartControl(Composite parent) {
		new CombinedViewController(parent);
		makeActions();
		createToolbar();
	}
	
	public void setFocus() {
	}
	
	// static methods and fields for creating the various widgets and composites used
	// to display node sets
	
	static Color methodColor = new Color(null, 100,100,100);
	static Color commentColor = new Color(null, 100,100,175);
	static Color methodNameColor = new Color(null, 0,0,0);
	static Color classNameColor = methodNameColor;
	
	static Label label(Composite parent, String text, int style, int height, Color color) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		fontStyle(label, style, height);
		label.setForeground(color);
		return label;
	}
	
	static void fontStyle(Control widget, int style, int height) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		fd[0].setHeight(height);
		widget.setFont(new Font(f.getDevice(), fd));
	}
	
	public static Composite classView(Composite parent, String text, String comment, Listener listener) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		result.setLayout(layout);
		label(result, text, SWT.BOLD, 13, classNameColor).addListener(SWT.MouseDoubleClick, listener);
		return result;
	}
	public static Composite methodView(Composite parent, String text, Listener listener) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		result.setLayout(layout);
		label(result, text, SWT.BOLD, 11, methodNameColor).addListener(SWT.MouseDoubleClick, listener);
		
		return result;
	}
	public static Widget methodBodyWidget(Composite parent, String text) {
		return label(parent, text, SWT.NORMAL, 11, methodColor);
	}
	public static Widget commentLabel(Composite parent, String text){
		return label(parent, text, SWT.NORMAL, 11, commentColor);
	}
	
	private void makeActions() {
		nameSetAction = new Action() {
		public void run(){
			InputDialog dialog = new InputDialog(null, 
					"Set Name",
					"Please enter a name for the new set:", "", null);
			dialog.open();
			String name = dialog.getValue();
			if ((name != null) && (name != "")) {
				NodeSet currentSet = NodeSetManager.instance().combinedSet();
				currentSet.name = name;
				currentSet.category = "named";
				NodeSetManager.instance().addSet(currentSet);
				Logger.instance().addEvent("Set Named " + "\t" + name);
			}
		}
	};
	
	nameSetAction.setToolTipText("Name this Set");  //change this for specified tooltip
	nameSetAction.setText("Name this Set");
	nameSetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));  //image of action

		
	}
	
	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(nameSetAction);
	}
}
