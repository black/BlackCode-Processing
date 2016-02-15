
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction threshold()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

String url="http://www.mon-club-elec.fr/mes_images/online/lena.jpg"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée --- 
        img=loadImage(url,"jpg"); // crée un PImage contenant le fichier à partir adresse web
        
        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(img.width, img.height); // initialise les buffers OpenCv à la taille de l'image
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        
        //--- initialise fenêtre Processing 
        size (opencv.width()*2, opencv.height()*3); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        
        //--- affiche image de départ ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image et affichage ---
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        opencv.threshold(0.5, "BINARY"); // applique seuillage binaire avec seuil 0.5 sur le buffer principal
        opencv.threshold(0.5); // autre forme
        opencv.threshold(opencv.Buffer,0.5, "BINARY"); // autre forme
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        opencv.threshold(0.5, "BINARY_INV"); // applique seuillage binaire inversé avec seuil 0.5 sur le buffer principal
        image(opencv.getBuffer(),0,opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing

        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        opencv.threshold(0.5, "TRUNC"); // applique seuillage TRUNC inversé avec seuil 0.5 sur le buffer principal
        image(opencv.getBuffer(),opencv.width(),opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing

        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        opencv.threshold(0.5, "TOZERO"); // applique seuillage TOZERO avec seuil 0.5 sur le buffer principal
        image(opencv.getBuffer(),0,opencv.height()*2); // affiche le buffer principal OpenCV dans la fenêtre Processing

        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        opencv.threshold(0.5, "TOZERO_INV"); // applique seuillage TOZERO inversé avec seuil 0.5 sur le buffer principal
        image(opencv.getBuffer(),opencv.width(),opencv.height()*2); // affiche le buffer principal OpenCV dans la fenêtre Processing

       noLoop(); // stop programme
}


void  draw() { // fonction exécutée en boucle

}


