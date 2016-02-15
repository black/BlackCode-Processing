package org.ivpr.pgui;

public class HBox extends Container {

	public HBox(PGui p, float size) {
		super(p, size);
	}

	public void layout(int x, int y, int w, int h) {
		super.layout(x, y, w, h);
		float sum = 0, cx = x, cy = y, cw, ch = h;
		for (Component c : children)
			sum += c.size;
		for (Component c : children) {
			cw = w * (c.size / sum);
			c.layout((int) cx, (int) cy, (int) cw+1, (int) ch+1);
			cx += cw;
		}
		layoutDirty = false;
	}

	public void draw() {
		for (Component c : children){
			c.draw();
//			System.out.println("c.x = " + c.x);
//			System.out.println("c.y = " + c.y);
//			System.out.println("c.w = " + c.w);
//			System.out.println("c.h = " + c.h);
//			System.out.println();
		}
	}
}
