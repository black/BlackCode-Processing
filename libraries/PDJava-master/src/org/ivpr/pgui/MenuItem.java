package org.ivpr.pgui;

import java.awt.event.ActionListener;

import processing.core.PApplet;
import processing.core.PFont;

public class MenuItem extends Component {
	String text;
	PFont font;
	ActionListener listener;

	public MenuItem(PGui p, float size, String text, PFont font,
			ActionListener listener) {
		super(p, size);
		this.text = text;
		this.font = font;
		this.listener = listener;
	}

	public void draw() {
		p.fill(r, g, b);
		p.noStroke();
		p.rect(x, y, w, h);
		p.fill(255, 255, 255);
		p.textFont(font, 32);

		p.textAlign(PApplet.LEFT);

		p.text(text, x + 40, y + h / 2+11);
	}

	public void fireActionPerformed() {
		listener.actionPerformed(null);
	}
}