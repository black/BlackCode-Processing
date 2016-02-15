/*
    Copyright 2008, 2009, 2013 Devon Rifkin

    This file is part of the Bezier Editor.

    The Bezier Editor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Bezier Editor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Bezier Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.drifkin.bezier_editor;

import processing.core.PVector;

class BezierVertex {
	PVector cp1, cp2, anchor;
	boolean selected;

	BezierVertex(PVector cp1, PVector cp2, PVector anchor) {
		this.cp1 = cp1;
		this.cp2 = cp2;
		this.anchor = anchor;
		this.selected = false;
	}

	public BezierVertex newInstance () {
		return new BezierVertex( new PVector(cp1.x, cp1.y),
		                         new PVector(cp2.x, cp2.y),
		                         new PVector(anchor.x, anchor.y) );
	}
}
