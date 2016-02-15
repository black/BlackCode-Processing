/**
 * Andrew's Utilities (AULib)
 * Motion blur, fields, easing, waves, uniformly-spaced curves, globs, and more!
 * http://imaginary-institute.com/resources/AULibrary/AULib.php
 *
 * Copyright (c) 2014-5 Andrew Glassner Andrew Glassner http://glassner.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Andrew Glassner http://glassner.com
 * @modified    08/01/2015
 * @version     2.2.1 (221)
 */

package AULib;


import processing.core.*;

/**
 * 
 * @example AUField_demo 
 * @example AUField_WithMask_demo
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

/*************************************************
* FIELDS
*************************************************/	

public class AUField {
	
	// theSketch is a reference to the parent sketch
	PApplet theSketch;

	public static final int FIELD_RED = 0;      //   use the red component
	public static final int FIELD_GREEN = 1;    //   use the green component
	public static final int FIELD_BLUE = 2;     //   use the blue component
	public static final int FIELD_AVG_RGB = 3;  //   use the average of the three components
	public static final int FIELD_LUM = 4;      //   use the luminance (.3*red) + (.59*green) + (.11*blue)
	
	public float[][] z;
	public int w, h;
	
	public AUField(PApplet _theSketch, int _wid, int _hgt) {
		if (_theSketch == null) {
			AULib.reportError("AUColorField", "AUColorField", "theSketch is null", "");
		}
		theSketch = _theSketch;
		w = Math.max(1, _wid);
		h = Math.max(1, _hgt);
		z = new float[_hgt][_wid];
		flatten(0);
	}
	
	public void flatten(float _v) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				z[y][x] = _v;
			}
		}
	}
	
	public void setRange(float _zmin, float _zmax) {
		float fmin = z[0][0];
		float fmax = z[0][0];
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float zv = z[y][x];
				fmin = Math.min(fmin, zv);
				fmax = Math.max(fmax, zv);
			}
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				z[y][x] = AUMisc.jmap(z[y][x], fmin, fmax, _zmin, _zmax);
			}
		}
	}
	
	public void normalize() {
		setRange(0, 1);
	}
	
	public void add(float _a) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				z[y][x] += _a;
			}
		}
	}
	
	public void mul(float _a) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				z[y][x] *= _a;
			}
		}
	}
	
	public void dissolve(float _target, float _blend) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float v = z[y][x];
				z[y][x] = v + (_blend * (_target-v));
			}
		}
	}
	
	public void dissolve(AUField _target, float _blend) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float v = z[y][x];
				z[y][x] = v + (_blend * (_target.z[y][x]-v));
			}
		}
	}
	
	public void constrain(float _minv, float _maxv) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float v = z[y][x];
				z[y][x] = Math.min(_maxv, Math.max(_minv, z[y][x]));
			}
		}
	}
	
	public void dissolve(AUField _target, AUField _blend) {
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float v = z[y][x];
				z[y][x] = v + (_blend.z[y][x] * (_target.z[y][x]-v));
			}
		}
	}
	
	public void add(AUField _f) {
		if ((_f.w != w) || (_f.h != h)) {
			AULib.reportError("AUField", "add", "the two fields do not have the same size", "");
			return;
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				z[y][x] += _f.z[y][x];
			}
		}
	}
	
	public void mul(AUField _f) {
		if ((_f.w != w) || (_f.h != h)) {
			AULib.reportError("AUField", "mul", "the two fields do not have the same size", "");
			return;
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				z[y][x] *= _f.z[y][x];
			}
		}
	}
	
	public AUField dupe() {
		AUField f = new AUField(theSketch, w, h);
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				f.z[y][x] = z[y][x];
			}
		}
		return f;
	}
	
	public void copy(AUField _dst) {
		if ((_dst.w != w) || (_dst.h != h)) {
			AULib.reportError("AUField", "copy", "the two fields do not have the same size", "");
			return;
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				_dst.z[y][x] = z[y][x];
			}
		}
	}
	
	void loadFromPixels(int _valueType, int[] _pixels, int _pixwid, int _pixhgt) {
		int wid = _pixwid;
		int hgt = _pixhgt;
		for (int y=0; y<h; y++) {
			if (y >= hgt) continue;
			for (int x=0; x<w; x++) {
				if (x >= wid) continue;
				int c = 0;
				c = _pixels[(y*wid)+x];
				float v = 0;
				switch (_valueType) {
					case FIELD_RED: v = (c>>16) & 0xFF; break;
					case FIELD_GREEN: v = (c>>8) & 0xFF; break;
					case FIELD_BLUE: v = c & 0xFF; break;
					default:
					case FIELD_AVG_RGB: v = (((c>>16) & 0xFF)+((c>>8) & 0xFF)+(c & 0xFF))/3.f; break;
					case FIELD_LUM: v = (.3f * ((c>>16) & 0xFF)) + (.59f * ((c>>8) & 0xFF)) + (.11f * (c & 0xFF)); break;
				}
				z[y][x] = v; 
			}
		}
	}
		
	public void fromPixels(int _valueType) {
		theSketch.loadPixels();
		int[] pixels = theSketch.pixels;
		loadFromPixels(_valueType, pixels, theSketch.width, theSketch.height);
	}
	
	public void fromPixels(int _valueType, PGraphics _pg) {
		_pg.loadPixels();
		int[] pixels = _pg.pixels;
		loadFromPixels(_valueType, pixels, _pg.width, _pg.height);
	}
	
	public void fromPixels(int _valueType, PImage _img) {
		_img.loadPixels();
		int[] pixels = _img.pixels;
		loadFromPixels(_valueType, pixels, _img.width, _img.height);
	}
	
	// the offset in x and y, the mask (and offset), and the destination pixels (and width and height)
	void writeToPixels(int _dx, int _dy, AUField _mask, int _mx, int _my, int[] _destPixels, int _destWid, int _destHgt) {
		for (int y=0; y<h; y++) {
			int destY = y+_dy;
			if ((destY < 0) || (destY >= _destHgt)) continue; // outside the target bounds?
			for (int x=0; x<w; x++) {	
				int destX = x+_dx;								
				if ((destX < 0) || (destX >= _destWid)) continue; // outside the target bounds?
				int destIndex = (destY*_destWid)+destX; 				
				int newColor = 0;
				int fieldGray = Math.round(z[y][x]);	
				if (_mask == null) {
					newColor = (0xFF << 24) | ((fieldGray & 0xFF) << 16) | ((fieldGray & 0xFF) << 8) | (fieldGray & 0xFF);
				} else {
					int maskX = x + _mx;
					int maskY = y + _my;
					float fieldAlpha = 1.f;
					if ((maskX >= 0) && (maskX < _mask.w) && (maskY >= 0) && (maskY < _mask.h)) {
						fieldAlpha = _mask.z[maskY][maskX] / 255.f;
					}
					int pixelColor = _destPixels[destIndex];
					float pixelRed = (pixelColor >> 16) & 0xFF;
					float pixelGreen = (pixelColor >> 8) & 0xFF;
					float pixelBlue = pixelColor & 0xFF;
					int pa = (pixelColor >> 24) & 0xFF;
					float pixelAlpha = pa / 255.f;
					
					float blendF = fieldAlpha;
					float blendP = pixelAlpha;

					// The following lines are from the old days when I had a separate routine just for writing
					// to PGraphics objects. We don't need this now, but I'm keeping it here for a while in case
					// I want to undo these changes.
					//float blendP = 1.f;
					//if (_pg != null) {   // the screen is always opaque, but PGraphics might not be
					//	blendF = fieldAlpha;
					//	blendP = pixelAlpha; // * (1.f - fieldAlpha); <- Alvy's scale factor not needed any more
					//}
					
					// use my blending algorithm to support when blendP != 1
					float blendAlpha = blendP + ((1.f - blendP) * blendF);
					if (blendAlpha != 0) {
						float kappa = 1.f - blendF;
						float k2 = 1.f - kappa;
						float blendRed   = ((fieldGray * k2) + (pixelRed * kappa))/blendAlpha;
						float blendGreen = ((fieldGray * k2) + (pixelGreen * kappa))/blendAlpha;
						float blendBlue  = ((fieldGray * k2) + (pixelBlue * kappa))/blendAlpha;
						int ired = Math.round(blendRed);
						int igrn = Math.round(blendGreen);
						int iblu = Math.round(blendBlue);
						int ialf = Math.round(255.f * blendAlpha); 
						
						newColor = ((ialf & 0xFF) << 24) | ((ired & 0xFF) << 16) | ((igrn & 0xFF) << 8) | (iblu & 0xFF);
					}
				}
				_destPixels[destIndex] = newColor;
			}
		}				
	}
	
	// And now the nine different ways we can write to pixels! There are three basic calls:
	//   1. write to this upper-left
	//   2. write to this upper-left with this mask
	//   3. write to this upper-left with this mask, which is offset by these values
	// For each of these three call, there are three variations:
	//   A. write to the screen
	//   B. write to a PGraphics object
	//   C. write to a PImage object
	// So that's a total of nine calls. They just call writeToPixels() with slightly different arguments.
	
	public void toPixels(float _dx, float _dy) {
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, Math.round(_dx), Math.round(_dy), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	
	public void toPixels(float _dx, float _dy, AUField _mask) {
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, Math.round(_dx), Math.round(_dy), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	
	public void toPixels(float _dx, float _dy, AUField _mask, float _mx, float _my) {
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, Math.round(_mx), Math.round(_my), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	
	public void toPixels(float _dx, float _dy, PGraphics _pg) {
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, Math.round(_dx), Math.round(_dy), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}

	public void toPixels(float _dx, float _dy, AUField _mask, PGraphics _pg) {
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, Math.round(_dx), Math.round(_dy), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}
	
	public void toPixels(float _dx, float _dy, AUField _mask, float _mx, float _my, PGraphics _pg) {
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, Math.round(_mx), Math.round(_my), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}
	
	public void toPixels(float _dx, float _dy, PImage _img) {
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, Math.round(_dx), Math.round(_dy), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}

	public void toPixels(float _dx, float _dy, AUField _mask, PImage _img) {
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, Math.round(_dx), Math.round(_dy), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}
	
	public void toPixels(float _dx, float _dy, AUField _mask, float _mx, float _my, PImage _img) {
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, Math.round(_mx), Math.round(_my), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}
}
