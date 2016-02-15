package hog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import processing.core.*;
import utility.DateUtils;
import utility.Matrix;


public class AS_Detector implements Detector{
	private GradientsComputation gc;
	private HistogramsComputation hc;
	private BlocksComputation bc;
	private DescriptorComputation dc;
	int window_width;
	int window_height;
	int stride;
	int number_of_resizes;
	private svm_model model;
	
	public AS_Detector(GradientsComputation gc,HistogramsComputation hc,BlocksComputation bc,DescriptorComputation dc,int window_width,int window_height,int stride, int n) {
		this.gc=gc;
		this.hc=hc;
		this.bc=bc;
		this.dc=dc;
		this.window_height=window_height;
		this.window_width=window_width;
		this.stride=stride;
		this.number_of_resizes=n;
	}
	
	public void load_model( String model_filename ) throws IOException{
		System.out.print("Loading model...");
		model = svm.svm_load_model(model_filename);
		System.out.println("Done.");
	}
	
	
	/* The training method only create a training file that have to be passed as parameter to the
	 * training application svm-train.
	 * training method. pos_directory must contain only images with pedestrian centered in.
	 * neg_directory must contain negative images (that not contain pedestrian).
	 * training_filename is the name of the output training file
	 * random_windows_for_negatives is the number of random windows that we take from each negative image
	 * 
	 */
	public void train(String pos_directory, String neg_directory, String training_filename, PApplet parent, int random_windows_from_negatives) throws IOException {
		
		BufferedWriter out = null;
		
		try{
			// Create training file 
			FileWriter fstream = new FileWriter(training_filename);
			out = new BufferedWriter(fstream);
			//Close the output stream
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		// preparing positive descriptors
		ArrayList<String> pos_files = utility.Utility.getFileNames(pos_directory);
		ArrayList<float[]> all_descriptors = new ArrayList<float[]>();
		ArrayList<Double> markers = new ArrayList<Double>();
		System.out.print("Preparing positive descriptors... ");
		
		int pos_size = pos_files.size();
		
		for( int i=0; i<pos_size; i++ ){
			if( i%(pos_size/10)==0 )
				System.out.println("siamo a "+i+" di "+pos_size);
			PImage img = parent.loadImage(pos_files.get(i));
			//we get only the center of the image.
			int border_x=(int)((img.width-window_width)/2.0);
			int border_y=(int)((img.height-window_height)/2.0);
			PImage center_image=img.get(border_x, border_y, window_width, window_height);
			ArrayList<DescriptorInfo> image_descriptors=getDescriptors(parent, center_image);
			for (int j=0; j<image_descriptors.size(); j++) {
				float[] v = image_descriptors.get(j).getDescriptor();
				out.write("+1");
				for( int p=0; p<v.length;p++){
					out.write(" "+(p+1)+":"+v[p]);
				}
				out.write("\n");
				
			}
		}
		System.out.println("done.");

		// preparing negative descriptors
		System.out.print("Preparing negative descriptors... ");
		ArrayList<String> neg_files = utility.Utility.getFileNames(neg_directory);
		int neg_size = neg_files.size();
		for( int i=0; i<neg_size; i++ ){
			if( i%(neg_size/10)==0 )
				System.out.println("siamo a "+i+" di "+neg_size);
			PImage img = parent.loadImage(neg_files.get(i));
			ArrayList<DescriptorInfo> image_descriptors = new ArrayList<DescriptorInfo>();
			if( random_windows_from_negatives<=0 ){
				// No random windows, using the whole image to compute descriptors
				image_descriptors=getDescriptors(parent, img);
			}else{
				for( int k=0; k<random_windows_from_negatives;k++ ){
					// Using random detection windows from the image to compute descriptors
					int x = (int)(Math.random()*(img.width-window_width));
					int y = (int)(Math.random()*(img.height-window_height));
					PImage sub_img = img.get(x, y, window_width, window_height);
					ArrayList<DescriptorInfo> single_descriptor = getDescriptors(parent, sub_img);
					for( int j=0; j<single_descriptor.size();j++ ){
						image_descriptors.add(single_descriptor.get(j));
					}
				}
			}
			
			for (int j=0; j<image_descriptors.size(); j++) {
				float[] v = image_descriptors.get(j).getDescriptor();
				out.write("-1");
				for( int p=0; p<v.length;p++){
					out.write(" "+(p+1)+":"+v[p]);
				}
				out.write("\n");
				
			}
		}
		System.out.println("done.");

		out.close();

	}
	

	public ArrayList<Point_3D> detect(PImage img_original, PApplet parent) throws InterruptedException{
		
		System.out.print("Detecting...");
		
		// Size of the image
		int img_width = img_original.width;
		int img_height = img_original.height;
		
		//setting the default stride
		if (stride<=0) {
			stride=1;
		}
		
		// Calculating the scale factor for each image dimension
		double fattore_x = Math.pow((double)window_width/img_width, 1.0/number_of_resizes);
		double fattore_y = Math.pow((double)window_height/img_height, 1.0/number_of_resizes);

		// Global image scale factor for both dimensions
		double scale_factor = ( fattore_x>fattore_y )?(fattore_x):(fattore_y);
		// If scale factor is greater than 1 (image size less than window) or no resize required
		if( scale_factor>=1 ){
			number_of_resizes = 0;
			scale_factor = 1;
		}
		
		//creating Semaphore for wait that all the Detection threads have finished
		Semaphore wait_for_all = new Semaphore(0);
		int number_of_detectionthread=0;
		
		ArrayList<Point_3D> detection_list=new ArrayList<Point_3D>(); /*detection_list will contain all the positive detection point found in the image
		 														at different scale.*/
		
		
		// For each resize
		for( int k=0; k<=number_of_resizes; k++ ){
			int new_width = (int) ( img_width * Math.pow(scale_factor, k));
			int new_height = (int) (img_height * Math.pow(scale_factor, k));
			int new_stride = (int) ( stride*Math.pow(scale_factor, k) );
			if( new_stride<=1 ) new_stride = 1;
			int new_window_width = (int) ( window_width * Math.pow(1.0/scale_factor, k));
			int new_window_height = (int) (window_height * Math.pow(1.0/scale_factor, k));
			if( new_width<window_width ) new_width=window_width;
			if( new_height<window_height ) new_height=window_height;
			PImage img = img_original.get();
			img.resize( new_width, new_height );
			PixelGradientVector pgv[][] = gc.computeGradients(img, parent);
			
			for (int x=0; x+window_width<=new_width; x+=new_stride) {
				for (int y=0; y+window_height<=new_height; y+=new_stride) {
					//increasing number_of_detectionthread
					number_of_detectionthread++;
					
					Rect image_rect=new Rect(x,y,x+window_width-1,y+window_height-1);
					
					//here i have the initial point for each window detection
					// (new_x_start,new_y_start) is the start point of the window
					// ( new_x_start+new_window_width, new_y_start+new_window_height ) is the end point
					int new_x_start = (int) ( x * Math.pow(1.0/scale_factor, k));
					int new_y_start = (int) ( y * Math.pow(1.0/scale_factor, k));
					
					
					Point_3D detection_point=new Point_3D(new_x_start+new_window_width/2,new_y_start+new_window_height/2,Math.pow(1.0/scale_factor, k),0);
					
					//create and start a new DetectionWindow
					DetectionWindow dw=new DetectionWindow(wait_for_all,pgv,hc,bc,dc,image_rect,detection_point,model,detection_list);
					dw.start();
				}
			}
			
		}
		//conclude detection when all threads have finished
		wait_for_all.acquire(number_of_detectionthread);
		System.out.println("Done.");
		
		System.out.print("Start meanshift procedure...");
		
		//we apply mean shift for fusing detection windows
		ArrayList<Point_3D> modes=new ArrayList<Point_3D>();
		int max_iter=100; //max number of iterations for the mean shift procedure
		for (int i=0; i<detection_list.size(); i++) {
			MeanShift ms=new MeanShift(wait_for_all,detection_list,modes,2,16,Math.log(1.3),max_iter,i);
			ms.start();
		}
		
		//conclude mean shift procedure when all threads have finished
		wait_for_all.acquire(detection_list.size());
		System.out.println("Done.");
		
		return modes;
	}
	
	public ArrayList<Point_3D> simple_detect(PImage img_original, PApplet parent){
		System.out.print("Detecting...");
		
		// Size of the image
		int img_width = img_original.width;
		int img_height = img_original.height;
		
		//setting the default stride
		if (stride<=0) {
			stride=1;
		}
		
		// Calculating the scale factor for each image dimension
		double fattore_x = Math.pow((double)window_width/img_width, 1.0/number_of_resizes);
		double fattore_y = Math.pow((double)window_height/img_height, 1.0/number_of_resizes);

		// Global image scale factor for both dimensions
		double scale_factor = ( fattore_x>fattore_y )?(fattore_x):(fattore_y);
		// If scale factor is greater than 1 (image size less than window) or no resize required
		if( scale_factor>=1 ){
			number_of_resizes = 0;
			scale_factor = 1;
		}
		
		ArrayList<Point_3D> detection_list=new ArrayList<Point_3D>();
		
		// For each resize
		for( int k=0; k<=number_of_resizes; k++ ){
			int new_width = (int) ( img_width * Math.pow(scale_factor, k));
			int new_height = (int) (img_height * Math.pow(scale_factor, k));
			int new_stride = (int) ( stride*Math.pow(scale_factor, k) );
			if( new_stride<=1 ) new_stride = 1;
			int new_window_width = (int) ( window_width * Math.pow(1.0/scale_factor, k));
			int new_window_height = (int) (window_height * Math.pow(1.0/scale_factor, k));
			if( new_width<window_width ) new_width=window_width;
			if( new_height<window_height ) new_height=window_height;
			PImage img = img_original.get();
			img.resize( new_width, new_height );
			PixelGradientVector pgv[][] = gc.computeGradients(img, parent);
			for (int x=0; x+window_width<=new_width; x+=new_stride) {
				for (int y=0; y+window_height<=new_height; y+=new_stride) {
					//here i have the initial point for each window detection
					// (new_x_start,new_y_start) is the start point of the window
					// ( new_x_start+new_window_width, new_y_start+new_window_height ) is the end point
					int new_x_start = (int) ( x * Math.pow(1.0/scale_factor, k));
					int new_y_start = (int) ( y * Math.pow(1.0/scale_factor, k));
					//we have to extract a submatrix
					PixelGradientVector sub_pgv[][]=Matrix.getSubMatrix(pgv, x, x+window_width-1, y, y+window_height-1);
					Histogram[][] histograms = hc.computeHistograms(sub_pgv);
					Block[][] unnormalized_blocks = bc.computeBlocks(histograms);
					Block[][] normalized_blocks = bc.normalizeBlocks(unnormalized_blocks);
					float[] descriptor = dc.computeDescriptor(normalized_blocks);
					
					//creating svm_node[]
					svm_node nodes[] = new svm_node[descriptor.length];
					for( int j=0; j<descriptor.length; j++ ){
						nodes[j] = new svm_node();
						nodes[j].index=j+1;
						nodes[j].value=descriptor[j];
					}
					
					//predicting the decision for this detection window
					double predict[] = new double[1];
					svm.svm_predict_values(model, nodes, predict);
					
					
					if (predict[0]>0) {
						Point_3D detection_point=new Point_3D(new_x_start+new_window_width/2,new_y_start+new_window_height/2,Math.pow(1.0/scale_factor, k),0);
						detection_list.add(detection_point);
					}
				}
			}
			
		}
		System.out.println("Done.");
		return detection_list;
	}
	
	
	private ArrayList<DescriptorInfo> getDescriptors(PApplet parent,PImage img_original){
		// Size of the image
		int img_width = img_original.width;
		int img_height = img_original.height;
		
		//setting the default stride
		if (stride<=1) {
			stride=1;
		}
		
		// Calculating the scale factor for each image dimension
		double fattore_x = Math.pow((double)window_width/img_width, 1.0/number_of_resizes);
		double fattore_y = Math.pow((double)window_height/img_height, 1.0/number_of_resizes);

		// Global image scale factor for both dimensions
		double scale_factor = ( fattore_x>fattore_y )?(fattore_x):(fattore_y);
		// If scale factor is greater than 1 (image size less than window) or no resize required
		if( scale_factor>=1 ){
			number_of_resizes = 0;
			scale_factor = 1;
		}

		
		
		ArrayList<DescriptorInfo> descriptors=new ArrayList<DescriptorInfo>();
		// For each resize
		for( int k=0; k<=number_of_resizes; k++ ){
			int new_width = (int) ( img_width * Math.pow(scale_factor, k));
			int new_height = (int) (img_height * Math.pow(scale_factor, k));
			int new_stride = (int) ( stride*Math.pow(scale_factor, k) );
			if( new_stride<=1 ) new_stride = 1;
			int new_window_width = (int) ( window_width * Math.pow(1.0/scale_factor, k));
			int new_window_height = (int) (window_height * Math.pow(1.0/scale_factor, k));
			if( new_width<window_width ) new_width=window_width;
			if( new_height<window_height ) new_height=window_height;
			PImage img = img_original.get();
			img.resize( new_width, new_height );
			PixelGradientVector pgv[][] = gc.computeGradients(img, parent);
			for (int x=0; x+window_width<=new_width; x+=new_stride) {
				for (int y=0; y+window_height<=new_height; y+=new_stride) {
					//here i have the initial point for each window detection
					// (new_x_start,new_y_start) is the start point of the window
					// ( new_x_start+new_window_width, new_y_start+new_window_height ) is the end point
					int new_x_start = (int) ( x * Math.pow(1.0/scale_factor, k));
					int new_y_start = (int) ( y * Math.pow(1.0/scale_factor, k));
					//we have to extract a submatrix
					PixelGradientVector sub_pgv[][]=Matrix.getSubMatrix(pgv, x, x+window_width-1, y, y+window_height-1);
					Histogram[][] histograms = hc.computeHistograms(sub_pgv);
					Block[][] unnormalized_blocks = bc.computeBlocks(histograms);
					Block[][] normalized_blocks = bc.normalizeBlocks(unnormalized_blocks);
					float[] descriptor = dc.computeDescriptor(normalized_blocks);
					descriptors.add(new DescriptorInfo(descriptor,new_x_start,new_x_start+new_window_width,new_y_start,new_y_start+new_window_height));
				}
			}
			
		}
		return descriptors;
	}
	
}
