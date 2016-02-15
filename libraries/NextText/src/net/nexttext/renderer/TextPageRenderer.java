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

import net.nexttext.*;
import processing.core.*;

/**
 * An interface that represents classes capable of rendering a TextPage in a PApplet.
 */
/* $Id$ */
public abstract class TextPageRenderer {
    protected PApplet p;
    protected PGraphics g;
    
    /**
     * Builds a TextPageRenderer.
     * 
     * @param p the parent PApplet
     */
    public TextPageRenderer(PApplet p) {
        this(p, p == null ? null : p.g);    
    }
    
    /**
     * 
     * @param p the parent PApplet
     * @param g the PGraphics
     */
    public TextPageRenderer(PApplet p, PGraphics g) {
        this.p = p;    
        this.g = g;     
    }
    
    /**
     * The rendering loop. Takes as input a TextPage and traverses its root
     * node, rendering all the TextObjects along the way.
     * 
     * @param textPage the TextPage to render
     */
    public abstract void renderPage(TextPage textPage);
    
    /**
     * Returns the PApplet used for drawing.
     *
     * @return PApplet the drawing surface
     */
    public PApplet getPApplet() { 
        return p;
    } 
}
