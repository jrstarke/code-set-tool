package ca.ucalgary.codesets;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ViewSettingsDialog;

import ca.ucalgary.codesets.sets.CodeSet;



public class CodeSetPreferences extends MessageDialog {

	private ArrayList<CodeSet> sets;
	private String[] buttonNames = {"History Set", "Change Set"};
	private ArrayList<CodeSet> modified = new ArrayList<CodeSet>();
	
	public CodeSetPreferences (Shell parentShell, ArrayList<CodeSet> sets) {
		super( parentShell,
                "Set Preferences",
                null,
                "Select the sets you would like to use.",
                0,
                new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
                0);
		this.sets = sets;
	}
	
	/**
	 * Modifies the preferences on buttons pressed.
	 * @param pButtonID The id for the button pressed.
	 */
	protected void buttonPressed( int pButtonID ) 
	{
		System.out.println(modified.size());
		if( pButtonID == 0 )
		{
			for (CodeSet m:modified) {
				if (m.isActivated())
					m.deactivate();
				else
					m.activate();
			}
		}
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
		for (CodeSet s:sets)
		{
			buttons.add(createToggleButton(parent, s));
		}
		return super.createCustomArea( parent );
	}
	
	protected Button createToggleButton(Composite parent, final CodeSet s) {
        final Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
        
        GridData data = new GridData(SWT.NONE);
        data.horizontalSpan = 2;
        button.setLayoutData(data);
        button.setFont(parent.getFont());
        button.setText(s.getAction().getText());
        button.setSelection(s.isActivated());

        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if (modified.contains(s)) {
                	modified.remove(s);
                }
                else
                	modified.add(s);
           }
        });

        return button;
	}

}
