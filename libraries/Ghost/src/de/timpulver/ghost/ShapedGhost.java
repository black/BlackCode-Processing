package de.timpulver.ghost;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import com.sun.awt.AWTUtilities;
import java.awt.GraphicsDevice.*; // PC only
import java.awt.Shape;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.Polygon;
import java.awt.Color;

@SuppressWarnings("restriction") // AWTUtilities is win only
public class ShapedGhost extends Ghost{
	private PApplet p;
	private PImage screenShot;
	private boolean clearBackground = true;
	protected static final String DEFAULT_RENDERER = JAVA2D; 

	private int x, y, w, h;
	
	public ShapedGhost(PApplet p, PVector[] vec){
		PVector topLeft = getSurroundingRectPositionTl(vec);
		PVector bottomRight = getSurroundingRectPositionBr(vec);
		init(	p, 
				(int)topLeft.x, 
				(int)topLeft.y, 
				(int)(bottomRight.x-topLeft.x), 
				(int)(bottomRight.y-topLeft.y), 
				DEFAULT_RENDERER);
		AWTUtilities.setWindowOpaque(p.frame, false);
		  AWTUtilities.setWindowOpacity(p.frame, 0.1f);
		  Shape shape = null;
	}
	
	public void init(PApplet p, int x, int y, int w, int h, String renderer){
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		p.size(w, h, DEFAULT_RENDERER);  
		screenShot = getScreenCapture(x, y, w, h);
		p.frame.removeNotify();
		p.frame.setUndecorated(true);
		p.registerMethod("pre", this); // new in Processing 2.0
		p.image(screenShot,0,0, w, h);
	}
	
	
	/**
	 * Equals a background(0) in the Processing draw() function. 
	 * When set to <i>true</i>, the background image will be drawn every frame, 
	 * so everything beneath it will not be visible any more. 
	 * @param b Whether or not the background should be cleared every frame
	 */
	public void clearBackground(boolean b){
		this.clearBackground = b;
	}
	
	/**
	 * Will be called before draw() in the main sketch. 
	 * We need to reposition the window every frame and 
	 * draw the screenshot as a background. Do not call this from your sketch!
	 */
	public void pre(){
		  p.frame.setLocation(x, y);
		  if(clearBackground){
			  p.image(screenShot,0,0, w, h);
		  }
	}
	
	/**
	 * Returns a new PVector with the smallest x and y coordinates found  
	 * in the array. The returned PVector does not need to be a member 
	 * of the array, x and y values may be taken from separate vectors. 
	 * This can be used to draw a rect around all vectors.  
	 * @param vec An array containing PVectors
	 * @return PVector with smallest x and y coordinates (top left vector)
	 */
	private PVector getSurroundingRectPositionTl(PVector[] vec){
		PVector smallest = new PVector(Float.MAX_VALUE, Float.MAX_VALUE);
		for(PVector p: vec){
			if(p.x < smallest.x) smallest.x = p.x;
			if(p.y < smallest.y) smallest.y = p.y;
		}
		return smallest;
	}
	
	/**
	 * Returns a new PVector with the biggest x and y coordinates found  
	 * in the array. The returned PVector does not need to be a member 
	 * of the array, x and y values may be taken from separate vectors. 
	 * This can be used to draw a rect around all vectors.  
	 * @param vec An array containing PVectors
	 * @return PVector with biggest x and y coordinates (bottom right vector)
	 */
	private PVector getSurroundingRectPositionBr(PVector[] vec){
		PVector biggest = new PVector(Float.MIN_VALUE, Float.MIN_VALUE);
		for(PVector p: vec){
			if(p.x > biggest.x) biggest.x = p.x;
			if(p.y > biggest.y) biggest.y = p.y;
		}
		return biggest;
	}
}
