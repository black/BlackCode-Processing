package hog;

public interface BlocksComputation {
	public Block[][] computeBlocks(Histogram[][] histograms);
	public Block[][] normalizeBlocks(Block[][] unnormalized_blocks);
}
