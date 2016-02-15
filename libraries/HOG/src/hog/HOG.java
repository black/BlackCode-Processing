package hog;
import processing.core.PApplet;
import processing.core.PImage;


public class HOG implements HOG_Factory{
	private static HOG instance=null;
	public static HOG createInstance() {
		if (instance==null) {
			instance=new HOG();
		}
		return instance;
	}

	public GradientsComputation createGradientsComputation() {
		AS_GradientsComputation gc=new AS_GradientsComputation();
		return gc;
	}

	public HistogramsComputation createHistogramsComputation(int bins, int cell_width,int cell_height, boolean signed, Voter voter) {
		AS_HistogramsComputation hc=new AS_HistogramsComputation(bins,cell_width,cell_height,signed,voter);
		return hc;
	}

	public BlocksComputation createBlocksComputation(int block_height, int block_width, int cell_overlap, Norm norm) {
		AS_BlocksComputation bc=new AS_BlocksComputation(block_width,block_height,cell_overlap,norm);
		return bc;
	}

	public DescriptorComputation createDescriptorComputation() {
		AS_DescriptorComputation dc=new AS_DescriptorComputation();
		return dc;
	}
	public Detector createDetector(GradientsComputation gc,HistogramsComputation hc,BlocksComputation bc,DescriptorComputation dc,int window_width,int window_height,int stride, int n) {
		AS_Detector detector=new AS_Detector(gc,hc,bc,dc,window_width,window_height,stride,n);
		return detector;
		
	}
	
}
