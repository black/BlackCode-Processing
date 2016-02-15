/*
  This file is part of the NextText project.
  http://www.nexttext.net/

  Copyright (c) 2004-08 Obx Labs / Jason Lewis

  NextText is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 2 of the License, or (at your option) any later 
  version.

  NextText is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  NextText.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.nexttext.renderer;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import processing.core.*;
import net.nexttext.Book;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextPage;

/**
 * 
 * Renders the text stored in a text page.
 * 
 * <p>
 * This TextPage renderer is based on the Java2D API.
 * </p>
 * 
 */
/* $Id$ */
public class Java2DTextPageRenderer extends G2DTextPageRenderer {

	/**
	 * Constructs a Java2DTextPageRenderer.
	 * @param p the parent PApplet
	 * @throws ClassCastException
	 */
    public Java2DTextPageRenderer(PApplet p) throws ClassCastException {
        this(p, p.g);
    }

	/**
	 * Constructs a Java2DTextPageRenderer.
	 * @param p the parent PApplet
	 * @throws ClassCastException
	 */
    public Java2DTextPageRenderer(PApplet p, PGraphics g) throws ClassCastException {
        super(p, g);
        this.g2 = ((PGraphicsJava2D)g).g2;
    }
    
    /**
     * The rendering loop. Takes as input a TextPage and traverses its root
     * node, rendering all the TextObjectGlyph objects along the way.
     * 
     * @param textPage the TextPage to render
     */
    public void renderPage(TextPage textPage) {
        // When resizing, it's possible to lose the reference to the graphics
        // context, so we skip rendering the frame.
        if (g2 == null) {
            System.out.println(("Skip rendering frame because the graphics context was lost temporarily."));
        }

        else if (textPage.getTextRoot() == null) {
            System.out.println("TextPage: No root specified yet");
        } 
        
        // traverse the TextObject hierarchy
        else {
            AffineTransform original = g2.getTransform();
            enterCoords(textPage);
            traverse(textPage.getTextRoot());
            exitCoords(textPage);
            g2.setTransform(original);
        }
    } // end rendering

    /**
     * Renders a TextObjectGlyph using quads, either as an outline or as a
     * filled shape.
     * 
     * @param glyph
     *            The TextObjectGlyph
     */
    protected void renderGlyph(TextObjectGlyph glyph) {
        // ////////////////////////////////////
        // Optimize based on presence of DForms and of outlines
        if (glyph.isDeformed() || glyph.isStroked()) {

            // ////////////////////////////////
            // Render glyph using vertex list

            // Use the cached path if possible.
            GeneralPath gp = glyph.getOutline();

            // draw the outline of the shape
            if (glyph.isStroked()) {
                g2.setColor(glyph.getStrokeColorAbsolute());
                g2.setStroke(glyph.getStrokeAbsolute());
                g2.draw(gp);
            }

            // fill the shape
            if (glyph.isFilled()) {
                g2.setColor(glyph.getColorAbsolute());
                g2.fill(gp);
            }
        }
        //if the PFont size and the set font size are the same then use the
        //text function to draw bitmaps.
        //this will create better results at small sizes.
        else if ((glyph.getFont().getNative() == null) ||
        		 (glyph.getSize() == ((Font)glyph.getFont().getNative()).getSize())) {
        	//set the color
        	g.fill(glyph.getColorAbsolute().getRGB());
        	//set the font
        	g.textFont(glyph.getFont());
        	//save the PApplet text alignment
        	int savedTextAlign = g.textAlign;
        	int savedTextAlignY = g.textAlignY;
        	//set the text alignment to LEFT / BASELINE
        	//to match the glyph position in NextText
        	g.textAlign(PConstants.LEFT, PConstants.BASELINE);
        	//draw the glyph
        	g.text(glyph.getGlyph(), 0, 0);
        	//set text alignment back to what it was
        	g.textAlign(savedTextAlign, savedTextAlignY);
        }
        //if the set font size is not the same as the PFont then draw using
        //the outlines so as not to get a pixelated scaling effect.
        else {
            // /////////////////////////////////////////
            // Render glyph using Graphics.drawString()
            g2.setColor(glyph.getColorAbsolute());
            // set the font
            g2.setFont(Book.loadFontFromPFont(glyph.getFont()));
            // draw the glyph
            g2.drawString(glyph.getGlyph(), 0, 0);
        }

    } // end renderGlyph
}