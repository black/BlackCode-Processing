import processing.core.PApplet;
import processing.net.Client;

@SuppressWarnings("serial")
public class RhythmSample extends PApplet {
	Client pd;
	int speed, sample, nVerticalBars = 3, nHorizontalBars = 4;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--location=0,0", "RhythmSample" });
	}

	public void setup() {
		size(400, 400, P2D);
		background(0);
		noStroke();
		pd = new Client(this, "localhost", 3002);
	}

	public void draw() {
		fill(0);
		rect(0, 0, width, height);
		for (float i = 0; i < nVerticalBars; i++) {
			fill(255 * i / nVerticalBars);
			rect(i / nVerticalBars * width, 0, width / nVerticalBars + 1,
					height);
		}
		for (float i = 0; i < nHorizontalBars; i++) {
			float fill = 255 * i / nHorizontalBars;
			float opacity = 100;
			fill(fill, fill, fill, opacity);
			rect(0, i / nHorizontalBars * height, width, height
					/ nHorizontalBars + 1);
		}
		fill(255, 200, 100);
		if (mousePressed)
			ellipse(mouseX, mouseY, 100, 100);
	}

	public void mouseDragged() {
		setSpeed();
	}

	public void mousePressed() {
		setSpeed();
	}

	public void mouseReleased() {
		pd.write("speed 0;");
		speed = 0;
	}

	private void setSpeed() {
		int nextSpeed = nVerticalBars * mouseX / width + 1;
		if (nextSpeed != speed)
			pd.write("speed " + (speed = nextSpeed) + ";");
		int nextSample = nHorizontalBars * mouseY / height + 1;
		if (nextSample != sample)
			pd.write("sample " + (sample = nextSample) + ";");
	}
}
