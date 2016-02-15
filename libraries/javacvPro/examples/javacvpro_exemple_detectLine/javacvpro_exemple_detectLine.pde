
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - decembre 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction detectCircles()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

import java.awt.*; // importe la classe Rectangle, Point du langage Java
// l'objet rectangle fournit les champs x,y du centre et hauteur/largeur (height/width) du rectangle

PImage img;

//Blob[] blobsArray=null; // tableau pour la détection des blobs (contour de forme)

//Circle[] circlesArray=null; // tableau pour la détection des cercles

Line[] linesArray=null; // tableau pour la détection des lignes

String url="http://www.mon-club-elec.fr/mes_images/online/lines.png"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal



void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée --- 
        img=loadImage(url,"jpg"); // crée un PImage contenant le fichier à partir adresse web
        
        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(img.width, img.height); // initialise les buffers OpenCv à la taille de l'image
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        
        //--- initialise fenêtre Processing 
        size (opencv.width()*2, opencv.height()*2); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // alternative en se basant sur l'image d'origine
  
        //--- coordonnées de référence pour center l'image
        int xRef0=width/4; 
        int yRef0=height/4; 

        //--- affiche image de départ ---         
        image(opencv.getBuffer(),xRef0+0,yRef0+0); // affiche le buffer principal OpenCV dans la fenêtre Processing


        //--- opérations sur image ---
        
        opencv.gray(); // passe en niveaux de gris

        println("debut =" + millis()); 
        
        linesArray=opencv.detectLines ( // détection de ligne avec algorithme de Hough version Standard
				opencv.Buffer, // opencv_core.IplImage iplImgIn, // image source 
				1, // double rhoIn, // la résolution pour la longueur du vecteur normal
				1, // double thetaIn, // la résolution angulaire reçue en degré - converti en radians pour la fonction native OpenCV
				200,//float thresholdCannyIn, // ajout : 1er seuil du filtre Canny utilisé. Le second seuil Canny vaut seuilCannyIn/2 - Canny pas utilisé ici si =0
				200,//int thresholdAccumulatorIn, // seuil pour l'accumulateur - droite sélectionnée seulement si nombre vote > seuil 
				true //boolean debug // drapeau affichage messages
				); 


/*        
        for (int i=0; i<linesArray.length; i++) { // passe en revue les lignes 
          
          stroke (255,0,0); 
          line(xRef0+linesArray[i].pointY0.x, yRef0+linesArray[i].pointY0.y, xRef0+linesArray[i].pointYMax.x, yRef0+linesArray[i].pointYMax.y); // trace lignes 
          
        }
*/


       opencv.drawLines( // dessine les lignes du tableau d'objet Line 
			linesArray, // Line[] linesIn, // le tableau d'objets Lines à tracer 
			xRef0, yRef0, // int xRefIn, int yRefIn, // coordonnées référence objet
			1, // float scaleIn, // facteur d'échelle à utilisr - 1 par défaut  
			color(255,0,0), 1, // int colorStrokeIn, int strokeWeightIn, // paramètres graphiques
			true // boolean debug // debug
			);

        println("fin =" + millis()); 
/*
      //-------- dessin de lignes individuelle ---------- 
       opencv.drawLine( // dessin d'un seul objet Line 
       			linesArray[0], // Line lineIn, // L'objet Line à tracer 
			xRef0, yRef0, // int xRefIn, int yRefIn, // coordonnées référence objet
			1, // float scaleIn, // facteur d'échelle à utilisr - 1 par défaut  
			color(0,0,255), 1, // int colorStrokeIn, int strokeWeightIn, // paramètres graphiques
			true // boolean debug // debug
			);

       opencv.drawLine( // dessin d'un seul objet Line 
       			linesArray[2], // Line lineIn, // L'objet Line à tracer 
			xRef0, yRef0, // int xRefIn, int yRefIn, // coordonnées référence objet
			1, // float scaleIn, // facteur d'échelle à utilisr - 1 par défaut  
			color(0,0,255), 1, // int colorStrokeIn, int strokeWeightIn, // paramètres graphiques
			true // boolean debug // debug
			);
*/
/*        
        //--- recherche points intersection 
        
        //Point interPoint= opencv.interLines (linesArray[0].a, linesArray[0].b, linesArray[2].a, linesArray[2].b); // à partir des paramètres des objets Line
        Point interPoint= opencv.interLines (linesArray[0], linesArray[2]); // à partir des objets Line
        
        fill(255,0,255); 
        stroke(255,0,255); 
        ellipse(xRef0+interPoint.x, yRef0+interPoint.y, 5,5); // trace le point
*/        

       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


