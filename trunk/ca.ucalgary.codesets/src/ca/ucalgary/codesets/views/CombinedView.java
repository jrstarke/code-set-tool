package ca.ucalgary.codesets.views;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import ca.ucalgary.codesets.controllers.CombinedViewController;
import ca.ucalgary.codesets.controllers.Logger;
import ca.ucalgary.codesets.models.NodeSet;
import ca.ucalgary.codesets.models.NodeSetManager;

//this view is the main presentation of the currently applicable set of ASTNode's.
public class CombinedView extends ViewPart  {
	Action nameSetAction;
	Action incDefAction;
	Action decDefAction;
	CombinedViewController cvc;
	static ElementLabelProvider labelProvider = new ElementLabelProvider();

	public void createPartControl(Composite parent) {
		cvc = new CombinedViewController(parent);
		makeActions();
		createToolbar();
	}

	public void setFocus() {
	}

	// static methods and fields for creating the various widgets and composites used
	// to display node sets

	static Color methodColor = new Color(null, 140,140,140);
	static Color commentColor = new Color(null, 175,140,140);
	static Color methodNameColor = new Color(null, 0,0,0);
	static Color classNameColor = methodNameColor; //new Color(null, 100,100,100);
	static Color classBGColor = new Color(null, 250,250,200);

	static Label label(Composite parent, String text, int style, int height, Color color) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		fontStyle(label, style, height);
		label.setForeground(color);
		return label;
	}

//	static Composite iconAndText(Composite parent, String text, Image i) {
//		Composite result = new Composite(parent, SWT.NONE);
//		Label icon = new Label(result, SWT.NONE);
//		label(result, text, SWT.BOLD, 11, classNameColor);
//		icon.setImage(i);
//		return result;
//	}
	
	static void fontStyle(Control widget, int style, int height) {
		Font f = widget.getFont();
		FontData[] fd = f.getFontData();
		fd[0].setStyle(style);
		fd[0].setHeight(height);
		widget.setFont(new Font(f.getDevice(), fd));
	}

	public static Composite classView(Composite parent, IType element, String comment) { //, Listener listener) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.spacing = 0;
		result.setLayout(layout);
		String text = labelProvider.getText(element);
		Label label = label(result, text, SWT.BOLD, 11, classNameColor); //.addListener(SWT.MouseDoubleClick, listener);
//		label.setBackground(classBGColor);
		return result;
	}
	public static Composite methodView(Composite parent, String text, Listener listener) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
//		layout.marginWidth = 10;
		layout.marginLeft = 12;
		layout.spacing = 0;
		result.setLayout(layout);
		label(result, text, SWT.NORMAL, 11, methodNameColor).addListener(SWT.MouseDoubleClick, listener);
		return result;
	}
	public static Composite fieldView(Composite parent, String text, Listener listener) {
		Composite result = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
//		layout.marginWidth = 0;
		layout.marginLeft = 12;
		layout.spacing = 0;
//		layout.fill = true;
		result.setLayout(layout);
		label(result, text, SWT.NORMAL, 11, methodNameColor).addListener(SWT.MouseDoubleClick, listener);
		return result;
	}
	public static Widget methodBodyWidget(Composite parent, String text, Listener listener) {
		Label label = label(parent, text, SWT.NORMAL, 10, methodColor);
		label.addListener(SWT.MouseDoubleClick, listener);
		return label;
	}
	public static Widget commentLabel(Composite parent, String text, Listener listener){
		Label label = label(parent, text, SWT.ITALIC, 10, commentColor);
		if(listener != null)
			label.addListener(SWT.MouseDoubleClick, listener);
		return label;
	}

	private void makeActions() {
		nameSetAction = new Action() {
			public void run(){
				InputDialog dialog = new InputDialog(null, 
						"Set Name",
						"Please enter a name for the new set:", "", null);
				dialog.open();
				String name = dialog.getValue();
				if(name != null){
					name = name.trim();
					if ((name != null) && (name != "")) {
						NodeSet currentSet = NodeSetManager.instance().combinedSet();
						currentSet.name = name;
						currentSet.category = "named";
						NodeSetManager.instance().addSet(currentSet);
						Logger.instance().addEvent("Set Named " + "\t" + name);
					}
				}
			}
		};

		nameSetAction.setToolTipText("Name this Set");  //change this for specified tooltip
		nameSetAction.setText("Name this Set");
		nameSetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(org.eclipse.ui.internal.IWorkbenchGraphicConstants.IMG_ETOOL_SAVE_EDIT));

		incDefAction = new Action() {
			public void run() {
				cvc.incLevel();
			}
		};
		incDefAction.setToolTipText("Increase Detail");
		incDefAction.setText("Increase Detail");
		incDefAction.setImageDescriptor(getImageDescriptor("plustool.png")); //("incdetail.png")); 

		decDefAction = new Action() {
			public void run() {
				cvc.decLevel();
			}
		};
		decDefAction.setToolTipText("Decrease Detail");
		decDefAction.setText("Decrease Detail");
		decDefAction.setImageDescriptor(getImageDescriptor("minustool.png")); //("decdetail.png"));
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(decDefAction);
		mgr.add(incDefAction);
		mgr.add(nameSetAction);
	}

	public static Label infoLabel(Composite parent, String text) {
		return label(parent, text, SWT.NORMAL, 11, commentColor);
	}

	private ImageDescriptor	getImageDescriptor(String name) {
		Bundle bundle = Platform.getBundle("ca.ucalgary.codesets");
		Path path = new Path("icons/" + name);
		URL url = FileLocator.find(bundle, path, null);
		return ImageDescriptor.createFromURL(url);
	}
}
