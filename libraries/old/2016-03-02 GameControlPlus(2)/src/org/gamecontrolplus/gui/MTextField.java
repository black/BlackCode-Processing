/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2012 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package org.gamecontrolplus.gui;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;

import org.gamecontrolplus.gui.MHotSpot.HSrect;
import org.gamecontrolplus.gui.MStyledString.TextLayoutHitInfo;
import org.gamecontrolplus.gui.MStyledString.TextLayoutInfo;

import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 * The text field component. <br>
 * 
 * This control allows the user to enter and edit a single line of text. The control
 * also allows default text and a horizontal scrollbar.
 * 
 * can be created to manage either a single line of text or
 * multiple lines of text. <br>
 * 
 * Enables user text input at runtime. Text can be selected using the mouse
 * or keyboard shortcuts and then copied or cut to the clipboard. Text
 * can also be pasted in.
 *
 * @author Peter Lager
 *
 */
public class MTextField extends MEditableTextControl {

	/**
	 * Create a text field without a scrollbar.
	 * 
	 * @param theApplet
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public MTextField(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, SCROLLBARS_NONE);
	}

	/**
	 * Create a text field with the given scrollbar policy. <br>
	 * This policy can be one of these <br>
	 * <ul>
	 * <li>SCROLLBARS_NONE</li>
	 * <li>SCROLLBARS_HORIZONTAL_ONLY</li>
	 * </ul>
	 * If you want the scrollbar to auto hide then perform a logical or with 
	 * <ul>
	 * <li>SCROLLBARS_AUTOHIDE</li>
	 * </ul>
	 * e.g. SCROLLBARS_HORIZONTAL_ONLY | SCROLLBARS_AUTOHIDE
	 * <br>
	 * @param theApplet
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param sbPolicy
	 */
	public MTextField(PApplet theApplet, float p0, float p1, float p2, float p3, int sbPolicy) {
		super(theApplet, p0, p1, p2, p3, sbPolicy);
		children = new LinkedList<MAbstractControl>();
		tx = ty = 2;
		tw = width - 2 * 2;
		th = height - ((scrollbarPolicy & SCROLLBAR_HORIZONTAL) != 0 ? 11 : 0);
		wrapWidth = Integer.MAX_VALUE;
		gpTextDisplayArea = new GeneralPath();
		gpTextDisplayArea.moveTo( 0,  0);
		gpTextDisplayArea.lineTo( 0, th);
		gpTextDisplayArea.lineTo(tw, th);
		gpTextDisplayArea.lineTo(tw,  0);
		gpTextDisplayArea.closePath();

		hotspots = new MHotSpot[]{
				new HSrect(1, tx, ty, tw, th),			// typing area
				new HSrect(9, 0, 0, width, height)		// control surface
		};

		M4P.pushStyle();
		M4P.showMessages = false;

		z = Z_STICKY;

		//G4P.control_mode = GControlMode.CORNER;
		if((scrollbarPolicy & SCROLLBAR_HORIZONTAL) != 0){
			hsb = new MScrollbar(theApplet, 0, 0, tw, 10);
			addControl(hsb, tx, ty + th + 2, 0);
			hsb.addEventHandler(this, "hsbEventHandler");
			hsb.setAutoHide(autoHide);
		}
		M4P.popStyle();
		setText("");

		createEventHandler(M4P.sketchWindow, "handleTextEvents", 
				new Class<?>[]{ MEditableTextControl.class, MEvent.class }, 
				new String[]{ "textcontrol", "event" } 
				);
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD | KEY_METHOD;
		
		// Must register control
		M4P.registerControl(this);
		bufferInvalid = true;

		
//		super(theApplet, p0, p1, p2, p3, sbPolicy);
//		children = new LinkedList<MAbstractControl>();
//		tx = ty = 2;
//		tw = width - 2 * 2;
//		th = height - ((scrollbarPolicy & SCROLLBAR_HORIZONTAL) != 0 ? 11 : 0);
//		wrapWidth = Integer.MAX_VALUE;
//		gpTextDisplayArea = new GeneralPath();
//		gpTextDisplayArea.moveTo( 0,  0);
//		gpTextDisplayArea.lineTo( 0, th);
//		gpTextDisplayArea.lineTo(tw, th);
//		gpTextDisplayArea.lineTo(tw,  0);
//		gpTextDisplayArea.closePath();
//
//		// The image buffer is just for the typing area
//		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
//		buffer.rectMode(PApplet.CORNER);
//		buffer.g2.setFont(localFont);
//		hotspots = new MHotSpot[]{
//				new HSrect(1, tx, ty, tw, th),			// typing area
//				new HSrect(9, 0, 0, width, height)		// control surface
//		};
//
//		M4P.pushStyle();
//		M4P.showMessages = false;
//
//		z = Z_STICKY;
//
//		if((scrollbarPolicy & SCROLLBAR_HORIZONTAL) != 0){
//			hsb = new MScrollbar(theApplet, 0, 0, tw, 10);
//			addControl(hsb, tx, ty + th + 2, 0);
//			hsb.addEventHandler(this, "hsbEventHandler");
//			hsb.setAutoHide(autoHide);
//		}
//		M4P.popStyle();
//		setText("");
//		//		z = Z_STICKY;
//		createEventHandler(M4P.sketchApplet, "handleTextEvents", 
//				new Class<?>[]{ MEditableTextControl.class, MEvent.class }, 
//				new String[]{ "textcontrol", "event" } 
//				);
//		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD | KEY_METHOD;
//		M4P.addControl(this);
	}

	/**
	 * Set the styled text for this textfield after ensuring that all EOL characters
	 * have been removed.
	 * @param ss
	 */
	public void setStyledText(MStyledString ss){
		stext = ss.convertToSingleLineText();
		stext.getLines(buffer.g2);
		if(stext.getNbrLines() > 0){
			endTLHI.tli = stext.getLines(buffer.g2).getFirst();
			endTLHI.thi = endTLHI.tli.layout.getNextLeftHit(1);	
			startTLHI.copyFrom(endTLHI);
			calculateCaretPos(endTLHI);
			keepCursorInView = true;
		}
		ptx = pty = 0;
		// If needed update the horizontal scrollbar
		if(hsb != null){
			if(stext.getMaxLineLength() < tw)
				hsb.setValue(0,1);
			else
				hsb.setValue(0, tw/stext.getMaxLineLength());
		}
		bufferInvalid = true;
	}

	/**
	 * Set the text to be displayed.
	 * 
	 * @param text
	 */
	public void setText(String text){
		if(text == null)
			text = "";
		setStyledText(new MStyledString(text, Integer.MAX_VALUE));
	}

	/**
	 * Add some plain text to the end of the existing text.
	 * 
	 * @param extraText
	 */
	public void appendText(String extraText){
		if(extraText == null || extraText.equals(""))
			return;
		if(stext.insertCharacters(stext.length(), extraText) == 0)
			return;
		//		text = stext.getPlainText();
		LinkedList<TextLayoutInfo> lines = stext.getLines(buffer.g2);
		endTLHI.tli = lines.getLast();
		endTLHI.thi = endTLHI.tli.layout.getNextRightHit(endTLHI.tli.nbrChars - 1);
		startTLHI.copyFrom(endTLHI);
		calculateCaretPos(endTLHI);
		if(hsb != null){
			float hvalue = lines.getLast().layout.getVisibleAdvance();
			float hlinelength = stext.getMaxLineLength();
			float hfiller = Math.min(1, tw/hlinelength);
			if(caretX < tw)
				hsb.setValue(0,hfiller);
			else 
				hsb.setValue(hvalue/hlinelength, hfiller);
			keepCursorInView = true;
		}
		bufferInvalid = true;
	}

	public PGraphics getSnapshot(){
		updateBuffer();
		PGraphicsJava2D snap = (PGraphicsJava2D) winApp.createGraphics(buffer.width, buffer.height, PApplet.JAVA2D);
		snap.beginDraw();
		snap.image(buffer,0,0);
		if(hsb != null){
			snap.pushMatrix();
			snap.translate(hsb.getX(), hsb.getY());
			snap.image(hsb.getBuffer(), 0, 0);
			snap.popMatrix();
		}
		snap.endDraw();
		return snap;
	}

	public void pre(){
		if(keepCursorInView){
			boolean horzScroll = false;
			float max_ptx = caretX - tw + 2;
			if(endTLHI != null){
				if(ptx > caretX){ 								// Scroll to the left (text moves right)
					ptx -= HORZ_SCROLL_RATE;
					if(ptx < 0) ptx = 0;
					horzScroll = true;
				}
				else if(ptx < max_ptx){ 						// Scroll to the right (text moves left)?
					ptx += HORZ_SCROLL_RATE;
					if(ptx > max_ptx) ptx = max_ptx;
					horzScroll = true;
				}
				// Ensure that we show as much text as possible keeping the caret in view
				// This is particularly important when deleting from the end of the text
				if(ptx > 0 && endTLHI.tli.layout.getAdvance() - ptx < tw - 2){
					ptx = Math.max(0, endTLHI.tli.layout.getAdvance() - tw - 2);
					horzScroll = true;
				}
				if(horzScroll && hsb != null)
					hsb.setValue(ptx / (stext.getMaxLineLength() + 4));
			}
			// If we have scrolled invalidate the buffer otherwise forget it
			if(horzScroll)
				bufferInvalid = true;
			else
				keepCursorInView = false;
		}
	}

	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		ox -= tx; oy -= ty; // Remove translation

		currSpot = whichHotSpot(ox, oy);

		if(currSpot == 1 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getAction()){
		case MouseEvent.PRESS:
			if(currSpot == 1){
				if(focusIsWith != this && z >= focusObjectZ()){
					keepCursorInView = true;
					takeFocus();
				}
				dragging = false;
				if(stext == null || stext.length() == 0){
					stext = new MStyledString(" ", wrapWidth);
					stext.getLines(buffer.g2);
				}
				endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
				startTLHI = new TextLayoutHitInfo(endTLHI);
				calculateCaretPos(endTLHI);
				bufferInvalid = true;
			}
			else { // Not over this control so if we have focus loose it
				if(focusIsWith == this)
					loseFocus(null);
			}		
			break;
		case MouseEvent.RELEASE:
			dragging = false;
			bufferInvalid = true;
			break;
		case MouseEvent.DRAG:
			if(focusIsWith == this){
				keepCursorInView = true;
				dragging = true;
				endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
				calculateCaretPos(endTLHI);
				fireEvent(this, MEvent.SELECTION_CHANGED);
				bufferInvalid = true;
			}
			break;
		}
	}

	protected void keyPressedProcess(int keyCode, char keyChar, boolean shiftDown, boolean ctrlDown){
		boolean validKeyCombo = true;

		switch(keyCode){
		case LEFT:
			moveCaretLeft(endTLHI);
			break;
		case RIGHT:
			moveCaretRight(endTLHI);
			break;
		case MConstants.HOME:
			moveCaretStartOfLine(endTLHI);
			break;
		case MConstants.END:
			moveCaretEndOfLine(endTLHI);
			break;
		case 'A':
			if(ctrlDown){
				moveCaretStartOfLine(startTLHI);
				moveCaretEndOfLine(endTLHI);
				// Make shift down so that the start caret position is not
				// moved to match end caret position.
				shiftDown = true; 
			}
			break;
		case 'C':
			if(ctrlDown)
				MClip.copy(getSelectedText());
			validKeyCombo = false;
			break;
		case 'V':
			if(ctrlDown){
				String p = MClip.paste();
				p.replaceAll("\n", "");
				if(p.length() > 0){
					// delete selection and add 
					if(hasSelection())
						stext.deleteCharacters(pos, nbr);
					stext.insertCharacters(pos, p);
					adjust = p.length();
					textChanged = true;
				}
			}
			break;
		default:
			validKeyCombo = false;	
		}
		calculateCaretPos(endTLHI);

		if(validKeyCombo){
			if(!shiftDown)				// Not extending selection
				startTLHI.copyFrom(endTLHI);
			bufferInvalid = true;		// Selection changed
		}
	}

	protected void keyTypedProcess(int keyCode, char keyChar, boolean shiftDown, boolean ctrlDown){
		int ascii = (int)keyChar;

		if(ascii >= 32 && ascii < 127){
			if(hasSelection())
				stext.deleteCharacters(pos, nbr);
			stext.insertCharacters(pos, "" + keyChar);
			adjust = 1; textChanged = true;
		}
		else if(keyChar == BACKSPACE){
			if(hasSelection()){
				stext.deleteCharacters(pos, nbr);
				adjust = 0; textChanged = true;				
			}
			else if(stext.deleteCharacters(pos - 1, 1)){
				adjust = -1; textChanged = true;
			}
		}
		else if(keyChar == DELETE){
			if(hasSelection()){
				stext.deleteCharacters(pos, nbr);
				adjust = 0; textChanged = true;				
			}
			else if(stext.deleteCharacters(pos, 1)){
				adjust = 0; textChanged = true;
			}
		}
		else if(keyChar == ENTER || keyChar == RETURN) {
			fireEvent(this, MEvent.ENTERED);
			// If we have a tab manager and can tab forward then do so
			if(tabManager != null && tabManager.nextControl(this)){
				startTLHI.copyFrom(endTLHI);
				return;
			}
		}
		else if(keyChar == TAB){
			// If possible move to next text control
			if(tabManager != null){
				boolean result = (shiftDown) ? tabManager.prevControl(this) : tabManager.nextControl(this);
				if(result){
					startTLHI.copyFrom(endTLHI);
					return;
				}
			}
		}
		// If we have emptied the text then recreate a one character string (space)
		if(stext.length() == 0){
			stext.insertCharacters(0, " ");
			adjust++; textChanged = true;
		}
	}

	protected boolean changeText(){
		if(!super.changeText())
			return false;
		startTLHI.copyFrom(endTLHI);
		return true;
	}


	public void draw(){
		if(!visible) return;
		updateBuffer();

		winApp.pushStyle();
		winApp.pushMatrix();

		winApp.translate(cx, cy);
		winApp.rotate(rotAngle);

		winApp.pushMatrix();
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(TINT_FOR_ALPHA, alphaLevel);
		winApp.image(buffer, 0, 0);

		// Draw caret if text display area
		if(focusIsWith == this && showCaret && endTLHI.tli != null){
			float[] cinfo = endTLHI.tli.layout.getCaretInfo(endTLHI.thi);
			float x_left =  - ptx + cinfo[0];
			float y_top = - pty + endTLHI.tli.yPosInPara; 
			float y_bot = y_top - cinfo[3] + cinfo[5];
			if(x_left >= 0 && x_left <= tw && y_top >= 0 && y_bot <= th){
				winApp.strokeWeight(1.9f);
				winApp.stroke(palette[15]);
				winApp.line(tx+x_left, ty+Math.max(0, y_top), tx+x_left, ty+Math.min(th, y_bot));
			}
		}

		winApp.popMatrix();

		if(children != null){
			for(MAbstractControl c : children)
				c.draw();
		}
		winApp.popMatrix();
		winApp.popStyle();
	}

	/**
	 * If the buffer is invalid then redraw it.
	 * @TODO need to use palette for colours
	 */
	protected void updateBuffer(){
		if(bufferInvalid) {
			buffer.beginDraw();
			Graphics2D g2d = buffer.g2;
			// Get the latest lines of text
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);	
			if(lines.isEmpty() && promptText != null)
				lines = promptText.getLines(g2d);

			bufferInvalid = false;
			TextLayoutHitInfo startSelTLHI = null, endSelTLHI = null;

			// Whole control surface if opaque
			if(opaque)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));

			// Now move to top left corner of text display area
			buffer.translate(tx,ty); 

			// Typing area surface
			buffer.noStroke();
			buffer.fill(palette[7]);
			buffer.rect(-1,-1,tw+2,th+2);

			g2d.setClip(gpTextDisplayArea);
			buffer.translate(-ptx, -pty);
			// Translate in preparation for display selection and text

			if(hasSelection()){
				if(endTLHI.compareTo(startTLHI) == -1){
					startSelTLHI = endTLHI;
					endSelTLHI = startTLHI;
				}
				else {
					startSelTLHI = startTLHI;
					endSelTLHI = endTLHI;
				}
			}
			// Display selection and text
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				// Draw selection if any
				if(hasSelection() && lineInfo.compareTo(startSelTLHI.tli) >= 0 && lineInfo.compareTo(endSelTLHI.tli) <= 0 ){				
					int ss = startSelTLHI.thi.getInsertionIndex();
					int ee = endSelTLHI.thi.getInsertionIndex();
					g2d.setColor(jpalette[14]);
					Shape selShape = layout.getLogicalHighlightShape(ss, ee);
					g2d.fill(selShape);
				}
				// Draw text
				g2d.setColor(jpalette[2]);
				lineInfo.layout.draw(g2d, 0, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			g2d.setClip(null);
			buffer.endDraw();
		}
	}

}
