// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction detectFinger() 

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

String url="http://www.mon-club-elec.fr/mes_images/online/fond.png"; // String contenant l'adresse internet de l'image à utiliser
String url2="http://www.mon-club-elec.fr/mes_images/online/fond_main.png"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

Blob[] blobsArray=null; // tableau pour la détection des blobs (contour de forme)

ConvexityDefect[] cdArray=null; // tableau pour le stockage de convexity defect


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


        //--- détection des Blobs --- 
        blobsArray=opencv.blobs(opencv.Memory2, opencv.area()/64,opencv.area(),20, true, 1000, true); // applique blobs à l'objet IplImage avec paramètres et renvoie tableau d'objets Blob


        //-- dessin du rectangle autour du tracé de la forme -- 
        opencv.drawRectBlobs(blobsArray,opencv.width(),opencv.height(),1); // trace rectangle en se basant sur point référence et avec les paramètres

        //-- dessin du pourtour du blob sur l'image de départ --
        opencv.drawBlobs(blobsArray,opencv.width(),opencv.height(),1 ); // trace les formes du tableau de Blobs en se basant sur point référence + paramètre par défaut

        //-- dessin du centre du blob sur le tracé de la forme -- 
        opencv.drawCentroidBlobs (blobsArray,opencv.width(),opencv.height(),1); // trace le centre des Blob en se basant sur point référence + échelle et avec les paramètres par défaut

        //--- dessin des points des "zones de creux" (Convexity Defect) ---
         //opencv.drawConvexityDefect(blobsArray, xRef, yRef, scale, radius,colorStroke, strokeWeight, fill, colorFill, minDepth, debug);
        //opencv.drawConvexityDefect(blobsArray, opencv.width(),opencv.height(),1, 10,color(255,0,255), 2, false, 0, 20, true);

       for (int i=0; i<blobsArray.length; i++) { // passe en revue les blobs

          // Sélectionne les objets ConvexityDefect utiles 
          // ConvexityDefect[] convexityDefects(Blob blobIn, float minDepthIn, float angleMaxIn, boolean debugIn)
          cdArray=opencv.convexityDefects(blobsArray[i], (float)20, radians(110), true); 

          //--- dessine le tableau de ConvexityDefect
          //drawConvexityDefects(ConvexityDefect[] cdArrayIn, int xRefIn, int yRefIn, float scaleIn, int radius, int colorStrokeIn, int strokeWeightIn, boolean fillIn, int colorFillIn, boolean lineIn, int colorStrokeLineIn, int strokeWeightLineIn, int mode, boolean debugIn )
          opencv.drawConvexityDefects(cdArray, opencv.width(),opencv.height(),1.0, 10,color(0,255,255), 2, false, 0, true, color(0,0,255), 2, 1, false ); 


        }// fin for i - défile blobs  

       println("Valeur renvoyée par la fonction de détection des doigts : " + opencv.detectFinger(cdArray, false) + " doigts."); 

       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}

