package monclubelec.javacvPro;

//======= import =========
//--- processing --- 
import processing.core.*;

//--- javacv / javacpp ---- 
import com.googlecode.javacpp.*;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

//---- java ---- 
import java.nio.*; // pour classe ByteBuffer
import java.awt.*; // pour classes Point , Rectangle..
import java.util.ArrayList; // pour ArrayList


public class Trajectory {
	
	/*
	 * La classe Trajectory crée un ArrayList de Point pour mémoriser une trajectoire  
	 * 
	 * 
	 */

	
	////////////// VARIABLES ////////////////
	// toutes les variables peuvent être private ou public
	
	//--- variables de d'instances (non static) ---
	// accessible pour une seule instance
	// à déclarer en private et accéder par accesseur


	ArrayList<Point> points = new ArrayList<Point>(); // alternative à un tableau fixe = créer un ArrayList de Point
	
	//--- variables de classe (static)
	// = accessible pour toutes les instances
	// à déclarer en public ou private 
	
	//--- variables générales 
	static PApplet p; // représente le PApplet Processing
		
	
	///////////   CONSTRUCTEURS  //////////
	// doivent avoir obligatoirement le meme nom que la classe
	
	//--- le constructeur par défaut
	public Trajectory (){
	
		
	}
	
	//--- le constructeur par défaut
	public Trajectory (PApplet theParent){
	
		p=theParent; 
	}




	///////////////////// METHODES ////////////////////////////
	
	// NB : les méthodes n'utilisant que des variables de classe doivent déclarées static
	
	//---- méthodes accesseurs (get) ---- (création automatique possible) 
	//--- pour accéder aux variables d'instance et aux variables de classe 
	
	//----- méthodes mutateurs (set) ---- (création automatique possible) 
	//---- pour modifier les variables d'instance et les variables de classe
	
	//---- méthodes de classe ------
	
	//--- add(x,y) --
	public void add(int xIn, int yIn) {
		
		points.add(new Point (xIn, yIn));
		
	} // fin add(int,int)
	
	//--- add(Point) ---
	public void add(Point pointIn) {
	
		points.add(new Point(pointIn.x, pointIn.y));
		
	} // fin add(Point)
	
	//--- size() : renvoie le nombre de points ajoutés à la trajectoire 
	public int size() {
		
		return(points.size()); // renvoie la taille du ArrayList de Points
		
	} // --- fin size()

	//--- draw() : trace la trajectoire 
	public void draw(int xRefIn, int yRefIn, float scaleIn, int radius, int colorStrokeIn, int strokeWeightIn, boolean fillIn, int colorFillIn, int mode) {
		
        //---------- fixe les paramètres graphiques à utiliser -----------
        if (fillIn) p.fill(colorFillIn) ; else p.noFill(); 
        p.stroke(colorStrokeIn); // couleur pourtour
        p.strokeWeight(strokeWeightIn); // épaisseur pourtour 

		if (points.size() >0) { // si au moins 1 point dans le ArrayList de point 
			
			if (radius==0) p.point(xRefIn+(points.get(0).x*scaleIn),yRefIn+(points.get(0).y*scaleIn)); // 1er point
			else p.ellipse(xRefIn+(points.get(0).x*scaleIn),yRefIn+(points.get(0).y*scaleIn),radius,radius); 
		      
		      for (int i=1; i<points.size(); i++) { // défile points 1 à fin 
		     
		      if (mode==0) { // si tracé de points / cercles 
					if (radius==0) p.point(xRefIn+(points.get(i).x*scaleIn),yRefIn+(points.get(i).y*scaleIn)); // 1er point
					else p.ellipse(xRefIn+(points.get(i).x*scaleIn),yRefIn+(points.get(i).y*scaleIn),radius,radius); 

		      } // fin if
		      
		      //-- sinon tracé de lignes  et cercle si radius > 1 -- 
		      else if (mode==1)	 {
		    	  p.line(xRefIn+(points.get(i-1).x*scaleIn),yRefIn+(points.get(i-1).y*scaleIn), xRefIn+(points.get(i).x*scaleIn),yRefIn+(points.get(i).y*scaleIn)); // trace ligne point i-1 à i
		  		if (radius>0)  p.ellipse(xRefIn+(points.get(i).x*scaleIn),yRefIn+(points.get(i).y*scaleIn),radius,radius); // trace point aussi si radius sup à 1 
		    	  
		      } // fin else if 
		      
		      } // fin for défilement points du ArrayList 

		} // fin points.size()>1 
	      
		
	} // fin draw 

	//---- clear() : vide le ArrayList de points --- 
	
	public void clear() {
	
		points.clear(); // vide le ArrayList de points 
	
	} // fin clear 
	
} // fin classe Trajectory

