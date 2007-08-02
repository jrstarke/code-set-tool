package ca.ucalgary.codesets.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ISourceReference;
import org.osgi.framework.Bundle;

import ca.ucalgary.codesets.models.CodeSet;
import ca.ucalgary.codesets.models.CodeSetManager;
import ca.ucalgary.codesets.models.ICodeSetListener;

public class Logger implements ICodeSetListener {

	private static Logger instance = new Logger();
	private BufferedWriter out;
	private PrintStream p;
	private File file;
	private Date lastDate = null;
	
	private Logger() { 
		CodeSetManager.instance().addListener(this);
		file = new File("/Users/logs/"+DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT, Locale.CANADA).format(Calendar.getInstance().getTime()) +".txt");
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
	 * DATE	Scale Changed	from #	  		to 		to #
	 * DATE	Searched For	Search Entry
	 * DATE	All Sets		Ignored
	 * DATE	Set Named 		Name of state
	 * 
	 * @param action
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
		Bundle bundle = Platform.getBundle("ca.ucalgary.codesets");
		Path path = new Path("logs/" + name);
		URL url = FileLocator.find(bundle,path,null);
		return new File(url.getFile());
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

	public void focusChanged(ISourceReference focus) {
		// TODO Auto-generated method stub
	}

	public void setAdded(CodeSet set) {
		// TODO Auto-generated method stub
		Logger.instance().addEvent("Set Added"+  "\t" + set.name + "\t" + set.category);
	}

	public void setChanged(CodeSet set) {
		// TODO Auto-generated method stub
		
	}

	public void stateChanged(CodeSet set) {
		// TODO Auto-generated method stub
		if(!CodeSetManager.instance().allCleared)
			Logger.instance().addEvent("State Changed" + "\t" + set.name +"\t"+set.state.toString());
	}

	public void statesCleared() {
		// TODO Auto-generated method stub
		Logger.instance().addEvent("All sets IGNORED");
	}
	
}
