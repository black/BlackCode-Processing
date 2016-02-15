package hog;
import processing.core.PApplet;
import processing.core.PImage;


public interface HOG_Factory {
	public GradientsComputation createGradientsComputation();
	public HistogramsComputation createHistogramsComputation(int bins,int cell_width,int cell_height,boolean signed,Voter voter);
	public BlocksComputation createBlocksComputation(int block_height,int block_width,int cell_overlap, Norm norm);
	public DescriptorComputation createDescriptorComputation();
	public Detector createDetector(GradientsComputation gc,HistogramsComputation hc,BlocksComputation bc,DescriptorComputation dc,int window_width,int window_height,int stride, int n);
}
