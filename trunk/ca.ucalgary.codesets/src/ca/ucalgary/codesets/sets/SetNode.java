package ca.ucalgary.codesets.sets;

import org.eclipse.jdt.core.ISourceReference;

public class SetNode implements Comparable {
	
	private ISourceReference isourcereference;
	private Long time;
	
	public SetNode(ISourceReference isr) {
		this.isourcereference = isr;
		this.time = System.currentTimeMillis();
	}

	public ISourceReference getIsourcereference() {
		return isourcereference;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
	
	public int hashCode() {
		return this.isourcereference.hashCode();
	}
	
	public boolean equals(Object other) {
		if (other instanceof SetNode)
			return this.isourcereference.equals(((SetNode)other).isourcereference);
		return false;
	}
	
	public int compareTo(Object element) throws ClassCastException {
	    if (!(element instanceof SetNode))
	      throw new ClassCastException("SetNode Expected");
	    Long thisTime = ((SetNode)element).getTime();
	    
	    if(time > thisTime)
	    	return -1;
	    if(time < thisTime)
	    	return 1;
	    return 0;
	  }
}
