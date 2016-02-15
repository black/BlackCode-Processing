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

import processing.core.*;

import java.util.ArrayList;
import java.util.Iterator;

class BezierSpline {
	public static final int NO_HANDLE = -1;
	public static final int N_HANDLE = 0;
	public static final int NE_HANDLE = 1;
	public static final int E_HANDLE = 2;
	public static final int SE_HANDLE = 3;
	public static final int S_HANDLE = 4;
	public static final int SW_HANDLE = 5;
	public static final int W_HANDLE = 6;
	public static final int NW_HANDLE = 7;

	public static final int SELECTOR_MODE = 0;
	public static final int PEN_MODE = 1;
	public static final int SPLINE_SELECTOR_MODE = 2;

	ArrayList<BezierVertex> vertices;
	PApplet p;

	PVector handles[];

	int selectedHandle;
	boolean selected;
	PVector nextTangent;

	BezierEditor e;

	float max_right = 0, max_left = 0, max_up = 0, max_down = 0;

	BezierSpline(PApplet p, BezierEditor e) {
		vertices = new ArrayList<BezierVertex>();
		handles = new PVector[8];
		selectedHandle = NO_HANDLE;
		selected = false;
		this.p = p;
		this.e = e;
	}

	public BezierSpline newInstance() {
			BezierSpline newSpline = new BezierSpline(p, e);
			newSpline.selectedHandle = selectedHandle;
			newSpline.selected = selected;

			for (Object o : vertices) {
					BezierVertex v = (BezierVertex) o;
					newSpline.addVertex(v.newInstance());
			}

			newSpline.nextTangent = new PVector(nextTangent.x, nextTangent.y);
			newSpline.calculatePoints();
			newSpline.calculateHandles();
			return newSpline;
	}

	public void setVerticesFromOtherSpline(BezierSpline a) {
			vertices = new ArrayList<BezierVertex>();

			for (Object o : a.vertices) {
					BezierVertex v = (BezierVertex) o;
					//System.out.print(v.cp1.x + ", " + v.cp2.y);
					this.addVertex(v.newInstance());
			}

			nextTangent = new PVector(a.nextTangent.x, a.nextTangent.y);
			//calculatePoints();
			//calculateHandles();
	}

	void addVertex(BezierVertex v) {
		vertices.add(v);
	}


	void calculateHandles() {
		handles[SW_HANDLE] = new PVector(max_left, max_down);
		handles[NW_HANDLE] = new PVector(max_left, max_up);
		handles[NE_HANDLE] = new PVector(max_right, max_up);
		handles[SE_HANDLE] = new PVector(max_right, max_down);

		float dy = (max_up - max_down) / 2.0f;
		float dx = (max_right - max_left) / 2.0f;

		handles[N_HANDLE] = new PVector(max_left + dx, max_up);
		handles[S_HANDLE] = new PVector(max_left + dx, max_down);
		handles[E_HANDLE] = new PVector(max_right, max_down + dy);
		handles[W_HANDLE] = new PVector(max_left, max_down + dy);
	}


		int detectSelectedHandle(float m_x, float m_y, float r) {
			PVector m = new PVector(m_x, m_y);

			float min_dist = 0;
			int min_dist_handle = -1;
			for (int i = 0; i < 8; i++) {
				float dist = m.dist(handles[i]);
				if (dist <= r && (min_dist_handle == -1 || dist < min_dist )) {
					dist = min_dist;
					min_dist_handle = i;
				}
			}

			if (min_dist_handle != -1) {
				return min_dist_handle;
			} else {
				return NO_HANDLE;
			}
		}

	void scaleHorizontally(float scale, float origin) {
		if (Float.isNaN(scale)) {
			System.out.println("NaN detected"); // debug
			return;
		}

		for (int i = 0; i < vertices.size(); i++ ) {
			BezierVertex v = vertices.get(i);

			v.anchor.x -= origin;
			v.cp1.x -= origin;
			v.cp2.x -= origin;

			v.anchor.x *= scale;
			v.cp1.x *= scale;
			v.cp2.x *= scale;

			v.anchor.x += origin;
			v.cp1.x += origin;
			v.cp2.x += origin;
		}

		nextTangent.x -= origin;
		nextTangent.x *= scale;
		nextTangent.x += origin;

		//calculatePoints();
		//calculateHandles();
	}

	void scaleVertically(float scale, float origin) {
		if (Float.isNaN(scale)) {
			System.out.println("NaN detected"); // debug
			return;
		}
		for (int i = 0; i < vertices.size(); i++ ) {
			BezierVertex v = vertices.get(i);

			v.anchor.y -= origin;
			v.cp1.y -= origin;
			v.cp2.y -= origin;

			v.anchor.y *= scale;
			v.cp1.y *= scale;
			v.cp2.y *= scale;

			v.anchor.y += origin;
			v.cp1.y += origin;
			v.cp2.y += origin;
		}

		nextTangent.y -= origin;
		nextTangent.y *= scale;
		nextTangent.y += origin;

		//calculatePoints();
		//calculateHandles();
	}

		@SuppressWarnings("static-access")
	void calculatePoints() {
		PVector lastAnchor = null;
		if (vertices.size() > 0) {
			lastAnchor = vertices.get(0).anchor;
			max_right = lastAnchor.x;
			max_left = lastAnchor.x;
			max_up = lastAnchor.y;
			max_down = lastAnchor.y;
		}
		for (int i = 1; i < vertices.size(); i++) {
			BezierVertex v = vertices.get(i);

			// calculate the constants
			PVector c = new PVector(3 * (v.cp1.x - lastAnchor.x),
															3 * (v.cp1.y - lastAnchor.y));

			PVector b = new PVector(3 * (v.cp2.x - v.cp1.x) - c.x,
															3 * (v.cp2.y - v.cp1.y) - c.y);

			PVector a = new PVector(v.anchor.x - lastAnchor.x - c.x - b.x,
															v.anchor.y - lastAnchor.y - c.y - b.y);

			// calculate the points
			float x = 0;
			float y = 0;
			for (float t = 0; t <= 1.0; t += 0.001) {
				x = a.x * t * t * t + b.x * t * t + c.x * t + lastAnchor.x;
				y = a.y * t * t * t + b.y * t * t + c.y * t + lastAnchor.y;

				if (x < max_left) {
					max_left = x;
				}

				if (x > max_right) {
					max_right = x;
				}

				if (y < max_down) {
					max_down = y;
				}

				if (y > max_up) {
					max_up = y;
				}
			}


			lastAnchor = v.anchor;
		}
		p.noFill();
		p.stroke(255, 0, 255);
		p.rectMode(p.CORNER);
		calculateHandles();
	}

	float getShortestDistance(float m_x, float m_y) {
		PVector m = new PVector(m_x, m_y);
		float shortestDistance = -1;

		PVector lastAnchor = null;
		if (vertices.size() > 0) {
			lastAnchor = vertices.get(0).anchor;
			shortestDistance = m.dist(lastAnchor);
		}
		for (int i = 1; i < vertices.size(); i++) {
			BezierVertex v = vertices.get(i);

			// calculate the constants
			PVector c = new PVector(3 * (v.cp1.x - lastAnchor.x),
															3 * (v.cp1.y - lastAnchor.y));

			PVector b = new PVector(3 * (v.cp2.x - v.cp1.x) - c.x,
															3 * (v.cp2.y - v.cp1.y) - c.y);

			PVector a = new PVector(v.anchor.x - lastAnchor.x - c.x - b.x,
															v.anchor.y - lastAnchor.y - c.y - b.y);

			// calculate the points
			float x = 0;
			float y = 0;
			for (float t = 0; t <= 1.0; t += 0.001) {
				x = a.x * t * t * t + b.x * t * t + c.x * t + lastAnchor.x;
				y = a.y * t * t * t + b.y * t * t + c.y * t + lastAnchor.y;

				float d = m.dist(new PVector(x, y));
				if (d < shortestDistance) {
					shortestDistance = d;
				}
			}

			lastAnchor = v.anchor;
		}

		return shortestDistance;
	}

	void moveSpline(float dx, float dy) {
		for (int i = 0; i < vertices.size(); i++) {
			BezierVertex v = vertices.get(i);
			v.cp1.x += dx;
			v.cp1.y += dy;
			v.cp2.x += dx;
			v.cp2.y += dy;
			v.anchor.x += dx;
			v.anchor.y += dy;
		}

		nextTangent.x += dx;
		nextTangent.y += dy;
	}

	void moveVertex(BezierVertex v, float x, float y) {
		float dx = x - v.anchor.x;
		float dy = y - v.anchor.y;

		v.anchor.x = x;
		v.anchor.y = y;

		v.cp2.x += dx;
		v.cp2.y += dy;

		int index = this.vertices.indexOf(v);
		if (index != -1 && index + 1 < this.vertices.size()) {
			BezierVertex nextVertex = this.vertices.get(index + 1);
			nextVertex.cp1.x += dx;
			nextVertex.cp1.y += dy;
		} else if (index + 1 == vertices.size()) {
			nextTangent.x += dx;
			nextTangent.y += dy;
		}
	}

	void addAutoTangentSegment(PVector anchor) {
		PVector lastTangent = (PVector) this.getLastVertex().cp2;
		PVector tangent = new PVector(lastTangent.x, lastTangent.y);
		PVector lastAnchor = (PVector) this.getLastVertex().anchor;

		// adjust to tangent in other direction
		tangent.x -= lastAnchor.x;
		tangent.y -= lastAnchor.y;

		tangent.x *= -1;
		tangent.y *= -1;
		tangent.x += lastAnchor.x;
		tangent.y += lastAnchor.y;

		vertices.add(new BezierVertex(tangent, new PVector(anchor.x, anchor.y), anchor));

	}

	void addNextVertex(PVector anchor) {
		vertices.add(new BezierVertex(nextTangent, new PVector(anchor.x, anchor.y), anchor));
	}

	void replaceLastTangent(PVector cp) {
		BezierVertex v = vertices.get(vertices.size() - 1);
		v.cp2 = cp;
	}

	BezierVertex getLastVertex() {
		return vertices.get(vertices.size() - 1);
	}

	int numberVertices() {
		return vertices.size();
	}

	void drawSelectionHandles() {
		p.rectMode(PApplet.CENTER);

		for (int i = 0; i < 8; i++) {
			p.rect(handles[i].x, handles[i].y, 6, 6);
		}
	}

	// TODO: convert to screen space (only needed if zoom is implemented
	PVector s(PVector c) {
		PVector n = new PVector(c.x, c.y);

		return n;
	}

	void bezierVertexScreen(float cp1_x, float cp1_y, float cp2_x, float cp2_y, float anchor_x, float anchor_y) {
			PVector cp1 = s(new PVector(cp1_x, cp1_y));
			PVector cp2 = s(new PVector(cp2_x, cp2_y));
			PVector anchor = s(new PVector(anchor_x, anchor_y));

			p.bezierVertex(cp1.x, cp1.y, cp2.x, cp2.y, anchor.x, anchor.y);
	}

	void draw(int mode, boolean drawFill) {
		calculatePoints();
		if (selected && mode == SPLINE_SELECTOR_MODE) {
			// draw outline box
			p.rect(max_left, max_down, max_right - max_left, max_up - max_down);
			drawSelectionHandles();
		}

		Iterator<BezierVertex> it = vertices.iterator();
		if (it.hasNext()) {
			if (drawFill) {
					p.fill(0, 153, 255, 50);
			} else {
					p.noFill();
			}

			p.stroke(0);
			p.beginShape();
			// first vertex is just an anchor
			BezierVertex anchorVertex = it.next();
			PVector anchorScreen = s(anchorVertex.anchor);
			p.vertex(anchorScreen.x, anchorScreen.y);

			// draw the rest of the vertices if there are more
			while (it.hasNext()) {
				BezierVertex v = it.next();
				bezierVertexScreen(v.cp1.x, v.cp1.y, v.cp2.x, v.cp2.y, v.anchor.x, v.anchor.y);
			}


			p.endShape();

			BezierVertex oldV = null;
			BezierVertex nextV = null;

			if (selected) {
				for (int i = 0; i < this.vertices.size(); i++) {
					BezierVertex v = this.vertices.get(i);

					this.drawAnchor(v.anchor, v.selected);

					if (v.selected) {
						this.drawTangent(v.anchor, v.cp2);

						if (i > 0) {
							oldV = vertices.get(i - 1);
							this.drawTangent(oldV.anchor, v.cp1);
						}

						if (i < vertices.size() - 1) {
							nextV = vertices.get(i + 1);
							drawTangent(v.anchor, nextV.cp1);

							drawTangent(nextV.anchor, nextV.cp2);
						} else if (i == vertices.size() - 1) {
							drawTangent(v.anchor, nextTangent);
						}
					}
				}
			}

			/*if (this.vertices.size() >= 2 && selected) {

				p.stroke(0, 0, 255);
				p.beginShape();
				p.vertex(lastLastVertex.anchor.x, lastLastVertex.anchor.y);
				p.bezierVertex(lastVertex.cp1.x, lastVertex.cp1.y, lastVertex.cp2.x, lastVertex.cp2.y, lastVertex.anchor.x, lastVertex.anchor.y);
				p.endShape();
			} */
		}
	}

	PVector createAutoTangent(PVector anchor, PVector cp) {
		return new PVector((-1 * (cp.x - anchor.x)) + anchor.x, (-1 * (cp.y - anchor.y)) + anchor.y);
	}

	public static void applyAutoTangent(PVector autoTangent, PVector anchor, PVector cp) {
			autoTangent.x = -1 * (cp.x - anchor.x) + anchor.x;
			autoTangent.y = -1 * (cp.y - anchor.y) + anchor.y;
	}

	void drawTangent(PVector anchor, PVector cp) {
		p.stroke(0, 0, 255);
		p.line(anchor.x, anchor.y, cp.x, cp.y);
		p.ellipse(cp.x, cp.y, 4, 4);
		p.stroke(0);
	}

	void drawAnchor(PVector anchor, boolean selected) {
		p.rectMode(PApplet.CENTER);
		p.stroke(155);
		if (selected) {
			p.fill(155);
		} else {
			p.noFill();
		}
		p.rect(anchor.x, anchor.y, 6, 6);
		p.stroke(0);
		p.noFill();
	}

	String generateCode() {
		String code = "";

		if (vertices.size() > 0) {
			code += "beginShape();\n";

			BezierVertex v = vertices.get(0);
			code += "vertex(" + v.anchor.x + ", " + v.anchor.y +");\n";
			for (int i = 0; i < vertices.size(); i++) {
				v = vertices.get(i);
				code += "bezierVertex(" + v.cp1.x + ", " + v.cp1.y + ", "
								+ v.cp2.x + ", " + v.cp2.y + ", " + v.anchor.x + ", "
								+ v.anchor.y + ");\n";
			}

			code += "endShape();\n";
		}
		return code;
	}
}



