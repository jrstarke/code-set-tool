package ca.ucalgary.codesets.sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashSet;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.ucalgary.codesets.ResultSets;
import ca.ucalgary.codesets.listeners.CodeSetListener;
import ca.ucalgary.codesets.listeners.SetListener;

public class CodeSet extends HashSet<SetNode> implements IStructuredContentProvider {

	protected CodeSetListener listener;
	protected boolean isActivated;
	protected SetListener listensToUs;
	protected Action action;
	
	protected String name;

	public CodeSet () {
		isActivated = false;
	}
	
	public void activate () {
		isActivated = true;
	}

	public void deactivate () {
		isActivated = false;
	}
	
	public boolean isActivated () {
		return isActivated;
	}
	
	public void changeListener (SetListener listens) {
		listensToUs = listens;
	}
	
	private void changed () {
		if (listensToUs != null)
			listensToUs.refresh(this);
	}
	
	/**
	 * Checks the hashSet to see if it contains the given ISourceReference, if it does contain it
	 * already, it will update the time the ISourceReference was added to the set. 
	 * If it does not contain the ISourceReference, then it will add a new SetNode.
	 * @param isr
	 * @return true
	 */
	public boolean add(ISourceReference isr) {		
		if(contains(isr))
			updateTime(isr);//change the date not add a new isr
		else
			super.add(new SetNode(isr));
		changed();
		return true;
	}
	
	/**
	 * This function searches through the hashSet for the give ISourceReference, and 
	 * when it finds it, it replaces (updates) the time to the current time.
	 * @param isr
	 */
	private void updateTime(ISourceReference isr) {
		java.util.Iterator<SetNode> i = iterator();
		
		while(i.hasNext()) {
			SetNode sn = i.next();
			if(sn.getIsourcereference() == isr) {
				sn.setTime(System.currentTimeMillis());
				return;
			}
		}
	}
	
	/**
	 * Checks if the provided ISourceReference already exists
	 * @param isr
	 * @return true if the ISourceReference exists
	 * @return false if the ISourceReference does not exist
	 */
	public boolean contains(ISourceReference isr) {
		SetNode node = new SetNode(isr);
		return contains(node);
	}
	
	
	/*
	 * Since the HashSet is a set of SetNodes, getting the elements will return an array 
	 * of SetNodes which will cause an error for the viewer.
	 * This copies the ISourceReferences from each node into a separate array and returns this array
	 * It also sorts the array using Arrays.sort() which uses the compareTo() method in the SetNode.
	 * This orders them chronologically
	 * non-JavaDoc
	 * @param parent
	 * @return isr -> the array of ISourceReferences
	 */
	public Object[] getElements(Object parent) {
		Object[] temp = toArray();
		Arrays.sort(temp);
		
		ISourceReference[]  isr = new ISourceReference[temp.length];
		for(int i = 0; i<isr.length;i++)
			isr[i] = ((SetNode)temp[i]).getIsourcereference();
		
		return isr;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setAction (Action action) {
		this.action = action;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
	
	public String toString () {
		return name;
	}

}
