import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.net.Client;

@SuppressWarnings("serial")
public class Synth extends PApplet {
	Client pd;
	List<Slider> sliders = new ArrayList<Slider>();

	class Slider {
		String param;
		float value = 0;
		boolean dragging = false;
		int x = 20, y1 = 300, y2 = 20;
		int size = 30;

		public Slider(String param, float value, int x, int y1, int y2, int size) {
			super();
			this.value = value;
			this.x = x;
			this.y1 = y1;
			this.y2 = y2;
			this.size = size;
			this.param = param;
		}

		public void draw() {
			stroke(200);
			fill(dragging ? 220 : 150);
			line(x, y1, x, y2);
			int vy = (int) (value * (y2 - y1) + y1);
			rect(x, vy, size, size);
		}

		public void mouseReleased() {
			dragging = false;
		}

		public void mousePressed() {
			int vy = (int) (value * (y2 - y1) + y1);
			if (abs(mouseX - x) <= size / 2 && abs(mouseY - vy) <= size / 2)
				dragging = true;
		}

		public void mouseDragged() {
			if (dragging) {
				float v = (float) (mouseY - y1) / (y2 - y1);
				v = max(min(v, 1), 0);
				if (value != v) {
					value = v;
					valueChanged();
				}
			}
			redraw();
		}

		protected void valueChanged() {
			pd.write(param + " " + value + ";");
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "--location=0,0", "Synth" });
	}

	public void setup() {
		size(800, 800, P2D);
		background(0);
		noStroke();
		rectMode(CENTER);
		pd = new Client(this, "localhost", 3002);

		sliders.add(new Slider("osc1_freq", 0, 20, 300, 20, 20));
		sliders.add(new Slider("osc2_freq", 0, 50, 300, 20, 20));
	}

	public void draw() {
		background(0);
		for (Slider s : sliders)
			s.draw();
	}

	public void mouseDragged() {
		for (Slider s : sliders)
			s.mouseDragged();
	}

	public void mousePressed() {
		for (Slider s : sliders)
			s.mousePressed();
	}

	public void mouseReleased() {
		for (Slider s : sliders)
			s.mouseReleased();
	}
}
