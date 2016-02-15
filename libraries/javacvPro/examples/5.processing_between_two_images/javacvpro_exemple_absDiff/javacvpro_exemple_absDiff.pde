
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction mixerRGBGray() avec comparatif mixerRGB()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

String url="http://www.mon-club-elec.fr/mes_images/online/fond.png"; // String contenant l'adresse internet de l'image à utiliser
String url2="http://www.mon-club-elec.fr/mes_images/online/fond_main.png"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée = le fond --- 
        img=loadImage(url); // crée un PImage contenant le fichier à partir adresse web
        
        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(img.width, img.height); // initialise les buffers OpenCv à la taille de l'image
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        opencv.remember(); // conserve le buffer dans le buffer Memory
        
        //--- initialise fenêtre Processing 
        size (opencv.width()*2, opencv.height()*2); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        
        //--- affiche image de départ ---         
        image(opencv.getMemory(),0,0); // affiche le buffer Memory OpenCV dans la fenêtre Processing


        //-- charge image utilisée = le fond + objet --- 
        img=loadImage(url2); // crée un PImage contenant le fichier à partir adresse web
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing


        //--- application de la fonction absDiff
        opencv.absDiff(); // réalise soustraction Memory et Buffer principal - résultat mis dans Memory 2

        image(opencv.getMemory2(),0,opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- application d'un seuillage binaire pour isoler objet détecté ---
        //opencv.gray(opencv.Memory2); // pas indispensable - la fonction threshold transforme en niveau de gris l'image avant seuillage 
        opencv.threshold(opencv.Memory2,0.15, "BINARY"); // seuillage binaire pour éliminer le fond - valeur seuil basse possible car fond éliminé
        image(opencv.getMemory2(),opencv.width(),opencv.height()); // affiche le buffer Memory2 OpenCV dans la fenêtre Processing

       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


