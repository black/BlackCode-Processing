import processing.core.PApplet;
import processing.net.Client;

@SuppressWarnings("serial")
public class ScalePad extends PApplet {
	Client pd;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--location=0,0", "ScalePad" });
	}

	public void setup() {
		size(500, 500, P2D);
		background(0);
		noStroke();
		pd = new Client(this, "localhost", 3002);
		pd.write("level 0.6;");
		pd.write("vibrato_amt 15;");
		pd.write("vibrato_freq 5;");
	}

	public void draw() {
		int n = 8;
		for(float i=0;i<n;i++){
			fill(255*i/n);
			rect(i/n*width,0,width/n+1,height);
		}
			
		fill(0, 255, 255);
		if (mousePressed)
			ellipse(mouseX, mouseY, 50, 50);
	}

	public void mouseDragged() {
		emitPadLocation();
		redraw();
	}

	public void mousePressed() {
		emitPadLocation();
		pd.write("pad_on 1;");
	}

	public void mouseReleased() {
		pd.write("pad_on 0;");
		redraw();
	}
	
	private void emitPadLocation() {
		pd.write("pad_x " + (float)mouseX/width + ";");
		pd.write("pad_y " + (1.0-(float)mouseY/height) + ";");
	}
}