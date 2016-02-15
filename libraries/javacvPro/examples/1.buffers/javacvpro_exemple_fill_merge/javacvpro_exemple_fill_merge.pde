// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Montre exemple utilisation de la fonction fill() et de la fonction mergeRGB()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        size(320*2,240*2); 

	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(320,240); // crée le buffer image de la taille voulue
 
        opencv.fill(opencv.BufferR, 255); // remplit tous les pixels du buffer Rouge avec la même valeur
        opencv.fill(opencv.BufferG, 255); // remplit tous les pixels du buffer Green (Vert) avec la même valeur
        opencv.fill(opencv.BufferB, 255); // remplit tous les pixels du buffer Bleu avec la même valeur
        
        image(opencv.getBufferR(),0,0); // affiche le buffer Rouge
        image(opencv.getBufferG(),320,0); // affiche le buffer Green (Vert)
        image(opencv.getBufferB(),0,240); // affiche le buffer Bleu

        opencv.mergeRGB(); // fusionne les buffers RGB dans le buffer principal
        image(opencv.getBuffer(),320,240); // affiche le buffer principal

        noLoop(); // stop programme         
}


void  draw() { // fonction exécutée en boucle

}


