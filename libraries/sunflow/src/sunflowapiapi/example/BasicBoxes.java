package sunflowapiapi.example;

import processing.core.PApplet;
import sunflowapiapi.P5SunflowAPIAPI;

public class BasicBoxes extends PApplet{
	private boolean render = false;
	private P5SunflowAPIAPI sunflow ;
	int sceneWidth = 640;
	int sceneHeight = 480;
	
	public void setup() {
		size(sceneWidth, sceneHeight, "sunflowapiapi.P5SunflowAPIAPI");

		sunflow = (P5SunflowAPIAPI) g;
		fill(255);
	}

	public void draw() {
		background(255);
		if (render) {
			// sunflow.setSunSkyLight("mySunskyLight"); // turn on if you have any other than the ambient occlusion shader
			// sunflow.setDirectionalLight("myDirectionalLight1", new Point3(0, 1, 0), new Vector3(1, 1, 0), 100, new Color(125, 125, 125));
			// sunflow.setAmbientOcclusionShader(new Color(255, 125, 125), new Color(0, 0, 0), 128, 16);
			// sunflow.setDiffuseShader();
			// sunflow.setMirrorShader();
			sunflow.setGlassShader();	// setting some default values, you are able to send your own finetuned glass shader values as well
		}

		for(int i=0;i<10;i++) {
			fill(random(255), random(255), random(255));
			pushMatrix();
				translate(random(-10, 10), random(-10, 10), random(-10, 10));
				pushMatrix();
					rotateY(0.5f + i*.001f);
					rotateX(0.5f);
					box(5);
					popMatrix();
			popMatrix();
		}
		if (render) {
			sunflow.setPathTracingGIEngine(16);
			sunflow.render("boxesTest.png");
			render = false;
		}
	}

	public void keyPressed() {
		switch (key) {
		case 'r':
			render = true;
			break;

		}
	}
}
