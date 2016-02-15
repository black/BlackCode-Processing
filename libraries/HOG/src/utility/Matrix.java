package utility;

import hog.PixelGradientVector;

public class Matrix {
	public static PixelGradientVector[][] getSubMatrix(PixelGradientVector[][] matrix,int xmin,int xmax,int ymin,int ymax) {
		PixelGradientVector[][] submatrix=new PixelGradientVector[ymax-ymin+1][xmax-xmin+1];
		for (int y=ymin; y<=ymax; y++) {
			for (int x=xmin; x<=xmax; x++) {
				submatrix[y-ymin][x-xmin]=matrix[y][x];
			}
		}	
		return submatrix;	
	}
	public static void printMatrix(Integer[][] matrix) {
		int matrix_width=matrix[0].length;
		int matrix_height=matrix.length;
		for (int y=0; y<matrix_height; y++) {
			System.out.println();
			for (int x=0; x<matrix_width; x++) {
				System.out.print(" "+matrix[y][x]);
			}
		}
	}
}
