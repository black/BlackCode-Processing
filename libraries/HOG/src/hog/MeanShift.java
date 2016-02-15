package hog;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import utility.Matrix;

public class MeanShift extends Thread{
	private Semaphore wait_for_all;
	private ArrayList<Point_3D> dataset_points;
	private ArrayList<Point_3D> modes;
	private double sigma_x;
	private double sigma_y;
	private double sigma_s;
	private int point_index;
	private int max_iter;
	
	public MeanShift(Semaphore wait_for_all,ArrayList<Point_3D> dataset_points,ArrayList<Point_3D> modes,
			double sigma_x,double sigma_y,double sigma_s, int max_iter, int point_index) {
		this.wait_for_all=wait_for_all;
		this.dataset_points=dataset_points;
		this.modes=modes;
		this.sigma_s=sigma_s;
		this.sigma_x=sigma_x;
		this.sigma_y=sigma_y;
		this.max_iter=max_iter;
		this.point_index=point_index;
	}
	public void run() {
		Point_3D old_ym = null;
		Point_3D new_ym = dataset_points.get(point_index);
		int iter=0;
		do {
			iter++;
			old_ym=new_ym;
			new_ym=compute_mode(old_ym);
			
		}while (!old_ym.equals(new_ym) && iter<=max_iter);
		
		synchronized (modes) {
			if (!new_ym.already_exists_in(modes))
			modes.add(new_ym);	
		}
		
		wait_for_all.release();
	}
	
	private Point_3D compute_mode(Point_3D ym) {
		double [][] Hh=compute_matrix_Hh(ym);
		double [] fact2= new double[3];
		for (int i=0; i<dataset_points.size(); i++) {
			double weight=compute_weight(i, ym);
			double [][] Hi=compute_matrix_H(dataset_points.get(i).getScale_factor());
			
			Hi[0][0]=1/Hi[0][0];
			Hi[1][1]=1/Hi[1][1];
			Hi[2][2]=1/Hi[2][2];
			
			fact2[0]+=weight*Hi[0][0]*dataset_points.get(i).getX();
			fact2[1]+=weight*Hi[1][1]*dataset_points.get(i).getY();
			fact2[2]+=weight*Hi[2][2]*dataset_points.get(i).getScale_factor();
			
		}
		
		int x=(int) (Hh[0][0]*fact2[0]);
		int y=(int) (Hh[1][1]*fact2[1]);
		double scale_factor=Hh[2][2]*fact2[2];
		
		Point_3D next_ym=new Point_3D(x,y,scale_factor,ym.getWeight());
		return next_ym;
	}
	private double [][] compute_matrix_Hh(Point_3D y) {
		double [][] Hh=new double[3][3];
		for (int i=0; i<dataset_points.size(); i++) {
			double [][] Hi =compute_matrix_H(dataset_points.get(i).getScale_factor());
			Hi[0][0]=1/Hi[0][0];
			Hi[1][1]=1/Hi[1][1];
			Hi[2][2]=1/Hi[2][2];
			
			Hh[0][0]+=compute_weight(i, y)*Hi[0][0];
			Hh[1][1]+=compute_weight(i, y)*Hi[1][1];
			Hh[2][2]+=compute_weight(i, y)*Hi[2][2];
			
		}
		
		Hh[0][0]=1/Hh[0][0];
		Hh[1][1]=1/Hh[1][1];
		Hh[2][2]=1/Hh[2][2];
		
		return Hh;
	}
	
	//calculate the uncertainty matrix H to be associated to each point
	private double[][] compute_matrix_H(double scale_factor) {
		double [][] H=new double[3][3];
		
		H[0][0]=Math.pow(Math.exp(scale_factor)*sigma_x, 2);
		H[1][1]=Math.pow(Math.exp(scale_factor)*sigma_y, 2);
		H[2][2]=Math.pow(sigma_s, 2);
		
		return H;
	}
	
	private double compute_weight(int i,Point_3D y) {
		double scale_factor=dataset_points.get(i).getScale_factor();
		double [][] Hi=compute_matrix_H(scale_factor);
		double num=point_contribute(Hi, y, dataset_points.get(i));
		double den=0;
		for (int j=0; j<dataset_points.size(); j++) {
			double [][] Hj=compute_matrix_H(dataset_points.get(j).getScale_factor());
			den+=point_contribute(Hj, y, dataset_points.get(j));
		}
		
		double weight=num/den;
		
		return weight;
	}
	
	private double compute_H_Norm(double[][] H) {
		//we use norm 1. It works only for diagonal matrix.
		return Math.max(Math.max(Math.abs(H[0][0]),Math.abs(H[1][1])), Math.abs(H[2][2])); 
	}
	
	private double compute_Mahalanabois_distance(Point_3D y,Point_3D yi,double [][] Hi) {
		double distance=0;
		distance+=Math.pow((y.getX()-yi.getX()),2)/Hi[0][0];
		distance+=Math.pow((y.getY()-yi.getY()),2)/Hi[1][1];
		distance+=Math.pow((y.getScale_factor()-yi.getScale_factor()),2)/Hi[2][2];
		return distance;
	}
	
	private double point_contribute(double [][] Hi,Point_3D y,Point_3D yi) {
		double H_norm=compute_H_Norm(Hi);
		double mahalanabois_distance=compute_Mahalanabois_distance(y, yi, Hi);
		return Math.pow(H_norm,-0.5)*weight_transformation(yi.getWeight(),0)*Math.exp(-mahalanabois_distance/2);
	}
	
	private double weight_transformation(double weight, int c) {
		if (weight<c)
			return 0;
		else
			return weight - c;
	}
}
