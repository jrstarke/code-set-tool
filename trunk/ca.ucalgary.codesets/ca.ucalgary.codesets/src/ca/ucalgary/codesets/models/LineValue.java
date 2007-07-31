package ca.ucalgary.codesets.models;

public class LineValue {
	private String source;
	private Integer value;
	private int lineNumber;
	private int lastLineNumber;
	
	public LineValue (String source, int value, int line, int lastLine) {
		this.source = source;
		this.value = value;
		this.lineNumber = line;
		this.lastLineNumber = lastLine;
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
	
	public int number () {
		return lineNumber;
	}
	
	public int lastNumber() {
		return lastLineNumber;
	}
}
