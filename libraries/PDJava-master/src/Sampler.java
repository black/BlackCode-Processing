import processing.core.PApplet;
import processing.net.Client;

@SuppressWarnings("serial")
public class Sampler extends PApplet {

	Client pd;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--location=0,0", "Sampler" });
	}

	public void setup() {
		size(800, 800, P2D);
		background(0);
		noStroke();
		pd = new Client(this, "localhost", 3001);

		pd.write("level 0.6;");
		pd.write("pad_w "+width+";");
		pd.write("pad_h "+height+";");
		pd.write("level 0.6;");
		
	}

	public void draw() {
		fill(0);
		rect(0,0,width,height);
		fill(255, 100, 100);
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
	
	public void keyPressed(){
		if(Character.isDigit(key))
			pd.write("speed "+key+";");
	}
}
