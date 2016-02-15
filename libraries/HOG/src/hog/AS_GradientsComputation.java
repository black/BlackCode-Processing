package hog;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import processing.core.*;
import processing.*;

public class AS_GradientsComputation implements GradientsComputation {

	public AS_GradientsComputation() {

	}
	public PixelGradientVector[][] computeGradients(PImage image,PApplet parent) {
		int width = image.width;
		int height= image.height;
		PixelGradientVector[][] gradientMatrix=new PixelGradientVector[height][width];
		int xstart=0;
		int ystart=0;
		
		//we apply a gray filter
		image.filter(image.GRAY);
		for (int x = xstart; x < width; x++) {
			for (int y = ystart; y < height; y++ ) {
				//compute horizontal filters
				float horizontal_derivative = filter1Dhorizontal(parent, image, x, y);
				//compute vertical filters
				float vertical_derivative = filter1Dvertical(parent, image, x, y);
				
				//calculate the magnitude
				float magnitude=computeModule(horizontal_derivative,vertical_derivative);
				
				//calculate angle for pixel's gradient
				float angle=PApplet.atan2(vertical_derivative, horizontal_derivative)*180/PApplet.PI;
				gradientMatrix[y][x]=new PixelGradientVector(magnitude,angle);

			}
		  }
		return gradientMatrix;
		
	}

	private float computeModule(float horizontal_derivative, float vertical_derivative) {
		float module=PApplet.sqrt(horizontal_derivative*horizontal_derivative+vertical_derivative*vertical_derivative);
		return module;
	}

	/*applica il filtro 1D orizzontale per la computazione del gradiente orizzontale.
	 * Restituisce
	 */
	private float filter1Dhorizontal(PApplet parent,PImage image,int x,int y) {
		int filtro[]={-1, 0, 1};
		float brightness=(float)0.0;
		float prova=(float)0.0;
		int offset = filtro.length / 2;
		for (int i = 0; i < filtro.length; i++){
			int xloc = x+i-offset;
		    int yloc = y;
		    int loc = xloc + image.width*yloc;
			// Make sure we haven't walked off our image, we could do better here
			loc = PApplet.constrain(loc,0,image.pixels.length-1);
			brightness += parent.brightness(image.pixels[loc])*filtro[i];
			
		}
		return brightness;
	}
	
	private float filter1Dvertical(PApplet parent,PImage image,int x,int y) {
		int filtro[]={-1, 0, 1};
		float brightness=(float)0.0;
		int offset = filtro.length / 2;
		for (int i = 0; i < filtro.length; i++){
			int yloc = y+i-offset;
			int xloc = x;
			int loc = xloc + image.width*yloc;
			// Make sure we haven't walked off our image, we could do better here
			loc = PApplet.constrain(loc,0,image.pixels.length-1);

			brightness+= parent.brightness(image.pixels[loc])*filtro[i];
		}
		return brightness;
	}
}
