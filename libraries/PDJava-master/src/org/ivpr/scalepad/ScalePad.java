package org.ivpr.scalepad;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.ivpr.pgui.HBox;
import org.ivpr.pgui.Menu;
import org.ivpr.pgui.PGui;
import org.ivpr.pgui.XYPad;

import processing.core.PApplet;
import processing.net.Client;

@SuppressWarnings("serial")
public class ScalePad extends PGui {
	final int DEFAULT_KEY = 0;
	final int LOWEST_OCTAVE = 0;
	final int HIGHEST_OCTAVE = 7;
	final int DEFAULT_OCTAVE = 4;
	final int DEFAULT_SCALE = 0;

	Client pd;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--location=0,0",
				"org.ivpr.scalepad.ScalePad" });
	}

	public void setup() {
		size(500, 500, P2D);
		background(0);
		noStroke();

		pd = new Client(this, "localhost", 3002);
		pd.write("level 0.4;");
		pd.write("vibrato_amt 15;");
		pd.write("vibrato_freq 5;");

		final Menu scaleMenu = new Menu(this, 1, "Scale");
		final String[] scales = { "Ionian", "Dorian", "Phrygian",
				"Lydian", "Mixolydian", "Aeolian", "Locrian" };
		for (int i = 0; i < scales.length; i++) {
			String s = scales[i];
			final int offset = i;
			scaleMenu.addMenuItem(s, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pd.write("scale_offset " + offset + ";");
					scaleMenu.setText(scales[offset]);
				}
			});
		}

		final String[] keys = { "C", "C#", "D", "D#", "E", "F", "F#", "G",
				"G#", "A", "A#", "B" };
		final Menu keyMenu = new Menu(this, 1, "Key");
		for (int i = 0; i < keys.length; i++) {
			final int offset = i;
			keyMenu.addMenuItem(keys[i], new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pd.write("key_offset " + offset + ";");
					keyMenu.setText(keys[offset]);
				}
			});
		}

		final Menu octaveMenu = new Menu(this, 1, "Octave");
		for (int i = LOWEST_OCTAVE; i <= HIGHEST_OCTAVE; i++) {
			final int octave = i;
			octaveMenu.addMenuItem(i + "", new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pd.write("octave " + octave + ";");
					octaveMenu.setText("" + octave);
				}
			});
		}

		HBox menuBox = new HBox(this, 1);
		Menu[] menus = { keyMenu, octaveMenu, scaleMenu };
		int[] defaults = { DEFAULT_KEY, DEFAULT_OCTAVE, DEFAULT_SCALE };
		for (int i = 0; i < menus.length; i++) {
			menus[i].menuItemList.get(defaults[i]).fireActionPerformed();
			menuBox.add(menus[i]);
		}

		add(menuBox);
		add(new XYPad(this, 12) {
			public void draw() {
				p.noStroke();
				int n = 8;
				for (float i = 0; i < n; i++) {
					fill(255 * i / n);
					rect(x + i / n * w, y, x + w / n + 1, h);
				}
				p.fill(0, 255, 220);
				for (Point t : touchPoints.values())
					p.ellipse(t.x, t.y, 50, 50);
			}

			public void touchPressed(int touchX, int touchY, int touchId) {
				emitTouchLocation(touchX, touchY, touchId);
				pd.write("pad_on 1;");
			}

			public void touchDragged(int touchX, int touchY, int touchId) {
				emitTouchLocation(touchX, touchY, touchId);
			}

			public void touchReleased(int touchX, int touchY, int touchId) {
				pd.write("pad_on 0;");
				redraw();
			}

			private void emitTouchLocation(int touchX, int touchY, int touchId) {
				pd.write("pad_x " + (float) touchX / width + ";");
				pd.write("pad_y " + (1.0 - (float) touchY / height) + ";");
			}
		});
	}
}
