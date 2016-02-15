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

import java.util.ArrayList;

import processing.core.*;

public class BezierEditor {
	public static final int NO_HANDLE = -1;
	public static final int N_HANDLE = 0;
	public static final int NE_HANDLE = 1;
	public static final int E_HANDLE = 2;
	public static final int SE_HANDLE = 3;
	public static final int S_HANDLE = 4;
	public static final int SW_HANDLE = 5;
	public static final int W_HANDLE = 6;
	public static final int NW_HANDLE = 7;

	// TODO: change to real enum now that I'm not using PDE
	public static final int SELECTOR_MODE = 0;
	public static final int PEN_MODE = 1;
	public static final int SPLINE_SELECTOR_MODE = 2;
	public static final int PAN_MODE = 3;

	private BezierApplet p;

	ArrayList<BezierSpline> splines;

	boolean drawFill = false;


	private int mode;


	private boolean breakTangent;

	BezierSpline activeSpline;
	BezierVertex selectedVertex;
	PVector selectedTangent;
	PVector selectedTangentPartner;
	PVector selectedTangentAnchor;

	PVector lastMousePress;

	PVector dragBegin;
	PVector dragBeginCoords;
	PVector dragAmount = new PVector(0, 0);
	PVector originalPan;

	BezierSpline originalSpline;

	float screenZoom;
	PVector screenPan;

	BezierEditor(BezierApplet p) {
		this.splines = new ArrayList<BezierSpline>();
		this.mode = PEN_MODE;
		this.activeSpline = null;
		this.selectedVertex = null;
		this.p = p;
		this.screenZoom = 1.0f;
		this.screenPan = new PVector(0.0f, 0.0f);
		breakTangent = false;
	}

	void drawGrid() {
		PVector spacing = new PVector(p.width / 10, p.height / 10);

		for (int x = 0; x < 10; x += spacing.x) {

		}
	}


	void mousePressed(float m_x, float m_y, int screen_x, int screen_y) {
		dragBegin = new PVector(screen_x, screen_y);
		dragBeginCoords = new PVector(m_x, m_y);
		lastMousePress = new PVector(m_x, m_y);

		if (this.mode == SELECTOR_MODE) {
			// first try to find a tangent

			if (activeSpline != null) {
					if (selectedVertex != null) {
						PVector mouse = new PVector(m_x, m_y);
						float minDistance = 4;
						for (int i = 0; i < activeSpline.vertices.size(); i++) {
							BezierVertex v = (BezierVertex) activeSpline.vertices.get(i);

							//this.drawAnchor(v.anchor, v.selected);

							if (v.selected) {
									selectedTangentAnchor = null;
									selectedTangentPartner = null;

									if (mouse.dist(v.cp1) < minDistance) {
										selectedTangent = v.cp1;

										if (i > 0) {
												BezierVertex prevV = (BezierVertex) activeSpline.vertices.get(i - 1);
												selectedTangentAnchor = prevV.anchor;
												selectedTangentPartner = prevV.cp2;
										}
										return;
									} else if (mouse.dist(v.cp2) < minDistance ) {
										selectedTangent = v.cp2;
										selectedTangentAnchor = v.anchor;

										if (i < activeSpline.vertices.size() - 1) {
												BezierVertex nextV = (BezierVertex) activeSpline.vertices.get(i + 1);
												selectedTangentPartner = nextV.cp1;
										} else if (i == activeSpline.vertices.size() - 1) {
												selectedTangentPartner = activeSpline.nextTangent;
										}
										return;
									}

									if (i > 0) {
											BezierVertex oldV = (BezierVertex) activeSpline.vertices.get(i - 1);

											if (mouse.dist(oldV.cp1) < minDistance) {
												selectedTangent = oldV.cp1;

												if (i > 1) {
														BezierVertex oldOldV = (BezierVertex) activeSpline.vertices.get(i - 2);

														selectedTangentAnchor = oldOldV.anchor;
														selectedTangentPartner = oldOldV.cp2;
												}
												return;
											}
									}

									if (i < activeSpline.vertices.size() - 1) {
											BezierVertex nextV = (BezierVertex) activeSpline.vertices.get(i + 1);


											if (mouse.dist(nextV.cp1) < minDistance) {
												selectedTangent = nextV.cp1;
												selectedTangentPartner = v.cp2;
												selectedTangentAnchor = v.anchor;
												return;
											} else if (mouse.dist(nextV.cp2) < minDistance) {
												selectedTangent = nextV.cp2;
												selectedTangentAnchor = nextV.anchor;

												if (i < activeSpline.vertices.size() - 2) {
														BezierVertex nextNextV = (BezierVertex) activeSpline.vertices.get(i + 2);
														selectedTangentPartner = nextNextV.cp1;
												}
												return;
											}
									} else if (i == activeSpline.vertices.size() - 1) {
											if (mouse.dist(activeSpline.nextTangent) < minDistance) {
												selectedTangent = activeSpline.nextTangent;
												selectedTangentAnchor = v.anchor;
												selectedTangentPartner = v.cp2;

												return;
											}
									}

							}
						}
					}
			}

			selectedTangent = null;


			if (this.selectedVertex != null) {
				this.selectedVertex.selected = false;
			}

			if (this.activeSpline != null) {
				activeSpline.selected = false;
			}

			this.activeSpline = null;
			this.selectedVertex = null;

			float minDistance = -1;

			float distanceRadius = 4;

			PVector mouse = new PVector(m_x, m_y);
			for (int i = 0; i < this.splines.size(); i++) {
				BezierSpline curSpline = this.splines.get(i);
				for (int j = 0; j < curSpline.vertices.size(); j++) {
					 BezierVertex curVertex = (BezierVertex) curSpline.vertices.get(j);
					 float distance = mouse.dist(curVertex.anchor);
					 if ( (distance <= distanceRadius) && (minDistance < 0 || distance < minDistance) ) {
						 curVertex.selected = true;
						 this.activeSpline = curSpline;
						 this.selectedVertex = curVertex;
						 this.activeSpline.selected = true;
					 }
				}


			}

			// if we didn't find a vertex from the mouse press,
			// let's see if we should try to at least select a spline
			float shortestDistance = -1;
			for (int i = 0; i < splines.size(); i++) {
				BezierSpline s = splines.get(i);
				float d = s.getShortestDistance(m_x, m_y);

				if ( ((d > 0 && d < shortestDistance) || shortestDistance < 0) && d < 10)	 {
					shortestDistance = d;
					activeSpline = s;
				}
			}

			if (activeSpline != null) {
				activeSpline.selected = true;
			}

		}
		else if (this.mode == PEN_MODE) {

			// create a new spline if we don't already have one selected
			if (activeSpline == null) {
				activeSpline = new BezierSpline(p, this);
				splines.add(activeSpline);
				activeSpline.addVertex(new BezierVertex(new PVector(m_x, m_y), new PVector(m_x, m_y), new PVector(m_x, m_y)));
				activeSpline.nextTangent = new PVector(m_x, m_y);
				activeSpline.selected = true;

				if (selectedVertex != null) {
					selectedVertex.selected = false;
				}
				selectedVertex = activeSpline.getLastVertex();
				selectedVertex.selected = true;
			} else {
				if (selectedVertex != null) {
					selectedVertex.selected = false;
				}
				activeSpline.addNextVertex(new PVector(m_x, m_y));
				activeSpline.nextTangent = new PVector(m_x, m_y);
				selectedVertex = activeSpline.getLastVertex();
				selectedVertex.selected = true;
			}
		} else if (mode == SPLINE_SELECTOR_MODE) {
			float minDistance = 6;

			if (this.activeSpline != null) {
				//activeSpline.calculateHandles();
				activeSpline.selectedHandle = activeSpline.detectSelectedHandle(m_x, m_y, 8);
			}

			if ( (activeSpline != null && activeSpline.selectedHandle == NO_HANDLE) || (activeSpline == null) ) {
				if (activeSpline != null) {
					activeSpline.selected = false;
				}

				activeSpline = null;


				float shortestDistance = -1;
				for (int i = 0; i < splines.size(); i++) {
					BezierSpline s = splines.get(i);
					float d = s.getShortestDistance(m_x, m_y);

					if ( ((d > 0 && d < shortestDistance) || shortestDistance < 0) && d < minDistance)	{
						shortestDistance = d;
						activeSpline = s;
						s.selectedHandle = NO_HANDLE;
					}
				}

				if (activeSpline != null) {
					activeSpline.selected = true;
				}
			}

			if (activeSpline != null) {
					dragBegin = new PVector(screen_x, screen_y);
					originalSpline = activeSpline.newInstance();
			}
		} else if (mode == PAN_MODE) {
				dragBegin = new PVector(screen_x, screen_y);
				dragAmount = new PVector(0.0f, 0.0f);
				originalPan = new PVector(screenPan.x, screenPan.y);
		}
	}

	void setDrawFill(boolean drawFill) {
			this.drawFill = drawFill;
			p.redraw();
	}

	void mouseDragged(float m_x, float m_y, int screen_x, int screen_y) {
		if (this.mode == SELECTOR_MODE) {
			if (this.selectedVertex != null && selectedTangent == null) {
				this.activeSpline.moveVertex(this.selectedVertex, m_x, m_y);
			}

			if (selectedTangent != null) {
					selectedTangent.x = m_x;
					selectedTangent.y = m_y;

					if (selectedTangentPartner != null && selectedTangentAnchor != null && !breakTangent) {
							BezierSpline.applyAutoTangent(selectedTangentPartner, selectedTangentAnchor, selectedTangent);
					}
			}
		} else if (this.mode == PEN_MODE) {
			if (activeSpline.numberVertices() > 0) {
				if (!breakTangent) {
					PVector lastAnchor = this.activeSpline.getLastVertex().anchor;
					PVector newTangent = new PVector((-1 * (m_x - lastAnchor.x)) + lastAnchor.x, (-1 * (m_y - lastAnchor.y)) + lastAnchor.y);
					this.activeSpline.replaceLastTangent(newTangent);
					activeSpline.nextTangent = new PVector(m_x, m_y);
				} else {
					activeSpline.nextTangent = new PVector(m_x, m_y);
				}
			}
		} else if (mode == SPLINE_SELECTOR_MODE) {
			if (activeSpline != null) {
				activeSpline.setVerticesFromOtherSpline(originalSpline);


				if (originalSpline.selectedHandle == E_HANDLE) {
					activeSpline.scaleHorizontally((m_x - originalSpline.handles[W_HANDLE].x) / (originalSpline.handles[E_HANDLE].x - originalSpline.handles[W_HANDLE].x),
																				 originalSpline.handles[W_HANDLE].x);
				} else if (originalSpline.selectedHandle == W_HANDLE) {
					activeSpline.scaleHorizontally((m_x - originalSpline.handles[E_HANDLE].x) / (originalSpline.handles[W_HANDLE].x - originalSpline.handles[E_HANDLE].x),
																				 originalSpline.handles[E_HANDLE].x);
				} else if (originalSpline.selectedHandle == N_HANDLE) {
					activeSpline.scaleVertically((m_y - originalSpline.handles[S_HANDLE].y) / (originalSpline.handles[N_HANDLE].y - originalSpline.handles[S_HANDLE].y),
																				 originalSpline.handles[S_HANDLE].y);
				} else if (originalSpline.selectedHandle == S_HANDLE) {
					activeSpline.scaleVertically((m_y - originalSpline.handles[N_HANDLE].y) / (originalSpline.handles[S_HANDLE].y - originalSpline.handles[N_HANDLE].y),
																				 originalSpline.handles[N_HANDLE].y);
				} else if (originalSpline.selectedHandle == NE_HANDLE) {
					activeSpline.scaleHorizontally((m_x - originalSpline.handles[W_HANDLE].x) / (originalSpline.handles[E_HANDLE].x - originalSpline.handles[W_HANDLE].x),
																				 originalSpline.handles[W_HANDLE].x);
					activeSpline.scaleVertically((m_y - originalSpline.handles[S_HANDLE].y) / (originalSpline.handles[N_HANDLE].y - originalSpline.handles[S_HANDLE].y),
																				 originalSpline.handles[S_HANDLE].y);
				} else if (originalSpline.selectedHandle == SE_HANDLE) {
					activeSpline.scaleHorizontally((m_x - originalSpline.handles[W_HANDLE].x) / (originalSpline.handles[E_HANDLE].x - originalSpline.handles[W_HANDLE].x),
																				 originalSpline.handles[W_HANDLE].x);
					activeSpline.scaleVertically((m_y - originalSpline.handles[N_HANDLE].y) / (originalSpline.handles[S_HANDLE].y - originalSpline.handles[N_HANDLE].y),
																				 originalSpline.handles[N_HANDLE].y);
				} else if (originalSpline.selectedHandle == SW_HANDLE) {
					activeSpline.scaleVertically((m_y - originalSpline.handles[N_HANDLE].y) / (originalSpline.handles[S_HANDLE].y - originalSpline.handles[N_HANDLE].y),
																				 originalSpline.handles[N_HANDLE].y);
					activeSpline.scaleHorizontally((m_x - originalSpline.handles[E_HANDLE].x) / (originalSpline.handles[W_HANDLE].x - originalSpline.handles[E_HANDLE].x),
																				 originalSpline.handles[E_HANDLE].x);
				} else if (originalSpline.selectedHandle == NW_HANDLE) {
					activeSpline.scaleVertically((m_y - originalSpline.handles[S_HANDLE].y) / (originalSpline.handles[N_HANDLE].y - originalSpline.handles[S_HANDLE].y),
																				 originalSpline.handles[S_HANDLE].y);
					activeSpline.scaleHorizontally((m_x - originalSpline.handles[E_HANDLE].x) / (originalSpline.handles[W_HANDLE].x - originalSpline.handles[E_HANDLE].x),
																				 originalSpline.handles[E_HANDLE].x);
				} else {
					activeSpline.moveSpline(m_x - dragBeginCoords.x, m_y - dragBeginCoords.y);
				}


				if (this.activeSpline != null) {
					activeSpline.selectedHandle = activeSpline.detectSelectedHandle(m_x, m_y, 8);
				}
			}
		} else if (mode == PAN_MODE) {
				screenPan.x = originalPan.x + (screen_x - dragBegin.x);
				screenPan.y = originalPan.y + (screen_y - dragBegin.y);

				//System.out.println("pan_x: " + screenPan.x + " pan_y: " + screenPan.y);
				//lastMousePress = new PVector(m_x, m_y);
		}
	}

	void mouseReleased() {

	}

	void keyPressed(char key) {
		 if (key == 's') {
			 this.mode = SELECTOR_MODE;
			 p.bezierTool.selectSelector();
		 } else if (key == 'd') {
			 this.mode = PEN_MODE;
			 p.bezierTool.selectPen();
		 } else if (key == 'c') {
			 mode = SPLINE_SELECTOR_MODE;
			 p.bezierTool.selectSplineSelector();
		 } else if (key == 'a') {
			 p.bezierTool.selectPan();
		 } else if (key == 'q') {
				 screenZoom += 0.5f;
		 } else if (key == 'w') {
				 screenZoom -= 0.5f;
		 } else if (key == 'e') {
				 screenZoom = 1.0f;
		 } else if (key == PApplet.BACKSPACE || key == PApplet.DELETE) {
			 if (mode == SPLINE_SELECTOR_MODE) {
				 deleteActiveSpline();
				 p.redraw();
			 } else if (mode == SELECTOR_MODE || mode == PEN_MODE) {
				 deleteSelectedVertex();
				 p.redraw();
			 }
		 }
	 }

	 void codedKeyPressed(int keyCode) {
		 if (keyCode == PApplet.ALT) {
			 breakTangent = true;
		 }
	 }

	void codedKeyReleased(int keyCode) {
		if (keyCode == PApplet.ALT) {
			breakTangent = false;
		}
	}

	void drawSplines() {
		for (int i = 0; i < this.splines.size(); i++) {
			BezierSpline s = this.splines.get(i);
			s.draw(mode, drawFill);
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
		p.redraw();
	}

	String generateCode() {
		String code = "";
		for (int i = 0; i < splines.size(); i++) {
			if (i != 0) {
				code += "\n";
			}
			BezierSpline s = splines.get(i);
			code += s.generateCode();
		}

		return code;
	}

	void deleteActiveSpline() {
		if (activeSpline != null) {
			splines.remove(activeSpline);
		}

		activeSpline = null;
	}

	void deleteSelectedVertex() {
		if (selectedVertex != null) {
			ArrayList<BezierVertex> vertices = activeSpline.vertices;

			int index = vertices.indexOf(selectedVertex);

			if (index > 0) { // not the first vertex
				if (index == vertices.size() - 1) { // last vertex
					activeSpline.nextTangent = selectedVertex.cp1;
				} else {
					BezierVertex next = (BezierVertex) vertices.get(index + 1);
					next.cp1 = selectedVertex.cp1;
				}
			}
			vertices.remove(selectedVertex);

			if (vertices.size() == 0) {
				deleteActiveSpline();
			}
		}
	}
}
