package hog;

public class DescriptorInfo {
	private float[] descriptor;
	private int xmin, xmax, ymin, ymax;
	public DescriptorInfo( float[] descriptor, int xmin, int xmax, int ymin, int ymax){
		this.descriptor=descriptor;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}
	public int getXmin() {
		return xmin;
	}
	public int getXmax() {
		return xmax;
	}
	public int getYmin() {
		return ymin;
	}
	public int getYmax() {
		return ymax;
	}
	public float[] getDescriptor() {
		// TODO Auto-generated method stub
		return descriptor;
	}
	public void print_coords() {
		System.out.println("("+xmin+","+ymin+") - ("+xmax+","+ymax+")");		
	}
}
