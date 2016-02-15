package hog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.*;

public class Hog_Printer {
	public static final int [] GREEN = {0,255,0};
	public static final int [] BLUE = {0,0,255};
	public static final int [] RED = {255, 0, 0};
	public static final int [] MAGENTA = {255, 0, 255};
	public static final int [] BLACK = {0, 0, 0};
	
	public static void print_results_onvideo(PApplet parent, ArrayList<Point_3D> detected_points, int window_width, int window_height, int [] rgb_boxes) {
		System.out.print("Printing bounding boxes...");
		//we print bounding boxes for all the positive detection windows fusing with mean shift.
		for (int i=0; i<detected_points.size(); i++) {
			Point_3D point=detected_points.get(i);
			int x_start=(int) (point.getX()-window_width/2*point.getScale_factor());
			int y_start=(int) (point.getY()-window_height/2*point.getScale_factor());
			int width=(int) (window_width*point.getScale_factor());
			int height=(int) (window_height*point.getScale_factor());
			
			parent.noFill();
			parent.stroke(rgb_boxes[0],rgb_boxes[1],rgb_boxes[2]);
			parent.rect(x_start, y_start, width, height);
		}
		
		System.out.print("Done.");
	}
	
	public static void print_results_onfile(ArrayList<Point_3D> detected_points, int window_width,int window_height, String filename, String image_name) throws IOException {
		
		BufferedWriter out = null;
		

		FileWriter fstream = new FileWriter(filename);
		out = new BufferedWriter(fstream);

		out.write("#Test image: "+image_name+"\n\n");
		out.write("Number of object detected: "+detected_points.size()+"\n\n");
		
		for (int i=0; i<detected_points.size(); i++) {
			Point_3D point=detected_points.get(i);
			int x_start=(int) (point.getX()-window_width/2*point.getScale_factor());
			int y_start=(int) (point.getY()-window_height/2*point.getScale_factor());
			int width=(int) (window_width*point.getScale_factor());
			int height=(int) (window_height*point.getScale_factor());
			
			out.write("#Details for object "+(i+1)+"\n");
			out.write("Center point on object "+(i+1)+" (X , Y) : ("+point.getX()+" , "+point.getY()+")\n");
			out.write("Bounding box for object "+(i+1)+" (Xmin, Ymin) - (Xmax, Ymax) : ("+x_start+" , "+y_start+") - ("+(x_start+width)+" , "+(y_start+height)+")\n");
		}
		
		out.close();

	}
}
