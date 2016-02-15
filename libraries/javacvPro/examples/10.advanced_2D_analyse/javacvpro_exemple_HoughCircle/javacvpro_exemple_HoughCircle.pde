
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - decembre 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction detectCircles()

import monclubelec.javacvPro.*; // importe la librairie javacvPro

//import java.awt.Rectangle; // importe la classe Rectangle du langage Java
// l'objet rectangle fournit les champs x,y du centre et hauteur/largeur (height/width) du rectangle

PImage img;

Circle[] circlesArray=null; // tableau pour la détection des cercles


String url="http://www.mon-club-elec.fr/mes_images/online/ball.jpg"; // String contenant l'adresse internet de l'image à utiliser
//String url="/home/hinault/Bureau/trans/contour1.png"; // String le chemin absolu de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //-- charge image utilisée --- 
        img=loadImage(url,"jpg"); // crée un PImage contenant le fichier à partir adresse web
        
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
        //image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //opencv.gray(); 

        //--- application d'un seuillage binaire ---
        opencv.threshold(0.8, "BINARY"); // seuillage binaire pour éliminer le fond 

        
        //--- application sobel --- 
        //opencv.sobel(); 

        //opencv.blur(5); // flou
        
        image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing
        //image(opencv.getBuffer(),0,opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing


        //detection des cercles 
        
        //opencv.detectCircles(opencv.Buffer, true);  // forme minimale 
        // NB : la fonction native intègre canny threshold et threshold/2
        
        float seuilCanny=400; 
        
        circlesArray=opencv.detectCircles ( // forme détaillée
				opencv.Buffer, // opencv_core.IplImage iplImgIn, // image source 
				0.2, // float dpIn, // coeff diviseur de la résolution de l'accumulateur dans l'espace de Hough
				10, //float minDistIn, // distance minmale entre 
				seuilCanny, // float thresholdCannyIn, //  1er seuil du filtre Canny utilisé. Le second seuil Canny vaut seuilCannyIn/2
				20, // float thresholdAccumulatorIn, // seuil utilisé par l'accumulateur pour prise en compte des centres des cercles. 
				0, // int minRadiusIn, // rayon minimum - mettre 0 par défaut
				0, // int maxRadiusIn, // rayon maximum - mettre 0 par défaut
				true // boolean debug // drapeau affichage messages
				);  

        //--- pour estimation effet du Canny en interne fonction detectCirles -- affiche après détection l'effet du Canny sur le Buffer
        opencv.canny(seuilCanny,seuilCanny/2); 
        image(opencv.getBuffer(),0,opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //-- réaffichage image de départ --
        image(img,opencv.width(), opencv.height()); 

        // -- dessine les cercles 
        opencv.drawCircles ( // trace le tableau d'objet Circles 
				circlesArray, // Circle[] circlesIn, // le tableau de cercles à tracer
				opencv.width(),opencv.height(), //int xRefIn, int yRefIn, // les coordonnées du cercle à tracer
				1, // float scaleIn, // l'échelle à utiliser
                                1, // radiusScaleIn, // échelle à utiliser pour le rayon
				color(255,255,0), 1, // int colorStrokeIn, int strokeWeightIn, // couleur et épaisseur du pourtour du cercle
				false, 0, //boolean fillIn, int colorFillIn, // drapeau de remplissage et couleur de remplissage
				true // boolean debugIn // drapeau d'affichage des messages 
				); 

           //--- alternative - dessine les centres des cercles 
           opencv.drawCenterCircles (
				circlesArray, // Circle[] circlesIn, // le tableau de cercles à tracer
				opencv.width(),opencv.height(), //int xRefIn, int yRefIn, // les coordonnées du cercle à tracer
				1, // float scaleIn, // l'échelle à utiliser
				5,// int radiusIn, // rayon à utiliser 
				color(255,0,0), 1, // int colorStrokeIn, int strokeWeightIn, // couleur et épaisseur du pourtour du cercle
				true, color(255,0,0), //boolean fillIn, int colorFillIn, // drapeau de remplissage et couleur de remplissage
				true // boolean debugIn // drapeau d'affichage des messages 
				);


       noLoop(); // stop programme        
}


void  draw() { // fonction exécutée en boucle

}


