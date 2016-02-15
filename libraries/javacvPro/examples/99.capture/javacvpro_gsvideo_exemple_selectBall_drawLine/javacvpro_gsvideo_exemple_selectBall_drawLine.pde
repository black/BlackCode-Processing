// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - Mars 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction selectBallBlobs - extraction de contour de formes binarisées

import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/

import monclubelec.javacvPro.*; // importe la librairie javacvPro

PImage img;

Blob[] blobsArray=null; // tableau pour la détection des blobs (contour de forme)

GSCapture cam; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

int widthCapture=320; // largeur image capture
int heightCapture=240; // hauteur image capture
int fpsCapture=30; // framerate de Capture

int millis0=0; // variable mémorisation millis()

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- initialise fenêtre Processing 
        size (widthCapture*2, heightCapture*2); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        frameRate(fpsCapture); // taux de rafraichissement de l'image 

       //---- initialise la webcam ---
       cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
       //cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video0", fpsCapture); // Initialise objet GSCapture désignant webcam - forme complète

        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture, heightCapture); // initialise les buffers OpenCv à la taille de l'image

      	cam.start();  // démarre objet GSCapture = la webcam 

}


void  draw() { // fonction exécutée en boucle

// Code capture GSVideo 

  if (cam.available() == true) { // si une nouvelle frame est disponible sur la webcam

        background(0);  // fond noir entre 2 images 

        //------ gestion image webcam par GSCapture ------ 
        cam.read(); // acquisition d'un frame 
        //image(cam1, 0, 0); // affiche image
        //set(0, 0, cam); // affiche image - plus rapide 

        //------- gestion image par Opencv ---------- 

        img=cam.get(); // récupère l'image GS video dans Pimage

        millis0=millis(); // mémorise millis()  
        opencv.copy(img); // autre possibilité - charge directement l'image GSVideo dans le buffer openCV
        println("Durée chargement buffer OpenCV=" + (millis()-millis0)+"ms."); 

        //--- affiche image de départ avant opération sur image ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        millis0=millis(); // mémorise millis()  

        //--- application du filtre mixerRGBGray()
        //opencv.mixerRGBGray(); // applique mixeur RGBGray sur le buffer principal OpenCV avec paramètres par défaut (1.0, 1.5,-2.0
        opencv.mixerRGBGray(1.0,1.5, -2.0); // mixerRGBGray appliqué sur objet IplImage avec paramètres - ici détection couleur orangée
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- application d'un seuillage binaire ---
        opencv.threshold(0.6, "BINARY"); // seuillage binaire pour éliminer le fond 
        image(opencv.getBuffer(),0,opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //-- détection de blobs ---
        //blobs=opencv.blobs(true); // blobs javacvPro avec paramètres par défaut +/- debug    

        //blobsArray=blobs(minArea,maxArea,maxBlob, findHoles, maxVertices, debug);
        blobsArray = opencv.blobs(opencv.area()/4000, opencv.area()/2, 20, true, 1000, true ); // blobs javacvPro +/- debug    
        // la fonction renvoie un tableau de Blob
        // chaque Blob est caractérisé par son aire, son centre, le rectangle entourant, le tableau des points du contour
        // pour info : la fonction blobs mémorise en interne un tableau des séquences de points détectés (jusqu'à l'appel suivant de la fonction blobs() )
        // pour utilisation avec les fonctions avancées convexHull et detectDefect par exemple

        //-- dessin du rectangle autour du tracé de la forme avant sélection -- 
        //opencv.drawRectBlobs (blobsArray, xRef, yRef, scale, colorStroke, strokeWeight, fill, colorFill)
        opencv.drawRectBlobs(blobsArray,0,opencv.height(),1, color(255,0,255),1,false,0); // trace le rectangle avec les paramètres

        //--- sélection des Blob cohérents --- 
        //blobsArray=opencv.selectBlobs(blobsArray, hwTest, ratioWHTest, ratioHWTest, areaTest, areaRatioTest, mode, debug); 
        //blobsArray=opencv.selectBlobs(blobsArray, true, 3, 3, true, 0.2, true, true); // sélectionne que les rectangles ayant au moins 20% surface occupée
        //blobsArray=opencv.selectBlobs(blobsArray, true, 0.8, 0.8, true, 0.6, true, true); // sélectionne que proche carré avec 60% surface occupée = balles        

        blobsArray=opencv.selectBallBlobs(blobsArray); // sélectionne uniquement les Blobs pouvant correspondre à 1 balle 
        //blobsArray=opencv.selectBlobs(blobsArray, true, 1, 20, true, 0.6, true, true); // équivalent - sélectionne que proche carré (ratio W/H=1+/-20%) avec 60% surface occupée = balles        

        //-- réaffichage image de départ --
        image(img,opencv.width(), opencv.height()); 

        //-- dessin du rectangle autour du tracé de la forme -- 
        //opencv.drawRectBlobs (blobsArray, xRef, yRef, scale, colorStroke, strokeWeight, fill, colorFill)
        // -- toutes ces formes sont possibles 
        //opencv.drawRectBlobs(blobsArray); // trace le rectangle avec les paramètres par défaut
        opencv.drawRectBlobs(blobsArray,opencv.width(),opencv.height(),1); // trace rectangle en se basant sur point référence et avec les paramètres
        //opencv.drawRectBlobs(blobsArray,opencv.width(),opencv.height(),1, color(255,0,255), 2, false, 0); // trace rectangle avec les paramètres

        //-- dessin du pourtour du blob sur l'image de départ --
        //opencv.drawBlobs (blobsArray, xRef, yRef, scale, radius, colorStroke, strokeWeight, fill, colorFill, mode);
        //-- toutes ces formes sont possibles 
        //opencv.drawBlobs(blobsArray); // trace les formes du tableau de Blobs avec paramètre par défaut
        opencv.drawBlobs(blobsArray,opencv.width(),opencv.height(),1 ); // trace les formes du tableau de Blobs en se basant sur point référence + paramètre par défaut
        //opencv.drawBlobs(blobsArray,opencv.width(),opencv.height(),1,5, color(0,0,255), 1, false, 0, 0); // trace pourtour en cercles bleus 

        //-- dessin du centre du blob sur le tracé de la forme -- 
        // opencv.drawCentroidBlobs (blobsArray, xRef, yRef, scale, radius, colorStroke, strokeWeight, fill, colorFill);
        // -- toutes ces formes sont possibles 
        //opencv.drawCentroidBlobs (blobsArray); // trace le centre des Blob avec les paramètres par défaut
        opencv.drawCentroidBlobs (blobsArray,opencv.width(),opencv.height(),1); // trace le centre des Blob en se basant sur point référence + échelle et avec les paramètres par défaut
        //opencv.drawCentroidBlobs (blobsArray,opencv.width(),opencv.height(),1,10); // trace le centre des Blob en se basant sur point référence + échelle + radius et avec les paramètres par défaut
        //opencv.drawCentroidBlobs (blobsArray,opencv.width(),opencv.height(),1,5, color(0,0,255), 1, false, 0); // trace le centre des Blob en fonction paramètres


        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 


        //------ tracé d'une ligne si 2 balles sont détectées ------- 
        
        if (blobsArray.length>1) { // si au moins 2 blobs détectés 
        
          opencv.drawLine( // trace la ligne entre les 2 blobs détectés en se basant sur les centres... 
                blobsArray[0].centroid, // le point de début de la ligne
                blobsArray[1].centroid, // le point de fin de la ligne
                widthCapture, heightCapture, // coordonnées référence objet
                1, // facteur d'échelle à utilisr - 1 par défaut  
                color(255,255,0), 5, // paramètres graphiques
                true // debug
                );
        }

        //--- affiche image finale --- 

        //image(opencv.getBuffer(),widthCapture,0); // affiche le buffer principal OpenCV dans la fenêtre Processing        

  } // fin if available

} // fin draw

