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

package net.nexttext.behaviour.standard;

import java.awt.Color;

import net.nexttext.TextObject;
import net.nexttext.behaviour.AbstractAction;
import net.nexttext.property.ColorProperty;
import net.nexttext.property.NumberProperty;

/**
 * This action fades an object's color alpha component to the specified value
 * and at a given speed.
 * 
 * <p>It does not modify the object's color. Instead, see {@link Colorize}. </p>
 */
/* $Id$ */
public class FadeTo extends AbstractAction {
    
    private boolean applyToFill = true;
    private boolean applyToStroke = false;
    
    /**
     * Default constructor.  Fades the fill colour to alpha 0 by default at a 
     * speed of 10.
     *
     * <p>This constructor is kept for code consistency with code that was
     * using FadeTo prior to the implementation of the stroke property. </p>
     */
    public FadeTo() {
        this(0, 10, true, false);
    }
    
    /**
     * Creates a FadeTo action with the parameters passed as arguments.
     * By default the parameters affect the fill color.
     *
     * <p>This constructor is kept for code consistency with code that was
     * using FadeTo prior to the implementation of the stroke property. </p>
     *
     * @param fadeTo A target alpha value in the range 0..255
     * @param speed The fade speed
     */
    public FadeTo ( int fadeTo, int speed ) {
        this(fadeTo, speed, true, false);
    }
    
    /**
     * Creates a FadeTo action with the parameters passed as arguments.
     *
     * @param fadeTo A target alpha value in the range 0..255
     * @param speed The fade speed
     * @param fill Indicates if the action has to be processed on the fill
     * @param stroke Indicates if the action has to be processed on the stroke
     */
    public FadeTo ( int fadeTo, int speed, boolean fill, boolean stroke ) {
        applyToFill = fill;
        applyToStroke = stroke;
        
        if (applyToFill) {
            properties().init("AlphaFill", new NumberProperty(fadeTo) );
            properties().init("SpeedFill", new NumberProperty(speed) );
        }
        if (applyToStroke) {
            properties().init("AlphaStroke", new NumberProperty(fadeTo) );
            properties().init("SpeedStroke", new NumberProperty(speed) );
        }
    }
    
    /**
     * Creates a FadeTo action with the parameters passed as arguments. 
     * 
     * 
     * 
     * @param fadeToFill A target fill color alpha value in the range 0..255
     * @param speedFill The fill color fade speed
     * @param fadeToStroke A target stroke color alpha value in the range 0..255
     * @param speedStroke The stroke color fade speed
     */
    public FadeTo ( int fadeToFill, int speedFill, int fadeToStroke, int speedStroke ) { 
        applyToFill = true;
        applyToStroke = true;
        properties().init("AlphaFill", new NumberProperty(fadeToFill) );
        properties().init("SpeedFill", new NumberProperty(speedFill) );
        properties().init("AlphaStroke", new NumberProperty(fadeToStroke) );
        properties().init("SpeedStroke", new NumberProperty(speedStroke) );
    }
    
    /**
     * Applies the Fade action to a TextObject.
     * 
     * <p>The returned ActionResult will set complete when the alpha value has
     * been reached, and will never return events.  </p>
     */
    public ActionResult behave(TextObject to) {
            
        ColorProperty cProp;
        boolean doneFill = false;
        boolean doneStroke = false;
        
        if (applyToFill) {
            // retrieve this object's colour
            cProp = to.getColor();    
            // retrieve this action's properties
            int alphaFill = (int)((NumberProperty)(properties().get("AlphaFill"))).get();
            int speedFill = (int)((NumberProperty)(properties().get("SpeedFill"))).get();
            // fade the fill colour
            doneFill = fadeTo(cProp, alphaFill, speedFill);
        }
        if (applyToStroke) {
            // retrieve this object's stroke colour
            cProp = to.getStrokeColor(); 
            // retrieve this action's properties
            int alphaStroke = (int)((NumberProperty)(properties().get("AlphaStroke"))).get();
            int speedStroke = (int)((NumberProperty)(properties().get("SpeedStroke"))).get();
            // fade the stroke colour
            doneStroke = fadeTo(cProp, alphaStroke, speedStroke);
        }
        
        if ((applyToFill==doneFill) && (applyToStroke==doneStroke))
            return new ActionResult(true, true, true);
        
        return new ActionResult(false, true, false);
    }
    
    private boolean fadeTo (ColorProperty prop, int fadeTo, int speed) {
        
        Color color = prop.get();
        int a = color.getAlpha();
        
        if ( a < fadeTo ) { 
            a += speed;
        	if ( a > fadeTo ) a = fadeTo; // check if we passed it             
    	}
    	else if ( a > fadeTo ) { 
	    a -= speed;           
	    	if ( a < fadeTo ) a = fadeTo; // check if we passed it
    	}
        
        // update the color property 
        Color newColor = new Color(color.getRed(), color.getGreen(), 
                                    color.getBlue(), a);    	
        prop.set( newColor );
        
        return a == fadeTo;
    }
}
     
