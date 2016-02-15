
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - Mars 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction Cascade - drawRect - détection de visages 

import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/

import monclubelec.javacvPro.*; // importe la librairie javacvPro
import java.awt.*; // pour classes Point , Rectangle..

PImage img;
Rectangle[] faceRect; 

GSCapture cam; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

int widthCapture=320; // largeur image capture
int heightCapture=240; // hauteur image capture
int fpsCapture=30; // framerate de Capture

int millis0=0; // variable mémorisation millis()

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- initialise fenêtre Processing 
        size (widthCapture, heightCapture); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        frameRate(fpsCapture); // taux de rafraichissement de l'image 

       //---- initialise la webcam ---
       //cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
       cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video0", fpsCapture); // Initialise objet GSCapture désignant webcam - forme complète

        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture, heightCapture); // initialise les buffers OpenCv à la taille de l'image
        
      	cam.start();  // démarre objet GSCapture = la webcam 
      
        //-- charge le fichier de description ---        
        //opencv.cascade("FRONTALFACE_ALT", true); // initialise détection de visage 
        opencv.cascade("/usr/local/share/OpenCV/haarcascades/","haarcascade_frontalface_alt.xml"); // utilise chemin absolu Rép + nom fichier
        // supporte chemin absolu avec ou sans / en fin de chaine

}


void  draw() { // fonction exécutée en boucle

// Code capture GSVideo 


  
  if (cam.available() == true) { // si une nouvelle frame est disponible sur la webcam

        //background(0);  // fond noir entre 2 images 
  
        //------ gestion image webcam par GSCapture ------ 
        cam.read(); // acquisition d'un frame 
        //image(cam1, 0, 0); // affiche image
        //set(0, 0, cam); // affiche image - plus rapide 
  
        //------- gestion image par Opencv ---------- 
  
        img=cam.get(); // récupère l'image GS video dans Pimage

        millis0=millis(); // mémorise millis()  
        opencv.copy(img); // charge l'image GSVideo dans le buffer openCV
        println("Durée chargement buffer OpenCV=" + (millis()-millis0)+"ms."); 
        
        //--- affiche image de départ avant opération sur image ---         
        image(img,0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        millis0=millis(); // mémorise millis()  

        //faceRect = opencv.detect(true); // détection des visages avec messages debug
        faceRect = opencv.detect(3,true); // détection des visages avec coeff vitesse élevée et messages debug
        
        //opencv.drawRectDetect(true); // affiche les rectangles détectés avec messages debug

        //drawRectDetect (xRef, yRef, scale, colorStroke, strokeWeight , fill, colorFill, debug);
        opencv.drawRectDetect (0, 0, 1, color(255,255,0), 3 , true, color(0,0,255), true); // tracé avec les paramètres


        println("Nombre de visages de face détectés =" + faceRect.length + "."); 
 
       
        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 
        
        //--- affiche image finale --- 

        //image(opencv.getBuffer(),widthCapture,0); // affiche le buffer principal OpenCV dans la fenêtre Processing        

  } // fin if available



} // fin draw


