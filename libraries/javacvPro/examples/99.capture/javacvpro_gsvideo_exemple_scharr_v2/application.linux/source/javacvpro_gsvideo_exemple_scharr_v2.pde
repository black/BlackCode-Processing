
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - Mars 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction scharr - détection de contour par noyau de circonvolution

import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

GSCapture cam; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

int widthCapture=320*2; // largeur image capture
int heightCapture=240*2; // hauteur image capture
int fpsCapture=30; // framerate de Capture

int millis0=0; // variable mémorisation millis()
int millis0b=0; // variable mémorisation millis()

boolean flagCapture=false; 

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- initialise fenêtre Processing 
        size (widthCapture*2, heightCapture); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        frameRate(fpsCapture); // taux de rafraichissement de l'image 

       //---- initialise la webcam ---
       //cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
       cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video0", fpsCapture); // Initialise objet GSCapture désignant webcam - forme complète

        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture, heightCapture); // initialise les buffers OpenCv à la taille de l'image
        
      	cam.start();  // démarre objet GSCapture = la webcam 
      
}


void  draw() { // fonction exécutée en boucle

// Code capture GSVideo 

  //if (cam.available() == true) { // si une nouvelle frame est disponible sur la webcam
  if (flagCapture == true) { // si une nouvelle frame est disponible sur la webcam

        //background(0);  // fond noir entre 2 images 
  
        //------ gestion image webcam par GSCapture ------ 
        //cam.read(); // acquisition d'un frame 
        //image(cam1, 0, 0); // affiche image
        //set(0, 0, cam); // affiche image - plus rapide 
  
        //------- gestion image par Opencv ---------- 
  
        //imgSrc=cam1.get(); // récupère l'image GS video dans Pimage
        //opencv.copy(imgSrc); // charge l'image dans le buffer openCV
/*
        millis0=millis(); // mémorise millis()  
        opencv.copy(cam.get()); // autre possibilité - charge directement l'image GSVideo dans le buffer openCV
        println("Durée chargement buffer OpenCV=" + (millis()-millis0)+"ms."); 
        
        //--- affiche image de départ avant opération sur image ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        millis0=millis(); // mémorise millis()  

        //-- toutes ces formes sont possibles :
        //opencv.scharr(); // applique le filtre de scharr sur le buffer principal OpenCV avec paramètres par défaut - coeff=1
        opencv.scharr(0.4); //applique le filtre de scharr sur le buffer principal OpenCV avec coeff

        //opencv.scharr(opencv.Buffer,0.5); //applique le filtre scharr sur le buffer OpenCV désigné avec paramètres

        //--- pour effet "dessin au fusain"
        opencv.gray(); // passage en niveau de gris
        opencv.invert(); // pour dessin au trait noir sur blanc
 
       
        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 
       
        //--- affiche image finale --- 

        image(opencv.getBuffer(),widthCapture,0); // affiche le buffer principal OpenCV dans la fenêtre Processing        
*/
        flagCapture=false; // RAZ flag capture
        
  } // fin if available

} // fin draw

void captureEvent(GSCapture cam) {

  println("Durée entre 2 captures =" + (millis()-millis0b)+"ms soit un framerate de " + (1000.0/(millis()-millis0b))+"fps"); 
  
  millis0b=millis(); // mémorise millis()  

  millis0=millis(); // mémorise millis()  
  cam.read();
  println("Durée read =" + (millis()-millis0)+"ms."); 
  
        millis0=millis(); // mémorise millis()  
        opencv.copy(cam.get()); // autre possibilité - charge directement l'image GSVideo dans le buffer openCV
        println("Durée chargement buffer OpenCV=" + (millis()-millis0)+"ms."); 
        
        //--- affiche image de départ avant opération sur image ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        millis0=millis(); // mémorise millis()  

        //-- toutes ces formes sont possibles :
        //opencv.scharr(); // applique le filtre de scharr sur le buffer principal OpenCV avec paramètres par défaut - coeff=1
        opencv.scharr(0.4); //applique le filtre de scharr sur le buffer principal OpenCV avec coeff

        //opencv.scharr(opencv.Buffer,0.5); //applique le filtre scharr sur le buffer OpenCV désigné avec paramètres

        //--- pour effet "dessin au fusain"
        //opencv.gray(); // passage en niveau de gris
        //opencv.invert(); // pour dessin au trait noir sur blanc
 
       
        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 
        
                //--- affiche image finale --- 

        millis0=millis(); // mémorise millis()  
        image(opencv.getBuffer(),widthCapture,0); // affiche le buffer principal OpenCV dans la fenêtre Processing        
        println("Durée transfert vers Processing =" + (millis()-millis0)+" ms."); 


  flagCapture=true; // témoin capture


}

