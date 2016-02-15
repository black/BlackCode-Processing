// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - decembre 2011
// Tous droits réservés - Licence GPLv3

// Exemple de détection des points clés d'une image objet et d'une image scene
// avec l'algorithme SURF

import monclubelec.javacvPro.*; // importe la librairie javacvPro

import java.awt.*; // pour Objets Java Point, Rectangle, etc.. 

PImage imgObjet, imgScene; // déclare 2 objets PImage (conteneur image Processing)

String urlObjet="http://www.mon-club-elec.fr/mes_images/online/objet2.png"; // String contenant l'adresse internet de l'image à utiliser
String urlScene="http://www.mon-club-elec.fr/mes_images/online/scene2b.png"; // String contenant l'adresse internet de l'image à utiliser

//String urlObjet="http://www.mon-club-elec.fr/mes_images/online/pumaobjet.jpg"; // String contenant l'adresse internet de l'image à utiliser
//String urlScene="http://www.mon-club-elec.fr/mes_images/online/pumascene.jpg"; // String contenant l'adresse internet de l'image à utiliser

OpenCV opencv; // déclare un objet OpenCV principal

Keypoint[] keypointsObjetArray=null; // déclaration d'un tableau pour le stockage des points clés de l'objet
Keypoint[] keypointsSceneArray=null; // déclaration d'un tableau pour le stockage des points clés de la scene


void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- création objet Opencv principal
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This

        // Remarque : l'image objet sera mémorisée dans le buffer Memory et l'image Scene dans le buffer principal
        // La librairie JavacvPro permet d'initialiser le buffer Memory et le buffer principal avec des tailles différentes
       
       
        //-- charge image objet utilisée --- 
        imgObjet=loadImage(urlObjet,"png"); // crée un PImage contenant le fichier à partir adresse web
        //imgObjet=loadImage(urlObjet,"jpg"); // crée un PImage contenant le fichier à partir adresse web

        opencv.allocateMemory(imgObjet.width, imgObjet.height); // initialise les buffers Memory OpenCv à la taille de l'image Objet
        
        opencv.remember(imgObjet); // charge le PImage dans le buffer Memory OpenCV
        //opencv.copyToMemory(imgObjet); // charge le PImage dans le buffer OpenCV - idem 
        
        //-- charge image scene utilisée --- 
        imgScene=loadImage(urlScene,"png"); // crée un PImage contenant le fichier à partir adresse web
        //imgScene=loadImage(urlScene,"jpg"); // crée un PImage contenant le fichier à partir adresse web

        opencv.allocateBuffer(imgScene.width, imgScene.height); // initialise les buffers OpenCv à la taille de l'image Scene
        
        opencv.copy(imgScene); // charge le PImage dans le buffer OpenCV

        
        //--- initialise fenêtre Processing 
        size (imgObjet.width+imgScene.width, opencv.height()*2); // crée une fenêtre Processing de la taille voulue
        smooth(); // activation lissage forme
        
        //--- affiche en double les images de départ ---         
        image(opencv.getMemory(),0,0); // affiche le buffer principal OpenCV dans la fenêtre Processing
        image(opencv.getMemory(),0,imgScene.height); // affiche le buffer principal OpenCV dans la fenêtre Processing

        image(opencv.getBuffer(),imgObjet.width,0); // affiche le buffer principal OpenCV dans la fenêtre Processing
        image(opencv.getBuffer(),imgObjet.width,imgScene.height); // affiche le buffer principal OpenCV dans la fenêtre Processing
        
        //----- initialisation SURF ---------- 
        //opencv.initSURF(); // initialisation par défaut 
        
        opencv.initSURF ( // initialisation avec paramètres 
		500, //float hessianThreshold, // entre 300 et 500 - 400 par défaut 
		4, //int octaves, // 4 par défaut 
		6, //int nOctaveLayers, // 2 par défaut 
		false // boolean upright // false si prise en compte de l'orientation, true sinon = plus rapide  
		);

        //--- détection des points clés de l'objet 
        // le tableau de point clé va contenir les points clés calculés par l'algorithme SURF
        
        // détection des point-clé de l'objet
        keypointsObjetArray=opencv.keypointsSURF(opencv.Memory,opencv.OBJECT,true); // forme simplifiée utilisant paramètres par défaut 
        //keypointsObjetArray=opencv.keypointsSURF(opencv.Memory, 0,imgScene.height,1,opencv.OBJECT, true, false); // forme avec coordonnées Ref pour tracé point natif si debug

        //--- dessin des points clés de l'objet à partir du tableau de points clés obtenu
        
        //opencv.drawKeypoints(keypointsObjetArray, true); // forme simplifiée utilisant paramètres par défaut 

        
        opencv.drawKeypoints ( // forme complète fonction 
		keypointsObjetArray, //Keypoint[] keypointsIn, 
		0,imgScene.height,1,//int xRefIn, int yRefIn, float scaleIn, 
                5, // int radius - utiliser -1 pour rayon des cercles = size des Keypoints
		color(0,255,255), 1, //int colorStrokeIn, int strokeWeightIn, 
		true,color(0,0,255),//boolean fillIn, int colorFillIn, 
		false //boolean debug
		); // fin draw keypoints

        //--- détection des points clés de la scène 
        keypointsSceneArray=opencv.keypointsSURF(opencv.Buffer,opencv.SCENE,true); // forme simplifiée
        //keypointsSceneArray=opencv.keypointsSURF(opencv.Buffer, imgObjet.width,imgScene.height,1, opencv.SCENE,true, false); // forme avec coordonnées Ref pour tracé point natif si debug
        
        //--- dessin des points clés de la scene à partir du tableau de points clés obtenu
        
        //opencv.drawKeypoints(keypointsSceneArray, true); // forme simplifiée utilisant paramètres par défaut 

        
        opencv.drawKeypoints ( // forme complète fonction 
		keypointsSceneArray, //Keypoint[] keypointsIn, 
		imgObjet.width,imgScene.height,1,//int xRefIn, int yRefIn, float scaleIn, 
                5, // int radius - utiliser -1 pour rayon des cercles = size des Keypoints
		color(255,255,0), 1, //int colorStrokeIn, int strokeWeightIn, 
		true,color(255,0,0),//boolean fillIn, int colorFillIn, 
		false //boolean debug
		); // fin draw keypoints


  noLoop();
  
} // fin Setup


void  draw() { // fonction exécutée en boucle

}


