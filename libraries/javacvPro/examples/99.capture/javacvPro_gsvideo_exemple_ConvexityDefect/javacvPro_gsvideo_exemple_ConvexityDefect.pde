// Programme processing
// généré avec le générateur de code Processing
// du site www.mon-club-elec.fr 
// par X. HINAULT - tous droits réservés

// Programme écrit le : 6/11/2011. MAJ : Mars 2012

// ------- Licence du code de ce programme : GPL v3----- 
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License,
//  or any later version.
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

/////////////// Description du programme //////////// 
// Utilise le clavier
// Utilise la librairie GSVideo de capture et lecture vidéo
// Utilise la librairie javacvPro de traitement d'image et reconnaissance visuelle

/*
Soustraction du fond par différence absolue pixel par pixel sur un flux vidéo
Détection de la forme de la main
Détection des points de convexité et des "creux" significatifs (convexityDefect)
*/

// XXXXXXXXXXXXXXXXXXXXXX ENTETE DECLARATIVE XXXXXXXXXXXXXXXXXXXXXX 

// inclusion des librairies utilisées 

import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/
// et ici : http://codeanticode.wordpress.com/2011/05/16/gsvideo-09-release

import monclubelec.javacvPro.*; // importe la librairie javacvPro qui implémente le traitement d'image avancé et la reconnaissance visuelle pour Processing
// cette librairie se base sur les fonctions java de la librairie javacv par Samuel Audet : http://code.google.com/p/javacv/
// javacv implémente en Java les centaines de fonctions natives de la librairie OpenCV (2500 algorithmes) !
// la librairie javacvPro doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// dispo ici : http://www.mon-club-elec.fr/pmwiki_reference_lib_javacvPro/pmwiki.php
// nécessite également que la librairie native OpenCV 2.3.1 soit installée sur votre ordinateur
// NB : compatibilité avec la plupart des fonctions de la librairie OpenCV pour Processing : http://ubaa.net/shared/processing/opencv/

// déclaration objets 

PImage imgSrc, imgDest; // déclare un/des objets PImage (conteneur d'image Processing)

GSCapture cam; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

Blob[] blobsArray=null; // tableau pour la détection des blobs (contour de forme)

ConvexityDefect[] cdArray=null; // tableau pour le stockage de convexity defect

// déclaration variables globales 

//------ déclaration des variables de couleur utiles ---- 
int jaune=color(255,255,0); 
int vert=color(0,255,0); 
int rouge=color(255,0,0); 
int bleu=color(0,0,255); 
int noir=color(0,0,0); 
int blanc=color(255,255,255); 
int bleuclair=color(0,255,255); 
int violet=color(255,0,255); 


// variable pour la taille de la capture video
int widthCapture=320; // largeur capture
int heightCapture=240; // hauteur capture
int fpsCapture=20; // framerate (image/secondes) pour la capture video

long millis0=0; // variable de mémorisation de millis

// XXXXXXXXXXXXXXXXXXXXXX  Fonction SETUP XXXXXXXXXXXXXXXXXXXXXX 

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

	// ---- initialisation paramètres graphiques utilisés
	colorMode(RGB, 255,255,255); // fixe format couleur R G B pour fill, stroke, etc...
	fill(0,0,255); // couleur remplissage RGB - noFill() si pas de remplissage
	stroke (0,0,255); // couleur pourtour RGB - noStroke() si pas de pourtour
	rectMode(CORNER); // origine rectangle : CORNER = coin sup gauche | CENTER : centre 
	imageMode(CORNER); // origine image : CORNER = coin sup gauche | CENTER : centre
	ellipseMode(CENTER); // origine cercles / ellipses : CENTER : centre (autres : RADIUS, CORNERS, CORNER
	//strokeWeight(0); // largeur pourtour
	frameRate(30);// Images par seconde - The default rate is 60 frames per second

	// --- initialisation fenêtre de base --- 
	size(widthCapture*2,heightCapture*2); // ouvre une fenêtre xpixels  x ypixels 
	background(0,0,0); // couleur fond fenetre



// --- initialisation des objets et fonctionnalités utilisées --- 

	//======== Initialisation Objets GSVideo (capture et/ou lecture video =========

	//GSCapture(this, int requestWidth, int requestHeight, [String sourceName], [String cameraName], [int frameRate]) 
        cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
	//cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video1",fpsCapture); // Initialise objet GSCapture désignant webcam
	// largeur et hauteur doivent être compatible avec la webcam - typiquement 160x120 ou 320x240 ou 640x480...
	// Meilleurs résultats avec framerate webcam entre 20 et 30 et frameRate programme idem ou multiple plus grand (40 pour 20 par ex)
	// la liste des webcam installées sous Ubuntu (Gnu/Linux) est donnée par la commande : ls /dev/video* 

	// cam1.play();  // démarre objet GSCapture = la webcam - version GSVideo avant 0.9
	cam.start();  // démarre objet GSCapture = la webcam - version GSVideo après 0.9

	//======== Initialisation Objets OpenCV (librairie javacvPro : traitement d'image et reconnaissance visuelle) =========

	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture,heightCapture); // crée les buffers image de la taille voulue


        println("Appuyer sur ESPACE pour mémoriser le fond."); 
        
} // fin fonction Setup

// XXXXXXXXXXXXXXXXXXXXXX Fonction Draw XXXXXXXXXXXXXXXXXXXX 

void  draw() { // fonction exécutée en boucle


// Code type capture GSVideo - utilisation possible aussi de captureEvent() 

  if (cam.available() == true) { // si une nouvelle frame est disponible sur la webcam
    cam.read(); // acquisition d'un frame 

    imgSrc=cam.get(); // récupère l'image GS video dans Pimage
    opencv.copy(imgSrc); // charge l'image dans le buffer openCV

    //opencv.copy(cam1.get()); // autre possibilité - charge directement l'image GSVideo dans le buffer openCV

    millis0=millis(); // mémorise millis()  

    opencv.blur(); // +/- effet flou préalable

    // +/- application de la différence absolue sur un seul canal couleur 
    //opencv.extractRGB(); // extrait les 3 canaux couleur du buffer principal
    //opencv.copyTo(opencv.BufferB,opencv.Buffer); // copie le buffer couleur dans le buffer principal 

    image(opencv.image(), 0, 0); // affiche buffer principal
    //image(opencv.getBufferB(), 0, 0); // affiche buffer couleur

    
    // --- réalise différence absolue -- 
    
    opencv.absDiff(); // réalise soustraction entre Buffer et Memory et met le résultat dans Memory2

    image(opencv.getMemory2(),0,heightCapture); // affiche image résultante stockée dans le mémory 2

    //--- opération sur le buffer Memory 2 

    

    //opencv.gray("MEMORY2"); // transforme en niveaux de gris le buffer MEMORY2
    //opencv.multiply(opencv.Memory2,4); 
    
    opencv.threshold(opencv.Memory2, 0.2, "BINARY"); // applique seuillage sur image - méthodes disponibles : BINARY, BINARY_INV, TRUNC, TOZERO, TOZERO_INV
    //-- le niveau de seuil est important--
    
    //opencv.invert("MEMORY2"); // inverse l'image du buffer MEMORY2
    image(opencv.getMemory2(),widthCapture,heightCapture); // affiche image résultante stockée dans le mémory 2


        //--- détection des Blobs --- 
        blobsArray=opencv.blobs(opencv.Memory2, opencv.area()/64,opencv.area(),20, true, 10000, true); // applique blobs à l'objet IplImage avec paramètres et renvoie tableau d'objets Blob


        //-- dessin du rectangle autour du tracé de la forme -- 
        opencv.drawRectBlobs(blobsArray,opencv.width(),opencv.height(),1); // trace rectangle en se basant sur point référence et avec les paramètres

        //-- dessin du pourtour du blob sur l'image de départ --
        opencv.drawBlobs(blobsArray,opencv.width(),opencv.height(),1 ); // trace les formes du tableau de Blobs en se basant sur point référence + paramètre par défaut

        //-- dessin du centre du blob sur le tracé de la forme -- 
        opencv.drawCentroidBlobs (blobsArray,opencv.width(),opencv.height(),1); // trace le centre des Blob en se basant sur point référence + échelle et avec les paramètres par défaut

        //--- dessin des points de convexité ---
        //opencv.drawConvexPoints(blobsArray, xRef, yRef, scale, radius,colorStroke, strokeWeight, fill, colorFill, debug);
        //opencv.drawConvexPoints(blobsArray, opencv.width(),opencv.height(),1, 5,color(0,255,0), 2, false, 0, true);

        //--- dessin des points des "zones de creux" (Convexity Defect) ---
         //opencv.drawConvexityDefect(blobsArray, xRef, yRef, scale, radius,colorStroke, strokeWeight, fill, colorFill, minDepth, debug);
        //opencv.drawConvexityDefect(blobsArray, opencv.width(),opencv.height(),1, 10,color(255,0,255), 2, false, 0, 20, true);

        //--- tracé des "ConvexityDefect" sur l'image de départ --- 
        //opencv.drawConvexityDefect(blobsArray, 0,0,1, 10,color(255,0,255), 2, false, 0, 20, true);
        
         for (int i=0; i<blobsArray.length; i++) { // passe en revue les blobs
        
          // Sélectionne les objets ConvexityDefect utiles 
          // ConvexityDefect[] convexityDefects(Blob blobIn, float minDepthIn, float angleMaxIn, boolean debugIn)
          cdArray=opencv.convexityDefects(blobsArray[i], (float)20, radians(110), true); 
          
          //--- dessine le tableau de ConvexityDefect
          //drawConvexityDefects(ConvexityDefect[] cdArrayIn, int xRefIn, int yRefIn, float scaleIn, int radius, int colorStrokeIn, int strokeWeightIn, boolean fillIn, int colorFillIn, boolean lineIn, int colorStrokeLineIn, int strokeWeightLineIn, int mode, boolean debugIn )
          opencv.drawConvexityDefects(cdArray, opencv.width(),opencv.height(),1.0, 10,color(0,255,255), 2, false, 0, true, color(0,0,255), 2, 1, false ); 


        }// fin for i - défile blobs  
        

        println("Durée traitement image OpenCV=" + (millis()-millis0)+"ms."); 


  } // fin if available


	// while(true); // stoppe boucle draw

} // fin de la fonction draw()

// XXXXXXXXXXXXXXXXXXXXXX Autres Fonctions XXXXXXXXXXXXXXXXXXXXXX 

//------------ gestion évènement clavier --------- 

void keyPressed() { // si une touche est appuyée

	if(key==' ') { // si touche enfoncee

    	opencv.remember();  // mémorise le Buffer dans le buffer Memory
    	image(opencv.getMemory(),widthCapture,0); // affiche l'image présente dans le buffer Memory

	} // fin si touche enfoncee 

} //--- fin si touche enfoncee


//------------- Fonction d'arret de Processing ---- 
	
public void stop(){ // fonction d'arrêt de Processing

	cam.delete(); // efface l'objet GScapture

	super.stop(); // obligatoire 

} // fin fonction stop()


//XXXXXXXXXXXXXXXXXX Fin du programme XXXXXXXXXXXXXXXXX

// ----- mémo fonctions processing -------

//----- la fenêtre ---- 
// size(x, y); // ouvre une fenêtre de x pixels  sur y pixels
// background(couleur); // couleur fond fenetre
// smooth(); 

// ----- couleurs formes ---- 
// fill(0,0,0); // couleur remplissage RGB
//fill(couleur); // couleur remplissage

// stroke (0,0,0); // couleur pourtour RGB
// stroke (coukeur); // couleur pourtour
// strokeWeight(largeur); // largeur pourtour
//----- tracé de formes ---- 
//arc(x, y, width, height, start, stop)
// rect(x, y, largeur, hauteur); // trace un rectangle, un carré
// point(x, y); // trace un point; // trace une ligne
// ellipse(x, y, largeur, hauteur); // trace un cercle, une ellipse
// line(x1, y1, x2, y2); // trace une  ligne du point x1,y1 au point x2,y2 
// triangle(x1, y1, x2, y2, x3, y3); // trace un triangle


//------ constantes disponibles ----- 
// PI, TWO_PI, QUARTER_PI, HALF_PI




