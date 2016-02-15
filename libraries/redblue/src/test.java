import processing.core.PApplet;
import megamu.redblue.*;

public class test extends PApplet {

	public void setup() {
		size(640, 480, "megamu.redblue.RedBlue");
		// size(640,480,P3D);
	}

	public void draw() {
		
		((RedBlue) g).setDivergence(map(mouseX,0,width,0,3));
		
		camera(width * 0.5f, height * 0.5f, width * 0.5f,
				width * 0.5f, height * 0.5f, 0, sin((float)millis()/3000), cos((float)millis()/3000), 0);
		
		float cameraZ = ((height/2.0f) / tan(PI*60.0f/360.0f));
		//perspective(map(mouseY,0,height,PI/6,PI/2), width/height, cameraZ/10.0f, cameraZ*10.0f);
		
		background(0);
		
		lights();
		
		translate(width/2,height/2);

		noStroke();

		float r = (float) millis() / 3000;

		// *
		int n = 10;
		for (int i = 0; i < n; i++) {

			rotateZ(r);
			//rotateY((float)mouseX/width*PI);
			rotateX(r * 0.213f);
			pushMatrix();
			translate(map(i, 0, n, -width * 0.3f, width * 0.3f), 0, map(i, 0, n,
					-width * 0.3f, width * 0.3f));

			fill(map(i, 0, n, 100, 240), map(i, 0, n, 50, 130), map(i, 0, n,
					200, 255));
			box(40);
			popMatrix();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PApplet.main(new String[] { "test" });
	}

}
