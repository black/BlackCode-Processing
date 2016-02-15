package mother.library;


import oscP5.*;
import netP5.*;
import processing.opengl.*;

import foetus.*;
import processing.core.*;

// Commented for P2.0
//import processing.core.PApplet.RegisteredMethods;

import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

public class testi extends PApplet
{
	// Messages:
	// "/Gradient/TopColor" iii
	// "/Gradient/BottomColor" iii

	public Foetus f;

	
	
	public void setup()
	{
		// When run as a synth, setup() is never called!
		// put the necessary initialization code in a method named initializeFoetus().
		// The necessary Processing initialization calls are called by Mother, and so should be left out from
		// initializeFoetus().
		// Finally, for the synth to work as a processing sketch within the PDE, call initializeFoetus() from within
		// setup().

		size(400, 300, OPENGL);
		frameRate(24);

		initializeFoetus();
	}
	
	public void init()
	{
		
// Commented for P2.0
//		sizeMethods 		= new RegisteredMethods();
//		preMethods 			= new RegisteredMethods();
//		drawMethods 		= new RegisteredMethods();
//		postMethods 		= new RegisteredMethods();
//		mouseEventMethods 	= new RegisteredMethods();
//		keyEventMethods 	= new RegisteredMethods();
//		disposeMethods 		= new RegisteredMethods();
		
		//addListeners();
	}

	public void initializeFoetus()
	{
		noStroke();

		// Instantiate foetus object here
		f = new Foetus(this);
	}

	/**
	 * 
	 */
	public void draw()
	{
		//System.out.println("drawing");
	//	background(0);
		/*pushMatrix();
		fill(0,0,255);
		translate(0.75f*width, 0.75f*height);
		rotateY(map(mouseX, 0, width, 0, PI));
		rotateX(map(mouseY, 0, height, 0, PI));
		box(90);
		popMatrix();*/
	
		/*fill(255,0,0);
		pushMatrix();
		translate(0.25f*width, 0.25f*height);
		rotateY(map(mouseX, 0, width, 0, PI));
		rotateX(map(mouseY, 0, height, 0, PI));
		beginShape(QUADS);
		vertex(0, 0, 0);
		vertex(0, 90, 0);
		vertex(0, 90, 90);
		vertex(0, 0, 90);
		endShape();
		popMatrix();*/
}

	/**
	 * This method is called when an OSC message is received by the synth.
	 */
	public void oscEvent(OscMessage theOscMessage)
	{
		
	}
}
