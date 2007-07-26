package ca.ucalgary.codesets.models;

public class LineValue {
	private String source;
	private Integer value;
	
	public LineValue (String source, int value) {
		this.source = source;
		this.value = value;
	}
	
	public void incrementValue(int value) {
		this.value += value;
	}
	
	public int value () {
		return value;
	}
	
	public String source () {
		return source;
	}
}
