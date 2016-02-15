package org.ivpr.pgui;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public abstract class Component {
	protected PGui p;
	public int x = 0, y = 0, w = 0, h = 0;
	public int r, g, b;
	float size = 1;

	protected Map<Integer, Point> touchPoints = new HashMap<Integer, Point>();

	public Component(PGui p, float size) {
		this.p = p;
		this.size = size;
	}

	public abstract void draw();

	public void layout(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void touchPressed(int touchX, int touchY, int touchId) {
	}

	public void touchDragged(int touchX, int touchY, int touchId) {
	}

	public void touchReleased(int touchX, int touchY, int touchId) {
	}

	public void _touchPressed(int touchX, int touchY, int touchId) {
		touchPoints.put(touchId, new Point(touchX, touchY));
		touchPressed(touchX, touchY, touchId);
	}

	public void _touchDragged(int touchX, int touchY, int touchId) {
		touchPoints.get(touchId).setLocation(touchX, touchY);
		touchDragged(touchX, touchY, touchId);
	}

	public void _touchReleased(int touchX, int touchY, int touchId) {
		touchPoints.remove(touchId);
		touchReleased(touchX, touchY, touchId);
	}

	public boolean contains(int touchX, int touchY) {
		return touchX > x && touchY > y && touchX < (x + w) && touchY < (y + h);
	}

	public void setColor(int r, int g, int b) {
		this.r = r;
		this.b = b;
		this.g = g;
	}
}
