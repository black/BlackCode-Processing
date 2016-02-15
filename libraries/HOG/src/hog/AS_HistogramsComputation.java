package hog;

/**
 * @author  alessio
 */
public class AS_HistogramsComputation implements HistogramsComputation{

	private int bins;
	private int cell_width;
	private int cell_height;
	private boolean signed;
	private Voter voter;
	
	public AS_HistogramsComputation(int bins,int cell_width,int cell_height,boolean signed,Voter voter ){
		
		this.bins=bins;
		this.cell_width=cell_width;
		this.cell_height=cell_height;
		this.signed=signed;
		this.voter=voter;
		
	}
	
	public Histogram[][] computeHistograms(PixelGradientVector[][] pgv){
		int ySize = pgv.length; //number of rows
		int xSize = pgv[0].length; //number of coloumns
		int histogram_width=xSize/cell_width;
		int histogram_height=ySize/cell_height;
		
		Histogram histogram[][] = new Histogram[histogram_height][histogram_width];
		
		// Getting the cells:
		//	- the step is "this.cellSize"
		//  - right and bottom border pixels may be excluded
		for( int y=cell_height; y<=ySize; y+=cell_height ){
			for( int x=cell_width; x<=xSize; x+=cell_width ){
				// Foreach cell, create Histogram exploring all the pixels
				int xPos =x/cell_width-1;
				int yPos = y/cell_height-1;
				histogram[ yPos ][ xPos ] = new Histogram(bins, cell_width,cell_height, signed);
				for( int i=x-cell_width; i<x; i++ ){
					for( int j=y-cell_height; j<y; j++ ){
						float magnitude = pgv[ j ][ i ].getMagnitude();
						float vote = voter.vote(magnitude);
						float angle = pgv[j][i].getAngle();
						int bin = getBin(angle, bins, signed);
						histogram[ yPos ][ xPos ].addVote( vote, bin );
					}
				}
			}
		}
		
		return histogram;
		
	}

	// Get the vote of the pixel
	private float getVote(float magnitude, Boolean w) {
		if( w==false )
			return 1;
		else
			return magnitude;
	}

	// Get the bin of the angle
	private int getBin(float angle, int bins, Boolean signed){
		int bin;
		
		// Signed: Forcing angles in [ 0; 360 ) degrees
		// Unsigned: Forcing angles in [ 0; 180 ) degrees
		if( signed==true && angle<0 )
			angle += 360;
		if(signed==false && angle<0)
			angle += 180;
		
		// Calculating the bin, but we can do better here
		int max_angle = 360;
		if( signed==false ) max_angle = 180;
		
		bin = ((int) (angle / max_angle * bins)) % bins;
		
		//System.out.println("bin: "+bin+", angle: "+angle);
		
		return bin;
	}	
}
