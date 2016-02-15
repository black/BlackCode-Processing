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

import java.awt.Dimension;

import processing.core.*;


// This applet was originally written in PDE, which explains why I didn't use
// some niceties like generics -- PDE doesn't support them.
// The tool is basically this applet, along with a few buttons.
@SuppressWarnings("serial")
public class BezierApplet extends PApplet {
	static final int WIDE = 480;
	static final int HIGH = 480;

	BezierEditor bezierEditor;
	public BezierEditorTool bezierTool;

	BezierApplet(BezierEditorTool bezierTool) {
		bezierEditor = new BezierEditor(this);
		this.bezierTool = bezierTool;
	}

	public void setup() {
		size(WIDE, HIGH);
		smooth();
		noLoop();
	}

	public void draw() {
		background(255);
		pushMatrix();

		translate(bezierEditor.screenPan.x, bezierEditor.screenPan.y);

		bezierEditor.drawSplines();
		stroke(0, 70);
		line(0, 0, 0, HIGH);
		line(0, 0, WIDE, 0);

		popMatrix();



		// TODO: draw grid here
	}

	public void updateLocation() {
		bezierTool.updateLocation(mouseX - bezierEditor.screenPan.x, mouseY - bezierEditor.screenPan.y);
	}

	public void mouseMoved() {
		updateLocation();
	}

	public void mousePressed() {
		bezierEditor.mousePressed(mouseX - bezierEditor.screenPan.x,
															mouseY - bezierEditor.screenPan.y,
															mouseX, mouseY);
		redraw();
	}

	public void mouseDragged() {
		bezierEditor.mouseDragged(mouseX - bezierEditor.screenPan.x,
															mouseY - bezierEditor.screenPan.y,
															mouseX, mouseY);

		updateLocation();

		redraw();
	}

	public void keyPressed() {
		if (key == CODED) {
			bezierEditor.codedKeyPressed(keyCode);
		} else {
			bezierEditor.keyPressed(key);
		}
	}

	public void keyReleased() {
		if (key == CODED) {
			bezierEditor.codedKeyReleased(keyCode);
		}
	}

	public Dimension getPreferredSize() {
		//System.out.println("getting pref " + WIDE + " " + HIGH);
		return new Dimension(WIDE, HIGH);
	}

	public Dimension getMinimumSize() {
		//System.out.println("getting min " + WIDE + " " + HIGH);
		return new Dimension(WIDE, HIGH);
	}

	public Dimension getMaximumSize() {
		//System.out.println("getting max " + WIDE + " " + HIGH);
		return new Dimension(WIDE, HIGH);
	}
}
