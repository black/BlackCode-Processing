
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

Circle[] circlesArray=null; // tableau pour la détection des cercles

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
       //cam = new GSCapture(this, widthCapture, heightCapture); // forme simplifiée
       cam = new GSCapture(this, widthCapture, heightCapture,"v4l2src","/dev/video1", fpsCapture); // Initialise objet GSCapture désignant webcam - forme complète

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
        //image(opencv.getBuffer(),opencv.width(),0); // affiche le buffer principal OpenCV dans la fenêtre Processing

        //opencv.gray();

        opencv.blur();
        
        //--- application d'un seuillage binaire ---
        opencv.threshold(0.6, "BINARY"); // seuillage binaire pour éliminer le fond


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
                                0.1, // float dpIn, // coeff diviseur de la résolution de l'accumulateur dans l'espace de Hough
                                10, //float minDistIn, // distance minmale entre
                                seuilCanny, // float thresholdCannyIn, //  1er seuil du filtre Canny utilisé. Le second seuil Canny vaut seuilCannyIn/2
                                15, // float thresholdAccumulatorIn, // seuil utilisé par l'accumulateur pour prise en compte des centres des cercles.
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


        println("Durée traitement image par OpenCV=" + (millis()-millis0)+" ms."); 

        //--- affiche image finale --- 

        //image(opencv.getBuffer(),widthCapture,0); // affiche le buffer principal OpenCV dans la fenêtre Processing        

  } // fin if available

} // fin draw


