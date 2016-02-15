package sunflowapiapi.example;

import java.awt.Color;
import sunflowapiapi.P5SunflowAPIAPI;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;
import processing.core.PApplet;

public class BasicScene extends PApplet {

	int sceneWidth = 640;
	int sceneHeight = 480;
	P5SunflowAPIAPI sunflow;

	public void setup() {
		size(sceneWidth, sceneHeight, "sunflowapiapi.P5SunflowAPIAPI");
		sunflow = (P5SunflowAPIAPI) g;
	}

	public void draw() {
		background(126, 130, 86);
		noStroke();

		// set basic light
		sunflow.setSunSkyLight("mySunskyLight");
		sunflow.setPointLight("myPointLight", new Point3(0, 5, 5), new Color(
				255, 255, 255));
		sunflow.setDirectionalLight("myDirectionalLight", new Point3(-2, 3, 0),
				new Vector3(0, 0, 0), 3, new Color(1f, 0f, 0f));
		sunflow.setSphereLight("mySphereLight", new Point3(0, 20, -5),
				new Color(0, 0, 255), 32, 10);

		// set some fill color
		fill(255, 0, 0);
		
		// set shader
		sunflow.setAmbientOcclusionShader();
		// draw an object
		pushMatrix();
		translate(-4, 0, 0);
		sphere(1);
		popMatrix();
		// sunflow version of a sphere
		sunflow.drawSphere("sphere00", -4, 2, 0, 1);

		// set shader
		sunflow.setDiffuseShader();
		
		// draw an object
		pushMatrix();
		translate(-2, 0, 0);
		sphere(1);
		popMatrix();
		// sunflow version of a sphere
		sunflow.drawSphere("sphere01", -2, 2, 0, 1);

		// set shader
		sunflow.setGlassShader();
		
		// draw an object
		pushMatrix();
		translate(0, 0, 0);
		sphere(1);
		popMatrix();
		// sunflow version of a sphere
		sunflow.drawSphere("sphere02", 0, 2, 0, 1);
		
		// set shader
		sunflow.setShinyDiffuseShader(2f);
		
		// draw an object
		pushMatrix();
		translate(2, 0, 0);
		sphere(1);
		popMatrix();
		// sunflow version of a sphere
		sunflow.drawSphere("sphere03", 2, 2, 0, 1);

		// set shader, this is the more advanced mode. you have to both set the sunflow and processung color
		// this will become more elegant lateron
		sunflow.setPhongShader(new Color(1f, 1f, 1f),
				new Color(.5f, .5f, .9f), 10, 16);
		fill(255);
		
		// draw an object
		pushMatrix();
		translate(4, 0, 0);
		sphere(1);
		popMatrix();
		// sunflow version of a sphere
		sunflow.drawSphere("sphere04", 4, 2, 0, 1);
	}

	public void mouseReleased() {
		sunflow.setAmbientOcclusionEngine(new Color(255), new Color(0), 16, 10);
		sunflow.render();
	}

	public void keyPressed() {
		if (key == ' ') {
			sunflow.setInstantGIEngine(16, 1, 1.0f, 1);
			sunflow.render("testrender.png");
		}
	}
}
