// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - decembre 2011
// Tous droits réservés - Licence GPLv3

// Test du SimpleBlobDetector

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img; 

String url="http://www.mon-club-elec.fr/mes_images/online/ball.jpg"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

Keypoint[] keypointsArray=null; // déclaration d'un tableau pour le stockage des points clés


void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée --- 
        img=loadImage(url,"jpg"); // crée un PImage contenant le fichier à partir adresse web

	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(img.width, img.height); // initialise les buffers OpenCv à la taille de l'image
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        
        //--- initialise fenêtre Processing 
        size (opencv.width()*2, opencv.height()); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        smooth(); 
        
        //--- affiche image de départ ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing
        
        //--- applicationmixer RGBgray ---
        opencv.mixerRGBGray(); // applique mixeur RGB sur le buffer principal OpenCV avec paramètres par défaut - détection couleur orange
                
        //--- affiche image finale --- 
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- détection des points clés à l'aide du SimpleBlobDetector
        // le tableau de point clé va contenir les points des centres des Blobs sélectionnés ainsi que le rayon contenant 
        
        //keypointsArray=opencv.keypointsSBD(opencv.Buffer, true); // forme simplifiée utilisant paramètres par défaut 
        
        keypointsArray=opencv.keypointsSBD(  // forme complète SimpleBlobDetector         
          opencv.Buffer, // image ipl
          150, 250, 10, // float minThreshold, float maxThreshold, float thresholdStep,
	  1, //float minDistBetweenBlobs,
  	  true, 255, // boolean filterByColor, int blobColor,
	  true, 250,5000, // boolean filterByArea, float minArea, float maxArea, 
	  false, 0.8,1.2,// boolean filterByCircularity, float minCircularity, float maxCircularity,
	  false,0,0, // boolean filterByConvexity, float minConvexity, float maxConvexity,
	  false, 0,0, // boolean filterByInertia, float minInertiaRatio, float maxInertiaRatio,
	  2,// long minRepeatability,
          true // debug          
          ); // fin keypointsSBD
        
        //--- dessin des points clés à partir du tableau de points clés
        
        //opencv.drawKeypoints(keypointsArray, true); // forme simplifiée utilisant paramètres par défaut 
        
        opencv.drawKeypoints ( // forme complète fonction 
		keypointsArray, //Keypoint[] keypointsIn, 
		opencv.width(),0,1,//int xRefIn, int yRefIn, float scaleIn, 
                -1, // int radius - utiliser -1 pour rayon des cercles = size des Keypoints
		color(0,0,255), 1, //int colorStrokeIn, int strokeWeightIn, 
		true,color(0,0,255),//boolean fillIn, int colorFillIn, 
		true //boolean debug
		); // fin draw keypoints


        noLoop(); 
        
}


void  draw() { // fonction exécutée en boucle

}


