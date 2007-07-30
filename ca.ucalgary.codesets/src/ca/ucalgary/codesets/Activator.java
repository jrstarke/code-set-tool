package ca.ucalgary.codesets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ca.ucalgary.codesets.models.DebugEventListener;
import ca.ucalgary.codesets.models.EditorFocusListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.ucalgary.codesets";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// start listening to editor events 
		IEditorPart part = null; //= getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		IWorkbench workbench = getWorkbench();
		if(workbench != null) {
			IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
			if(activeWindow != null){
				IWorkbenchPage iwp = activeWindow.getActivePage();
				if(iwp != null){
					part = iwp.getActiveEditor();
				}
			}
		}
		
		
//		new EditorFocusListener(part);
		
//		if (part != null)
			getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new EditorFocusListener(part));

		// start listening to debug events
		new DebugEventListener();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
