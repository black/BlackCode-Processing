
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction canny()

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
        size (opencv.width()*2, opencv.height()); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        
        //--- affiche image de départ ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        //-- toutes ces formes sont possibles : 
        //opencv.canny(); // applique le filtre de canny sur le buffer principal OpenCV avec paramètres par défaut
        //opencv.canny(100,200); //applique le filtre de canny sur le buffer principal OpenCV avec paramètres - noyau 3x3 par défaut
        opencv.canny(1000,2000,5); //applique le filtre de canny sur le buffer OpenCV désigné avec paramètres

        //opencv.canny(opencv.Buffer,100,400); //applique le filtre de canny sur le buffer OpenCV désigné avec paramètres - noyau 3x3 par défaut
        //opencv.canny(opencv.Buffer,100,200,3); //applique le filtre de canny sur le buffer OpenCV désigné avec paramètres
        
        //opencv.invert(); // pour dessin au trait noir sur blanc
        
        //--- affiche image finale --- 
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing

       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


