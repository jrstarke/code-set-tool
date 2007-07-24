package ca.ucalgary.codesets.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.IWorkingCopyManager;

// Listens to debug events and creates a CodeSet for each debugging session
public class DebugEventListener implements IDebugEventSetListener {
	CodeSet set;
	
	public DebugEventListener() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}
	
	// called for each set of debug events
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			try {
				DebugEvent event = events[i];
				Object source = event.getSource();

				// there are three kinds of events that we care about (so far) ...
				
				if (event.getKind() == DebugEvent.CREATE && source instanceof IProcess) {
					// start recording new set
					String name = new SimpleDateFormat("hh:mm a").format(new Date());
					set = new CodeSet(name, "debugging session");

				} else if (event.getKind() == DebugEvent.SUSPEND && 
						event.getDetail() == DebugEvent.BREAKPOINT && source instanceof IThread) {
					// add entity from each stack frame to set
					IStackFrame[] frames = ((IThread)source).getStackFrames();
					for (int j = 0; j < frames.length; j++)
						set.add((ISourceReference)getSourceReference(frames[j]));
					
				} else if (event.getKind() == DebugEvent.TERMINATE && source instanceof IProcess) {
					// session is ending so add set to code manager
					if (set.size() != 0)
						CodeSetManager.instance().addSet(set);
				}
			} catch (DebugException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	// returns the character position at the start of the given line number
	static int getPosition(String source, int lineNumber) {
		int lines = 0;
		for (int i = 0; i < source.length(); i++) {
			if (lines == lineNumber - 1)
				return i;
			if (source.charAt(i) == '\n')
				lines++;
		}
		
		return -1;
	}
	
	// computes and returns the java element corresponding to the given frame
	IJavaElement getSourceReference(IStackFrame frame) {
		try {
			ISourceLocator locator = frame.getLaunch().getSourceLocator();
			Object source = locator.getSourceElement(frame);
			
			if (source instanceof IFile) {
				ICompilationUnit unit = (ICompilationUnit) JavaCore.create((IFile) source);
				return EditorFocusListener.getElementAt(unit, 
					getPosition(unit.getSource(), frame.getLineNumber()), false);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
