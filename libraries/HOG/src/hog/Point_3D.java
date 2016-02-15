package hog;

import java.util.ArrayList;

public class Point_3D {
	private int x;
	private int y;
	private double scale_factor;
	private double weight;
	public Point_3D(int x, int y, double scale_factor,double weight) {
		this.x=x;
		this.y=y;
		this.scale_factor=scale_factor;
		this.weight=weight;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getScale_factor() {
		return scale_factor;
	}
	public void setScale_factor(double scale_factor) {
		this.scale_factor = scale_factor;
	}
	public void setWeight(double weight) {
		this.weight=weight;
	}
	public double getWeight() {
		return weight;
	}
	public boolean equals(Point_3D point) {
		if (x==point.getX() && y==point.getY() && scale_factor==point.getScale_factor())
			return true;
		return false;
	}
	public boolean equals_with_tollerance(Point_3D point,double tollerance) {
		if (Math.abs((x-point.getX())/(double)x)<=tollerance && Math.abs((y-point.getY())/(double)y)<=tollerance && Math.abs((scale_factor-point.getScale_factor())/scale_factor)<=tollerance)
			return true;
		return false;
	}
	public boolean already_exists_in(ArrayList<Point_3D> points) {
		// TODO Auto-generated method stub
		boolean found=false;
		int i=0;
		while (i<points.size() && !found) {
			if (this.equals_with_tollerance(points.get(i),0.01)) {
				found=true;
			}
			i++;
		}
		return found;
	}
}
