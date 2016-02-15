package org.ivpr.pgui;

import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;

public class Menu extends Component {

	String text;
	PFont textFont;
	VBox menuItemBox;
	public List<MenuItem> menuItemList = new LinkedList<MenuItem>();

	public Menu(PGui p, float size, String text) {
		super(p, size);
		this.text = text;

		textFont = p.loadFont("CourierNew36.vlw");

		menuItemBox = new VBox(p, size);
	}

	public void draw() {
		p.fill(100);
		p.noStroke();
		p.rect(x, y, w, h);
		p.fill(255, 255, 255);
		p.textFont(textFont, 32);
		p.textAlign(PApplet.CENTER);
		p.text(text, x + w / 2, y + h / 2 + 9);
	}

	public void addMenuItem(final String text, ActionListener listener) {
		MenuItem m = new MenuItem(p, 1, text, textFont, listener);
		menuItemBox.add(m);
		menuItemList.add(m);
	}

	public void touchPressed(int touchX, int touchY, int touchId) {
		p.foreground.add(menuItemBox);
		touchDragged(touchX, touchY, touchId);
	}

	public void touchDragged(int touchX, int touchY, int touchId) {
		for (MenuItem menuItem : menuItemList)
			if (menuItem.contains(touchX, touchY))
				menuItem.setColor(100, 100, 100);
			else
				menuItem.setColor(0, 0, 0);
	}

	public void touchReleased(int touchX, int touchY, int touchId) {
		for (MenuItem menuItem : menuItemList)
			if (menuItem.contains(touchX, touchY))
				menuItem.fireActionPerformed();
		p.foreground.remove(menuItemBox);
	}

	public void setText(String text) {
		this.text = text;
	}

}
