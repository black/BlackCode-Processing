// Programme d'exemple de la librairie javacvPro
// par X. HINAULT - novembre 2011
// Tous droits réservés - Licence GPLv3

// Montre exemple utilisation de la fonction bgsMOGInit() et bgsMOGApply()


import codeanticode.gsvideo.*; // importe la librairie vidéo GSVideo qui implémente GStreamer pour Processing (compatible Linux)
// librairie comparable à la librairie native vidéo de Processing (qui implémente QuickTime..)- Voir Reference librairie Video Processing
// cette librairie doit être présente dans le répertoire modes/java/libraries du répertoire Processing (1-5)
// voir ici : http://gsvideo.sourceforge.net/
// et ici : http://codeanticode.wordpress.com/2011/05/16/gsvideo-09-release

import monclubelec.javacvPro.*; // importe la librairie javacvPro

GSCapture cam1; // déclare un objet GSCapture représentant une webcam
// L'objet GSCapture étend PImage - se comporte comme un conteneur des frames issues de la webcam

OpenCV opencv; // déclare un objet OpenCV principal

PImage imgSrc; 

int widthCapture=320;
int heightCapture=240;
int fpsCapture=20; 

void setup(){ // fonction d'initialisation exécutée 1 fois au démarrage

        size(widthCapture*2,heightCapture); 

	//======== Initialisation Objets GSVideo (capture et/ou lecture video =========

	// GSCapture(this, int requestWidth, int requestHeight, [int frameRate], [String sourceName], [String cameraName]) 
	cam1 = new GSCapture(this, widthCapture, heightCapture,fpsCapture,"v4l2src","/dev/video0"); // Initialise objet GSCapture désignant webcam
	// largeur et hauteur doivent être compatible avec la webcam - typiquement 160x120 ou 320x240 ou 640x480...
	// Meilleurs résultats avec framerate webcam entre 20 et 30 et frameRate programme idem ou multiple plus grand (40 pour 20 par ex)
	// la liste des webcam installées sous Ubuntu (Gnu/Linux) est donnée par la commande : ls /dev/video* 

	// cam1.play();  // démarre objet GSCapture = la webcam - version GSVideo avant 0.9
	cam1.start();  // démarre objet GSCapture = la webcam - version GSVideo après 0.9

	//======== Initialisation Objets OpenCV (librairie javacvPro : traitement d'image et reconnaissance visuelle) =========

	opencv = new OpenCV(this); // initialise objet OpenCV à partir du parent This
        opencv.allocate(widthCapture,heightCapture); // crée le buffer image de la taille voulue


        //--- Initialisation objet MOG --- Un objet bsgMOG est déclaré en interne
        //opencv.bgsMOG2Init(); // initialise bgsMOG avec paramètres par défaut
        // default : history = 16; varThreshold = 16, bShadowDetection= true

        opencv.bgsMOG2Init(1000,16,false); // initialise bgsMOG2 avec paramètres voulus 

}


void  draw() { // fonction exécutée en boucle

  if (cam1.available() == true) { // si une nouvelle frame est disponible sur la webcam
    cam1.read(); // acquisition d'un frame 

    imgSrc=cam1.get(); // récupère l'image GS video dans Pimage
    opencv.copy(imgSrc); // charge l'image dans le buffer openCV

    //opencv.copy(cam1.get()); // autre possibilité - charge directement l'image GSVideo dans le buffer openCV

    image(opencv.getBuffer(), 0, 0); // affiche image

    opencv.bgsMOG2Apply(opencv.Buffer, opencv.BufferGray, -1); // ajoute un frame à l'objet BackGround Subtractor MOG
    // la fonction initialise est appelée en interne au premier passage si besoin

    image(opencv.getBufferGray(), widthCapture, 0); // affiche image



  } // fin if available

} // fin draw


