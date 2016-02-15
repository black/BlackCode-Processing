// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple utilisation de la fonction average() - moyenne entre 2 images 

import monclubelec.javacvPro.*; // importe la librairie javacvPro

OpenCV opencv; // déclare un objet OpenCV principal

int widthCapture=320;
int heightCapture=200;

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        size(widthCapture*3,heightCapture); 

	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture,heightCapture); // crée le buffer image de la taille voulue

        opencv.fill(opencv.Memory,0); // remplit le buffer Memory en noir 
        image (opencv.getMemory(), 0,0); // affiche le buffer OpenCV

        opencv.fill(255); // remplit le buffer principal en blanc
        image (opencv.getBuffer(), widthCapture,0); // affiche le buffer OpenCV
        
        opencv.average(opencv.Memory, opencv.Buffer, opencv.Buffer); // réalise la moyenne des 2 images        
        image (opencv.getBuffer(), widthCapture*2,0); // affiche le buffer OpenCV
        
        noLoop(); // stop programme         
}


void  draw() { // fonction exécutée en boucle

}


