package hog;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.*;


public interface Detector {
	public void train( String pos_directory, String neg_directory, String training_filename, PApplet parent, int random_windows_from_negatives ) throws IOException;
	public ArrayList<Point_3D> detect( PImage img_original, PApplet parent ) throws InterruptedException;
	public ArrayList<Point_3D> simple_detect( PImage img_original, PApplet parent );
	public void load_model( String model_filename ) throws IOException;
}
