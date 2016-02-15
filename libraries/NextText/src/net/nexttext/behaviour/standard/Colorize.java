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
import processing.core.PApplet;

/**
 * Fades the color of an object to a new color over time.
 */
/* $Id$ */
public class Colorize extends AbstractAction {
     
    protected boolean applyToFill;
    protected boolean applyToStroke;
    
    //CONSTRUCTORS ------------------------------------------------------------------------------------
    /**
     * The Colorize action will only influence the fill colour.
     *
     * <p>This constructor is kept for code consistency with code that was
     * using Colorize prior to the implementation of the stroke property. </p>
     *
     * @param color The target color
     * @param speed The speed factor at which the colorization is applied
     */
    public Colorize ( Color color, float speed ) {        
        this(color, speed, true, false);
    }
    
    /**
     * The Colorize action is applied to the given glyph colour component
     * (i.e. the stroke and/or the fill)
     * 
     * @param color The target color
     * @param speed The speed factor at which the colorization is applied
     * @param fill Indicates if the action has to be processed on the fill
     * @param stroke Indicates if the action has to be processed on the stroke
     */
    public Colorize ( Color color, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        if (fill) {
            properties().init("ColorFill", new ColorProperty(color) );
            properties().init("SpeedFill", new NumberProperty(speed) );
        }
        if (stroke) {
            properties().init("ColorStroke", new ColorProperty(color) );
            properties().init("SpeedStroke", new NumberProperty(speed) );
        }
    }
    
    /**
     * The Colorize action is applied to the fill and the stroke colours,
     * according to the given values.
     * 
     * @param colorFill The target color of the fill
     * @param speedFill The colorization speed for the fill
     * @param colorStroke The target color of the stroke
     * @param speedStroke The colorization speed for the stroke
     */
    public Colorize ( Color colorFill, float speedFill, Color colorStroke, float speedStroke ) {
        applyToFill = true;
        applyToStroke = true;
        properties().init("ColorFill", new ColorProperty(colorFill) );
        properties().init("SpeedFill", new NumberProperty(speedFill) );
        properties().init("ColorStroke", new ColorProperty(colorStroke) );
        properties().init("SpeedStroke", new NumberProperty(speedStroke) );
    }
    
    /**
     * Creates a Colorize which changes the glyphs color over time. The colorization is only applied to 
     * the glyphs fill property. Specify red, green and blue color values.
     *
     * @param r the red value of the color, an integer from 0 to 255
     * @param g the green value of the color, an integer from 0 to 255
     * @param b the blue color value of the color, an integer from 0 to 255
     * @param speed The speed factor at which the colorization is applied
     */
    public Colorize ( int r, int g, int b, float speed ) {        
    	this(r, g, b, speed, true, false);
    }
    
    /**
     * Creates a Colorize which changes the glyphs color over time. The colorization is only applied to 
     * the glyphs fill property. Specify a hexadecimal color value with 6 numbers (24 bits).
     *
     *@param color hexadecimal argb color value. eg. 0xFF3421 is a light green (the format is RRGGBB). 
     * @param speed The speed factor at which the colorization is applied
     */
    public Colorize ( int color, float speed ) { 
    	this(color, speed, true, false);
    }
    /**
     * Creates a Colorize which changes the glyphs color over time. The colorization is only applied to 
     * the glyphs fill property. Specify a hexadecimal color value with 6 numbers (24 bits).
     *
     * @param color hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @alpha transparency value, range is from 0 to 255 
     * @param speed The speed factor at which the colorization is applied
     */
    public Colorize ( int color, int alpha, float speed ) {        
    	this(color, alpha, speed, true, false);
    }
    
    
    /**
     * Creates a Colorize which changes the glyphs color over time. The colorization is only applied to 
     * the glyphs fill property. Specify red, green and blue color values as well as an alpha value.
     *
     * @param r the red value of the color, an integer from 0 to 255
     * @param g the green value of the color, an integer from 0 to 255
     * @param b the blue color value of the color, an integer from 0 to 255
     * @param alpha the alpha or transparency of the color, an integer from 0 to 255
     * @param speed The speed factor at which the colorization is applied
     */
    public Colorize ( int r, int g, int b, int alpha , float speed ) {        
    	this(r, g, b, alpha, speed, true, false);
    }
    
    /**
     * Creates a Colorize which changes the glyphs color over time. Colorization can be applied to the 
     * glyph's fill and stroke properties. Specify red, green and blue color values.
     * 
     * @param r the red value of the color, an integer from 0 to 255
     * @param g the green value of the color, an integer from 0 to 255
     * @param b the blue color value of the color, an integer from 0 to 255
     * @param speed The speed factor at which the colorization is applied
     * @param fill Indicates if the action has to be processed on the fill
     * @param stroke Indicates if the action has to be processed on the stroke
     */
    public Colorize ( int r, int g, int b, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        Color color = new Color(r, g, b);
            properties().init("ColorFill", new ColorProperty(color) );
            properties().init("SpeedFill", new NumberProperty(speed) );
            properties().init("ColorStroke", new ColorProperty(color) );
            properties().init("SpeedStroke", new NumberProperty(speed) );
    }
    
    
    /**
     * Creates a Colorize which changes the glyphs color over time. Colorization can be applied to the 
     * glyphs fill and stroke properties. Specify red, green and blue color values as well as an alpha
     * value.
     * 
     * @param r the red value of the color, an integer from 0 to 255
     * @param g the green value of the color, an integer from 0 to 255
     * @param b the blue color value of the color, an integer from 0 to 255
     * @param alpha the alpha or transparency of the color, an integer from 0 to 255
     * @param speed The speed factor at which the colorization is applied
     * @param fill Indicates if the action has to be processed on the fill
     * @param stroke Indicates if the action has to be processed on the stroke
     */
    public Colorize ( int r, int g, int b, int alpha , float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        if(alpha <= 0) { //the minimum value of the alpha is 1, not 0
        	alpha = 1;
        }
        Color color = new Color(r, g, b, alpha);
        properties().init("ColorFill", new ColorProperty(color) );
        properties().init("SpeedFill", new NumberProperty(speed) );
        properties().init("ColorStroke", new ColorProperty(color) );
        properties().init("SpeedStroke", new NumberProperty(speed) );
    }
    
    /**
     * Creates a Colorize which changes the glyphs color over time. Colorization can be applied to the 
     * glyph's fill and stroke properties. Specify a hexadecimal color value. There is no transparency.
     * 
     *@param color hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB). 
     * @param speed The speed factor at which the colorization is applied
     * @param fill Indicates if the action has to be processed on the fill
     * @param stroke Indicates if the action has to be processed on the stroke
     */
    public Colorize ( int color, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        Color c = new Color(color); 
        properties().init("ColorFill", new ColorProperty(c) );
        properties().init("SpeedFill", new NumberProperty(speed) );
        properties().init("ColorStroke", new ColorProperty(c) );
        properties().init("SpeedStroke", new NumberProperty(speed) );
    }
    /**
     * Creates a Colorize which changes the glyphs color over time. Colorization can be applied to the 
     * glyph's fill and stroke properties. Specify a hexadecimal color value. There is no transparency.
     * 
     * @param color hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB). 
     * @param alpha transparency value, the range is from 0 to 255
     * @param speed The speed factor at which the colorization is applied
     * @param fill Indicates if the action has to be processed on the fill
     * @param stroke Indicates if the action has to be processed on the stroke
     */
    
    public Colorize ( int color, int alpha, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        if(alpha <= 0) { //the minimum value of the alpha is 1, not 0
        	alpha = 1;
        }
        color += alpha * (int)Math.pow(16, 6);
        Color c = new Color(color, true); 
        properties().init("ColorFill", new ColorProperty(c) );
        properties().init("SpeedFill", new NumberProperty(speed) );
        properties().init("ColorStroke", new ColorProperty(c) );
        properties().init("SpeedStroke", new NumberProperty(speed) );
    }
    
    /**
     * The Colorize action is applied to the fill and the stroke colours,
     * according to the given values.
     * 
     * @param rFill red color value for fill (0 to 255)
     * @param gFill green color value for fill (0 to 255)
     * @param bFill blue color value for fill (0 to 255)
     * @param speedFill The colorization speed for the fill
     * @param rStroke red color value for stroke (0 to 255)
     * @param gStroke green color value for stroke (0 to 255)
     * @param bStroke blue color value for stroke (0 to 255)
     * @param speedStroke The colorization speed for the stroke
     */
    public Colorize ( 	int rFill, int gFill, int bFill, float speedFill, int rStroke, 
    					int gStroke, int bStroke, float speedStroke ) {
        applyToFill = true;
        applyToStroke = true;
        Color cFill = new Color(rFill, gFill, bFill);
        Color cStroke = new Color(rStroke, gStroke, bStroke);
        properties().init("ColorFill", new ColorProperty(cFill) );
        properties().init("SpeedFill", new NumberProperty(speedFill) );
        properties().init("ColorStroke", new ColorProperty(cStroke) );
        properties().init("SpeedStroke", new NumberProperty(speedStroke) );
    }
    
    /**
     * The Colorize action is applied to the fill and the stroke colours,
     * according to the given values.
     * 
     * @param rFill red color value for fill (0 to 255)
     * @param gFill green color value for fill (0 to 255)
     * @param bFill blue color value for fill (0 to 255)
     * @param alphaFill transparency for the fill (0 to 255)
     * @param speedFill The colorization speed for the fill
     * @param rStroke red color value for stroke (0 to 255)
     * @param gStroke green color value for stroke (0 to 255)
     * @param bStroke blue color value for stroke (0 to 255)
      * @param alphaStroke transparency for the stroke (0 to 255)
     * @param speedStroke The colorization speed for the stroke
     */
    public Colorize ( 	int rFill, int gFill, int bFill, int alphaFill, float speedFill, int rStroke, 
    					int gStroke, int bStroke, int alphaStroke, float speedStroke ) {
        applyToFill = true;
        applyToStroke = true;
        if(alphaFill <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaFill = 1;
        }
        if(alphaStroke <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaStroke = 1;
        }
        Color cFill = new Color(rFill, gFill, bFill, alphaFill);
        Color cStroke = new Color(rStroke, gStroke, bStroke, alphaStroke);
        properties().init("ColorFill", new ColorProperty(cFill) );
        properties().init("SpeedFill", new NumberProperty(speedFill) );
        properties().init("ColorStroke", new ColorProperty(cStroke) );
        properties().init("SpeedStroke", new NumberProperty(speedStroke) );
    }
    
    /**
     * The Colorize action is applied to the fill and the stroke colours,
     * according to the given values.
     * 
     * @param colorFill hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @param speedFill The colorization speed for the fill
     * @param colorStroke hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format 
     * is RRGGBB).
     * @param speedStroke The colorization speed for the stroke
     */
    public Colorize ( 	int colorFill, float speedFill, int colorStroke, float speedStroke ) {
        applyToFill = true;
        applyToStroke = true;
        Color cFill = new Color(colorFill);
        Color cStroke = new Color(colorStroke);
        properties().init("ColorFill", new ColorProperty(cFill) );
        properties().init("SpeedFill", new NumberProperty(speedFill) );
        properties().init("ColorStroke", new ColorProperty(cStroke) );
        properties().init("SpeedStroke", new NumberProperty(speedStroke) );
    }
    
    /**
     * The Colorize action is applied to the fill and the stroke colours,
     * according to the given values.
     * 
     * @param colorFill hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @param alphaFill transparency for the fill (0 to 255)
     * @param speedFill The colorization speed for the fill
     * @param colorStroke hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format 
     * is RRGGBB).
     * @param alphaStroke transparency for the stroke (0 to 255)
     * @param speedStroke The colorization speed for the stroke
     */
    public Colorize ( 	int colorFill, int alphaFill, float speedFill, int colorStroke,
    		int alphaStroke, float speedStroke ) {
        applyToFill = true;
        applyToStroke = true;
        if(alphaFill <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaFill = 1;
        }
        if(alphaStroke <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaStroke = 1;
        }
        colorFill += alphaFill * (int)Math.pow(16, 6); //add the alpha
        colorStroke += alphaStroke * (int)Math.pow(16, 6); //add the alpha
        Color cFill = new Color(colorFill, true);
        Color cStroke = new Color(colorStroke, true);
        properties().init("ColorFill", new ColorProperty(cFill) );
        properties().init("SpeedFill", new NumberProperty(speedFill) );
        properties().init("ColorStroke", new ColorProperty(cStroke) );
        properties().init("SpeedStroke", new NumberProperty(speedStroke) );
    }
    
    //BEHAVE FUNCTION -------------------------------------------------------------------------
    
    public ActionResult behave(TextObject to) {
        
        boolean doneFill = false;
        boolean doneStroke = false;
        
        if (applyToFill) {
            doneFill =  fadeTo(to.getColor(), 
                    ((ColorProperty)properties().get("ColorFill")).get(), 
                    (int)((NumberProperty)properties().get("SpeedFill")).get());
        }
        
        if (applyToStroke) {
            doneStroke =  fadeTo(to.getStrokeColor(), 
                    ((ColorProperty)properties().get("ColorStroke")).get(), 
                    (int)((NumberProperty)properties().get("SpeedStroke")).get());
        }
        
        if ((applyToFill==doneFill) && (applyToStroke==doneStroke))
            return new ActionResult (true, true, false);
        
        return new ActionResult(false, true, false);
    }
    
    protected boolean fadeTo( ColorProperty currentProp, Color target, int speed ) {
        
        Color currentCol = currentProp.get();
        
        int tR = target.getRed();
        int tG = target.getGreen();
        int tB = target.getBlue();
        int tA = target.getAlpha();

        int newR = fadeComponentTo( currentCol.getRed(),   tR, speed );
        int newG = fadeComponentTo( currentCol.getGreen(), tG, speed );
        int newB = fadeComponentTo( currentCol.getBlue(),  tB, speed );
        int newA = fadeComponentTo( currentCol.getAlpha(), tA, speed );

        currentProp.set(new Color(newR, newG, newB, newA));

        return (newR == tR && newG == tG && newB == tB && newA == tA);
    }
    
    private int fadeComponentTo( int component, int target, int speed ) {
        
        if ( component < target ) {
            component += speed;
            if ( component > target ) {
                component = target;
            }
        }
        else {
            component -= speed;
            if ( component < target ) {
                component = target;
            }
        }
        return component;
    }
    
  //SET FUNCTIONS ----------------------------------------------------------------------------

    /**
     * Set function which changes the color and speed of the fill.
     * @param r red color value for fill (0 to 255)
     * @param g green color value for fill (0 to 255)
     * @param b blue color value for fill (0 to 255)
     * @param speed speed of the colorize
     */
    public void set ( int r, int g, int b, float speed ) {        
    	set(r, g, b, speed, true, false);
    }
    
    /**
     * Set function which changes the color and speed of the fill.
     * @param r red color value for fill (0 to 255)
     * @param g green color value for fill (0 to 255)
     * @param b blue color value for fill (0 to 255)
     * @param alpha transparency of the fill (0 to 255)
     * @param speed speed of the colorize
     */
    public void set ( int r, int g, int b, int alpha , float speed ) {        
    	set(r, g, b, alpha, speed, true, false);
    }

    /**
     * Set function which changes the color and speed of the fill.
     * @param color hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB). 
     * @param speed speed of the colorize
     */
    public void set ( int color, float speed ) {        
    	set(color, speed, true, false);
    }

    /**
     * Set function which changes the color and speed of the fill.
     * @param color hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB). 
     * @param alpha transparency of the fill (0 to 255)
     * @param speed speed of the colorize
     */
    public void set ( int color, int alpha, float speed ) {        
    	set(color, alpha, speed, true, false);
    }

    /**
     * Set function which changes the color and speed of the fill or stroke, or both.
     * @param r red color value (0 to 255)
     * @param g green color value (0 to 255)
     * @param b blue color value (0 to 255)
     * @param speed speed at which the color changes
     * @param fill determines whether the fill changes color
     * @param stroke determines whether the stroke changes color
     */
    public void set ( int r, int g, int b, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        Color color = new Color(r, g, b);
        if (fill) {
            ((ColorProperty)properties().get("ColorFill")).set(color);
            ((NumberProperty)properties().get("SpeedFill")).set(speed);
        }
        if (stroke) {
        	((ColorProperty)properties().get("ColorStroke")).set(color);
            ((NumberProperty)properties().get("SpeedStroke")).set(speed);
        }
    }
    
    /**
     * Set function which changes the color and speed of the fill or stroke, or both.
     * @param r red color value (0 to 255)
     * @param g green color value (0 to 255)
     * @param b blue color value (0 to 255)
     * @param alpha transparency value (0 to 255) 
     * @param speed speed at which the color changes
     * @param fill determines whether the fill changes color
     * @param stroke determines whether the stroke changes color
     */
    public void set ( int r, int g, int b, int alpha, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        if(alpha <= 0) { //the minimum value of the alpha is 1, not 0
        	alpha = 1;
        }
        Color color = new Color(r, g, b, alpha);
        if (fill) {
            ((ColorProperty)properties().get("ColorFill")).set(color);
            ((NumberProperty)properties().get("SpeedFill")).set(speed);
        }
        if (stroke) {
        	((ColorProperty)properties().get("ColorStroke")).set(color);
            ((NumberProperty)properties().get("SpeedStroke")).set(speed);
        }
    }
    
    /**
     * Set function which changes the color and speed of the fill or stroke, or both.
     * @param color hexadecimal RGB color value eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @param speed speed at which the color changes
     * @param fill determines whether the fill changes color
     * @param stroke determines whether the stroke changes color
     */
    public void set ( int color, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        Color c = new Color(color);
        if (fill) {
            ((ColorProperty)properties().get("ColorFill")).set(c);
            ((NumberProperty)properties().get("SpeedFill")).set(speed);
        }
        if (stroke) {
        	((ColorProperty)properties().get("ColorStroke")).set(c);
            ((NumberProperty)properties().get("SpeedStroke")).set(speed);
        }
    }
    
    /**
     * Set function which changes the color and speed of the fill or stroke, or both.
     * @param color hexadecimal RGB color value eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @param alpha transparency value (0 to 255) 
     * @param speed speed at which the color changes
     * @param fill determines whether the fill changes color
     * @param stroke determines whether the stroke changes color
     */
    public void set ( int color, int alpha, float speed, boolean fill, boolean stroke ) {        
        applyToFill = fill;
        applyToStroke = stroke;
        if(alpha <= 0) { //the minimum value of the alpha is 1, not 0
        	alpha = 1;
        }
        color += alpha * (int)Math.pow(16, 6);
        Color c = new Color(color, true);
        if (fill) {
            ((ColorProperty)properties().get("ColorFill")).set(c);
            ((NumberProperty)properties().get("SpeedFill")).set(speed);
        }
        if (stroke) {
        	((ColorProperty)properties().get("ColorStroke")).set(c);
            ((NumberProperty)properties().get("SpeedStroke")).set(speed);
        }
    }

    /**
     * Change the color and speed of the Colorize for both stroke and fill.
     * 
     * @param rFill red color value for fill (0 to 255)
     * @param gFill green color value for fill (0 to 255)
     * @param bFill blue color value for fill (0 to 255)
     * @param speedFill The colorization speed for the fill
     * @param rStroke red color value for stroke (0 to 255)
     * @param gStroke green color value for stroke (0 to 255)
     * @param bStroke blue color value for stroke (0 to 255)
     * @param speedStroke The colorization speed for the stroke
     */
    public void set ( 	int rFill, int gFill, int bFill, float fillSpeed, int rStroke, 
    					int gStroke, int bStroke, float strokeSpeed ) {
        applyToFill = true;
        applyToStroke = true;
        Color cFill = new Color(rFill, gFill, bFill);
        Color cStroke = new Color(rStroke, gStroke, bStroke);
        ((ColorProperty)properties().get("ColorFill")).set(cFill);
        ((NumberProperty)properties().get("SpeedFill")).set(fillSpeed);
        ((ColorProperty)properties().get("StrokeFill")).set(cStroke);
        ((NumberProperty)properties().get("SpeedStroke")).set(strokeSpeed);
    }
    
    /**
     * The Colorize action is applied to the fill and the stroke colours,
     * according to the given values.
     * 
     * @param rFill red color value for fill (0 to 255)
     * @param gFill green color value for fill (0 to 255)
     * @param bFill blue color value for fill (0 to 255)
     * @param alphaFill transparency for the fill (0 to 255)
     * @param speedFill The colorization speed for the fill
     * @param rStroke red color value for stroke (0 to 255)
     * @param gStroke green color value for stroke (0 to 255)
     * @param bStroke blue color value for stroke (0 to 255)
      * @param alphaStroke transparency for the stroke (0 to 255)
     * @param speedStroke The colorization speed for the stroke
     */
    public void set ( 	int rFill, int gFill, int bFill, int alphaFill, float fillSpeed, int rStroke, 
    					int gStroke, int bStroke, int alphaStroke, float strokeSpeed ) {
        applyToFill = true;
        applyToStroke = true;
        if(alphaFill <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaFill = 1;
        }
        if(alphaStroke <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaStroke = 1;
        }
        Color cFill = new Color(rFill, gFill, bFill, alphaFill);
        Color cStroke = new Color(rStroke, gStroke, bStroke, alphaStroke);
        ((ColorProperty)properties().get("ColorFill")).set(cFill);
        ((NumberProperty)properties().get("SpeedFill")).set(fillSpeed);
        ((ColorProperty)properties().get("StrokeFill")).set(cStroke);
        ((NumberProperty)properties().get("SpeedStroke")).set(strokeSpeed);
    }
    
    /**
     * Change the color and speed values for the fill and stroke.
     * 
     * @param colorFill hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @param speedFill The colorization speed for the fill
     * @param colorStroke hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format 
     * is RRGGBB).
     * @param speedStroke The colorization speed for the stroke
     */
    public void set ( int colorFill, float fillSpeed, int colorStroke, float strokeSpeed ) {
        applyToFill = true;
        applyToStroke = true;
        Color cFill = new Color(colorFill);
        Color cStroke = new Color(colorStroke);
        ((ColorProperty)properties().get("ColorFill")).set(cFill);
        ((NumberProperty)properties().get("SpeedFill")).set(fillSpeed);
        ((ColorProperty)properties().get("StrokeFill")).set(cStroke);
        ((NumberProperty)properties().get("SpeedStroke")).set(strokeSpeed);
    }
    
    /**
     * Change the color and speed values for the fill and stroke.
     * 
     * @param colorFill hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format is RRGGBB).
     * @param alphaFill transparency for the fill (0 to 255)
     * @param speedFill The colorization speed for the fill
     * @param colorStroke hexadecimal rgb color value. eg. 0xFF3421 is a light green (the format 
     * is RRGGBB).
     * @param alphaStroke transparency for the stroke (0 to 255)
     * @param speedStroke The colorization speed for the stroke
     */
    public void set ( 	int colorFill, int alphaFill, float fillSpeed, int colorStroke,
    		int alphaStroke, float strokeSpeed ) {
        applyToFill = true;
        applyToStroke = true;
        if(alphaFill <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaFill = 1;
        }
        if(alphaStroke <= 0) { //the minimum value of the alpha is 1, not 0
        	alphaStroke = 1;
        }
        colorFill += alphaFill * (int)Math.pow(16, 6); //add the alpha
        colorStroke += alphaStroke * (int)Math.pow(16, 6); //add the alpha
        Color cFill = new Color(colorFill, true);
        Color cStroke = new Color(colorStroke, true);
        ((ColorProperty)properties().get("ColorFill")).set(cFill);
        ((NumberProperty)properties().get("SpeedFill")).set(fillSpeed);
        ((ColorProperty)properties().get("StrokeFill")).set(cStroke);
        ((NumberProperty)properties().get("SpeedStroke")).set(strokeSpeed);
    }
 
}

