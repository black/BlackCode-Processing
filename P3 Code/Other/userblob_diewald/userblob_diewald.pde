import SimpleOpenNI.*;
import java.util.Locale;
import diewald_CV_kit.libraryinfo.*;
import diewald_CV_kit.utility.*;
import diewald_CV_kit.blobdetection.*;

PFont font;
PImage edgeImg;
int size_x, size_y;

BlobDetector blob_detector;
SimpleOpenNI kinect;

void setup() {

  size(640, 480, OPENGL);
  smooth();
  // initialize kinect object
  kinect = new SimpleOpenNI(this);
  kinect.setMirror(true);
  kinect.enableDepth();
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);

  edgeImg = createImage(width, height, ALPHA);
  blob_detector = new BlobDetector( width, height);
  blob_detector.setResolution(1);
  blob_detector.computeContours(true);
  blob_detector.computeBlobPixels(false);
  blob_detector.setMinMaxPixels(12*12, 300*height);
  blob_detector.setBLOBable(new BLOBable_Colorizer(this, edgeImg));
  userInt = new int[width*height];
}

int[] userInt;
int userID;
// used by kinect
void onNewUser(int uID) {
  userID = uID;
  println("tracking");
}

public void draw() {
  background(255);
  kinect.update();
  if (kinect.getNumberOfUsers() > 0) {
    int[] userMap = kinect.getUsersPixels(SimpleOpenNI.USERS_ALL);

    edgeImg.loadPixels();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int i = x + y * width;
        if (userMap[i] != 0) {
          userInt[i] = 255;
        } 
        else {
          userInt[i] = 0;
        }
        edgeImg.pixels[i] = color(userInt[i]);
      }
    }
    edgeImg.updatePixels();
    blob_detector.update();
    ArrayList<Blob> blob_list = blob_detector.getBlobs();
    for (int blob_idx = 0; blob_idx < blob_list.size(); blob_idx++ ) {
      // get the current blob from the blob-list
      Blob blob = blob_list.get(blob_idx);
      //      System.out.println("number of pixels = " +blob.getNumberOfPixels() );
      // get the list of all the contours from the current blob
      ArrayList<Contour> contour_list = blob.getContours();
      // iterate through the contour_list
      for (int contour_idx = 0; contour_idx < contour_list.size(); contour_idx++ ) {
        // get the current contour from the contour-list
        Contour contour = contour_list.get(contour_idx);
        // get the current boundingbox from the current contour
        BoundingBox bb = contour.getBoundingBox();
        // handle the first contour (outer contour = contour_idx == 0) different to the inner contours
        if ( contour_idx == 0) {
          // draw the contour
          drawContour(contour.getPixels(), color(255, 0, 0), color(0, 255, 0, 50), !true, 1); 
          // example how to simplify a contour
          int repeat_simplifying = (int) map(mouseX, 0, width, 0, 50);
          // can improve speed, if the contour is needed for further work
          ArrayList<Pixel> contour_simple = Polyline.SIMPLIFY(contour, 2, 1);
          // repeat the simplifying process a view more times
          for (int simple_cnt = 0; simple_cnt < repeat_simplifying; simple_cnt++) {
            contour_simple= Polyline.SIMPLIFY(contour_simple, 2, simple_cnt);
          }
          // draw the simplified contour
          drawContour(contour_simple, color(0, 200, 200), color(0, 0, 200, 50), false, 2);
          ConvexHullDiwi convex_hull = new ConvexHullDiwi();
          convex_hull.update(contour_simple);
        }
      }
    }
  }
}

