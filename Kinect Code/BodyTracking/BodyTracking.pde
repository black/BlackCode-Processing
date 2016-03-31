import SimpleOpenNI.*;
import java.util.*; 
import diewald_CV_kit.libraryinfo.*;
import diewald_CV_kit.utility.*;
import diewald_CV_kit.blobdetection.*;

BlobDetector blob_detector;

ArrayList triangles = new ArrayList();
ArrayList points = new ArrayList(); 

color c[] = { 
  #2ecc71, #d35400, #f1c40f, #c0392b, #2980b9, #8e44ad
};


SimpleOpenNI kinect;  

//color usericon = color(0, 255, 0);
int blob_array[];
int userCurID;
int cont_length = 640*480; 
PImage edgeImg ;
int kheight = 480;
int kwidth = 640;
color cc;
void setup() {
  size(displayWidth, displayHeight);
  kinect = new SimpleOpenNI(this);
  kinect.setMirror(true);
  kinect.enableDepth();
  kinect.enableUser();
  kinect.enableRGB();
  blob_array=new int[cont_length]; 
  blob_detector = new BlobDetector( kwidth, kheight); 
  blob_detector.setResolution(1);
  edgeImg =  createImage(kwidth, kheight, ARGB);
  cc = c[(int)random(6)];
}


void draw() {
  //  background(0);  
  noStroke();
  fill(0);
  rect(0, 0, width, height);
  kinect.update();

  int[] depthValues = kinect.depthMap();
  int[] userMap =null;
  int userCount = kinect.getNumberOfUsers();
  // println(userCount);
  if (userCount > 0) {
    userMap = kinect.userMap();
  }


  for (int y=0; y<kinect.depthHeight (); y++) {
    for (int x=0; x<kinect.depthWidth (); x++) {
      int index = x + y * kinect.depthWidth();
      if (userMap != null && userMap[index] > 0) {
        // int colorIndex = userMap[index] % userColors.length;
        userCurID = userMap[index];
        //blob_array[index] = 255;  
        edgeImg.pixels[index] = color(0, 255, 0);
      } else {
        // blob_array[index] = 0; 
        edgeImg.pixels[index] = 0;
      }
    }
  }  
  edgeImg.loadPixels();
  // use of contour
  blob_detector.computeContours(true);
  blob_detector.computeBlobPixels(false);
  blob_detector.setMinMaxPixels(12*12, 300*kheight); // blbol threshold
  blob_detector.setBLOBable(new BLOBable_Colorizer(this, edgeImg)); // assign blob detection to edgeImg
  blob_detector.update();

  ArrayList<Blob> blob_list = blob_detector.getBlobs(); 
  for (int blob_idx = 0; blob_idx < blob_list.size (); blob_idx++ ) { 
    Blob blob = blob_list.get(blob_idx);     
    ArrayList<Contour> contour_list = blob.getContours();
    for (int contour_idx = 0; contour_idx < contour_list.size (); contour_idx++ ) {
      Contour contour = contour_list.get(contour_idx);
      BoundingBox bb = contour.getBoundingBox();
      if (contour_idx == 0) {
        int repeat_simplifying = 8; // CONTROL CONTOUR
        ArrayList<Pixel> contour_simple = Polyline.SIMPLIFY(contour, 4, 2);
        for (int simple_cnt = 0; simple_cnt < repeat_simplifying; simple_cnt++) {
          contour_simple= Polyline.SIMPLIFY(contour_simple, 2, simple_cnt);
        }
        drawContour(contour_simple, color(255, 255, 255), cc, true, 4);
      } else {
      }
    }
  }
} 

void onNewUser(int userId) {
  println("detected" + userId);
  cc = c[(int)random(6)];
}

void onLostUser(int userId) {
  println("lost: " + userId);
} 

boolean sketchFullScreen() {
  return true;
}

