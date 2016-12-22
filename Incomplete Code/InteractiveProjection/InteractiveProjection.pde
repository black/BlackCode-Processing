import SimpleOpenNI.*; 
import blobDetection.*; // blobs
import java.awt.Polygon;

SimpleOpenNI  context; 
PImage cam, blobs;
BlobDetection theBlobDetection;
PolygonBlob poly = new PolygonBlob();
int kinectWidth = 640;
int kinectHeight = 480;

void setup() {
  size(640, 480);
  BlobDetection theBlobDetection;
  context = new SimpleOpenNI(this);
  if (context.isInit() == false) {
    println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
    exit();
    return;
  } else {
    context.setMirror(true); 
    context.enableDepth(); 
    context.enableUser();
    context.enableRGB();
    blobs = createImage(kinectWidth/3, kinectHeight/3, RGB);
    theBlobDetection = new BlobDetection(blobs.width, blobs.height);
    theBlobDetection.setThreshold(0.2);
  }
}

void draw() {
  background(-1);  
  context.update();
  cam = context.rgbImage().get();
  blobs.copy(cam, 0, 0, cam.width, cam.height, 0, 0, blobs.width, blobs.height);
  blobs.filter(BLUR);
  println(blobs.pixels);
  blobs.loadPixels();
  theBlobDetection.computeBlobs(blobs.pixels);
  blobs.updatePixels();
  poly.reset(); // clear the polygon (original functionality)
  poly.createPolygon();   // create the polygon from the blobs (custom functionality, see class)
}

