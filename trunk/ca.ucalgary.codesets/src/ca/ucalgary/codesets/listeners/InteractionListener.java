package ca.ucalgary.codesets.listeners;

import java.util.ArrayList;
import java.util.HashMap;

//import org.eclipse.mylar.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.mylar.monitor.core.IInteractionEventListener;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;
import org.eclipse.mylar.context.core.*;

import ca.ucalgary.codesets.ResultSet;
import ca.ucalgary.codesets.sets.CodeSet;
import ca.ucalgary.codesets.views.CodeSetView;

public class InteractionListener implements IInteractionEventListener {
	
	private static ResultSet referenceTo;
	private static ResultSet referenceFrom;
	private static CodeSetView view;
	private static InteractionListener thisListener;
	private static HashMap<InteractionEvent.Kind,ArrayList<CodeSetListener>> listenerKinds;

	/**
	 * Creates a new InteractionListener.  The interaction Listener is like an event bus
	 * which allows several listeners to listen to different kinds of events
	 *
	 */
	public InteractionListener() {
		initialize();
	}

	/**
	 * Initializes the InteractionListeners listenerKinds Arrays 
	 *
	 */
	private void initialize () {
		referenceTo = new ResultSet();
		referenceFrom = new ResultSet();
		
		referenceTo.changeListener(view);
		referenceTo.setName("References To");
		referenceFrom.changeListener(view);
		referenceFrom.setName("References From");
		
		listenerKinds = new HashMap<InteractionEvent.Kind, ArrayList<CodeSetListener>>();
		for(InteractionEvent.Kind kind: InteractionEvent.Kind.values()) {
			listenerKinds.put(kind,new ArrayList<CodeSetListener>());
		}
		MylarMonitorUiPlugin.getDefault().addInteractionListener(this);
	}

	/**
	 * Is notified when an interaction occurs.  This method then determines the JavaElement which
	 * participated in the event, and notifies any interested parties
	 */
	public void interactionObserved(InteractionEvent event) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(event.getStructureKind());
		Object theObject = bridge.getObjectForHandle(event.getStructureHandle());
		System.out.println(event.getKind() + ": " + event.getDate()); 
		if (theObject instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) theObject;
			IJavaModel model = element.getJavaModel();
			for (CodeSetListener listener: listenerKinds.get(event.getKind())) {
				listener.eventOccured(element);
			}
		}
	}

	/**
	 * Tells this class to start listening to the Mylar Monitor to keep track of events that occur
	 * and notify any interested parties
	 */
	public void startMonitoring () {
	}

	/**
	 * Stops listening to the Mylar Monitor
	 */
	public void stopMonitoring () {
	}

	/**
	 * Adds a specific CodeSetListener to a specific InteractionEvent.Kind 
	 * @param kind
	 * @param listener
	 */
	public static void addListener(InteractionEvent.Kind kind, CodeSetListener listener) {
		checkActivated();
		ArrayList<CodeSetListener> listeners = listenerKinds.get(kind);
		listeners.add(listener);
	}

	/**
	 * Adds a specific CodeSetListener to All InteractionEvent.Kind
	 * @param listener
	 */
	public static void addListener(CodeSetListener listener) {
		checkActivated();
		for (InteractionEvent.Kind kind:InteractionEvent.Kind.values()) {
			addListener(kind, listener);
		}
	}

	/**
	 * Removes a specific CodeSetListener from All InteractionEvent.Kind
	 * @param listener
	 */
	public static void removeListener (CodeSetListener listener) {
		checkActivated();
		for (ArrayList<CodeSetListener> listeners: listenerKinds.values()) {
			listeners.remove(listener);
		}
	}
	
//	public static void setResultSets (ResultSets resultSets) {
//		InteractionListener.resultSets = resultSets;
//	}
	
	public static void addReferenceTo (CodeSet set) {
		InteractionListener.referenceTo.add(set);
	}
	
	public static void addReferenceFrom (CodeSet set) {
		InteractionListener.referenceFrom.add(set);
	}
	
public static CodeSet getReferenceFrom (String name) {
		return InteractionListener.referenceFrom.get(name);
	}
	
	public static ResultSet getReferenceFrom () {
		return InteractionListener.referenceFrom;
	}
	
	public static ResultSet getReferenceTo () {
		return InteractionListener.referenceTo;
	}
	
	public static void checkActivated() {
		if (thisListener == null)
			thisListener = new InteractionListener();
	}
		
	public static void setView (CodeSetView view) {
		InteractionListener.view = view;
	}
}