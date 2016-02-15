
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - Mars 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction keyPointSBD - implémente Simple Blob Detector

import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

GSCapture cam; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

Keypoint[] keypointsArray=null; // déclaration d'un tableau pour le stockage des points clés


int widthCapture=320; // largeur image capture
int heightCapture=240; // hauteur image capture
int fpsCapture=30; // framerate de Capture

int millis0=0; // variable mémorisation millis()

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- initialise fenêtre Processing 
        size (widthCapture*2, heightCapture); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        frameRate(fpsCapture); // taux de rafraichissement de l'image 

       //---- initialise la webcam ---
       cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
       //cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video0", fpsCapture); // Initialise objet GSCapture désignant webcam - forme complète

        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture, heightCapture); // initialise les buffers OpenCv à la taille de l'image
        
      	cam.start();  // démarre objet GSCapture = la webcam 
      
}


void  draw() { // fonction exécutée en boucle

// Code capture GSVideo 

  if (cam.available() == true) { // si une nouvelle frame est disponible sur la webcam

        background(0);  // fond noir entre 2 images 
  
        //------ gestion image webcam par GSCapture ------ 
        cam.read(); // acquisition d'un frame 
        //image(cam1, 0, 0); // affiche image
        //set(0, 0, cam); // affiche image - plus rapide 
  
        //------- gestion image par Opencv ---------- 
  
        //imgSrc=cam1.get(); // récupère l'image GS video dans Pimage
        //opencv.copy(imgSrc); // charge l'image dans le buffer openCV

        millis0=millis(); // mémorise millis()  
        opencv.copy(cam.get()); // autre possibilité - charge directement l'image GSVideo dans le buffer openCV
        println("Durée chargement buffer OpenCV=" + (millis()-millis0)+"ms."); 
        
        //--- affiche image de départ avant opération sur image ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        millis0=millis(); // mémorise millis()  

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
       
        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 
        
     

  } // fin if available

} // fin draw


