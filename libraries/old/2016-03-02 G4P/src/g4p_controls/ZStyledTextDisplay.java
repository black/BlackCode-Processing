package g4p_controls;

import g4p_controls.ZStyledText.LineInfo;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextHitInfo;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

/**
 * This will ultimately replace GTextArea
 * 
 * @author Peter Lager
 *
 */
public class ZStyledTextDisplay {

	// Display width and height
	private int dx, dy;
	private int dw, dh;

	public PGraphicsJava2D	buffer;
	private Graphics2D g2d;

	// Position of top left corner of first character relative to display top-left corner
	// always <= 0
	private float pxlOffsetX = 0, pxlOffsetY = 0;

	private LinkedList<ZParagraph> paragraphs = new LinkedList<ZParagraph>();

	private Font font;

	private int wrapWidth = Integer.MAX_VALUE;

	private boolean bufferInvalid = true;

	private ZCaret startCaret = new ZCaret(), endCaret = new ZCaret();

	public ZStyledTextDisplay(PApplet app, int dx, int dy,  int dw, int dh, int wrapwidth, Font font) {
		super();
		this.dx = dx;
		this.dy = dy;
		this.dw = Math.max(10, dw);
		this.dh = Math.max(10, dh);
		buffer = (PGraphicsJava2D) app.createGraphics(this.dw, this.dh, PApplet.JAVA2D);
		buffer.smooth();
		g2d = buffer.g2;
		this.wrapWidth = wrapwidth;
		this.font = font;
		g2d.setFont(this.font);
	}

	/**
	 * 
	 * @param pn0
	 * @param pln0
	 * @param cn0
	 * @param pn1
	 * @param pln1
	 * @param cn1
	 */
	public void setSelection(int pn0, int pln0, int cn0, int pn1, int pln1, int cn1){
		startCaret.set(pn0, pln0, cn0);
		endCaret.set(pn1, pln1, cn1);
		for(int i = pn0; i <= pn1; i++){
			paragraphs.get(i).setSelection(i, startCaret, endCaret);
		}
		bufferInvalid = true;
	}
	
	public void setText(String text){
		String[] paras = text.split("\n");
		for(String s : paras){
			ZParagraph p = new ZParagraph(s, wrapWidth, g2d);
			paragraphs.add(p);
		}
		bufferInvalid = true;
	}

	public void setText(String[] paras){
		for(String s : paras){
			ZParagraph p = new ZParagraph(s, wrapWidth, g2d);
			paragraphs.add(p);
		}
		bufferInvalid = true;
	}

	public void updateBuffer(){
		if(bufferInvalid) {
			System.out.println("Buffer updating");
			buffer.beginDraw();	
			//buffer.background(240);	
			buffer.clear();	
			buffer.translate(pxlOffsetX, pxlOffsetY);
			for(ZParagraph p : paragraphs) {
				p.display(buffer, g2d);
			}
			buffer.endDraw();
			bufferInvalid = false;
		}
	}

	public void draw(PApplet app){
		updateBuffer();
		app.pushMatrix();
		app.pushStyle();
		app.translate(dx, dy);
		app.image(buffer, 0, 0);
		// Get details of the caret position and draw it. caret
		if(endCaret.valid){
			CaretInfo ci = getCaretInfo(endCaret, null);
			if(ci.valid){
				float x = ci.cx + pxlOffsetX;
				float y = ci.cy + pxlOffsetY;
				app.fill(255,0,0);
				app.noStroke();
				app.rect(x, y, 1,ci.h);
			}
		}
		drawInfo(app, dx, dy + dh + 20);
		app.popStyle();
		app.popMatrix();	
	}

	public void drawInfo(PApplet app, int px, int py){
		app.pushMatrix();
		app.pushStyle();
		app.translate(px, py);
		app.fill(0);
		app.textSize(20);
		app.text(endCaret.charNbr, 0, 30);
		app.text(""+endCaret.leadingEdge, 0, 50);
		app.popStyle();
		app.popMatrix();	
	}
	
	// Used for testing
	public void updateCaret(int x, int y){
		endCaret = getCaretCharPos(x, y, endCaret);
		startCaret.copyFrom(endCaret);
	}

	/**
	 * Get the Caret for a given screen pixel position. It takes care of the
	 * position of the buffer on the screen and any offset for the text 
	 * (allowance for scrolling)
	 * 
	 * @param screenX
	 * @param screenY
	 * @param caret
	 * @return
	 */
	public ZCaret getCaretCharPos(float screenX, float screenY, ZCaret caret){
		if(caret == null)
			caret = new ZCaret();
		float textX = screenX - dx - pxlOffsetX;
		float textY = screenY - dy - pxlOffsetY;
		LineInfo pli = null;
		caret.valid = false;
		caret.paraNbr = 0;
		for(ZParagraph p : paragraphs){
			if(textY > p.paraHeight){
				textY -= p.paraHeight;
				caret.paraNbr++;
				continue;
			}
			pli = p.getLinePixelY(textY);
			if(pli != null){
				caret.valid = true;
				caret.paraLineNbr = pli.lineNo;
				TextHitInfo  thi = pli.layout.hitTestChar(textX, pli.baseline);
				caret.charNbr = thi.getCharIndex();
				caret.leadingEdge = thi.isLeadingEdge();
				break;
			}
		}
		return caret;
	}

	/**
	 * Get the information needed to draw the caret. 
	 * @param c the Caret we are interested in
	 * @param ci the matching caret information
	 * @return the match caret information
	 */
	public CaretInfo getCaretInfo(ZCaret c, CaretInfo ci){
		//System.out.println(c);
		return getCaretInfoImpl(c.paraNbr, c.paraLineNbr, c.charNbr, c.leadingEdge, ci);
	}

	/**
	 * Get the caret information. Assumes that we are assuming the leading edge is true.
	 * 
	 * @param pn paragraph number
	 * @param pln paragraph line number
	 * @param cn character number in line
	 * @param ci the caret information
	 * @return the match caret information
	 */
	public CaretInfo getCaretInfo(int pn, int pln, int cn, CaretInfo ci){
		return getCaretInfoImpl(pn, pln, cn, true, ci);
	}
	
	/**
	 * Implementation for all methods required to get Ccaret information.
	 * 
	 * @param pn paragraph number
	 * @param pln paragraph line number
	 * @param cn character number in line
	 * @param le whether it is leading edge or not
	 * @param ci the caret information
	 * @return the match caret information
	 */
	private CaretInfo getCaretInfoImpl(int pn, int pln, int cn, boolean le, CaretInfo ci){
		if(ci == null)
			ci = new CaretInfo();
		else
			ci.clear();
		// Move to the correct paragraph
		for(int i = 0; i < pn; i++)
			ci.cy += paragraphs.get(i).paraHeight;
		// Find the spot in the paragraph
		paragraphs.get(pn).getCaretInfo(pln, cn, le, ci);
//		System.out.println(ci);
		return ci;		
	}


}