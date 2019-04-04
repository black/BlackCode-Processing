/*
  Part of the G4P library for Processing 
  	http://www.lagers.org.uk/g4p/index.html
	http://sourceforge.net/projects/g4p/files/?source=navbar

  Copyright (c) 2016 Peter Lager

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

package g4p_controls;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * 
 * Class for the creation of non-animated icons to be used with GButton, GLabel, GCheckbox and GOption controls. <br>
 * 
 * Introduced in v4.1
 * @author Peter Lager
 *
 */
public class GIcon {

	/* This must be set by the constructor */
	protected PApplet app = null;

	protected GAbstractControl owner = null;

	private static final String WARNING = "warn3.png";

	protected PImage[] img = null;
	protected int width = 0;
	protected int height = 0;

	protected int nbrFrames = 0;
	protected int currFrame = 0;

	/**
	 * This constructor is not to be used.
	 */
	protected GIcon(){ }

	/**
	 * Create an animated icon
	 * 
	 * @param papp the PApplet object that will be used to display this icon
	 * @param fname the name of the file containing the tiled image.
	 * @param nbrCols number of horizontal tiles
	 * @param nbrRows number of vertical tiles
	 */
	public GIcon(PApplet papp, String fname, int nbrCols, int nbrRows){
		this(papp, ImageManager.loadImage(papp, fname), nbrCols, nbrRows);
	}

	/**
	 * Create an animated icon.
	 * 
	 * @param papp the PApplet object that will be used to display this icon
	 * @param image the tiled image containing the frames.
	 * @param nbrCols number of horizontal tiles
	 * @param nbrRows number of vertical tiles
	 */
	public GIcon(PApplet papp, PImage image, int nbrCols, int nbrRows){
		app = papp;
		if(nbrCols >= 1 && nbrRows >= 1 && image != null ) 
			// Create an array from tiled image
			img = ImageManager.makeTiles1D(app, image, nbrCols, nbrRows);
		else
			// Create a one element array with warning sign
			img = new PImage[] { ImageManager.loadImage(app, WARNING) };
		nbrFrames = img.length;
		width = img[0].width;
		height = img[0].height;
	}

	/**
	 * @return a copy of this icon to be used with another control.
	 */
	public GIcon copy(){
		GIcon icon = new GIcon();
		icon.app = app;
		icon.img = img;
		icon.width = width;
		icon.height = height;
		icon.nbrFrames = nbrFrames;
		return icon;
	}

	/**
	 * 
	 * @return always returns null
	 */
	GAnimIcon me(){
		return null;
	}

	/**
	 * Set the current frame to be displayed. The supplied frame number will be constrained 
	 * to a valid value.
	 * @param fn the frame number to display.
	 * @return this icon
	 */
	GIcon setFrame(int fn){
		currFrame = PApplet.constrain(fn,  0,  nbrFrames - 1);
		return this;
	}

	/**
	 * Get the image to be displayed for the specified frame number. The supplied frame number 
	 * will be constrained to a valid value.
	 * @param fn the frame image to retrieve.
	 * @return this icon
	 */
	public PImage getFrame(int fn){
		fn = PApplet.constrain(Math.abs(fn), 0, nbrFrames-1);
		return img[fn];
	}

	/**
	 * @return the image for the current frame to be displayed.
	 */
	public PImage getFrame(){
		return img[currFrame];		
	}

}
