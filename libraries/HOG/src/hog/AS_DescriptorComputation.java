package hog;

public class AS_DescriptorComputation implements DescriptorComputation {
	
	public float[] computeDescriptor(Block[][] blocks) {
		int horizontal_blocks=blocks[0].length;
		int vertical_blocks=blocks.length;
		//calculate the dim of the vector of a single block
		int dim_single_vector=blocks[0][0].toVector().length;
		
		float[] descriptor=new float[vertical_blocks*horizontal_blocks*dim_single_vector];
		for (int y=0; y<vertical_blocks; y++) {
			for (int x=0; x<horizontal_blocks; x++) {
				float[] block_vector=blocks[y][x].toVector();
				for (int i=0; i<dim_single_vector; i++) {
					descriptor[y*horizontal_blocks*dim_single_vector+x*dim_single_vector+i]=block_vector[i];
				}
			}
		}
		return descriptor;
	}


}
