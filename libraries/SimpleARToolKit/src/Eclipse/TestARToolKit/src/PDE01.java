import processing.core.*;
import JMyron.*;
import processing.opengl.*;
import pARToolKit.SimpleARToolKit;

public class PDE01 extends PApplet {

	private static final long serialVersionUID = 01L;
	private JMyron m;
	private PImage img;
	private SimpleARToolKit ar;
	private int cW, cH;

	public void setup() {
		size(640, 480, OPENGL);
		cW = 320;
		cH = 240;
		m = new JMyron();
		m.start(cW, cH);
		m.findGlobs(0);
		img = createImage(cW, cH, ARGB);
		ar = new SimpleARToolKit(this, cW, cH);
		ar.loadPattern("patt.hiro", 80, 0.0f, 0.0f);
		ar.register("showBox");
	}

	public void draw() {
		background(0);
		m.update();
		m.imageCopy(img.pixels);
		img.updatePixels();
		hint(DISABLE_DEPTH_TEST);
		image(img,0,0,width,height);
		hint(ENABLE_DEPTH_TEST);
		if (ar.findMatch(img,100)) {
			ar.showObject();
		}
	}

	public void showBox(SimpleARToolKit _a) {
		noFill();
		stroke(255, 200, 0);
		box(50);
	}
}
