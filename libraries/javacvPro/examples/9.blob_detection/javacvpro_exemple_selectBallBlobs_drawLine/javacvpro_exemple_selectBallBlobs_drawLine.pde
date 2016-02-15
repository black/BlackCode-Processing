
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - octobre 2011
// Tous droits réservés - Licence GPLv3

// Exemple fonction blobs() et drawBlobs()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

//import java.awt.Rectangle; // importe la classe Rectangle du langage Java
// l'objet rectangle fournit les champs x,y du centre et hauteur/largeur (height/width) du rectangle

PImage img;

Blob[] blobsArray=null; // tableau pour la détection des blobs (contour de forme)


//String url="http://www.mon-club-elec.fr/mes_images/online/selectblobs.png"; // String contenant l'adresse internet de l'image à utiliser
String url="/home/hinault/Bureau/trans/ballx2.png"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée --- 
        img=loadImage(url,"png"); // crée un PImage contenant le fichier à partir adresse web
        
        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(img.width, img.height); // initialise les buffers OpenCv à la taille de l'image
        
        opencv.copy(img); // charge le PImage dans le buffer OpenCV
        
        //--- initialise fenêtre Processing 
        size (opencv.width()*2, opencv.height()*2); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        
        //--- affiche image de départ ---         
        image(opencv.getBuffer(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        //--- application du filtre mixerRGBGray()
        //opencv.mixerRGBGray(); // applique mixeur RGBGray sur le buffer principal OpenCV avec paramètres par défaut (1.0, 1.5,-2.0
        opencv.mixerRGBGray(1.0,1.5, -2.0); // mixerRGBGray appliqué sur objet IplImage avec paramètres - ici détection couleur orangée

        //--- application d'un seuillage binaire ---
        opencv.threshold(0.6, "BINARY"); // seuillage binaire pour éliminer le fond 
        // NB : le seuil est mis ici volontairement bas pour augmenter le nombre de fausses détections
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //-- détection de blobs ---
        //blobs=opencv.blobs(true); // blobs javacvPro avec paramètres par défaut +/- debug    
        
        //blobsArray=blobs(minArea,maxArea,maxBlob, findHoles, maxVertices, debug);
        blobsArray = opencv.blobs(opencv.area()/512, opencv.area()/2, 20, true, 1000, true ); // blobs javacvPro +/- debug    
        // la fonction renvoie un tableau de Blob
        // chaque Blob est caractérisé par son aire, son centre, le rectangle entourant, le tableau des points du contour
        // pour info : la fonction blobs mémorise en interne un tableau des séquences de points détectés (jusqu'à l'appel suivant de la fonction blobs() )
        // pour utilisation avec les fonctions avancées convexHull et detectDefect par exemple

        //--- affichage des Blobs avant sélection ---  

        //-- réaffichage image de départ --
        image(img,0, opencv.height()); 

        //-- dessin du rectangle autour du tracé de la forme -- 
        opencv.drawRectBlobs(blobsArray,0,opencv.height(),1); // trace rectangle en se basant sur point référence et avec les paramètres

        //-- dessin du pourtour du blob sur l'image de départ --
        opencv.drawBlobs(blobsArray,0,opencv.height(),1 ); // trace les formes du tableau de Blobs en se basant sur point référence + paramètre par défaut

        //-- dessin du centre du blob sur le tracé de la forme -- 
        opencv.drawCentroidBlobs (blobsArray,0,opencv.height(),1); // trace le centre des Blob en se basant sur point référence + échelle et avec les paramètres par défaut
 

        //--- sélection des Blob cohérents --- 
        //blobsArray=opencv.selectBlobs(blobsArray, hwTest, ratioWHTest, ratioHWTest, areaTest, areaRatioTest, mode, debug); 
        //blobsArray=opencv.selectBlobs(blobsArray, true, 3, 3, true, 0.2, true, true); // sélectionne que les rectangles ayant au moins 20% surface occupée
        //blobsArray=opencv.selectBlobs(blobsArray, true, 0.8, 0.8, true, 0.6, true, true); // sélectionne que proche carré avec 60% surface occupée = balles        

        blobsArray=opencv.selectBallBlobs(blobsArray); // sélectionne uniquement les Blobs pouvant correspondre à 1 balle 
        //blobsArray=opencv.selectBlobs(blobsArray, true, 0.7, 0.7, true, 0.6, true, true); // équivalent - sélectionne que proche carré avec 60% surface occupée = balles        

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
        
        
         //------ tracé d'une ligne si 2 balles sont détectées ------- 
        
        if (blobsArray.length==2) { // si 2 blobs détectés 
        
          opencv.drawLine( // trace la ligne entre les 2 blobs détectés en se basant sur les centres... 
                blobsArray[0].centroid, // le point de début de la ligne
                blobsArray[1].centroid, // le point de fin de la ligne
                opencv.width(), opencv.height(), // coordonnées référence objet
                1, // facteur d'échelle à utilisr - 1 par défaut  
                color(255,0,0), 5, // paramètres graphiques
                true // debug
                );
        }

        
       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


