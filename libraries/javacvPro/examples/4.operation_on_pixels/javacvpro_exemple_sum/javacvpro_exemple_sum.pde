// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Montre exemple utilisation de la fonction sum() et variantes

import monclubelec.javacvPro.*; // importe la librairie javacvPro

OpenCV opencv; // déclare un objet OpenCV principal

int widthCapture=320;
int heightCapture=200;

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        size(widthCapture*2,heightCapture); 

	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture,heightCapture); // crée le buffer image de la taille voulue

        opencv.fill(0,0,0); 
        
        float val=opencv.sum();
        println(); 
        println("Image RGB noire : La somme des pixels du Buffer principal vaut : "+val); 

       image(opencv.getBuffer(), 0,0);  

        opencv.fill(255,255,255); 
        
        val=opencv.sum();
        println(); 
        println("Image RGB blanche : La somme des pixels du Buffer principal vaut : "+val); 
        
        println("Contrôle = " + widthCapture +" x "+heightCapture+" pixels x 3 x 255 =" +  (float)(widthCapture*heightCapture*3*255)); 

        float[] valArray=opencv.sumRGB();

        println(); 
        println("La somme des pixels du canal R du Buffer principal vaut : "+valArray[0]); 
        println("La somme des pixels du canal G du Buffer principal vaut : "+valArray[1]); 
        println("La somme des pixels du canal B du Buffer principal vaut : "+valArray[2]); 
        println("La somme des 3 canaux vaut : " + (valArray[0] + valArray[1] + valArray[2])); 
        
       image(opencv.getBuffer(), widthCapture,0);  

        noLoop(); // stop programme         
}


void  draw() { // fonction exécutée en boucle

}


