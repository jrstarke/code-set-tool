package ca.ucalgary.codesets.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;

import ca.ucalgary.codesets.models.INodeSetListener;
import ca.ucalgary.codesets.models.NodeSet;
import ca.ucalgary.codesets.models.NodeSetManager;

public class Logger implements INodeSetListener {

	private static Logger instance = new Logger();
	private BufferedWriter out;
	private PrintStream p;
	private File file;
	private Date lastDate = null;
	
	private Logger() { 
		NodeSetManager.instance().addListener(this);
		file = getFile(""+DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT, Locale.CANADA).format(Calendar.getInstance().getTime()) +".log");
	}
	
	// there is one global instance of this class
	public static Logger instance() {
		return instance;
	}
	
	/**
	 * Type of Actions: delimited by tabs
	 * DATE	Set Added		Name of Set				Category of Set   
	 * DATE	Double Clicked  Label double clicked 
	 * DATE	State Changed 	Set Name				State changed to
	 * DATE	Searched For	Search Entry
	 * DATE	All Sets		Ignored
	 * DATE	Set Named 		Name of set
	 * DATE All Sets		Removed
	 * DATE Comments Changed	level
	 * DATE Started
	 * DATE Stopped
	 * 
	 * @param action : A string representing the action
	 */
	public void addEvent(String action) {
		try {
			p = new PrintStream( new FileOutputStream(file ,true));

			if(lastDate!=null && Calendar.getInstance().getTime().getMinutes() > (lastDate.getMinutes()+5))
				p.println();
			p.println(Calendar.getInstance().getTime() + "\t" + action);
			
			lastDate = Calendar.getInstance().getTime();
			p.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}	
 	}
	
	File getFile(String name) {
		IPath path = Platform.getLocation();
		path = path.append("/" + name);
		File file = path.toFile();
		return file;
	}
	
	
	public void closeFile() {
		p.close();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAdded(NodeSet set) {
		Logger.instance().addEvent("Set Added"+  "\t" + set.name + "\t" + set.category);
	}

	public void stateChanged(NodeSet set) {
		if(!NodeSetManager.instance().allCleared)  //When a state change has occurred
			Logger.instance().addEvent("State Changed" + "\t" + set.name +"\t"+set.state.toString());
		else
			Logger.instance().addEvent("All Sets" + "\t" + "IGNORED");  //When the ignore sets button pressed
	}

	// The methods below here are auto generated methods required for the
	// INodeSetListener.  At this point, they are all stubs
	
	public void focusChanged(ISourceReference focus) {
	}
	
	public void setChanged(NodeSet set) {
	}

	public void statesCleared() {
	}

	public void focusChanged(IJavaElement focus) {
	}

	public void setRemoved(NodeSet set) {
	}
	
}
