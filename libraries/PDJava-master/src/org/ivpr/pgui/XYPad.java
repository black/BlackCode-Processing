package org.ivpr.pgui;

import java.awt.Point;

public class XYPad extends Component {
	
	public XYPad(PGui p, float size) {
		super(p, size);
	}

	public void draw() {
		p.fill(0);
		p.rect(x, y, w, h);
		p.fill(0, 255, 220);
		for (Point t : touchPoints.values())
			p.ellipse(t.x, t.y, 50, 50);
	}
}
