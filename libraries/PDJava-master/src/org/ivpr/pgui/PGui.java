package org.ivpr.pgui;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class PGui extends PApplet {
	VBox gui = new VBox(this, 1);
	VBox foreground = new VBox(this, 1);

	public void draw() {
		if (gui.layoutDirty)
			gui.layout(0, 0, width, height);
		gui.draw();

		if (foreground.hasChildren()) {
			if (foreground.layoutDirty)
				foreground.layout(0, 0, width, height);
			foreground.draw();
		}
	}

	public void add(Component c) {
		gui.add(c);
	}

	public void mousePressed() {
		gui._touchPressed(mouseX, mouseY, 0);
	}

	public void mouseDragged() {
		gui._touchDragged(mouseX, mouseY, 0);
	}

	public void mouseReleased() {
		gui._touchReleased(mouseX, mouseY, 0);
	}
}
