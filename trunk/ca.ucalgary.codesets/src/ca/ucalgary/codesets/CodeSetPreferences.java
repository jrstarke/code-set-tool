package ca.ucalgary.codesets;

import java.util.ArrayList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ca.ucalgary.codesets.sets.CodeSet;

/**
 * The CodeSetPreferences a set of code designed to allow the selection, and activation or deactivation
 * of a given set or sets.
 * @author starkej
 *
 */
public class CodeSetPreferences extends MessageDialog {

	private ArrayList<CodeSet> sets;
	
	/**
	 * CodeSetPreferences is used for controlling all of the active sets that run at a given
	 * time.  
	 * @param parentShell
	 * @param sets
	 */
	public CodeSetPreferences (Shell parentShell, ArrayList<CodeSet> sets) {
		super( parentShell,
                "Set Preferences",
                null,
                "Select the sets you would like to use.",
                0,
                new String[]{"Done"},
                0);
		this.sets = sets;
	}
	
	/**
	 * Sets the function for the done button to close the window
	 * @param pButtonID The id for the button pressed.
	 */
	protected void buttonPressed( int pButtonID ) 
	{
		close();
	}

	/**
	 * Builds the custom layout for this dialog.
	 * @param pParent The parent widget.
	 * @return The control.
	 */
	protected Control createCustomArea( Composite parent )
	{
		ArrayList<Button> buttons = new ArrayList<Button>();
		//Create a toggle for each set
		for (CodeSet s:sets)
		{
			buttons.add(createToggleButton(parent, s));
		}
		return super.createCustomArea( parent );
	}
	
	/**
	 * Toggles the activated state of a set.  For instance, if a set is activated, it will
	 * deactivate that set
	 * @param set
	 */
	protected void toggle(CodeSet set) {
		if (set.isActivated())
			set.deactivate();
		else
			set.activate();
	}
	
	protected Button createToggleButton(Composite parent, final CodeSet s) {
        Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
        
        GridData data = new GridData(SWT.NONE);
        data.horizontalSpan = 2;
        button.setLayoutData(data);
        button.setFont(parent.getFont());
        button.setText(s.getAction().getText());
        button.setSelection(s.isActivated());

        // Listens for selection of widgets, in this case, ToggleButtons
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	toggle(s);
           }
        });

        return button;
	}

}
