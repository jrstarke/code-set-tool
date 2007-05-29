package ca.ucalgary.codesets.listeners;

import java.util.ArrayList;
import java.util.EventListener;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

public class PartListener implements IPartListener {

	static IWorkbenchPart currentPart;
	static ArrayList<IWorkbenchPart> parts = new ArrayList<IWorkbenchPart>();

	private static ArrayList<CodeSetListener> listeners = new ArrayList<CodeSetListener>();

	public void partActivated(IWorkbenchPart part) {
		if (part instanceof JavaEditor) {
			parts.add(part);
			for (CodeSetListener l:listeners) {
				l.register((JavaEditor)part);
			}
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {
	}

	public void partClosed(IWorkbenchPart part) {

	}

	public void partDeactivated(IWorkbenchPart part) {
		if (part instanceof JavaEditor) {
			parts.remove(part);
			for (CodeSetListener l:listeners) {
				l.unregister((JavaEditor)part);
			}
		}
	}

	public void partOpened(IWorkbenchPart part) {

	}

	public static void addListener(CodeSetListener listener) {
		listeners.add(listener);
		for (IWorkbenchPart p:parts) {
			listener.register((JavaEditor)p);
		}
	}

	public static void removeListener(CodeSetListener listener) {
		listeners.remove(listener);
		for (IWorkbenchPart p:parts) {
			listener.unregister((JavaEditor)p);
		}
	}

	public ArrayList<CodeSetListener> getListeners () {
		return listeners;
	}
};

