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
 * @example AUMultiField_demo 
 * @example AUMultiField_WithMask_demo 
 * @example AUMultiField_Compositing_demo
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

/*************************************************
* MULTIFIELDS
*************************************************/	

public class AUMultiField {
	
	// theSketch is a reference to the parent sketch
	PApplet theSketch;
	
	public AUField[] fields;
	public int w, h;
	
	/**********************
	 * Constructors
	 *********************/

	public AUMultiField(PApplet _theSketch, int _numFields, int _wid, int _hgt) {
		if (_theSketch == null) {
			AULib.reportError("AUMultiField", "AUMultiField", "theSketch is null", "");
		}
		theSketch = _theSketch;
		w = Math.max(1, _wid);
		h = Math.max(1, _hgt);
		_numFields = Math.max(1, _numFields);
		fields = new AUField[_numFields];
		for (int i=0; i<_numFields; i++) {
			 fields[i] = new AUField(theSketch, _wid, _hgt);
		}
	}
	
	/**********************
	 * Flatten
	 *********************/
	
	public void flattenRGBA(float _fr, float _fg, float _fb, float _fa) {
		if (fields.length < 4) {
			AULib.reportError("AUMultiField", "flattenRGBA", "less than 4 fields avialable", "fields.length="+Float.toString(fields.length));
			return;
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				fields[0].z[y][x] = _fr;
				fields[1].z[y][x] = _fg;
				fields[2].z[y][x] = _fb;
				fields[3].z[y][x] = _fa;
			}
		}
	}
	
	public void flattenRGB(float _fr, float _fg, float _fb) {
		if (fields.length < 3) {
			AULib.reportError("AUMultiField", "flattenRGB", "less than 3 fields avialable", "fields.length="+Float.toString(fields.length));
			return;
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				fields[0].z[y][x] = _fr;
				fields[1].z[y][x] = _fg;
				fields[2].z[y][x] = _fb;
			}
		}
	}

	public void flatten(float _v) {
		for (int f=0; f<fields.length; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					fields[f].z[y][x] = _v;
				}
			}
		}
	}
	
	/**********************
	 * from pixels
	 *********************/

	/*
	void loadFromPixels(boolean _saveAlpha, PGraphics _pg) {
		int fieldsNeeded = 3;
		if (_saveAlpha) fieldsNeeded = 4;
		if (fields.length < fieldsNeeded) {
			AULib.reportError("AUMultiField", "RGBAfromPixels", "there are not at least "+fieldsNeeded+" fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		int wid = 0;
		int hgt = 0;
		int[] thesePixels;
		if (_pg != null) {
			wid = _pg.width;
			hgt = _pg.height;
			_pg.loadPixels();
			thesePixels = _pg.pixels;
		} else {
			wid = theSketch.width;
			hgt = theSketch.height;
			theSketch.loadPixels();
			thesePixels = theSketch.pixels;
		}
		for (int y=0; y<h; y++) {
			if (y >= hgt) continue;
			for (int x=0; x<w; x++) {
				if (x >= wid) continue;
				int c = thesePixels[(y*wid)+x];
				fields[0].z[y][x] = (c>>16) & 0xFF;//AUMisc.jred(c);
				fields[1].z[y][x] = (c>>8) & 0xFF;//AUMisc.jgreen(c);				
				fields[2].z[y][x] = c & 0xFF;//AUMisc.jblue(c);
				if (_saveAlpha) fields[3].z[y][x] = (c>>24) & 0xFF;//AUMisc.jalpha(c);
			}
		}
	}
	*/
	void loadFromPixels(boolean _saveAlpha, int[] _pixels, int _pixwid, int _pixhgt) {
		int fieldsNeeded = 3;
		if (_saveAlpha) fieldsNeeded = 4;
		if (fields.length < fieldsNeeded) {
			AULib.reportError("AUMultiField", "RGBAfromPixels", "there are not at least "+fieldsNeeded+" fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		int wid = _pixwid;
		int hgt = _pixhgt;
		for (int y=0; y<h; y++) {
			if (y >= hgt) continue;
			for (int x=0; x<w; x++) {
				if (x >= wid) continue;
				int c = _pixels[(y*wid)+x];
				fields[0].z[y][x] = (c>>16) & 0xFF;//AUMisc.jred(c);
				fields[1].z[y][x] = (c>>8) & 0xFF;//AUMisc.jgreen(c);				
				fields[2].z[y][x] = c & 0xFF;//AUMisc.jblue(c);
				if (_saveAlpha) fields[3].z[y][x] = (c>>24) & 0xFF;//AUMisc.jalpha(c);
			}
		}
	}
	
	public void RGBfromPixels() {
		theSketch.loadPixels();
		loadFromPixels(false, theSketch.pixels, theSketch.width, theSketch.height);
	}
	
	public void RGBAfromPixels() {
		theSketch.loadPixels();
		loadFromPixels(true, theSketch.pixels, theSketch.width, theSketch.height);
	}
	
	public void RGBfromPixels(PGraphics _pg) {
		_pg.loadPixels();
		loadFromPixels(false, _pg.pixels, _pg.width, _pg.height);
	}
	
	public void RGBAfromPixels(PGraphics _pg) {
		_pg.loadPixels();
		loadFromPixels(true, _pg.pixels, _pg.width, _pg.height);
	}
	
	public void RGBfromPixels(PImage _img) {
		_img.loadPixels();
		loadFromPixels(false, _img.pixels, _img.width, _img.height);
	}
	
	public void RGBAfromPixels(PImage _img) {
		_img.loadPixels();
		loadFromPixels(true, _img.pixels, _img.width, _img.height);
	}
	
	/**********************
	 * Add constants
	 *********************/

	public void RGBAadd(float _fr, float _fg, float _fb, float _fa) {
		if (fields.length < 4) {
			AULib.reportError("AUMultiField", "RGBAadd", "there are not at least 4 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		fields[0].add(_fr);
		fields[1].add(_fg);
		fields[2].add(_fb);
		fields[3].add(_fa);
	}
	
	public void RGBadd(float _fr, float _fg, float _fb) {
		if (fields.length < 3) {
			AULib.reportError("AUMultiField", "RGBadd", "there are not at least 3 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		fields[0].add(_fr);
		fields[1].add(_fg);
		fields[2].add(_fb);
	}
	
	public void add(float _a) {
		for (int f=0; f<fields.length; f++) {
			fields[f].add(_a);
		}
	}
	
	/**********************
	 * Multiply constants
	 *********************/

	public void RGBAmul(float _mr, float _mg, float _mb, float _ma) {
		if (fields.length < 4) {
			AULib.reportError("AUMultiField", "RGBAmul", "there are not at least 4 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		fields[0].mul(_mr);
		fields[1].mul(_mg);
		fields[2].mul(_mb);
		fields[3].mul(_ma);
	}

	public void RGBmul(float _mr, float _mg, float _mb) {
		if (fields.length < 3) {
			AULib.reportError("AUMultiField", "RGBmul", "there are not at least 3 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		fields[0].mul(_mr);
		fields[1].mul(_mg);
		fields[2].mul(_mb);
	}
	
	public void mul(float _m) {
		for (int f=0; f<fields.length; f++) {
			fields[f].mul(_m);
		}
	}
	
	/**********************
	 * Add and Multiply MultiFields
	 *********************/

	public void add(AUMultiField _mf) {
		if ((_mf.w != w) || (_mf.h != h)) {
			AULib.reportError("AUMultiField", "add", "the two fields do not have the same size ", "");
			return;
		}
		int numFields = Math.min(_mf.fields.length, fields.length);
		for (int f=0; f<numFields; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					fields[f].z[y][x] += _mf.fields[f].z[y][x];
				}
			}
		}
	}
	
	public void mul(AUMultiField _mf) {
		if ((_mf.w != w) || (_mf.h != h)) {
			AULib.reportError("AUMultiField", "mul", "the two fields do not have the same size", "");
			return;
		}
		int numFields = Math.min(_mf.fields.length, fields.length);
		for (int f=0; f<numFields; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					fields[f].z[y][x] *= _mf.fields[f].z[y][x];
				}
			}
		}
	}
	
	/**********************
	 * Duplicate, copy, swap
	 *********************/
	
	public AUMultiField dupe() {
		AUMultiField mf = new AUMultiField(theSketch, fields.length, w, h);
		for (int f=0; f<fields.length; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					mf.fields[f].z[y][x] = fields[f].z[y][x];
				}
			}
		}
		return mf;
	}
	
	public void copy(AUMultiField _dst) {
		if ((_dst.w != w) || (_dst.h != h) || (_dst.fields.length != fields.length)) {
			AULib.reportError("AUMultiField", "copy", "the two fields do not have the same size or depth", "");
			return;
		}
		for (int f=0; f<fields.length; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					_dst.fields[f].z[y][x] = fields[f].z[y][x];
				}
			}
		}
	}
	
	public void copyFieldToField(int from, int to) {
		if ((fields.length <= from) || (fields.length <= to)) {
			AULib.reportError("AUMultiField", "copyField", "either to or from is larger than the number of fields available", "");
			return;
		}

		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				fields[to].z[y][x] = fields[from].z[y][x];
			}
		}
	}
	
	public void swapFields(int a, int b) {
		if ((fields.length <= a) || (fields.length <= b)) {
			AULib.reportError("AUMultiField", "swapFields", "either to or from is larger than the number of fields available", "");
			return;
		}
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				float old_a = fields[a].z[y][x];
				fields[a].z[y][x] = fields[b].z[y][x];
				fields[b].z[y][x] = old_a;
			}
		}
	}
	
	public void copySeveralFields(int from, int to, int n) {
		if (to > from) {
			for (int i=n-1; i>=0; i--) {
				copyFieldToField(from+i, to+i);
			}
		} else {
			for (int i=0; i<n; i++) {
				copyFieldToField(from+i, to+i);
			}
		}
	}

	public void swapSeveralFields(int a, int b, int n) {
		for (int i=0; i<n; i++) {
			swapFields(a+i, b+i);
		}
	}
	
	/**********************
	 * Set range
	 *********************/

	public void setRangeTogether(float _zmin, float _zmax, int _numFields) {
		int numFields = Math.min(fields.length, _numFields);
		float fmin = fields[0].z[0][0];
		float fmax = fmin;
		for (int f=0; f<numFields; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					float zv = fields[f].z[y][x];
					fmin = Math.min(fmin, zv);
					fmax = Math.max(fmax, zv);
				}
			}
		}
		for (int f=0; f<numFields; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					fields[f].z[y][x] = AUMisc.jmap(fields[f].z[y][x], fmin, fmax, _zmin, _zmax);
				}
			}
		}
	}

	public void setRangeSeparate(float _zmin, float _zmax, int _numFields) {
		int numFields = Math.min(fields.length, _numFields);
		float[] minVals = new float[fields.length];
		float[] maxVals = new float[fields.length];
		for (int f=0; f<numFields; f++) {
			minVals[f] = fields[f].z[0][0];
			maxVals[f] = minVals[f];
		}
		for (int f=0; f<numFields; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					float fv = fields[f].z[y][x];
					minVals[f] = Math.min(minVals[f], fv);
					maxVals[f] = Math.max(maxVals[f], fv);
				}
			}
		}

		for (int f=0; f<numFields; f++) {
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					fields[f].z[y][x] = AUMisc.jmap(fields[f].z[y][x], minVals[f], maxVals[f], _zmin, _zmax);
				}
			}
		}
	}
	
	public void setRangeTogether(float _zmin, float _zmax) {
		setRangeTogether(_zmin, _zmax, fields.length);
	}
	
	public void setRangeSeparate(float _zmin, float _zmax) {
		setRangeSeparate(_zmin, _zmax, fields.length);
	}

	public void normalizeTogether(int _numFields) {
		setRangeTogether(0, 1, _numFields);
	}

	public void normalizeSeparate(int _numFields) {
		setRangeSeparate(0, 1, _numFields);
	}
	
	public void normalizeTogether() {
		setRangeTogether(0, 1, fields.length);
	}

	public void normalizeSeparate() {
		setRangeSeparate(0, 1, fields.length);
	}
	
	public void normalizeRGBTogether() {
		setRangeTogether(0, 255, 3);
	}

	public void normalizeRGBSeparate() {
		setRangeSeparate(0, 255, 3);
	}
	
	public void normalizeRGBATogether() {
		setRangeTogether(0, 255, 4);
	}

	public void normalizeRGBASeparate() {
		setRangeSeparate(0, 255, 4);
	}
	
	/**********************
	 * Set triple and quad
	 *********************/
	
	public void setTriple(int _x, int _y, float _v0, float _v1, float _v2) {
		if ((_x < 0) || (_x >= w) || (_y < 0) || (_y >= h)) {
			AULib.reportError("AUMultiField", "setTriple", "the point x,y is beyond the field's bounds", "x="+Float.toString(_x)+" y="+Float.toString(_y));
		}
		if (fields.length < 3) {
			AULib.reportError("AUMultiField", "setTriple", "there are not at least 3 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		fields[0].z[_y][_x] = _v0;
		fields[1].z[_y][_x] = _v1;
		fields[2].z[_y][_x] = _v2;
	}
	
	public void setQuad(int _x, int _y, float _v0, float _v1, float _v2, float _v3) {
		if ((_x < 0) || (_x >= w) || (_y < 0) || (_y >= h)) {
			AULib.reportError("AUMultiField", "setQuad", "the point x,y is beyond the field's bounds", "x="+Float.toString(_x)+" y="+Float.toString(_y));
			return;
		}
		if (fields.length < 4) {
			AULib.reportError("AUMultiField", "setQuad", "there are not at least 4 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		fields[0].z[_y][_x] = _v0;
		fields[1].z[_y][_x] = _v1;
		fields[2].z[_y][_x] = _v2;
		fields[3].z[_y][_x] = _v3;
	}
		
	/**********************
	 * Set/Get a color
	 *********************/
	
	public void RGBAsetColor(int _x, int _y, int _c) {
		if (fields.length < 4) {
			AULib.reportError("AUMultiField", "RGBAsetColor", "there are not at least 4 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		int ired = (_c >> 16) & 0xFF;
		int igrn = (_c >> 8) & 0xFF;
		int iblu = (_c) & 0xFF;
		int ialpha = (_c >> 24) & 0xFF;
		setQuad(_x, _y, ired, igrn, iblu, ialpha);
		//setQuad(_x, _y, AUMisc.jred(_c), AUMisc.jgreen(_c), AUMisc.jblue(_c), AUMisc.jalpha(_c));
	}
	
	public void RGBsetColor(int _x, int _y, int _c) {
		if (fields.length < 3) {
			AULib.reportError("AUMultiField", "setColor", "there are not at least 3 fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		int ired = (_c >> 16) & 0xFF;
		int igrn = (_c >> 8) & 0xFF;
		int iblu = (_c) & 0xFF;
		setTriple(_x, _y, ired, igrn, iblu);
		//setTriple(_x, _y, AUMisc.jred(_c), AUMisc.jgreen(_c), AUMisc.jblue(_c));
	}
	
	int getColor(int _x, int _y, boolean _getAlpha) {
		int fieldsNeeded = 3;
		if (_getAlpha) fieldsNeeded = 4;
		if (fields.length < fieldsNeeded) {
			AULib.reportError("AUMultiField", "setColor", "there are not at least "+fieldsNeeded+" fields available", "fields.length="+Float.toString(fields.length));
			return 0;
		}
		int red = Math.round(fields[0].z[_y][_x]);
		int grn = Math.round(fields[1].z[_y][_x]);
		int blu = Math.round(fields[2].z[_y][_x]);
		int alf = 255;
		if (_getAlpha) alf = (int)(fields[3].z[_y][_x]);
		//int clr = AUMisc.jcolor(red, grn, blu, alf);
		int clr = ((alf&0xFF)<<24) | ((red&0xFF)<<16) | ((grn&0xFF)<<8) | (blu&0xFF);
		return clr;
	}
	
	public int getRGBAColor(int _x, int _y) {
		return getColor(_x, _y, true);
	}

	public int getRGBColor(int _x, int _y) {
		return getColor(_x, _y, false);
	}

	
	/**********************
	 * over
	 *********************/
	
	// This is very much like writeToPixels, but for efficiency I'm repeating it here for compositions between
	// AUMultiFields, rather than have to have tests at every pixel
	
	public void over(AUMultiField B) {
		over(B, null);
	}

	public void over(AUMultiField B, AUField _mask) {
		if (fields.length < 3) {
			AULib.reportError("AUMultiField", "over", "The source has only "+fields.length+" layers, and needs at least 3", "");
			return;
		}
		if (B.fields.length < 3) {
			AULib.reportError("AUMultiField", "over", "The destination has only "+B.fields.length+" layers, and needs at least 3", "");
			return;
		}
		if ((B.w != w) || (B.h != h)) {
			AULib.reportError("AUMultiField", "over", "the two fields do not have the same size", "");
			return;
		}
		AUField AalphaField = null;
		if (_mask != null) {
			AalphaField= _mask;
		} else if (fields.length > 3) {
			AalphaField = fields[3];
		}
		
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {

				float Ared = fields[0].z[y][x];
				float Agrn = fields[1].z[y][x];
				float Ablu = fields[2].z[y][x];
				float Aalf = 1.f;
				if (AalphaField != null) Aalf = AalphaField.z[y][x]/255.f;

				float Bred = B.fields[0].z[y][x];
				float Bgrn = B.fields[1].z[y][x];
				float Bblu = B.fields[2].z[y][x];
				float Balf = 1.f;
				if (B.fields.length > 3) Balf = B.fields[3].z[y][x]/255.f;
				
				float b2 = (1.f-Aalf) * Balf;
				float newAlf = Aalf + b2;
				
				if (newAlf == 0) {
					B.fields[0].z[y][x] = 0;
					B.fields[1].z[y][x] = 0;
					B.fields[2].z[y][x] = 0;
					if (B.fields.length > 3) B.fields[3].z[y][x] = 0;
				} else {
					B.fields[0].z[y][x] = ((Aalf * Ared) + (b2 * Bred))/newAlf;
					B.fields[1].z[y][x] = ((Aalf * Agrn) + (b2 * Bgrn))/newAlf;
					B.fields[2].z[y][x] = ((Aalf * Ablu) + (b2 * Bblu))/newAlf;
					if (B.fields.length > 3) B.fields[3].z[y][x] = newAlf * 255.f;
				}
			}
		}
	}
	
	/**********************
	 * write to Pixels
	 *********************/
	
	// Write out the pixels in this field to a destination. The upper-left corner of the field will appear
	// at (_dx, _dy) in the destination. The mask provides alpha blending. There's a boolean that lets you
	// use the 4th channel (that is, layer 3) as a mask, as usual for RGBA images. The mask is aligned to
	// the destination coordinate system; it is not aligned to the source image. The upper-left corner of
	// the mask is place at (_mx, _my). Finally, the rectangle of destination pixels if of size 
	// (_destWid, _destHgt) and the contents are in the pixel array _destPixels, packed as 8 bits each in
	// order RGBA into a 24-bit int in standard Processing format: ARGB (A in the MSB, B in the LSB).
	
	void writeToPixels(int _dx, int _dy, AUField _mask, boolean _useLayer3AsMask, int _mx, int _my, int[] _destPixels, int _destWid, int _destHgt) {
		if ((_mask != null) && (_useLayer3AsMask)) {
			AULib.reportError("AUMultiField", "writeToPixels", "you supplied a mask AND said to use layer 3 as alpha. Ignoring layer 3 and using the mask.", "");
			_useLayer3AsMask = false;
		}
		int fieldsNeeded = 3;
		if (_useLayer3AsMask) fieldsNeeded = 4;
		if (fields.length < fieldsNeeded) {
			AULib.reportError("AUMultiField", "writeToPixels", "there are not at least "+fieldsNeeded+" fields available", "fields.length="+Float.toString(fields.length));
			return;
		}
		int h = fields[0].h;
		int w = fields[0].w;
		AUField thisMask = null;
		if (_mask != null) thisMask = _mask;
		else if (_useLayer3AsMask) thisMask = fields[3];
		for (int y=0; y<h; y++) {
			int destY = y+_dy;
			if ((destY < 0) || (destY >= _destHgt)) continue; // outside the target bounds?
			for (int x=0; x<w; x++) {	
				int destX = x+_dx;								
				if ((destX < 0) || (destX >= _destWid)) continue; // outside the target bounds?
				int destIndex = (destY*_destWid)+destX; 
				int srcIndex = (y*w)+x;
				int pixelColor = _destPixels[destIndex];
				
				//System.out.println("x="+x+" y="+y+" destX="+destX+" destY="+destY+" destIndex="+destIndex+" _destWid="+(_destWid)+" _destHgt="+(_destHgt)+" w="+w+" h="+h);
				
				float fieldRed = fields[0].z[y][x];
				float fieldGreen = fields[1].z[y][x];
				float fieldBlue = fields[2].z[y][x];
				float fieldAlpha = 1;

				if (thisMask != null) {
					int maskX = x + _mx;
					int maskY = y + _my;
					if ((maskX >= 0) && (maskX < thisMask.w) && (maskY >= 0) && (maskY < thisMask.h)) {
						fieldAlpha = thisMask.z[maskY][maskX] / 255.f;
					}
				}
				
				float pixelRed = (pixelColor >> 16) & 0xFF; 
				float pixelGreen = (pixelColor >> 8) & 0xFF; 
				float pixelBlue = pixelColor & 0xFF; 
				float pixelAlpha = ((pixelColor >> 24) & 0xFF) / 255.f; 
				
				float blendF = fieldAlpha;
				float blendP = 1.f;
				// as with AUField, this is superfluous now that the write calls are unified, but leaving it in for a while
				//if (_pg != null) {   // the screen is always opaque, but PGraphics might not be
				//	blendF = fieldAlpha;
				//	blendP = pixelAlpha; // * (1.f - fieldAlpha); <- Alvy's scale factor not needed any more
				//}
				
				float blendRed = 0;
				float blendGreen = 0;
				float blendBlue = 0;
				
				// use my blending algorithm to support when blendP != 1
				float blendAlpha = blendP + ((1.f - blendP) * blendF);
				if (blendAlpha != 0) {
					float kappa = 1.f - blendF;
					float k2 = 1.f - kappa;
					blendRed   = ((fieldRed * k2) + (pixelRed * kappa))/blendAlpha;
					blendGreen = ((fieldGreen * k2) + (pixelGreen * kappa))/blendAlpha;
					blendBlue  = ((fieldBlue * k2) + (pixelBlue * kappa))/blendAlpha;
				}
				int ired = Math.round(blendRed);
				int igrn = Math.round(blendGreen);
				int iblu = Math.round(blendBlue);
				int ialf = Math.round(255.f * blendAlpha); 
				
				int newColor = ((ialf & 0xFF) << 24) | ((ired & 0xFF) << 16) | ((igrn & 0xFF) << 8) | (iblu & 0xFF);
								
				_destPixels[destIndex] = newColor;
			}
		}
	}
	
	
	// And now the 12 different ways we can write to pixels! There are four basic calls:
	//   1. write RGB to this upper-left
	//   2. write RGBA to this upper-left 
	//   3. write RGB to this upper-left with this mask
	//   4. write RGB to this upper-left with this mask, which is offset by these values
	// I don't include writing RGBA when a mask is present, because the mask provides the
	// alpha information on a per-pixel basis.
	//
	// For each of these three call, there are three variations:
	//   A. write to the screen
	//   B. write to a PGraphics object
	//   C. write to a PImage object
	// So that's a total of 12 calls. They just call writeToPixels() with slightly different arguments.
	
	public void RGBtoPixels(float _dx, float _dy) {  // copy opaque RGB values into pixels
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, false, Math.round(_dx), Math.round(_dy), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	
	public void RGBAtoPixels(float _dx, float _dy) {  // copy RGBA values into pixels
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, true, Math.round(_dx), Math.round(_dy), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	
	public void RGBtoPixels(float _dx, float _dy, AUField _mask) {  // use mask to mix in RGB
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, false, Math.round(_dx), Math.round(_dy), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	
	public void RGBtoPixels(float _dx, float _dy, AUField _mask, float _mx, float _my) {  // use mask to mix in RGB
		theSketch.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, false, Math.round(_mx), Math.round(_my), theSketch.pixels, theSketch.width, theSketch.height);
		theSketch.updatePixels();
	}
	

	
	public void RGBtoPixels(float _dx, float _dy, PGraphics _pg) {  // copy opaque RGB values into pixels
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, false, Math.round(_dx), Math.round(_dy), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}
	
	public void RGBAtoPixels(float _dx, float _dy, PGraphics _pg) {  // copy RGBA values into pixels
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, true, Math.round(_dx), Math.round(_dy), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}
	
	public void RGBtoPixels(float _dx, float _dy, AUField _mask, PGraphics _pg) {  // use mask to mix in RGB
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, false, Math.round(_dx), Math.round(_dy), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}
	
	public void RGBtoPixels(float _dx, float _dy, AUField _mask, float _mx, float _my, PGraphics _pg) {  // use mask to mix in RGB
		_pg.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, false, Math.round(_mx), Math.round(_my), _pg.pixels, _pg.width, _pg.height);
		_pg.updatePixels();
	}
	
	
	
	public void RGBtoPixels(float _dx, float _dy, PImage _img) {  // copy opaque RGB values into pixels
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, false, Math.round(_dx), Math.round(_dy), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}
	
	public void RGBAtoPixels(float _dx, float _dy, PImage _img) {  // copy RGBA values into pixels
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), null, true, Math.round(_dx), Math.round(_dy), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}
	
	public void RGBtoPixels(float _dx, float _dy, AUField _mask, PImage _img) {  // use mask to mix in RGB
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, false, Math.round(_dx), Math.round(_dy), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}
	
	public void RGBtoPixels(float _dx, float _dy, AUField _mask, float _mx, float _my, PImage _img) {  // use mask to mix in RGB
		_img.loadPixels();
		writeToPixels(Math.round(_dx), Math.round(_dy), _mask, false, Math.round(_mx), Math.round(_my), _img.pixels, _img.width, _img.height);
		_img.updatePixels();
	}
}



  