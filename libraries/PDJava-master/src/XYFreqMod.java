import processing.core.PApplet;
import processing.net.Client;

@SuppressWarnings("serial")
public class XYFreqMod extends PApplet {
	Client pd;
	
	public static void main(String[] args) {
		PApplet.main(new String[] {"--location=0,0","XYFreqMod"});
	}

	public void setup() {
		size(800, 800, P2D);
		background(0);
		noStroke();
		pd = new Client(this, "localhost", 3001);
		
		pd.write("level 0.6;");
		pd.write("fm_mod_index 300;");
		pd.write("dly_dry 1.0;");
		pd.write("dly_wet 0.4;");
		pd.write("dly_fbk 0.5;");
		pd.write("dly_time 500;");
	}

	public void draw() {
		fill(0, 0, 0, 1);
		rect(0, 0, width, height);
		fill(0, 255, 255, 30);
		if (mousePressed)
			ellipse(mouseX, mouseY, 50, 50);
	}

	public void mouseDragged() {
		pd.write("pad_x " + mouseX + ";");
		pd.write("pad_y " + mouseY + ";");
		redraw();
	}

	public void mousePressed() {
		pd.write("pad_x " + mouseX + ";");
		pd.write("pad_y " + mouseY + ";");
		pd.write("pad_on 1;");
	}

	public void mouseReleased() {
		pd.write("pad_on 0;");
		redraw();
	}
}