
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction cascade), detect() et drawRectDetect()

import monclubelec.javacvPro.*; // importe la librairie javacvPro
import java.awt.*; // pour classes Point , Rectangle..

PImage img;

Rectangle[] faceRect; 

String url="http://www.mon-club-elec.fr/mes_images/online/lena.jpg"; // String contenant l'adresse internet de l'image à utiliser
//String url="http://www.mon-club-elec.fr/mes_images/online/visages.png"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée --- 
        img=loadImage(url); // crée un PImage contenant le fichier à partir adresse web
        
        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(img.width, img.height); // initialise les buffers OpenCv à la taille de l'image
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
         
        //-- charge le fichier de description ---        
        //opencv.cascade("FRONTALFACE_ALT", true); // initialise détection de visage 
        //opencv.cascade("/usr/share/opencv/haarcascades/","haarcascade_frontalface_alt.xml"); // utilise chemin absolu Rép + nom fichier
        //opencv.cascade("/usr/share/opencv/haarcascades/","haarcascade_frontalface_alt.xml"); // utilise chemin absolu Rép + nom fichier
        opencv.cascade("/usr/local/share/OpenCV/haarcascades/","haarcascade_frontalface_alt.xml"); // utilise chemin absolu Rép + nom fichier
        
        // supporte chemin absolu avec ou sans / en fin de chaine

        //--- initialise fenêtre Processing 
        size (opencv.width(), opencv.height()); // crée une fenêtre Processing de la taille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        
        //--- affiche image de départ ---         
        image(opencv.image(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        faceRect = opencv.detect(true); // détection des visages avec messages debug
        
        opencv.drawRectDetect(true); // affiche les rectangles détectés avec messages debug

        println("Nombre de visages de face détectés =" + faceRect.length + "."); 

       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


