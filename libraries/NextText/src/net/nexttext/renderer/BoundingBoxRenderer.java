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

import java.awt.*;
import net.nexttext.*;
import processing.core.*;

/**
 * Traverses the TextObject hierarchy and draws every object's bounding box.
 */
/* $Id$ */
public class BoundingBoxRenderer extends TextPageRenderer {
    int boxColor;
    boolean doGlyphs;
    boolean doGroups;
    
    /**
     * Builds a BoundingBoxRenderer.
     * 
     * @param p the parent PApplet
     * @param boxColor the color that will be used to render the boxes
     * @param doGlyphs whether or not to draw bounding boxes around the TextObjectGlyphs
     * @param doGroups whether or not to draw bounding boxes around the TextObjectGroups
     */
    public BoundingBoxRenderer(PApplet p, int boxColor, boolean doGlyphs, boolean doGroups) {
        super(p);
        this.boxColor = boxColor;
        this.doGlyphs = doGlyphs;
        this.doGroups = doGroups;
    }
    
    /**
     * Builds a BoundingBoxRenderer.
     * 
     * @param p the parent PApplet
     * @param rColor the red value of the color that will be used to render the boxes
     * @param gColor the green value of the color that will be used to render the boxes
     * @param bColor the blue value of the color that will be used to render the boxes
     * @param doGlyphs whether or not to draw bounding boxes around the TextObjectGlyphs
     * @param doGroups whether or not to draw bounding boxes around the TextObjectGroups
     */
    public BoundingBoxRenderer(PApplet p, int rColor, int gColor, int bColor, boolean doGlyphs, boolean doGroups) {
        this(p, p.color(rColor, gColor, bColor), doGlyphs, doGroups);
    }

    /**
     * Traverse the TextObject tree and render all of its elements.
     * 
     * @param textPage the TextPage to render
     */
    public void renderPage(TextPage textPage) {
        TextObjectGroup root = textPage.getTextRoot();
        TextObjectIterator toi = root.iterator();
        while (toi.hasNext()) {
            TextObject to = toi.next();

            if (to != root) {
                if ((to instanceof TextObjectGlyph && doGlyphs) || (to instanceof TextObjectGroup && doGroups)) {
                    renderTextObject(to);
                }
            }
        }
    }

    /**
     * Renders a bounding box for the TextObject.
     * 
     * @param to The TextObject to render
     */
    private void renderTextObject(TextObject to) {
        // save the current properties
        p.pushStyle();
        
        // draw the bounding box
        Rectangle bounds = to.getBounds();
        p.rectMode(PApplet.CORNER);
        p.stroke(boxColor);
        p.noFill();
        p.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // restore saved properties
        p.popStyle();
    }
}
