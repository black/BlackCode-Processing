package hog;
import processing.core.*;


public interface GradientsComputation {
	public PixelGradientVector[][] computeGradients(PImage image,PApplet parent);
}
