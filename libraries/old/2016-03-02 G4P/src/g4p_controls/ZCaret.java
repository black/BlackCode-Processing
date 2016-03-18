package g4p_controls;

/**
 * Defines a position in multi-paragraph text
 * 
 * @author Peter Lager
 *
 */
class ZCaret implements Comparable<ZCaret> {
	public int paraNbr, paraLineNbr, charNbr;	
	public boolean leadingEdge = true;
	public boolean valid = false;

	/**
	 * Determine which is the 'first' caret.
	 * 
	 * @param c0 a caret position
	 * @param c1 a caret position
	 * @param result the resulting caret
	 * @return the 'first' caret
	 */
	public static ZCaret min(ZCaret c0, ZCaret c1, ZCaret result){
		if(result == null)
			result = new ZCaret();
		if(c0.compareTo(c1) <= 0)
			result.copyFrom(c0);
		else
			result.copyFrom(c1);
		return result;
	}
	
	/**
	 * Determine which is the 'second' caret.
	 * 
	 * @param c0 a caret position
	 * @param c1 a caret position
	 * @param result the resulting caret
	 * @return the 'second' caret
	 */
	public static ZCaret max(ZCaret c0, ZCaret c1, ZCaret result){
		if(result == null)
			result = new ZCaret();
		if(c0.compareTo(c1) >= 0)
			result.copyFrom(c0);
		else
			result.copyFrom(c1);
		return result;
	}
	

	public ZCaret() {
		super();
	}

	public ZCaret(int paraNbr, int paraLineNbr, int charNbr) {
		super();
		this.paraNbr = paraNbr;
		this.paraLineNbr = paraLineNbr;
		this.charNbr = charNbr;
	}

	public void set(int paraNbr, int paraLineNbr, int charNbr) {
		this.paraNbr = paraNbr;
		this.paraLineNbr = paraLineNbr;
		this.charNbr = charNbr;
	}

	public void set(int paraNbr, int paraLineNbr, int charNbr, boolean leadingEdge) {
		this.paraNbr = paraNbr;
		this.paraLineNbr = paraLineNbr;
		this.charNbr = charNbr;
		this.leadingEdge = leadingEdge;
	}


	public void copyFrom(ZCaret c){
		paraNbr = c.paraNbr;
		paraLineNbr = c.paraLineNbr;
		charNbr = c.charNbr;
	}

	public boolean equals(ZCaret c){
		return paraNbr == c.paraNbr && paraLineNbr == c.paraLineNbr && charNbr == c.charNbr;
	}

	@Override
	public int compareTo(ZCaret c) {
		if(paraNbr != c.paraNbr)
			return (paraNbr < c.paraNbr) ? -1 : 1;
		if(paraLineNbr != c.paraLineNbr)
			return (paraLineNbr < c.paraLineNbr) ? -1 : 1;
		if(charNbr != c.charNbr)
			return (charNbr < c.charNbr) ? -1 : 1;
		if(leadingEdge ^ c.leadingEdge)
			return leadingEdge ? -1 : 1;
		return 0;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("Caret\t");
		sb.append("Para\t" + paraNbr + "\t");
		sb.append("Line\t" + paraLineNbr + "\t");
		sb.append("Char\t" + charNbr + "\t");
		sb.append("Valid\t" + valid);
		return sb.toString();
	}
}


class CaretInfo {
	public float cx, cy, h;
	public boolean valid = false;

	public void clear() {
		cx = cy = h = 0;
		valid = false;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder("Caret Info\t");
		sb.append("X\t" + cx + "\t");
		sb.append("Y\t" + cy + "\t");
		sb.append("H\t" + h);
		return sb.toString();
	}
}

class Selection {
	public ZCaret start = new ZCaret();
	public ZCaret end = new ZCaret();
	
	public Selection(ZCaret start, ZCaret end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public boolean isValid(){
		return start.valid & end.valid;
	}
	
}