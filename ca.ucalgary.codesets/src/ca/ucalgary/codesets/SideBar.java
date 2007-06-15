package ca.ucalgary.codesets;

import java.util.HashMap;

import javax.swing.event.HyperlinkListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.*;

import ca.ucalgary.codesets.listeners.InteractionListener;
import ca.ucalgary.codesets.listeners.LinkListener;
import ca.ucalgary.codesets.listeners.SetListener;
import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.views.CodeSetView;

public class SideBar implements SetListener {

	Color backgroundColor = null; //new Color(null,245,245,250);
	HashMap<Object,Form> forms;
	
	CodeSetView theView;
	
	Composite container;
	FormToolkit toolkit;
	GridLayout mainLayout;
	GridLayout subLayout;
	
	public SideBar (Composite parent, CodeSetView theView, CodeSet history, CodeSet edit) {
		
		initialize(parent);
		this.theView = theView;
		createSet(history);
		createSet(edit);
		InteractionListener.getReferenceFrom().changeListener(this);
		createSet(InteractionListener.getReferenceFrom());
		createSet(InteractionListener.getReferenceTo());
		
	}
	
	public void initialize (Composite parent) {
		
		forms = new HashMap<Object,Form>();
		
		mainLayout = new GridLayout();
		mainLayout.verticalSpacing = 20;
		mainLayout.marginLeft = 0;
		mainLayout.numColumns = 1;
		
		subLayout = new GridLayout();
		subLayout.verticalSpacing = 0;
		subLayout.marginLeft = 0;
		subLayout.numColumns = 1;
		
		container = new Composite(parent, SWT.V_SCROLL);
		container.setLayout(mainLayout);
		container.setBackground(backgroundColor);
		
		toolkit = new FormToolkit(container.getDisplay());
		toolkit.setBackground(backgroundColor);
	}
	
	public void createSet (Object set) {
		
		Form setpanel = toolkit.createForm(container);
		forms.put(set, setpanel);
		int hashCode = set.hashCode();
		
		setpanel.setBackground(backgroundColor);
		setpanel.getBody().setLayout(subLayout);
		
		createContents(set,setpanel);
	}
		
	public void createContents (Object set, Form setpanel) {	
		if (set instanceof CodeSet) {
			CodeSet codeSet = (CodeSet) set;
			
			Hyperlink link = toolkit.createHyperlink(setpanel.getBody(), codeSet.getName(), SWT.NONE);
			link.addHyperlinkListener(new LinkListener(theView));
			link.setHref(codeSet);
		}
		else if (set instanceof ResultSet) {
			ResultSet resultSet = (ResultSet) set;
			
			setpanel.setText(resultSet.getName());
			for (CodeSet c:resultSet) {
				Hyperlink link = toolkit.createHyperlink(setpanel.getBody(), c.getName(), SWT.NONE);
				link.addHyperlinkListener(new LinkListener(theView));
				link.setHref(c);
			}
			Control[] children = setpanel.getChildren();
			System.out.println();
		}
	}

	public void refresh(Object s) {
		Form form = forms.get(s);
		
		if (form != null) {
			for (Control c: form.getBody().getChildren()) {
				c.dispose();
			}
			Control[] children = form.getBody().getChildren();
			createContents(s,form);
			form.update();
			children = form.getBody().getChildren();
			form.getDisplay().update();
		}
		container.redraw();
	}
}
