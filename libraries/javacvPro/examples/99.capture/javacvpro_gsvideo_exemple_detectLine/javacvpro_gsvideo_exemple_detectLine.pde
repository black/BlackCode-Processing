
// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - Mars 2012
// Tous droits réservés - Licence GPLv3

// Exemple fonction detectLine - détection de lignes par Algorithme de Hough

import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/

import monclubelec.javacvPro.*; // importe la librairie javacvPro
import java.awt.*; // importe la classe Rectangle, Point du langage Java
// l'objet rectangle fournit les champs x,y du centre et hauteur/largeur (height/width) du rectangle

PImage img;

GSCapture cam; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

Line[] linesArray=null; // tableau pour la détection des lignes

int widthCapture=320; // largeur image capture
int heightCapture=240; // hauteur image capture
int fpsCapture=30; // framerate de Capture

int millis0=0; // variable mémorisation millis()

//--- coordonnées de référence pour centrer l'image
int xRef0=0; 
int yRef0=0; 

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        //--- initialise fenêtre Processing 
        size (widthCapture*2, heightCapture); // crée une fenêtre Processing de la 2xtaille du buffer principal OpenCV
        //size (img.width, img.height); // aalternative en se basant sur l'image d'origine
        frameRate(fpsCapture); // taux de rafraichissement de l'image 

       //---- initialise la webcam ---
       //cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
       cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video1", fpsCapture); // Initialise objet GSCapture désignant webcam - forme complète

        //--- initialise OpenCV ---
	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture, heightCapture); // initialise les buffers OpenCv à la taille de l'image
        
      	cam.start();  // démarre objet GSCapture = la webcam
     
        //--- coordonnées de référence pour centrer l'image
        //xRef0=width/4; 
        //yRef0=height/4; 
 
      
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
        image(img,xRef0+0,yRef0+0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //--- opérations sur image ---

        millis0=millis(); // mémorise millis()  

        opencv.sobel(); // applique le filtre de sobel sur le buffer principal OpenCV avec paramètres par défaut noyau = 3 et coeff=1)
        opencv.gray(); // passage en niveau de gris
        opencv.threshold(0.8, "BINARY"); // seuillage binaire pour éliminer le fond 
        
      
        
        //---- détection des lignes ----- 
        
        float seuilCanny=0; //--- 0 si pas filtre de Canny 

        linesArray=opencv.detectLines ( // détection de ligne avec algorithme de Hough version Standard
				opencv.Buffer, // opencv_core.IplImage iplImgIn, // image source 
				1, // double rhoIn, // la résolution pour la longueur du vecteur normal
				5, // double thetaIn, // la résolution angulaire reçue en degré - converti en radians pour la fonction native OpenCV
				seuilCanny,//float thresholdCannyIn, // ajout : 1er seuil du filtre Canny utilisé. Le second seuil Canny vaut seuilCannyIn/2 - Canny pas utilisé ici si =0
				60,//int thresholdAccumulatorIn, // seuil pour l'accumulateur - droite sélectionnée seulement si nombre vote > seuil 
				true //boolean debug // drapeau affichage messages
				); 

        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 

/*
        //--- pour estimation effet du Canny en interne fonction detectLines -- affiche après détection l'effet du Canny sur le Buffer
        opencv.canny(seuilCanny,seuilCanny/2); 
        image(opencv.getBuffer(),0,opencv.height()); // affiche le buffer principal OpenCV dans la fenêtre Processing
        //image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing
*/

        //---- affichage du tableau de lignes --- 
        for (int i=0; i<linesArray.length; i++) { // passe en revue les lignes 
          
          stroke (255,0,0); 
          line(xRef0+linesArray[i].pointY0.x, yRef0+linesArray[i].pointY0.y, xRef0+linesArray[i].pointYMax.x, yRef0+linesArray[i].pointYMax.y); // trace lignes 
          
        }


        //--- affiche image finale --- 

        image(opencv.getBuffer(),xRef0+widthCapture,yRef0+0); // affiche le buffer principal OpenCV dans la fenêtre Processing        

  } // fin if available

} // fin draw


