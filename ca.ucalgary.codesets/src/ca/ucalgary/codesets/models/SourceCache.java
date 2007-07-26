package ca.ucalgary.codesets.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;

public class SourceCache {
	int INITIALLINEVALUE = 0;

	private HashMap<ISourceReference,LineValue[]> srcCache;

	public SourceCache () {
		srcCache = new HashMap<ISourceReference,LineValue[]>();
	}

	public int[] lineValues(ISourceReference isr) {
		int[] values = null;

		LineValue[] lineValue = srcCache.get(isr);
		if (lineValue != null) {
			values = new int[lineValue.length];
			for (int i = 0; i < lineValue.length; i++) {
				values[i] = lineValue[i].value();
			}
		}
		return values;
	}

	public void incrementPosition(ISourceReference isr, int position, int value) {
		try {
			if ((isr.getSourceRange().getOffset() <= position) && (position <= isr.getSourceRange().getOffset() + isr.getSourceRange().getLength())) {
				int isrOffset = isr.getSourceRange().getOffset();
				int offset = position - isrOffset;

				String source = isr.getSource();
				if (source != null) {
					int start = source.indexOf("{");
					offset = offset - (start + 1);

					source = source.substring(start + 1);

					int line = 0;
					String[] lines = source.split("\n");
					if (offset > 0) {
						while (line < lines.length && offset > 0) {
							String currentLine = lines[line];
							if (currentLine.length() < offset) {
								line++;
								offset -= (currentLine.length() + 1);
							}
							else
								offset -= offset;
						}
						incrementLine(isr,line,value);
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}


	public void incrementLine(ISourceReference isr, int line, int value) {
		LineValue[] source = srcCache.get(isr);
		if (source == null)
			source = cacheSource(isr);
		if (line < source.length)
			source[line].incrementValue(value);
	}

	public String[] source(ISourceReference isr, int numLines) {
		String[] sizedSource = null;
		if (numLines > 0) {
			LineValue[] source = get(isr);
			if (source != null) {  //If this element Actually has sourcecode available
				ArrayList<LineValue> output = new ArrayList<LineValue>();
				for (LineValue l:source) { //Get the 'Most Important' lines of code to numLines size
					if (output.size() == numLines) {
						if (smallestValue(output) < l.value()) {
							makeRoom(output);
							output.add(l);
						}			
					}
					else
						output.add(l);
				}
				sizedSource = new String[output.size()];
				for (int i = 0; i < output.size(); i++) {
					sizedSource[i] = output.get(i).source();
				}
			}
		}
		return sizedSource;
	}

	private LineValue[] cacheSource(ISourceReference isr) {
		String rawSource = null;
		LineValue[] formattedSource = null;

		try {
			rawSource = isr.getSource();
			if (rawSource != null) {
				int startBody = rawSource.indexOf("{");
				int endBody = rawSource.lastIndexOf("}");
				if ((startBody >0) && (endBody >2)) {
					rawSource = rawSource.substring(startBody +1, endBody -1);
				}
				else rawSource = rawSource.replace("{", "").replace("}", "");
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		if (rawSource != null) {
			String[] temp = rawSource.replace("\t\t", "\t").split("\n");
			formattedSource = new LineValue[temp.length];
			for (int i = 0; i < temp.length; i++) {
				formattedSource[i] = new LineValue(temp[i],INITIALLINEVALUE);
			}
		}
		srcCache.put(isr, formattedSource);
		return formattedSource;
	}

	private Integer smallestValue(ArrayList<LineValue> lv) {
		Integer smallest = null;
		if (lv.size() > 0)
			for (LineValue l:lv) {
				if (smallest == null)
					smallest = l.value();
				else if (l.value() < smallest)
					smallest = l.value();
			}
		return smallest;
	}

	private void makeRoom(ArrayList<LineValue> output) {
		Integer smallestNum = null;
		LineValue smallestObject = null;
		for (LineValue l:output) {
			if (smallestNum != null) {
				if (l.value() <= smallestNum) {
					smallestNum = l.value();
					smallestObject = l;
				}
			} else {
				smallestNum = l.value();
				smallestObject = l;
			}
		}
		output.remove(smallestObject);
	}

	public void updateLineValues(CodeSet s) {
		for (ISourceReference isr:s) {
			int[] values = s.srcCache.lineValues(isr);
			if (values != null)
				for (int i = 0; i < values.length; i++) {
					incrementLine(isr, i, values[i]);
				}
		}
	}

	private LineValue[] get (ISourceReference isr) {
		LineValue[] tempSource = srcCache.get(isr);
		if (tempSource == null)
			tempSource = cacheSource(isr);

		ArrayList<LineValue> cleanedSource = new ArrayList<LineValue>();
		if (tempSource != null) {
			for (LineValue l:tempSource) {
				if (l.source().replace("{", "").replace("}", "").trim().length() > 0)
					cleanedSource.add(new LineValue(l.source().replace("\t","    "),l.value()));
			}
		}

		LineValue[] output = new LineValue[cleanedSource.size()];
		for (int i = 0; i < output.length; i++) {
			output[i] = cleanedSource.get(i);
		}
		return output;
	}
}
