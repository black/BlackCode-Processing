// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction loadImage()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

String url="http://www.mon-club-elec.fr/mes_images/online/lena.jpg"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- initialise OpenCV ---
        opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This

        opencv.loadImage(url); // initialise les buffers OpenCV et charge le fichier image dans le buffer OpenCV 

        //--- initialise fenêtre Processing
        size (opencv.width(), opencv.height()); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV

        //--- affiche image ---        
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing


       noLoop(); // stop programme      

}


void  draw() { // fonction exécutée en boucle

}
