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

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import net.nexttext.TextObject;
import net.nexttext.TextObjectGlyph;
import net.nexttext.TextObjectGroup;
import net.nexttext.TextPage;

public abstract class G3DTextPageRenderer extends TextPageRenderer {

    /**
     * Renderer type enumeration.
     * <p>This is the type of the PApplet renderer, not the NextText renderer.</p>
     */
	public enum RendererType
	{
		TWO_D,
		THREE_D
	}
	RendererType renderer_type = RendererType.THREE_D;

	//detail level for curve approximation
	protected float bezierDetail;
	
    /**
     * Builds a G3DTextPageRenderer.
     * 
     * @param p the parent PApplet
     * @param curveDetail level of detail for curve approximation
     */
    public G3DTextPageRenderer(PApplet p, float curveDetail) {
        this(p, p.g, curveDetail);
    }

    /**
     * Builds a G3DTextPageRenderer.
     * 
     * @param p the parent PApplet
     * @param g the PGraphics
     * @param curveDetail level of detail for curve approximation
     */
    public G3DTextPageRenderer(PApplet p, PGraphics g, float curveDetail) {
        super(p, g);
        bezierDetail = curveDetail;
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
        if (g == null) {
            System.out.println(("Skipping rendering frame because the graphics context was lost temporarily."));
        }

        else if (textPage.getTextRoot() == null) {
            System.out.println("TextPage: No root specified yet");
        } 

        // traverse the TextObject hierarchy
        else {
        	enterCoords(textPage);
            traverse(textPage.getTextRoot());
            exitCoords();
        }
	}
	
    /**
     * Traverse the TextObject tree to render all of its glyphs.
     * 
     * <p>The tree is traversed using a variable to point at the current node
     * being processed. TextObjects specify their rotation and position
     * relative to their parent, which is handled by registering coordinate
     * system changes with the drawing surface as the tree is traversed.</p>
     * 
     * Currently, rendering is not synchronized with modifications to the
     * TextObjectTree. This is dodgy, but gives a performance boost, so will 
     * stay that way for the moment. However, it affects this method, because we
     * can't assume that the tree is always well structured.</p>
     * 
     * <p>Transformations are stored in a stack so that they can be undone as
     * needed. It is not appropriate to use the position of the TextObject to 
     * undo the transformation, because this may have changed due to the lack of
     * synchronization.</p>
     * 
     * @param root the TextObject node to traverse
     */
    protected void traverse(TextObject root) {
        TextObject current = root;
        do {
        	// Draw any glyphs
            if (current instanceof TextObjectGlyph) {
        		enterCoords(current);
        		renderGlyph((TextObjectGlyph) current);
        		exitCoords();
            }

            // Descend to process any children
            if (current instanceof TextObjectGroup) {
                TextObjectGroup tog = (TextObjectGroup) current;
                TextObject child = tog.getLeftMostChild();
                if (child != null) {
                    enterCoords(current);
                    current = child;
                    continue;
                }
            }
        
            // Processing of this node is complete, so move on to siblings.
            // Since a node may not have siblings, a search is made up the tree
            // for the first appropriate sibling. The search ends if a sibling
            // is found, or if it reaches the top of the tree.
            while (current != root) {
                TextObject sibling = current.getRightSibling();
                if (sibling != null) {
                    current = sibling;
                    break;
                } else {
                    current = current.getParent();
                    if (current == null) {
                        // Aaarghh, we were detached from the tree mid-render,
                        // so just abort the whole process.
                        return;
                    }
                    exitCoords();
                }
            }
        } while (current != root);
    }
    
    /**
     * Transform the drawing surface into the coordinates of the given 
     * TextPage.
     * 
     * @param page the TextPage
     */
    protected void enterCoords(TextPage page) {
        g.pushMatrix();

        // properties
        PVector pos = page.getPosition().get();
        PVector rot = page.getRotation().get();
        PVector center = page.getTextRoot().getCenter();
        
        //only use 3D function if the renderer is 3D and the position
        //and rotation are actually using 3D. This allows to use 2D recorders
        //with 3D renderers.
        if (((pos.z != 0) || (rot.x != 0) || (rot.y != 0))
        	&& (renderer_type == RendererType.THREE_D)) {
			g.translate((float)pos.x, (float)pos.y, (float)pos.z);
        	g.translate(center.x, center.y, pos.z);
        	//g.translate(g.width/2.0f, g.height/2.0f, 0);
        	g.rotateX((float)rot.x);
        	g.rotateY((float)rot.y);
        	g.rotateZ((float)rot.z);
        	g.translate(-center.x, -center.y, -pos.z);
        	//g.translate(-g.width/2.0f, -g.height/2.0f, 0);
		}
		else {
			g.translate((float)pos.x, (float)pos.y);		
        	g.translate(center.x, center.y);
        	//g.translate(g.width/2.0f, g.height/2.0f);
			g.rotate((float)rot.z);
        	g.translate(-center.x, -center.y);
        	//g.translate(-g.width/2.0f, -g.height/2.0f);
		}
    }
    
    /**
     * Transform the drawing surface into the coordinates of the given 
     * TextObject.
     * 
     * <p>Once this transformation is done, the TextObject and any of its 
     * children can be drawn directly to the PApplet without having to handle
     * position or rotation.</p>
     * 
     * @param node the TextObject holding the translation and rotation info
     */
    protected void enterCoords(TextObject node) {
        g.pushMatrix();

        // translation
        PVector pos = node.getPosition().get();
        
        //3D TextObject's positioning is not supported yet. 
        //if ((pos.z != 0) && (renderer_type == RendererType.THREE_D))
        //	p.translate((float)pos.x, (float)pos.y, 0); //todo: use Z coord
        //else
        	g.translate((float)pos.x, (float)pos.y);
        // rotation
        float rotation = (float)node.getRotation().get();	//todo: rotate in 3D
        g.rotate(rotation);
    }

    /**
     * Transform the drawing surface out of the coordinates on top of the stack.
     * 
     * <p>This undoes the change of enterCoords(...).</p>
     */
    protected void exitCoords() {
        g.popMatrix();
    }
    
    /**
     * Render a glyph for the specific renderer it is implemented for.
     * @param glyph
     */
    protected abstract void renderGlyph(TextObjectGlyph glyph);	
}
