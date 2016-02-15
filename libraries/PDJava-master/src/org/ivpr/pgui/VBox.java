package org.ivpr.pgui;

public class VBox extends Container {

	public VBox(PGui p, float size) {
		super(p, size);
	}

	public void layout(int x, int y, int w, int h) {
		super.layout(x, y, w, h);
		float sum = 0, cx = x, cy = y, cw = w, ch;
		for (Component c : children)
			sum += c.size;
		for (Component c : children) {
			ch = h * (c.size / sum);
			c.layout((int) cx, (int) cy, (int) cw + 1, (int) ch + 1);
			cy += ch;
		}
		layoutDirty = false;
	}

	public void draw() {
		for (Component c : children)
			c.draw();
	}
}
