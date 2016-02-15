package g4p_controls;

import g4p_controls.ZStyledText.LineInfo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextHitInfo;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import processing.core.PGraphicsJava2D;

public class ZParagraph {

	private final Graphics2D g2d;

	public ZStyledText st = new ZStyledText("");

	public float paraHeight = 0;

	public boolean selectionOn = false;

	public ZCaret startC = new ZCaret();
	public ZCaret endC = new ZCaret();

	public CaretInfo startCI = new CaretInfo();
	public CaretInfo endCI = new CaretInfo();

	Color scol = new Color(240,240,128);

	public ZParagraph(String s, int wrapwidth, Graphics2D g2d){
		st = new ZStyledText(s, wrapwidth);
		this.g2d = g2d;
		st.setFont(g2d.getFont());
		paraHeight = g2d.getFont().getSize();
	}

	public void setFont(Font baseFont){
		st.setFont(baseFont);
		paraHeight = baseFont.getSize();
	}

	public void clearSelection(){
		for(LineInfo line : getDisplayLines())
			line.selection = null;
	}

	/**
	 * 
	 * @param paraNbr the position this paragraph is in display >= 0
	 * @param start
	 * @param end
	 */
	public void setSelection(int paraNbr, ZCaret start, ZCaret end){
		clearSelection();
		// If this paragraph is empty ignore
		if(st.isEmpty())
			return;
		// Make sure we are using the latest formated text
		LinkedList<LineInfo> lines = getDisplayLines();
		startC.set(paraNbr, 0, 0);
		endC.set(paraNbr, lines.size() - 1, lines.get(lines.size() - 1).nbrChars - 1);
		endC.leadingEdge = false;

		ZCaret.max(startC, start, startC);
		ZCaret.min(endC, end, endC);

		getCaretInfo(startC.paraLineNbr, startC.charNbr, startC.leadingEdge, startCI);
		getCaretInfo(endC.paraLineNbr, endC.charNbr, endC.leadingEdge, endCI);

		float sx, ex;
		for(int i = 0; i < lines.size(); i++){
			LineInfo line = lines.get(i);
			if(i >= startC.paraLineNbr && i <= endC.paraLineNbr){
				sx = (i == startC.paraLineNbr) ? startCI.cx : 0;
				ex = (i == endC.paraLineNbr) ? endCI.cx : line.advance;
				lines.get(i).selection = new Rectangle2D.Float(sx, 0, ex - sx, line.baseline + line.leading);
			}
		}
	}

//	public void getCaretInfoXXX(int pln, int cn, CaretInfo ci){
//		getCaretInfo(pln, cn, true, ci);
//	}

	/**
	 * For this paragraph get the caret information.
	 * 
	 * @param pln paragraph line number
	 * @param cn character number in line
	 * @param le whether it is leading edge or not
	 * @param ci the caret information.
	 */
	public void getCaretInfo(int pln, int cn, boolean le, CaretInfo ci){
		// Move to the correct line
		LinkedList<LineInfo> lines = getDisplayLines();
		if(lines.isEmpty()){
			ci.cx = 0;
			ci.cy += 0.1f * g2d.getFont().getSize();
			ci.h = 0.9f * g2d.getFont().getSize();
			ci.valid = true;
		}
		else {
			LineInfo pli;
			for(int i = 0; i < pln; i++){
				pli = lines.get(i);
				ci.cy += pli.baseline + pli.leading;
			}
			// Get the text line info and calculate 
			pli = lines.get(pln);
			ci.cy += pli.descent;
			TextHitInfo  thi = (le) ? pli.layout.getNextLeftHit(cn+1) : pli.layout.getNextRightHit(cn);
			if(thi != null){
				float[] ccc = pli.layout.getCaretInfo(thi);
				ci.cx = ccc[0];
				ci.h = -ccc[3];
				ci.valid = true;
			}
			else {
				System.out.println("#################  " + cn);
				System.out.println("###### " + pli);
			}
		}
//		System.out.println(ci);
	}

	/**
	 * Get the line that contains the given position
	 * @param py
	 * @param caret
	 * @return
	 */
	public LineInfo getLinePixelY(float py){
		LinkedList<LineInfo> lines = st.getLines(g2d);
		for(LineInfo pli : lines){
			py -= pli.baseline;
			py -= pli.leading;
			if(py <= 0)
				return pli;
		}
		return null;
	}

	public LinkedList<LineInfo> getDisplayLines(){
		return st.getLines(g2d);
	}

	public float display(PGraphicsJava2D buffer, Graphics2D g2d) {
		paraHeight = 0;
		System.out.println(st.getPlainText());
		LinkedList<LineInfo> lines = st.getLines(g2d);
		for(LineInfo pli : lines){
			if(pli.selection != null){
				g2d.setColor(scol);
				g2d.fill(pli.selection);
			}
			g2d.setColor(Color.BLACK);
			paraHeight += pli.baseline;
			buffer.translate(0, pli.baseline);
			pli.layout.draw(g2d, 0, 0);
			buffer.translate(0, pli.leading);
			paraHeight += pli.leading;
		}
		// If empty paragrapgh insert blank line
		if(paraHeight == 0){
			paraHeight = g2d.getFont().getSize();
			buffer.translate(0, paraHeight);
			System.out.println("Lines " + lines.size());
		}
		return paraHeight;
	}
}

