package hog;

public class Histogram {
	private int bins;
	private int cell_width;
	private int cell_height;
	private boolean signed;
	private float histogram[];
	
	public Histogram(int bins, int cell_width, int cell_height, boolean signed){
		// Histogram settings
		this.bins = bins;
		this.cell_height = cell_height;
		this.cell_width=cell_width;
		this.signed = signed;
		
		// Histogram initialization
		histogram = new float[bins]; 
	}
	public Histogram(float[] histogram,int bins, int cell_width, int cell_height, boolean signed){
		// Histogram settings
		this.bins = bins;
		this.cell_height = cell_height;
		this.cell_width=cell_width;
		this.signed = signed;
		this.histogram=histogram; 
	}
	
	// Add a vote to the Histogram
	public boolean addVote(float vote, int bin){
		// If bin is outside the channels, stop
		if( bin<0 || bin>=bins ){
			return false;
		}
		
		// Add the current vote to the histogram
		histogram[bin] += vote;
		return true;
		
	}
	
	public int getBins() {
		return bins;
	}
	
	public void printHistogram() {
		for (int i=0; i<histogram.length; i++) {
			System.out.print(histogram[i]+", ");
		}
	}

	public float[] toVector() {
		// TODO Auto-generated method stub
		return histogram;
	}

	public int getCellWidth() {
		// TODO Auto-generated method stub
		return cell_width;
	}

	public int getCellHeight() {
		// TODO Auto-generated method stub
		return cell_height;
	}

	public boolean getSigned() {
		// TODO Auto-generated method stub
		return signed;
	}
	
}
