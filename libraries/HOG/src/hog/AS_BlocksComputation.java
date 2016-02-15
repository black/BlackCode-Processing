package hog;

public class AS_BlocksComputation implements BlocksComputation {
	private int block_height;
	private int block_width;
	private int cell_overlap;
	private Norm norm;

	public AS_BlocksComputation(int block_width,int block_height, int cell_overlap, Norm norm) {
		this.block_height=block_height;
		this.block_width=block_width;
		this.cell_overlap=cell_overlap;
		this.norm=norm;
	}

	public Block[][] computeBlocks(Histogram[][] histograms) {
		
		// Is overlap suitable for block size ?
		int max = (block_height>block_width)?(block_height-1):(block_width-1);
		if( cell_overlap>max || cell_overlap<0 ){
			cell_overlap=0;
		}
		int vertical_histograms = histograms.length;		// Rows
		int horizontal_histograms = histograms[0].length;		// Columns
		
		
		int y_step = block_height - cell_overlap;
		int x_step = block_width - cell_overlap;
	
		//counting the number of horizontal blocks
		int horizontal_stride=block_width-cell_overlap;
		int horizontal_blocks=0;
		int pos=0;
		while (pos+block_width<=horizontal_histograms) {
			pos=pos+horizontal_stride;
			horizontal_blocks++;
		}
		
		//counting the number of vertical blocks
		int vertical_stride=block_height-cell_overlap;
		int vertical_blocks=0;
		pos=0;
		while (pos+block_height<=vertical_histograms) {
			pos=pos+vertical_stride;
			vertical_blocks++;
		}
		
		//int horizontal_blocks=(int)Math.min(horizontal_histograms/x_step+1, (horizontal_histograms+1)/x_step);
		//int vertical_blocks=(int)Math.min(vertical_histograms/y_step+1, (vertical_histograms+1)/y_step);
		
		Block[][] blocks = new Block[vertical_blocks][horizontal_blocks];
		
		// For each block
		for( int y=0; y<vertical_blocks; y++ ){
			for( int x=0; x<horizontal_blocks; x++ ){
				// Collecting histograms of the block
				Histogram[][] h = new Histogram[block_height][block_width];
				// Retrieve the positions of the cells
				for( int i=x*(block_width-cell_overlap); i<x*(block_width-cell_overlap)+block_width; i++ ){
					for( int j=y*(block_height-cell_overlap); j<y*(block_height-cell_overlap)+block_height; j++ ){
						//System.out.println("x: "+x+" y:"+y);
						int posJ = j-y*(block_height-cell_overlap);
						int posI = i-x*(block_width-cell_overlap);
						h[posJ][posI] = histograms[j][i];
						//System.out.println("posJ:"+posJ+" posI:"+posI+" j:"+j+" i:"+i);
					}
				}
				blocks[y][x] = new Block(h);
			}
		}
		
		return blocks;
	}

	public Block[][] normalizeBlocks(Block[][] unnormalized_blocks) {
		//instance normalized_blocks. It will contains the normalized blocks
		Block normalized_blocks[][]=new Block[unnormalized_blocks.length][unnormalized_blocks[0].length];
		for (int y=0; y<unnormalized_blocks.length; y++) {
			for (int x=0; x<unnormalized_blocks[0].length; x++) {
				Block unnormalized_block=unnormalized_blocks[y][x];
				float block_descriptor[]=unnormalized_block.toVector();
				//normalizing the block descriptor
				block_descriptor=norm.normalize(block_descriptor);
				//we create a Block object containing the normalized block_descriptor
				Block normalized_block=unnormalized_block.toBlock(block_descriptor);
				normalized_blocks[y][x]=normalized_block;
			}
		}
		return normalized_blocks;
	}

}
