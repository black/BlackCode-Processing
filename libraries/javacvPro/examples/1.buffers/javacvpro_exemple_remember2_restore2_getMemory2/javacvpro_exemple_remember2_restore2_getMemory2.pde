
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction remember(), restore(), getMemory()

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
        
        //--- mémorise image du buffer principal dans buffer Memory 
        //opencv.remember2(); 
        
        //--- affiche image de départ via le buffer mémory ---         
        image(opencv.getMemory2(),0,0); // affiche le buffer Memory OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        opencv.gray("BUFFER"); // transforme le buffer OpenCV désigné en niveau de gris - copie mise dans le buffer Gray
        
        //--- affiche image finale --- 
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing = image traitée
        
        opencv.restore2(); // recharge le buffer memory dans le buffer principal = l'image de départ 
        image (opencv.getBuffer(), opencv.width(), 0); // affiche le buffer principal OpenCV dans la fenêtre Processing
        
       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


