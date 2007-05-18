package ca.ucalgary.codesets;

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



public class CodeSetPreferences extends MessageDialog {

	private String[] buttonNames = {"History Set", "Change Set"};
	private boolean[] values = {true, false};
	
	public CodeSetPreferences (Shell parentShell) {
		super( parentShell,
                "Set Preferences",
                null,
                "Select the sets you would like to use.",
                0,
                new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
                0);
	}
	
//	/**
//	 * Modifies the preferences on buttons pressed.
//	 * @param pButtonID The id for the button pressed.
//	 */
//	protected void buttonPressed( int pButtonID ) 
//	{
//		if( pButtonID == 0 )
//		{
//			ConcernMapper.getDefault().getPreferenceStore().
//				setValue( ConcernMapperPreferencePage.P_FILTER_ENABLED, getToggleState());
//			ConcernMapper.getDefault().getPreferenceStore().
//				setValue( ConcernMapperPreferencePage.P_FILTER_THRESHOLD, new Integer( aThresholdSlider.getSelection()).toString() );
//		}
//		close();
//	}

	/**
	 * Builds the custom layout for this dialog.
	 * @param pParent The parent widget.
	 * @return The control.
	 */
	protected Control createCustomArea( Composite pParent )
	{
		Button[] buttons = new Button[buttonNames.length];
		for (int i = 0; i < buttonNames.length; i++)
		{
			buttons[i] = createToggleButton(pParent, buttonNames[i], values[i], i);
		}
		return super.createCustomArea( pParent );
	}
	
	protected Button createToggleButton(Composite parent, String buttonDescription, Boolean toggleState, final int i) {
        final Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
        
        GridData data = new GridData(SWT.NONE);
        data.horizontalSpan = 2;
        button.setLayoutData(data);
        button.setFont(parent.getFont());
        button.setText(buttonNames[i]);
        button.setSelection(values[i]);

        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                values[i] = button.getSelection();
           }

        });

        return button;
	}

}
