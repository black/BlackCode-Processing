package hog;


public class Block {
	private Histogram[][] histograms;

	public Block(Histogram[][] _histograms) {
		this.histograms=_histograms;
		
	}

	public float[] toVector() {
		// TODO Auto-generated method stub
		int block_height=histograms.length;
		int block_width=0;
		if (block_height>0) {
			block_width=histograms[0].length;
		}
		float vector[];
		if (block_width>0 || block_height>0) {
			int bins=histograms[0][0].getBins();
			int dim_vector=block_height*block_width*bins;
			vector=new float[dim_vector];
			for (int y=0; y<block_height; y++) {
				for (int x=0; x<block_width; x++) {
					float[] histogram_vector=histograms[y][x].toVector();
					int start_pos=(y*block_width+x)*bins;
					for (int i=0; i<histogram_vector.length; i++) {
						vector[start_pos+i]=histogram_vector[i];
					}
				}
			}
		}else{
			vector=new float[0];
		}
		return vector;
		
	}

	public Block toBlock(float[] block_descriptor) {
		// TODO Auto-generated method stub
		Block return_block;
		int block_height=histograms.length;
		int block_width=0;
		if (block_height>0) {
			block_width=histograms[0].length;
		}
		if (block_width>0 || block_height>0) {
			Histogram[][] _histograms=new Histogram[block_height][block_width];
			int bins=histograms[0][0].getBins();
			int cell_width=histograms[0][0].getCellWidth();
			int cell_height=histograms[0][0].getCellHeight();
			boolean signed=histograms[0][0].getSigned();
			for (int y=0; y<block_height; y++) {
				for (int x=0; x<block_width; x++) {
					float _histogram[]=new float[bins];
					for (int i=0; i<bins; i++) {
						_histogram[i]=block_descriptor[2*y*bins+x*bins+i];
					}
					_histograms[y][x]=new Histogram(_histogram,bins,cell_width,cell_height,signed);
				}
			}
			return_block=new Block(_histograms);
		}else{
			Histogram[][] _histograms=new Histogram[0][0];
			return_block=new Block(_histograms);
		}
		
		return return_block;
	}
}
